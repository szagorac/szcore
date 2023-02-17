package com.xenaksys.szcore.server;


import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.ErrorEvent;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.HelloEvent;
import com.xenaksys.szcore.event.IncomingOscEvent;
import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.event.PingEvent;
import com.xenaksys.szcore.event.ServerHelloEvent;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.OscReceiver;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.net.ParticipantStats;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.process.DisruptorFactory;
import com.xenaksys.szcore.process.SimpleLogger;
import com.xenaksys.szcore.process.SzcoreThreadFactory;
import com.xenaksys.szcore.publish.OscDisruptorPublishProcessor;
import com.xenaksys.szcore.publish.OscPortFactory;
import com.xenaksys.szcore.receive.OscReceiveProcessor;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.server.processor.ServerEventDisruptorProcessor;
import com.xenaksys.szcore.server.processor.ServerLogProcessor;
import com.xenaksys.szcore.server.receive.ServerEventReceiver;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.BasicScheduler;
import com.xenaksys.szcore.time.BasicTimer;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.time.beatstrategy.SimpleBeatTimeStrategy;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import com.xenaksys.szcore.time.waitstrategy.BockingWaitStrategy;
import com.xenaksys.szcore.util.ThreadUtil;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SzcoreServer extends Server implements EventService, ScoreService {
    private static final String PROP_APP_NAME = "appName";

    private static final AtomicInteger DEFAULT_POOL_NUMBER = new AtomicInteger(1);

    private OscReceiveProcessor eventReceiver;
    private OscPublisher eventPublisher;
    private ServerEventReceiver serverEventReceiver;
    private ScoreProcessor scoreProcessor;
    private ServerLogProcessor logProcessor;
    private OscReceiver eventProcessor;
    private Scheduler scheduler;
    private EventFactory eventFactory;
    private TaskFactory taskFactory;
    private MutableNanoClock clock;
    private Disruptor<OscEvent> outDisruptor;
    private Disruptor<IncomingOscEvent> inDisruptor;

    private Map<String, InetAddress> participants = new ConcurrentHashMap<>();
    private Map<String, ParticipantStats> participantStats = new ConcurrentHashMap<>();
    private PingEvent pingEvent;

    protected SzcoreServer(String id) {
        super(id);
    }

    public static Server buildStandAlone() {
        String serverId = createServerId();
        return new SzcoreServer(serverId);
    }

    protected void configure() throws Exception {
        eventFactory = new EventFactory();
        taskFactory = new TaskFactory();
        initProcessors();
    }

    public void initProcessors(){
        clock = new MutableNanoClock();
        eventReceiver = new OscReceiveProcessor(new OscListenerId(Consts.DEFAULT_ALL_PORTS, getAddress().getHostAddress(), "OscReceiveProcessor"), clock);

        inDisruptor = DisruptorFactory.createInDisruptor();
        eventProcessor = new ServerEventDisruptorProcessor(this, clock, eventFactory, inDisruptor);
        serverEventReceiver = new ServerEventReceiver(eventProcessor, eventReceiver,
                new OscListenerId(Consts.DEFAULT_ALL_PORTS, getAddress().getHostAddress(), "ServerEventReceiver"));
        serverEventReceiver.init();

        WaitStrategy waitStrategy = new BockingWaitStrategy(1, TimeUnit.MILLISECONDS);

        Timer timer = new BasicTimer(waitStrategy, clock);

        outDisruptor = DisruptorFactory.createOutDisruptor();
        eventPublisher = new OscDisruptorPublishProcessor(outDisruptor);

        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.SCHEDULER_THREAD_FACTORY + "_" +  DEFAULT_POOL_NUMBER.getAndIncrement());
        ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);

        scheduler = new BasicScheduler(clock, timer, executor);
        BeatTimeStrategy beatTimeStrategy = new SimpleBeatTimeStrategy();
        TransportFactory transportFactory = new TransportFactory(clock, scheduler, beatTimeStrategy);

        scoreProcessor = new ScoreProcessorImpl(transportFactory, clock, eventPublisher, scheduler, eventFactory, taskFactory);

        logProcessor = new ServerLogProcessor(new SimpleLogger());

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
        scheduler.stop();
        super.stop();
    }

    public void publish(SzcoreEvent event){
        eventPublisher.process(event);
    }

    @Override
    public InetAddress getAddress() {
        return getServerAddress();
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

    public boolean isParticipant(InetAddress addr){
        if(addr == null){
            return false;
        }

        String ip = addr.getHostAddress();
        return participants.containsKey(ip);
    }

    public void addParticipant(InetAddress addr){
        if(addr == null){
            return;
        }
        participants.put(addr.getHostAddress(), addr);
    }

    public void addParticipantStats(ParticipantStats stats){
        if(stats == null || stats.getIpAddress() == null){
            return;
        }
        participantStats.put(stats.getIpAddress(), stats);
    }

    public ParticipantStats getParticipantStats(String ipAddress){
        return participantStats.get(ipAddress);
    }

    public InetAddress getParticipantAddress(String ipAddress){
        return participants.get(ipAddress);
    }

    public Collection<String> getParticipants(){
        return participants.keySet();
    }

    public void sendHello(String remoteAddr){
        HelloEvent helloEvent = eventFactory.createHelloEvent(remoteAddr, 0l);
        eventPublisher.process(helloEvent);
    }

    public void sendServerHelloEvent(String remoteAddr){
        ServerHelloEvent pingEvent = eventFactory.createServerHelloEvent(getServerAddress().getHostAddress(), remoteAddr, 0l);
        eventPublisher.process(pingEvent);
    }

    public void addOutPort(InetAddress addr, int port){
        String remoteAddr = addr.getHostAddress();
        if(!eventPublisher.isDestination(remoteAddr, port)) {
            OSCPortOut outPort = OscPortFactory.createOutPort(addr, port);
            eventPublisher.addOscPort(remoteAddr, outPort);
        }
    }

    public void addInstrumentOutPort(InetAddress addr, String instrument){
        String remoteAddr = addr.getHostAddress();
        OSCPortOut outPort = eventPublisher.getOutPort(remoteAddr);
        if(outPort == null){
            LOG.error("Add Instrument: Failed to find out port for instrument: " + instrument + " addr: " + remoteAddr);
            return;
        }

        eventPublisher.addOscPort(instrument, outPort);
    }

    public void sendScoreInfo(String instrument){
        if(instrument == null){
            return;
        }

        Score score = scoreProcessor.getScore();
        if(score == null){
            return;
        }

        List<SzcoreEvent> events = new ArrayList<>();
        String title = score.getName();
        if(title != null) {
            events.add(eventFactory.createTitleEvent(instrument, title, clock.getSystemTimeMillis()));
        }

        events.add(eventFactory.createPartEvent(instrument, instrument, clock.getSystemTimeMillis()));

        events.add(eventFactory.createResetStavesEvent(instrument, clock.getSystemTimeMillis()));

        for (SzcoreEvent initEvent : events) {
            eventPublisher.process(initEvent);
            ThreadUtil.doSleep(Thread.currentThread(), Consts.DEFAULT_THREAD_SLEEP_MILLIS);
        }

    }

    public void addInPort(int port){
        eventReceiver.addListener(serverEventReceiver, port);
    }

    public void logEvent(SzcoreEvent event){
        logProcessor.process(event);
    }

    @Override
    public void loadScoreAndPrepare(String filePath) {
        try {
            scoreProcessor.loadAndPrepare(filePath);
        } catch (Exception e) {
            LOG.error("Failed to load and prepare score: " + filePath, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to load score: " + filePath, "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public Score loadScore(File file) {
        try {
            Score score = scoreProcessor.loadScore(file);
            //TODO
            scoreProcessor.prepare(score);
            return score;
        } catch (Exception e) {
            LOG.error("Failed to load score: " + file, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to load score: " + file, "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
        return null;
    }

    @Override
    public boolean reset() {
        try {
            scoreProcessor.reset();
        } catch (Exception e) {
            LOG.error("Failed to reset score", e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to reset score", "SzcoreServer", e, clock.getSystemTimeMillis()));
            return false;
        }
        return true;
    }

    @Override
    public void play(long startMillis) {
        try {
            scoreProcessor.play();
        } catch (Exception e) {
            LOG.error("Failed to play score", e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to play score", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void stopPlay() {
        try {
            scoreProcessor.stop();
        } catch (Exception e) {
            LOG.error("Failed to stop score", e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to stop score", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void setPosition(long millis) {
        try {
            scoreProcessor.setPosition(millis);
        } catch (Exception e) {
            LOG.error("Failed to set position", e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set position", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void subscribe(SzcoreEngineEventListener eventListener) {
        scoreProcessor.subscribe(eventListener);
    }

    @Override
    public void setTempoModifier(Id transportId, TempoModifier tempoModifier) {
        try {
            scoreProcessor.setTempoModifier(transportId, tempoModifier);
        } catch (Exception e) {
            LOG.error("Failed to set tempo modifier", e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set tempo modifier", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    protected void tick(){
        if(pingEvent == null) {
            pingEvent = eventFactory.createPingEvent(Consts.ALL_DESTINATIONS, clock.getSystemTimeMillis());
        } else {
            pingEvent.addCommandArg(clock.getSystemTimeMillis());
        }

//        LOG.debug("Sending ping event: " + pingEvent);
        publish(pingEvent);
    }

}
