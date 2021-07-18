package com.xenaksys.szcore.server;


import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventContainer;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.gui.ErrorEvent;
import com.xenaksys.szcore.event.gui.ParticipantEvent;
import com.xenaksys.szcore.event.gui.ParticipantStatsEvent;
import com.xenaksys.szcore.event.osc.HelloEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.osc.PingEvent;
import com.xenaksys.szcore.event.osc.ServerHelloEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.ClientInfo;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.net.ParticipantStats;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.process.DisruptorFactory;
import com.xenaksys.szcore.process.SimpleLogger;
import com.xenaksys.szcore.process.SzcoreThreadFactory;
import com.xenaksys.szcore.publish.OscDisruptorPublishProcessor;
import com.xenaksys.szcore.publish.OscPortFactory;
import com.xenaksys.szcore.publish.WebPublisherDisruptorProcessor;
import com.xenaksys.szcore.receive.OscReceiveProcessor;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.WebAudienceScore;
import com.xenaksys.szcore.server.processor.InEventContainerDisruptorProcessor;
import com.xenaksys.szcore.server.processor.ServerLogProcessor;
import com.xenaksys.szcore.server.receive.ServerEventReceiver;
import com.xenaksys.szcore.server.web.AudienceWebServer;
import com.xenaksys.szcore.server.web.ScoreWebServer;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.BasicScheduler;
import com.xenaksys.szcore.time.BasicTimer;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.time.beatstrategy.SimpleBeatTimeStrategy;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import com.xenaksys.szcore.time.waitstrategy.BlockingWaitStrategy;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.ThreadUtil;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebConnection;
import com.xenaksys.szcore.web.WebProcessor;
import com.xenaksys.szcore.web.WebScoreStateListener;
import com.xenaksys.szcore.web.ZsWebRequest;
import com.xenaksys.szcore.web.ZsWebResponse;
import org.apache.commons.net.util.SubnetUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xenaksys.szcore.Consts.PING_EXPIRY_MILLIS;
import static com.xenaksys.szcore.Consts.WEB_ROOT;

public class SzcoreServer extends Server implements EventService, ScoreService {
    private static final String PROP_APP_NAME = "appName";

    private static final AtomicInteger DEFAULT_POOL_NUMBER = new AtomicInteger(1);


    private OscReceiveProcessor oscEventReceiver;
    private OscPublisher oscEventPublisher;
    private WebPublisher webEventPublisher;
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
    private Disruptor<OscEvent> outOscDisruptor;
    private Disruptor<OutgoingWebEvent> outWebDisruptor;
    //    private Disruptor<IncomingOscEvent> inDisruptor;
    private Disruptor<EventContainer> inDisruptor;
    private AudienceWebServer audienceWebServer;
    private ScoreWebServer scoreWebServer;

    private final Map<String, ClientInfo> participants = new ConcurrentHashMap<>();
    private final Map<String, ParticipantStats> participantStats = new ConcurrentHashMap<>();
    private PingEvent pingEvent;

