package com.xenaksys.szcore.player;


import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.IncomingOscEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.OscReceiver;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.player.process.PlayerEventProcessor;
import com.xenaksys.szcore.player.process.PlayerEventReceiver;
import com.xenaksys.szcore.process.DisruptorFactory;
import com.xenaksys.szcore.process.SimpleLogger;
import com.xenaksys.szcore.publish.OscDisruptorPublishProcessor;
import com.xenaksys.szcore.receive.OscReceiveProcessor;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import com.xenaksys.szcore.server.Server;
import com.xenaksys.szcore.server.processor.ServerLogProcessor;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SzcorePlayer extends Server implements EventService {
    static final Logger LOG = LoggerFactory.getLogger(SzcorePlayer.class);

    private static final String PROP_APP_NAME = "appName";

    private static final AtomicInteger DEFAULT_POOL_NUMBER = new AtomicInteger(1);

    private OscReceiveProcessor eventReceiver;
    private OscPublisher eventPublisher;
    private PlayerEventReceiver playerEventReceiver;
    private ServerLogProcessor logProcessor;
    private OscReceiver eventProcessor;
    private EventFactory eventFactory;
    private TaskFactory taskFactory;
    private MutableNanoClock clock;
    private Disruptor<OscEvent> outDisruptor;
    private Disruptor<IncomingOscEvent> inDisruptor;
    private OscEvent pingResponseEvent;
    private OscEvent inscoreHelloEvent;

    private volatile boolean isRegistered = false;

    protected SzcorePlayer(String id) {
        super(id);
    }

    public static SzcorePlayer buildStandAlone() {
        String serverId = createServerId();
        return new SzcorePlayer(serverId);
    }

    protected void configure() throws Exception {
        eventFactory = new EventFactory();
        taskFactory = new TaskFactory();
        initProcessors();
    }

    public void initProcessors(){
        clock = new MutableNanoClock();
        eventReceiver = new OscReceiveProcessor(new OscListenerId(Consts.DEFAULT_ALL_PORTS, serverAddress.getHostAddress(), "OscReceiveProcessor"), clock);

        inDisruptor = DisruptorFactory.createInDisruptor();
        eventProcessor = new PlayerEventProcessor(this, clock, eventFactory, inDisruptor);
        playerEventReceiver = new PlayerEventReceiver(eventProcessor, eventReceiver,
                new OscListenerId(Consts.DEFAULT_ALL_PORTS, serverAddress.getHostAddress(), "PlayerEventReceiver"));
        playerEventReceiver.init();

        outDisruptor = DisruptorFactory.createDefaultDisruptor();
        eventPublisher = new OscDisruptorPublishProcessor(outDisruptor);

        logProcessor = new ServerLogProcessor(new SimpleLogger());

        setUpOutPorts();
        pingResponseEvent = createPingResponseEvent();
        inscoreHelloEvent = createInscoreHelloEvent();
    }

    protected void setUpOutPorts(){
        try {

//            InetAddress address = InetAddress.getLocalHost();
            InetAddress address = InetAddress.getByName("192.168.0.10");
            int remotePort = Consts.DEFAULT_OSC_SERVER_PORT;
            OSCPortOut oscPort = new OSCPortOut(address, remotePort);
            Map<String, OSCPortOut> oscPublishPorts = new HashMap<>();
            oscPublishPorts.put(Consts.DEFAULT_OSC_PORT_NAME, oscPort);

            eventPublisher.setPublishPorts(oscPublishPorts);
        } catch (Exception e) {
            LOG.error("Failed to set up ports", e);
        }
    }

    protected void onStart() throws Exception {
        if(outDisruptor != null) {
            outDisruptor.start();
        }
        if(eventPublisher != null) {
            eventPublisher.start();
        }
        if(inDisruptor != null) {
            inDisruptor.start();
        }
        if(eventProcessor != null) {
            eventProcessor.start();
        }
    }

    private static String createServerId() {
        String appName = "SzcoreServer";
        if (properties != null) {
            String propAppName = properties.getProperty(PROP_APP_NAME);
            if (propAppName != null) {
                appName = propAppName;
            }
        }

        String location = getLocation();
        if (location != null) {
            appName += location.toUpperCase();
        }

        return appName;
    }

    public void stop(){
        eventReceiver.stop();
        super.stop();
    }

    public void publish(SzcoreEvent event){
        eventPublisher.process(event);
    }

    @Override
    public void receive(SzcoreEvent event) {
//        serverEventReceiver.onEvent(event);
    }

    public void subscribe(SzcoreIncomingEventListener listener){
        eventProcessor.addListener(listener);
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    @Override
    public Clock getClock() {
        return clock;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public OscPublisher getEventPublisher() {
        return eventPublisher;
    }


    public void logEvent(SzcoreEvent event){
        logProcessor.process(event);
    }


    protected void tick(){
        if(!isRegistered){
            sendHelloEvent();
        }
    }

    private OscEvent createHelloEvent(){
        String address = Consts.SZCORE_ADDR;
        List<Object> arguments = new ArrayList<>();
        arguments.add(Consts.ARG_HELLO);
        arguments.add(Consts.DEFAULT_OSC_PORT);
        arguments.add(Consts.DEFAULT_OSC_OUT_PORT);
        String destination =  Consts.DEFAULT_OSC_PORT_NAME;
        long creationTime = clock.getSystemTimeMillis();
        return eventFactory.createOscEvent(address, arguments, destination, creationTime);
    }

    private OscEvent createPingResponseEvent(){
        String address = Consts.SZCORE_ADDR;
        List<Object> arguments = new ArrayList<>();
        arguments.add(Consts.ARG_PING);
        String destination =  Consts.DEFAULT_OSC_PORT_NAME;
        long creationTime = clock.getSystemTimeMillis();
        return eventFactory.createOscEvent(address, arguments, destination, creationTime);
    }

    private OscEvent createInscoreHelloEvent() {
        String address = Consts.OSC_INSCORE_ADDRESS_ROOT;
        List<Object> arguments = new ArrayList<>();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
            LOG.error("Failed to get localhost address", e);
            return null;
        }

        if(inetAddress == null){
            return null;
        }

        String ipAddress = inetAddress.getHostAddress();
        arguments.add(ipAddress);

        arguments.add(Consts.DEFAULT_OSC_PORT);
        arguments.add(Consts.DEFAULT_OSC_OUT_PORT);
        arguments.add(Consts.DEFAULT_OSC_ERR_PORT);

        String destination =  Consts.DEFAULT_OSC_PORT_NAME;
        long creationTime = clock.getSystemTimeMillis();
        return eventFactory.createOscEvent(address, arguments, destination, creationTime);
    }

    private void sendHelloEvent() {
        OscEvent hello = createHelloEvent();
        eventPublisher.process(hello);
    }

    public void setRegistered(boolean isRegistered){
        this.isRegistered = isRegistered;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void sendPingEvent(String serverTime, long creationTime){
        if(pingResponseEvent == null){
            pingResponseEvent = createPingResponseEvent();
        }

        List<Object> args = pingResponseEvent.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, serverTime);
        eventPublisher.process(pingResponseEvent);
        long diff = System.currentTimeMillis() - creationTime;
        LOG.debug("processing time: " + diff);
    }

    public void sendInscoreHello() {
        if(inscoreHelloEvent == null){
            inscoreHelloEvent = createInscoreHelloEvent();
        }
        eventPublisher.process(inscoreHelloEvent);
    }

    public static void main(String[] args){
        SzcorePlayer player =  SzcorePlayer.buildStandAlone();
        SzcorePlayer.createInstanceAndRun(player);
    }

}
