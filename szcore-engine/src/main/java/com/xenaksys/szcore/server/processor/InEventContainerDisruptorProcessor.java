package com.xenaksys.szcore.server.processor;

import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventContainer;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.event.IncomingOscEvent;
import com.xenaksys.szcore.event.IncomingWebEvent;
import com.xenaksys.szcore.event.InstrumentEvent;
import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.event.ParticipantEvent;
import com.xenaksys.szcore.event.ParticipantStatsEvent;
import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.model.ClientInfo;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.net.ParticipantStats;
import com.xenaksys.szcore.process.AbstractContainerEventReceiverDisruptorProcessor;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.util.IpAddressValidator;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.xenaksys.szcore.Consts.DEFAULT_OSC_ERR_PORT;
import static com.xenaksys.szcore.Consts.DEFAULT_OSC_OUT_PORT;
import static com.xenaksys.szcore.Consts.DEFAULT_OSC_PORT;


public class InEventContainerDisruptorProcessor extends AbstractContainerEventReceiverDisruptorProcessor {
    static final Logger LOG = LoggerFactory.getLogger(InEventContainerDisruptorProcessor.class);

    private List<SzcoreIncomingEventListener> listeners = new CopyOnWriteArrayList<>();
    private final IpAddressValidator ipValidator = new IpAddressValidator();

    private final SzcoreServer server;
    private final Clock clock;
    private final EventFactory eventFactory;

    public InEventContainerDisruptorProcessor(SzcoreServer server, Clock clock, EventFactory eventFactory, Disruptor<EventContainer> disruptor) {
        super(disruptor);
        this.server = server;
        this.clock = clock;
        this.eventFactory = eventFactory;
    }

    @Override
    protected void processInternal(EventContainer eventContainer) {
        if(eventContainer == null || eventContainer.getEvent() == null){
            return;
        }

        SzcoreEvent event = eventContainer.getEvent();
        server.logEvent(event);
        try {
            EventType type = event.getEventType();
            switch (type) {
                case OSC:
                    processOscEvent((OscEvent) event);
                    break;
                case WEB_IN:
                    processWebEvent((IncomingWebEvent) event);
                    break;
                case WEB_SCORE:
                    processWebScoreEvent((WebScoreEvent) event);
                    break;
            }

        } catch (Exception e) {
            LOG.error("Failed to process event: " + eventContainer, e);
        }
    }

    private void processWebEvent(IncomingWebEvent event) {
//        LOG.info("processWebEvent: {}", event);
        server.getWebProcessor().process(event);
    }

    private void processWebScoreEvent(WebScoreEvent event) {
//        LOG.info("processWebEvent: {}", event);
        server.getScoreProcessor().process(event);
    }

    private void processOscEvent(OscEvent oscEvent) {
        if (oscEvent instanceof IncomingOscEvent) {
            processIncomingOscEvent((IncomingOscEvent) oscEvent);
        } else {
            LOG.error("Unexpected oscEvent type: {}", oscEvent);
        }
    }

    private void processIncomingOscEvent(IncomingOscEvent event) {
        if (event == null) {
            return;
        }

//        InetAddress inetAddress = event.getInetAddress();
//        boolean isParticipant = checkAddress(inetAddress);
//
//        if(!isParticipant){
//            addParticipant(inetAddress);
//        }

        String address = event.getAddress();
        switch (address) {
            case Consts.SZCORE_ADDR:
                processSzcoreMessage(event);
                break;
            case Consts.INSCORE_ADDR:
                processInscoreMessage(event);
                break;
            case Consts.OSC_INSCORE_ADDRESS_ROOT:
                processAppMessage(event);
                break;
            case Consts.ERR_ADDR:
                processError(event);
                break;
            default:
                LOG.error("Unknown address: " + address);
        }
    }

    private void processAppMessage(IncomingOscEvent event) {
        LOG.debug("Received APP message: " + event.getAddress() + " args: " + event.getArguments());
        if(isInscoreHello(event)){
            processInscoreHello(event);
            return;
        }

        notifyListeners(event);
    }

