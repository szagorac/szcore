package com.xenaksys.szcore.server;


import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.*;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.*;
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
import com.xenaksys.szcore.score.WebScore;
import com.xenaksys.szcore.server.processor.InEventContainerDisruptorProcessor;
import com.xenaksys.szcore.server.processor.ServerLogProcessor;
import com.xenaksys.szcore.server.receive.ServerEventReceiver;
import com.xenaksys.szcore.server.web.WebServer;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.BasicScheduler;
import com.xenaksys.szcore.time.BasicTimer;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.time.beatstrategy.SimpleBeatTimeStrategy;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import com.xenaksys.szcore.time.waitstrategy.BockingWaitStrategy;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.ThreadUtil;
import com.xenaksys.szcore.web.WebProcessor;
import com.xenaksys.szcore.web.WebScoreEventListener;
import com.xenaksys.szcore.web.ZsHttpRequest;
import com.xenaksys.szcore.web.ZsHttpResponse;
import org.apache.commons.net.util.SubnetUtils;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
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
    private WebProcessor webProcessor;
    private ServerLogProcessor logProcessor;
//    private OscReceiver eventProcessor;
    private InEventContainerDisruptorProcessor eventProcessor;
    private Scheduler scheduler;
    private EventFactory eventFactory;
    private TaskFactory taskFactory;
    private MutableNanoClock clock;
    private Disruptor<OscEvent> outDisruptor;