    private volatile String subnetMask = Consts.DEFAULT_SUBNET_MASK;
    private volatile int inscorePort = Consts.DEFAULT_OSC_PORT;
    private volatile int maxPort = Consts.DEFAULT_OSC_MAX_PORT;
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
            int[] remotePorts = getRemotePorts();
            initBroadcastAddresses(serverIp, subnetMask, remotePorts);
        } catch (Exception e) {
            LOG.error("Failed to init net info", e);
        }
    }

    public int[] getRemotePorts() {
        int inscorePort = getInscorePort();
        int maxPort = getMaxPort();
        return new int[]{inscorePort, maxPort};
    }

    public void initBroadcastAddresses(String serverIp, String subnetMask, int[] remotePorts) throws Exception {
        InetAddress broadcastAddress = getBroadcastAddress();
        if (broadcastAddress == null) {
            broadcastAddress = detectBroadcastAddress(serverIp, subnetMask);
            setBroadcastAddress(broadcastAddress);
        }

        List<InetAddress> out = new ArrayList<>();
        List<InetAddress> broadcastAddrs = NetUtil.listAllBroadcastAddresses();
        if (broadcastAddrs.isEmpty()) {
            out.add(broadcastAddress);
        } else {
            if(!broadcastAddrs.contains(broadcastAddress)) {
                LOG.warn("Retrieved broadcast addresses do not contain address: {}, adding", broadcastAddress.getHostAddress());
                out.add(broadcastAddress);
            }
            out.addAll(broadcastAddrs);
        }

        oscEventPublisher.resetBroadcastPorts();

        for(InetAddress badr : out) {
            for (int port : remotePorts) {
                addBroadcastPort(badr, port);
            }
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
        Properties props = getProperties();
        oscEventReceiver = new OscReceiveProcessor(new OscListenerId(Consts.DEFAULT_ALL_PORTS, getServerAddress().getHostAddress(), "OscReceiveProcessor"), clock);

//        inDisruptor = DisruptorFactory.createInDisruptor();
//        eventProcessor = new InServerEventDisruptorProcessor(this, clock, eventFactory, inDisruptor);
        inDisruptor = DisruptorFactory.createContainerInDisruptor();
        eventProcessor = new InEventContainerDisruptorProcessor(this, clock, eventFactory, inDisruptor);

        serverEventReceiver = new ServerEventReceiver(eventProcessor, oscEventReceiver,
                new OscListenerId(Consts.DEFAULT_ALL_PORTS, getServerAddress().getHostAddress(), "ServerEventReceiver"));
        serverEventReceiver.init();

        WaitStrategy waitStrategy = new BlockingWaitStrategy(1, TimeUnit.MILLISECONDS);

        Timer timer = new BasicTimer(waitStrategy, clock);

        outOscDisruptor = DisruptorFactory.createOscOutDisruptor();
        oscEventPublisher = new OscDisruptorPublishProcessor(outOscDisruptor);

        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.SCHEDULER_THREAD_FACTORY + "_" +  DEFAULT_POOL_NUMBER.getAndIncrement());
        ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);

        scheduler = new BasicScheduler(clock, timer, executor);
        BeatTimeStrategy beatTimeStrategy = new SimpleBeatTimeStrategy();
        TransportFactory transportFactory = new TransportFactory(clock, scheduler, beatTimeStrategy);

        logProcessor = new ServerLogProcessor(new SimpleLogger());

        webProcessor = new WebProcessor(this, this, clock, eventFactory, eventProcessor);
        outWebDisruptor = DisruptorFactory.createWebOutDisruptor();
        webEventPublisher = new WebPublisherDisruptorProcessor(outWebDisruptor, webProcessor);

        scoreProcessor = new ScoreProcessorImpl(transportFactory, clock, oscEventPublisher, webEventPublisher, scheduler, eventFactory, taskFactory);
        subscribe(webProcessor);

        String webRoot = props.getProperty(WEB_ROOT);