    private void processInscoreHello(IncomingOscEvent event) {
        List<Object> args = event.getArguments();

        String ip = (String) args.get(0);
        InetAddress inetAddress = event.getInetAddress();
        if (!inetAddress.getHostAddress().equals(ip)) {
            LOG.error("Inscore HELLO Event IP address: " + ip + " is not the same as InetAddress: " + inetAddress.getHostAddress() + ", assuming local host");
        }

        int remoteInPort = PropertyUtil.parseIntArg(args, 1, DEFAULT_OSC_PORT);
        String clientId = NetUtil.createClientId(inetAddress, remoteInPort);
        addParticipant(clientId, inetAddress, remoteInPort);
        addOutPort(clientId, inetAddress, remoteInPort);

        int remoteOutPort = PropertyUtil.parseIntArg(args, 2, DEFAULT_OSC_OUT_PORT);
        if (remoteOutPort != 0) {
            addInPort(remoteOutPort);
        }

        int remoteErrPort = PropertyUtil.parseIntArg(args, 3, DEFAULT_OSC_ERR_PORT);
        if (remoteErrPort != 0) {
            addInPort(remoteErrPort);
        }

        server.sendServerHelloEvent(clientId);

        ParticipantEvent participantEvent = eventFactory.createParticipantEvent(inetAddress, inetAddress.getHostAddress(), remoteInPort, remoteOutPort,
                remoteErrPort, 0, Consts.NAME_NA, clock.getSystemTimeMillis());

        notifyListeners(participantEvent);
    }

    private void processInscoreMessage(IncomingOscEvent event) {
        LOG.debug("Received Inscore message: " + event.getAddress() + " args: " + event.getArguments());
        notifyListeners(event);
    }

    private void processSzcoreMessage(IncomingOscEvent event) {
//        LOG.debug("Received SZCORE message: " + event.getAddress() + " args: " + event.getArguments());

        if (isSzcoreHello(event)) {
            processSzcoreHello(event);
        } else if (isSzcorePing(event)) {
            processPing(event);
        } else if (isSzcoreSetInstrument(event)) {
            processSetInstrument(event);
        } else if (isSzcoreSelectInstrumentSlot(event)) {
            processSelectInstrumentSlot(event);
        }

        notifyListeners(event);
    }

    private void processSetInstrument(IncomingOscEvent event) {
        List<Object> args = event.getArguments();

        String instrument = PropertyUtil.parseStringArg(args, 1, null);
        if (instrument == null) {
            LOG.error("processSetInstrument: Received invalid instrument");
            return;
        }

        int port = PropertyUtil.parseIntArg(args, 2, DEFAULT_OSC_PORT);
        InetAddress inetAddress = event.getInetAddress();
        String clientId = NetUtil.createClientId(inetAddress, port);

        if (!server.isParticipant(clientId)) {
            LOG.error("Invalid participant in set Instrument: " + inetAddress.getHostAddress());
            addParticipant(clientId, inetAddress, port);
            server.sendHello(inetAddress.getHostAddress());
        }

        server.addInstrumentOutPort(clientId, instrument);

        server.sendScoreInfo(instrument);

        InstrumentEvent instrumentEvent = new InstrumentEvent(inetAddress, port, instrument, clock.getSystemTimeMillis());

        notifyListeners(instrumentEvent);

    }

    private void processSelectInstrumentSlot(IncomingOscEvent event) {
        List<Object> args = event.getArguments();

        int slotNo = PropertyUtil.parseIntArg(args, 1, -1);
        if (slotNo < 0) {
            LOG.error("processSelectInstrumentSlot: Received invalid instrument slot");
            return;
        }

        String slotInstrument = PropertyUtil.parseStringArg(args, 2, null);
        if (slotInstrument == null) {
            LOG.error("processSelectInstrumentSlot: Received invalid slot instrument");
            return;
        }

        String sourceInst = PropertyUtil.parseStringArg(args, 3, null);
        if (sourceInst == null) {
            LOG.error("processSelectInstrumentSlot: Received invalid source instrument");
            return;
        }

        server.processSelectInstrumentSlot(slotNo, slotInstrument, sourceInst);
    }

    private void processSzcoreHello(IncomingOscEvent event) {
        List<Object> args = event.getArguments();

        int clientInPort = PropertyUtil.parseIntArg(args, 1, DEFAULT_OSC_PORT);
        InetAddress inetAddress = event.getInetAddress();
        String clientId = NetUtil.createClientId(inetAddress, clientInPort);
        addParticipant(clientId, inetAddress, clientInPort);
        addOutPort(clientId, inetAddress, clientInPort);

        int clientOutPort = PropertyUtil.parseIntArg(args, 2, DEFAULT_OSC_OUT_PORT);
        addInPort(clientOutPort);

        server.sendHello(clientId);

        ParticipantEvent participantEvent = eventFactory.createParticipantEvent(inetAddress, inetAddress.getHostAddress(), clientInPort, clientOutPort,
                0, 0, Consts.NAME_NA, clock.getSystemTimeMillis());

        notifyListeners(participantEvent);
    }