//    private Disruptor<IncomingOscEvent> inDisruptor;
    private Disruptor<EventContainer> inDisruptor;
    private WebServer webServer;

    private Map<String, InetAddress> participants = new ConcurrentHashMap<>();
    private Map<String, ParticipantStats> participantStats = new ConcurrentHashMap<>();
    private PingEvent pingEvent;

    private volatile String subnetMask = Consts.DEFAULT_SUBNET_MASK;
    private volatile int inscorePort = Consts.DEFAULT_OSC_PORT;
    private InetAddress broadcastAddress = null;

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
        initNetInfo();
    }

    public void initNetInfo() {
        try {
            String serverIp = getServerAddress().getHostAddress();
            String subnetMask = getSubnetMask();
            int remotePort = getInscorePort();
            initBroadcastAddresses(serverIp, subnetMask, remotePort);
        } catch (Exception e) {
            LOG.error("Failed to init net info", e);
        }
    }

    public void initBroadcastAddresses(String serverIp, String subnetMask, int remotePort) throws Exception {
        InetAddress broadcastAddress = getBroadcastAddress();
        if(broadcastAddress == null) {
            broadcastAddress = detectBroadcastAddress(serverIp, subnetMask);
            setBroadcastAddress(broadcastAddress);
        }

        List<InetAddress> out = new ArrayList<>();
        List<InetAddress> broadcastAddrs = NetUtil.listAllBroadcastAddresses();
        if(broadcastAddrs.isEmpty()) {
            out.add(broadcastAddress);
        } else {
            if(!broadcastAddrs.contains(broadcastAddress)) {
                LOG.warn("Retrieved broadcast addresses do not contain address: {}, adding", broadcastAddress.getHostAddress());
                out.add(broadcastAddress);
            }
            out.addAll(broadcastAddrs);
        }

        eventPublisher.resetBroadcastPorts();
        for(InetAddress badr : out) {
            addBroadcastPort(badr, remotePort);
        }
    }

    private InetAddress detectBroadcastAddress(String serverIp, String subnetMask) throws Exception {
        SubnetUtils su = new SubnetUtils(serverIp, subnetMask);
        SubnetUtils.SubnetInfo info = su.getInfo();
        String broadcastAddr = info.getBroadcastAddress();
        return InetAddress.getByName(broadcastAddr);
    }

    public void initProcessors(){
        clock = new MutableNanoClock();
        eventReceiver = new OscReceiveProcessor(new OscListenerId(Consts.DEFAULT_ALL_PORTS, getServerAddress().getHostAddress(), "OscReceiveProcessor"), clock);

//        inDisruptor = DisruptorFactory.createInDisruptor();
//        eventProcessor = new InServerEventDisruptorProcessor(this, clock, eventFactory, inDisruptor);
        inDisruptor = DisruptorFactory.createContainerInDisruptor();
        eventProcessor = new InEventContainerDisruptorProcessor(this, clock, eventFactory, inDisruptor);

        serverEventReceiver = new ServerEventReceiver(eventProcessor, eventReceiver,
                new OscListenerId(Consts.DEFAULT_ALL_PORTS, getServerAddress().getHostAddress(), "ServerEventReceiver"));
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

        webProcessor = new WebProcessor( this, this, clock, eventFactory);
        subscribe(webProcessor);

//        LinkedList<WebScoreEvent> events = loadWebScoreEvents();
//        scoreProcessor.loadWebScore(events);

//        webServer = new WebServer("C:\\dev\\projects\\github\\scores\\ligetiq\\export\\web", 80, 1024, this);
        webServer = new WebServer("/Users/slavko/MyHome/Dev/projects/github/scores/ligetiq/export/web", 80, 1024, this);

        webServer.start();
    }

    private LinkedList<WebScoreEvent> loadWebScoreEvents() {
        LinkedList<WebScoreEvent> events = new LinkedList<>();
//
//        List<String> scripts = new ArrayList<>();
//        scripts.add("var activeRows=[1, 2]; webScore.setActiveRows(activeRows);");
//        scripts.add("var targets=['centreShape']; webScore.setAction('startZoom', 'ZOOM', targets);");
//        scripts.add("webScore.setZoomLevel('centreShape');");
//        scripts.add("var tileIds=['t2-1','t2-2','t2-3','t2-4']; var values=['text1','bigtext2','biggertext3','space text4']; webScore.setTileTexts(tileIds, values);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var elementIds=['centreShape']; webScore.setVisible(elementIds, true);");
//        scripts.add("var targets=['centreShape']; webScore.setAction('start', 'TIMELINE', targets);");
//        scripts.add("var tileIds=['t1-1','t2-1']; webScore.setPlayingTiles(tileIds);");
//        scripts.add("var targets=['t1-1','t2-1']; var params={'angle': 45, 'duration' : 5}; webScore.setAction('start', 'ROTATE', targets, params);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("webScore.getTopSelectedTiles(1);");
//        scripts.add("var tileIds=['t1-6','t2-6']; webScore.setPlayingNextTiles(tileIds);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-6','t2-6']; webScore.setPlayingTiles(tileIds);");
//        scripts.add("var targets=['t1-6','t2-6']; var params={'angle': 45, 'duration' : 5}; webScore.setAction('start', 'ROTATE', targets, params);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-5','t2-5']; webScore.setPlayingNextTiles(tileIds);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-5','t2-5']; webScore.setPlayingTiles(tileIds);");
//        scripts.add("var targets=['t1-5','t2-5']; var params={'angle': 45, 'duration' : 5}; webScore.setAction('start', 'ROTATE', targets, params);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-7','t2-7']; webScore.setPlayingNextTiles(tileIds);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-7','t2-7']; webScore.setPlayingTiles(tileIds);");
//        scripts.add("var targets=['t1-7','t2-7']; var params={'angle': 45, 'duration' : 5}; webScore.setAction('start', 'ROTATE', targets, params);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-4']; webScore.setPlayingNextTiles(tileIds);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));
//
//        scripts.clear();
//        scripts.add("var tileIds=['t1-4']; webScore.setPlayingTiles(tileIds);");
//        scripts.add("var targets=['t1-4']; var params={'angle': 45, 'duration' : 5}; webScore.setAction('start', 'ROTATE', targets, params);");
//        events.add(eventFactory.createWebScoreEvent(WebScoreEventType.START, null, null, scripts, 0L));

        return events;
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
    public void receive(SzcoreEvent event) {
        serverEventReceiver.onEvent(event);
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
        HelloEvent helloEvent = eventFactory.createHelloEvent(remoteAddr, 0L);
        eventPublisher.process(helloEvent);
    }

    public void sendServerHelloEvent(String remoteAddr){
        ServerHelloEvent pingEvent = eventFactory.createServerHelloEvent(getServerAddress().getHostAddress(), remoteAddr, 0L);
        eventPublisher.process(pingEvent);
    }

    public void addOutPort(InetAddress addr, int port){
        String remoteAddr = addr.getHostAddress();
        if(!eventPublisher.isDestination(remoteAddr, port)) {
            OSCPortOut outPort = OscPortFactory.createOutPort(addr, port);
            eventPublisher.addOscPort(remoteAddr, outPort);
        }
    }

    public void addBroadcastPort(InetAddress addr, int port){
        if(addr == null) {
            return;
        }

        List<OSCPortOut> bPorts = eventPublisher.getBroadcastPorts();
        for(OSCPortOut bPort : bPorts) {
            if(bPort.getAddress().equals(addr)) {
                LOG.info("addBroadcastPort: broadcast addr {} already registered, ignoring add broadcast port", addr.getHostAddress());
                return;
            }
        }

        OSCPortOut broadcastPort = OscPortFactory.createOutPort(addr, port);
        if (broadcastPort != null) {
            eventPublisher.addOscBroadcastPort(broadcastPort);
        }
    }

    @Override
    public InetAddress getBroadcastAddress() {
        return broadcastAddress;
    }

    @Override
    public void setBroadcastAddress(InetAddress broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }

    @Override
    public List<InetAddress> getDetectedBroadcastAddresses() {
        List<InetAddress> out = new ArrayList<>();
        if(eventPublisher == null) {
            return out;
        }

        List<OSCPortOut> bPorts = eventPublisher.getBroadcastPorts();
        for(OSCPortOut bPort : bPorts) {
            out.add(bPort.getAddress());
        }
        return out;
    }

    public List<NetUtil.NetworkDevice> getParallelConnectedNetworkClients(){
        List<NetUtil.NetworkDevice> connectedClients = new ArrayList<>();
        try {
            connectedClients = NetUtil.discoverConnectedDevices();
        } catch (Exception e) {
            LOG.error("Failed to retrieve connected clients", e);
        }

        return connectedClients;
    }

    @Override
    public ZsHttpResponse onHttpRequest(ZsHttpRequest zsRequest) {
        LOG.info("onHttpRequest: path: {} sourceAddr: {}", zsRequest.getRequestPath(), zsRequest.getSourceAddr());
        return webProcessor.onHttpRequest(zsRequest);
    }

    @Override
    public void startWebServer() {
        if(webServer == null) {
            LOG.error("startWebServer: Invalid Web server");
            return;
        }

        if(webServer.isRunning()) {
            LOG.error("startWebServer: Web server is already running");
            return;
        }

        webServer.start();
    }

    @Override
    public void stopWebServer() {
        if(webServer == null) {
            LOG.error("stopWebServer: Invalid Web server");
            return;
        }

        if(!webServer.isRunning()) {
            LOG.error("stopWebServer: Web server is not running");
            return;
        }

        webServer.stop();
    }

    @Override
    public boolean isWebServerRunning() {
        if(webServer == null) {
            return false;
        }

        return webServer.isRunning();
    }

    @Override
    public void onIncomingWebEvent(IncomingWebEvent webEvent) {
        try {
            scoreProcessor.onIncomingWebEvent(webEvent);
        } catch (Exception e) {
            LOG.error("Failed to process web event: {}", webEvent, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to process web event.", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void pushToWebClients(String data) {
        webServer.pushToAll(data);
    }

    public WebProcessor getWebProcessor() {
        return webProcessor;
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
            scoreProcessor.prepare(score);
            return score;
        } catch (Exception e) {
            LOG.error("Failed to load score: " + file, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to load score: " + file, "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
        return null;
    }

    @Override
    public WebScore loadWebScore(File file) {
        try {
            return scoreProcessor.loadWebScore(file);
        } catch (Exception e) {
            LOG.error("Failed to load score: " + file, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to load WebScore: " + file, "SzcoreServer", e, clock.getSystemTimeMillis()));
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
    public void subscribe(WebScoreEventListener eventListener) {
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

    @Override
    public void setRandomisationStrategy(List<Integer> randomisationStrategy) {
        try {
            scoreProcessor.setRandomisationStrategy(randomisationStrategy);
        } catch (Exception e) {
            LOG.error("Failed to set randomisation strategy", e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set randomisation strategy", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void usePageRandomisation(Boolean value) {
        try {
            scoreProcessor.usePageRandomisation(value);
        } catch (Exception e) {
            LOG.error("Failed to set Use Randomisation Strategy: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Use Randomisation Strategy", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void useContinuousPageChange(Boolean value) {
        try {
            scoreProcessor.useContinuousPageChange(value);
        } catch (Exception e) {
            LOG.error("Failed to set Use Continuous Page Change: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Use Continuous Page Change", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void setDynamicsValue(long value, List<Id> instrumentIds) {
        try {
            scoreProcessor.setDynamicsValue(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Dynamics Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Dynamics Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUseDynamicsOverlay(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUseDynamicsOverlay(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Dynamics Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Dynamics Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUseDynamicsLine(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUseDynamicsLine(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Dynamics Line Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Dynamics Line Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void setPressureValue(long value, List<Id> instrumentIds) {
        try {
            scoreProcessor.setPressureValue(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Pressure Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Pressure Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUsePressureOverlay(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUsePressureOverlay(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Pressure Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Pressure Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUsePressureLine(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUsePressureLine(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Pressure Line Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Pressure Line Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void setSpeedValue(long value, List<Id> instrumentIds) {
        try {
            scoreProcessor.setSpeedValue(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Speed Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Speed Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUseSpeedOverlay(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUseSpeedOverlay(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Speed Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Speed Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUseSpeedLine(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUseSpeedLine(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Speed Line Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Speed Line Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void setPositionValue(long value, List<Id> instrumentIds) {
        try {
            scoreProcessor.setPositionValue(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Position Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Position Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUsePositionOverlay(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUsePositionOverlay(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Position Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Position Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUsePositionLine(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUsePositionLine(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Position Line Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Position Line Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void setContentValue(long value, List<Id> instrumentIds) {
        try {
            scoreProcessor.setContentValue(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Content Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Content Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUseContentOverlay(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUseContentOverlay(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Content Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Content Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void onUseContentLine(Boolean value, List<Id> instrumentIds) {
        try {
            scoreProcessor.onUseContentLine(value, instrumentIds);
        } catch (Exception e) {
            LOG.error("Failed to set Content Line Value: {}", value, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to set Content Line Value", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public InetAddress getServerAddress() {
        return serverAddress;
    }

    @Override
    public int getInscorePort() {
        return inscorePort;
    }

    @Override
    public void setInscorePort(int inscorePort) {
        this.inscorePort = inscorePort;
    }

    @Override
    public String getSubnetMask() {
        return subnetMask;
    }

    @Override
    public void  setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
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