//        audienceWebServer = new InscoreWebServer(webRoot, 8000, 1024, 10, true, this);
//        audienceWebServer.start();

        scoreWebServer = new ScoreWebServer(webRoot, 8080, 1024, 10, true, this);
        scoreWebServer.start();
    }

    protected void onStart() throws Exception {
        if(outOscDisruptor != null) {
            outOscDisruptor.start();
        }
        if(oscEventPublisher != null) {
            oscEventPublisher.start();
        }
        if(outWebDisruptor != null) {
            outWebDisruptor.start();
        }
        if(webEventPublisher != null) {
            webEventPublisher.start();
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
        oscEventReceiver.stop();
        scheduler.stop();
        super.stop();
    }

    public void publish(SzcoreEvent event){
        scoreProcessor.publishToWebScore(event);
        oscEventPublisher.process(event);
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

    public OscPublisher getOscEventPublisher() {
        return oscEventPublisher;
    }

    public boolean isParticipant(String clientId) {
        if (clientId == null) {
            return false;
        }
        return participants.containsKey(clientId);
    }

    public void addParticipant(String id, InetAddress addr, int port) {
        if (id == null || addr == null) {
            return;
        }
        if (participants.containsKey(id)) {
            LOG.info("addParticipant: participant key {} exists already, ignoring add..", id);
            return;
        }

        ClientInfo info = new ClientInfo(id, addr, port);
        participants.put(id, info);
    }

    public void addParticipantStats(ParticipantStats stats) {
        if (stats == null || stats.getId() == null) {
            return;
        }
        participantStats.put(stats.getId(), stats);
    }

    public ParticipantStats getParticipantStats(String participantId) {
        return participantStats.get(participantId);
    }

    public InetAddress getParticipantAddress(String participantId) {
        if (participants.containsKey(participantId)) {
            return null;
        }
        return participants.get(participantId).getAddr();
    }

    public Collection<String> getParticipantIds() {
        return participants.keySet();
    }

    public void sendHello(String clientId) {
        HelloEvent helloEvent = eventFactory.createHelloEvent(clientId, 0L);
        oscEventPublisher.process(helloEvent);
    }

    public void sendServerHelloEvent(String remoteAddr) {
        ServerHelloEvent pingEvent = eventFactory.createServerHelloEvent(getServerAddress().getHostAddress(), remoteAddr, 0L);
        oscEventPublisher.process(pingEvent);
    }

    public void addOutPort(String destinationId, InetAddress addr, int port) {
        if (!oscEventPublisher.isDestination(destinationId)) {
            OSCPortOut outPort = OscPortFactory.createOutPort(addr, port);
            oscEventPublisher.addOscPort(destinationId, outPort);
        }
    }

    public void addBroadcastPort(InetAddress addr, int port) {
        if (addr == null) {
            return;
        }

        List<OSCPortOut> bPorts = oscEventPublisher.getBroadcastPorts();
        for(OSCPortOut bPort : bPorts) {
            if (bPort.getAddress().equals(addr) && bPort.getPort() == port) {
                LOG.info("addBroadcastPort: broadcast addr {} already registered, ignoring add broadcast port", addr.getHostAddress());
                return;
            }
        }

        OSCPortOut broadcastPort = OscPortFactory.createOutPort(addr, port);
        if (broadcastPort != null) {
            oscEventPublisher.addOscBroadcastPort(broadcastPort);
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
        if(oscEventPublisher == null) {
            return out;
        }

        List<OSCPortOut> bPorts = oscEventPublisher.getBroadcastPorts();
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
    public ZsWebResponse onWebRequest(ZsWebRequest zsRequest) {
//        LOG.info("onHttpRequest: path: {} sourceAddr: {}", zsRequest.getRequestPath(), zsRequest.getSourceAddr());
        return webProcessor.onWebRequest(zsRequest);
    }

    @Override
    public void onWebConnection(WebConnection webConnection) {
        webProcessor.onWebConnection(webConnection);
    }

    @Override
    public void startAudienceWebServer() {
        if (audienceWebServer == null) {
            LOG.error("startWebServer: Invalid Web server");
            return;
        }

        if (audienceWebServer.isServerRunning()) {
            LOG.error("startWebServer: Web server is already running");
            return;
        }

        audienceWebServer.start();
    }

    @Override
    public void stopAudienceWebServer() {
        if (audienceWebServer == null) {
            LOG.error("stopWebServer: Invalid Web server");
            return;
        }

        if (!audienceWebServer.isServerRunning()) {
            LOG.error("stopWebServer: Web server is not running");
            return;
        }

        audienceWebServer.stop();
    }

    @Override
    public boolean isAudienceWebServerRunning() {
        if (audienceWebServer == null) {
            return false;
        }

        return audienceWebServer.isServerRunning();
    }

    @Override
    public void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent) {
        try {
            scoreProcessor.onIncomingWebAudienceEvent(webEvent);
        } catch (Exception e) {
            LOG.error("Failed to process web audience event: {}", webEvent, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to process web event.", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }


    @Override
    public void onIncomingWebScoreEvent(WebScoreInEvent webEvent) {
        try {
            scoreProcessor.onIncomingWebScoreEvent(webEvent);
        } catch (Exception e) {
            LOG.error("Failed to process web Score event: {}", webEvent, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to process web Score event.", "SzcoreServer", e, clock.getSystemTimeMillis()));
        }
    }

    @Override
    public void pushToWebAudience(String data) {
        if (audienceWebServer == null) {
            return;
        }
        audienceWebServer.pushToAll(data);
    }

    @Override
    public void updateAudienceWebServerConnections(Set<WebConnection> connections) {
        if (webProcessor == null) {
            return;
        }
        webProcessor.onUpdateWebAudienceConnections(connections);
    }

    @Override
    public void updateScoreServerConnections(Set<WebConnection> connections) {
        if (webProcessor == null) {
            return;
        }
        LOG.info("updateScoreServerConnections: have {} connections", connections.size());
        webProcessor.onUpdateWebScoreConnections(connections);
    }


    @Override
    public void banWebClient(WebClientInfo clientInfo) {
        audienceWebServer.banWebClient(clientInfo);
    }

    @Override
    public void pushToScoreWeb(String target, WebScoreTargetType targetType, String data) {
        if (scoreWebServer == null) {
            return;
        }
        scoreWebServer.pushData(target, targetType, data);
    }

    @Override
    public void closeScoreConnections(List<String> connectionIds) {
        if (scoreWebServer == null) {
            return;
        }
        scoreWebServer.closeConnections(connectionIds);
    }

    @Override
    public void onWebScorePing(WebClientInfo clientInfo, long serverTime, long eventTime) {
        try {
            String ipAddress = clientInfo.getHost();
            String clientId = clientInfo.getClientAddr();
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            ParticipantStats stats = getParticipantStats(clientId);
            int port = clientInfo.getPort();
            if (stats == null) {
                stats = new ParticipantStats(clientId, ipAddress);
                addParticipantStats(stats);
            }

            double latency = 1.0 * (eventTime - serverTime);
            double oneWayLatency = latency / 2.0;
            oneWayLatency = Math.round(oneWayLatency * 100.0) / 100.0;

            if (!participants.containsKey(clientId)) {
                addParticipant(clientId, inetAddress, port);
                ParticipantEvent participantEvent = eventFactory.createParticipantEvent(inetAddress, ipAddress, port, 0,
                        0, 0, Consts.NAME_NA, clock.getSystemTimeMillis());
                eventProcessor.notifyListeners(participantEvent);
            }

            stats.setPingLatency(latency);
            stats.setOneWayPingLatency(oneWayLatency);
            stats.setLastPingResponseTime(eventTime);

            ParticipantStatsEvent statsEvent = eventFactory.createParticipantStatsEvent(inetAddress, ipAddress, port, latency,
                    oneWayLatency, false, 0L, clock.getSystemTimeMillis());
            eventProcessor.notifyListeners(statsEvent);
        } catch (UnknownHostException e) {
            LOG.error("onWebScorePing: failed to process web score ping");
        }
    }

    public WebProcessor getWebProcessor() {
        return webProcessor;
    }

    public ScoreProcessor getScoreProcessor() {
        return scoreProcessor;
    }

    public void addInstrumentOutPort(String clientId, String instrument) {
        OSCPortOut outPort = oscEventPublisher.getOutPort(clientId);
        if (outPort == null) {
            LOG.error("Add Instrument: Failed to find out port for instrument: " + instrument + " clientId: " + clientId);
            return;
        }

        oscEventPublisher.addOscPort(instrument, outPort);
    }

    public void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst) {
        scoreProcessor.processSelectInstrumentSlot(slotNo, slotInstrument, sourceInst);
    }

    public void sendScoreInfo(String instrument) {
        if (instrument == null) {
            return;
        }

        Score score = scoreProcessor.getScore();
        if (score == null) {
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
            oscEventPublisher.process(initEvent);
            ThreadUtil.doSleep(Thread.currentThread(), Consts.DEFAULT_THREAD_SLEEP_MILLIS);
        }

    }

    public void addInPort(int port){
        oscEventReceiver.addListener(serverEventReceiver, port);
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
    public WebAudienceScore loadWebScore(File file) {
        try {
            return scoreProcessor.loadWebScore(file);
        } catch (Exception e) {
            LOG.error("Failed to load score: " + file, e);
            eventProcessor.notifyListeners(new ErrorEvent("Failed to load WebAudienceScore: " + file, "SzcoreServer", e, clock.getSystemTimeMillis()));
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
    public void subscribe(WebScoreStateListener eventListener) {
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
    public int getMaxPort() {
        return maxPort;
    }

    @Override
    public void setMaxPort(int maxPort) {
        this.maxPort = maxPort;
    }

    @Override
    public String getSubnetMask() {
        return subnetMask;
    }

    @Override
    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    private void systemCheck() {
        long now = clock.getSystemTimeMillis();
        for (String key : participantStats.keySet()) {
            ParticipantStats stats = participantStats.get(key);
            long pingTime = stats.getLastPingResponseTime();
            long diff = now - pingTime;
            if (diff > PING_EXPIRY_MILLIS) {
                ClientInfo clientInfo = participants.get(key);
                eventProcessor.expireParticipant(stats, clientInfo, diff);
            }
        }
    }

    protected void tick() {
        if (pingEvent == null) {
            pingEvent = eventFactory.createPingEvent(Consts.ALL_DESTINATIONS, clock.getSystemTimeMillis());
        } else {
            pingEvent.addCommandArg(clock.getSystemTimeMillis());
        }

//        LOG.debug("Sending ping event: " + pingEvent);
        publish(pingEvent);
        systemCheck();
    }

}
