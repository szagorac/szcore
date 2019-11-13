package com.xenaksys.szcore.player.process;

import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.IncomingOscEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.player.SzcorePlayer;
import com.xenaksys.szcore.process.AbstractOscReceiverDisruptorProcessor;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import com.xenaksys.szcore.util.IpAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PlayerEventProcessor extends AbstractOscReceiverDisruptorProcessor {
    static final Logger LOG = LoggerFactory.getLogger(PlayerEventProcessor.class);

    private List<SzcoreIncomingEventListener> listeners = new CopyOnWriteArrayList<>();
    private final IpAddressValidator ipValidator = new IpAddressValidator();

    private final SzcorePlayer player;
    private final Clock clock;
    private final EventFactory eventFactory;

    public PlayerEventProcessor(SzcorePlayer player, Clock clock, EventFactory eventFactory, Disruptor<IncomingOscEvent> disruptor) {
        super(disruptor);
        this.player = player;
        this.clock = clock;
        this.eventFactory = eventFactory;
    }

    @Override
    protected void processInternal(IncomingOscEvent event) {
        if(event == null){
            return;
        }

        player.logEvent(event);

        try {
            processIncomingOscEvent(event);
        } catch (Exception e) {
            LOG.error("Failed to process event: " + event, e);
        }
    }

    private void processIncomingOscEvent(IncomingOscEvent event) {
        if(event == null){
            return;
        }

//        LOG.debug("Received event: " + event);

        String address = event.getAddress();
        switch(address){
            case Consts.OSC_INSCORE_ADDRESS_ROOT:
                processInscoreMessage(event);
                break;
            case Consts.OSC_ADDRESS_SCORE_JAVASCRIPT:
                processJsMessage(event);
                break;
            default:
                LOG.error("Unknown address: " + address);
        }
    }

    private void processInscoreMessage(IncomingOscEvent event) {
        if(isHello(event)){
            processHelloEvent(event);
        }
    }

    private void processJsMessage(IncomingOscEvent event) {
        if(isPing(event)){
            processPingEvent(event);
        }

    }

    private void processPingEvent(IncomingOscEvent event) {
        if(!player.isRegistered()) {
            player.setRegistered(true);
        }
        String serverTime = getPingServerTime(event);
        long creationTime = event.getCreationTime();
        long st = Long.valueOf(serverTime);
        long diff = creationTime - st;
LOG.debug("Diff remote server/local create time: " + diff);
        player.sendPingEvent(serverTime, creationTime);

    }

    private void processHelloEvent(IncomingOscEvent event) {
        if(!player.isRegistered()) {
            player.setRegistered(true);
        }

        player.sendInscoreHello();
    }

    private String getPingServerTime(IncomingOscEvent event) {
        List<Object> args = event.getArguments();
        String command = (String)args.get(1);
        String serverTime = command.substring(6, command.lastIndexOf("'"));

//        LOG.info("Got server time: " + serverTime);
        return serverTime;
    }

    private boolean isPing(IncomingOscEvent event) {
        List<Object> args = event.getArguments();
        if(args.size() < 2){
            return false;
        }
        String command = (String)args.get(1);
        return command != null && command.startsWith(Consts.OSC_JS_PING_CMD);
    }

    private boolean isHello(IncomingOscEvent event) {
        List<Object> args = event.getArguments();
        if(args.size() < 1){
            return false;
        }
        String command = (String)args.get(0);
        return command != null && command.equals(Consts.HELLO);
    }

    public void addListener(SzcoreIncomingEventListener listener){
        listeners.add(listener);
    }

    public void notifyListeners(SzcoreEvent event){
        for(SzcoreIncomingEventListener listener : listeners){
            listener.onEvent(event);
        }
    }


}