    private void processPing(IncomingOscEvent event) {
        long now = clock.getSystemTimeMillis();
        List<Object> args = event.getArguments();

        long sendTime = PropertyUtil.parseLongArg(args, 1, 0L);
        int port = PropertyUtil.parseIntArg(args, 2, DEFAULT_OSC_PORT);

        if (sendTime == 0L) {
            return;
        }

        InetAddress inetAddress = event.getInetAddress();
        String ipAddress = inetAddress.getHostAddress();
        String clientId = NetUtil.createClientId(inetAddress, port);

        if (!server.isParticipant(clientId)) {
            LOG.error("Ping from Invalid participant: " + inetAddress.getHostAddress());
            return;
        }

        ParticipantStats stats = server.getParticipantStats(clientId);
        if (stats == null) {
            stats = new ParticipantStats(clientId, ipAddress);
            server.addParticipantStats(stats);
        }

        long receivedTime = event.getCreationTime();
        double latency = 1.0 * (receivedTime - sendTime);
        double oneWayLatency = latency / 2.0;
        oneWayLatency = Math.round(oneWayLatency * 100.0) / 100.0;
//LOG.debug("Calculated oneWayLatency: " + oneWayLatency + " roundTriplatency: " + latency + " for " + ipAddress + " receivedTime: " + receivedTime + " sendTime: " + sendTime);

        stats.setPingLatency(latency);
        stats.setOneWayPingLatency(oneWayLatency);
        stats.setLastPingResponseTime(now);

        ParticipantStatsEvent statsEvent = eventFactory.createParticipantStatsEvent(inetAddress, ipAddress, port, latency,
                oneWayLatency, false, 0L, clock.getSystemTimeMillis());

        notifyListeners(statsEvent);
    }

    private void processError(IncomingOscEvent event) {
        LOG.debug("Received ERROR message: " + event.getAddress() + " args: " + event.getArguments());
        notifyListeners(event);
    }

    public void addListener(SzcoreIncomingEventListener listener){
        listeners.add(listener);
    }

    public void notifyListeners(SzcoreEvent event){
        for(SzcoreIncomingEventListener listener : listeners){
            listener.onEvent(event);
        }
    }

    private boolean isSzcoreHello(IncomingOscEvent event){

        List<Object> args =  event.getArguments();
        if(args == null || args.size() < 1) {
            return false;
        }

        Object arg = args.get(0);
        if (!(arg instanceof String)) {
            return false;
        }

        String sarg = (String)arg;
        return Consts.ARG_HELLO.equals(sarg);
    }

    private boolean isSzcorePing(IncomingOscEvent event){

        List<Object> args =  event.getArguments();
        if(args == null || args.size() < 1) {
            return false;
        }

        Object arg = args.get(0);
        if (!(arg instanceof String)) {
            return false;
        }

        String sarg = (String)arg;
        return Consts.ARG_PING.equals(sarg);
    }

    private boolean isSzcoreSetInstrument(IncomingOscEvent event){

        List<Object> args =  event.getArguments();
        if(args == null || args.size() < 1) {
            return false;
        }

        Object arg = args.get(0);
        if (!(arg instanceof String)) {
            return false;
        }

        String sarg = (String) arg;
        return Consts.ARG_SET_INSTRUMENT.equals(sarg);
    }

    private boolean isSzcoreSelectInstrumentSlot(IncomingOscEvent event) {

        List<Object> args = event.getArguments();
        if (args == null || args.size() < 1) {
            return false;
        }

        Object arg = args.get(0);
        if (!(arg instanceof String)) {
            return false;
        }

        String sarg = (String) arg;
        return Consts.ARG_SELECT_INST_SLOT.equals(sarg);
    }

    private boolean isInscoreHello(IncomingOscEvent event) {
        String oscAddress = event.getAddress();
        if (!Consts.OSC_INSCORE_ADDRESS_ROOT.equals(oscAddress)) {
            return false;
        }

        List<Object> args = event.getArguments();
        if (args == null || args.size() != 4) {
            return false;
        }

        Object arg = args.get(0);
        if (!(arg instanceof String)) {
            return false;
        }

        //TODO when local host Inscore sends empty string
//        String ip = (String)arg;
//        if(!isIpAddress(ip)){
//            return false;
//        }

        for(int i = 1 ; i <=3; i++){
            arg = args.get(i);
            if (!(arg instanceof Integer)) {
                return false;
            }
        }

        return true;
    }

    private boolean isIpAddress(String ip) {
        return ipValidator.validate(ip);
    }

    private void addParticipant(String id, InetAddress address, int port) {
        server.addParticipant(id, address, port);
    }

    private void addOutPort(String id, InetAddress address, int port) {
        server.addOutPort(id, address, port);
    }

    private void addInPort(int port) {
        server.addInPort(port);
    }

    public void expireParticipant(ParticipantStats stats, ClientInfo clientInfo, long lastPingMillis) {
        ParticipantStatsEvent statsEvent = eventFactory.createParticipantStatsEvent(clientInfo.getAddr(), clientInfo.getHost(),
                clientInfo.getPort(), stats.getPingLatency(), stats.getOneWayPingLatency(), true, lastPingMillis, clock.getSystemTimeMillis());
        notifyListeners(statsEvent);
    }
}