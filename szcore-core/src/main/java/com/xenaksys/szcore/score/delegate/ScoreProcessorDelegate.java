package com.xenaksys.szcore.score.delegate;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategy;
import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.algo.config.ScoreRandomisationStrategyConfig;
import com.xenaksys.szcore.algo.config.StrategyConfigLoader;
import com.xenaksys.szcore.algo.config.TranspositionStrategyConfig;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.event.gui.ClientEvent;
import com.xenaksys.szcore.event.gui.ClientInEvent;
import com.xenaksys.szcore.event.gui.ClientInEventType;
import com.xenaksys.szcore.event.gui.ScoreInfoEvent;
import com.xenaksys.szcore.event.gui.ScoreSectionInfoEvent;
import com.xenaksys.szcore.event.gui.StrategyEvent;
import com.xenaksys.szcore.event.gui.StrategyEventType;
import com.xenaksys.szcore.event.music.ModWindowEvent;
import com.xenaksys.szcore.event.music.MusicEvent;
import com.xenaksys.szcore.event.music.MusicEventType;
import com.xenaksys.szcore.event.music.PrecountBeatSetupEvent;
import com.xenaksys.szcore.event.music.PrepStaveChangeEvent;
import com.xenaksys.szcore.event.music.ScoreSectionEvent;
import com.xenaksys.szcore.event.music.ScoreSectionEventType;
import com.xenaksys.szcore.event.music.StopEvent;
import com.xenaksys.szcore.event.music.TimeSigChangeEvent;
import com.xenaksys.szcore.event.music.TransitionEvent;
import com.xenaksys.szcore.event.music.TransportEvent;
import com.xenaksys.szcore.event.music.TransportPositionEvent;
import com.xenaksys.szcore.event.osc.BeatScriptEvent;
import com.xenaksys.szcore.event.osc.DateTickEvent;
import com.xenaksys.szcore.event.osc.ElementSelectedAudienceEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.osc.OscEventType;
import com.xenaksys.szcore.event.osc.OscScriptEvent;
import com.xenaksys.szcore.event.osc.OscStaveActivateEvent;
import com.xenaksys.szcore.event.osc.OscStaveTempoEvent;
import com.xenaksys.szcore.event.osc.OscStopEvent;
import com.xenaksys.szcore.event.osc.PageMapDisplayEvent;
import com.xenaksys.szcore.event.osc.StaveActiveChangeEvent;
import com.xenaksys.szcore.event.osc.StaveClockTickEvent;
import com.xenaksys.szcore.event.osc.StaveDateTickEvent;
import com.xenaksys.szcore.event.osc.StaveDyTickEvent;
import com.xenaksys.szcore.event.osc.StaveStartMarkEvent;
import com.xenaksys.szcore.event.osc.StaveYPositionEvent;
import com.xenaksys.szcore.event.osc.TempoChangeEvent;
import com.xenaksys.szcore.event.osc.VoteAudienceEvent;
import com.xenaksys.szcore.event.script.ScriptingEngineEvent;
import com.xenaksys.szcore.event.script.ScriptingEngineResetEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceEventType;
import com.xenaksys.szcore.event.web.audience.WebAudiencePlayTilesEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceResetEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStopEvent;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEventType;
import com.xenaksys.szcore.event.web.in.WebScorePartReadyEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectInstrumentSlotEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectSectionEvent;
import com.xenaksys.szcore.event.web.out.DelayedOutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEventType;
import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.EventReceiver;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.MusicTask;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptEventPreset;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoImpl;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.Transition;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.TransportListener;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.process.OscDestinationEventListener;
import com.xenaksys.szcore.publish.LatencyCompensator;
import com.xenaksys.szcore.score.BasicBar;
import com.xenaksys.szcore.score.BasicBeat;
import com.xenaksys.szcore.score.BasicPage;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.BasicScript;
import com.xenaksys.szcore.score.BasicStave;
import com.xenaksys.szcore.score.BasicTransition;
import com.xenaksys.szcore.score.BeatFollowerPositionStrategy;
import com.xenaksys.szcore.score.InscoreMapElement;
import com.xenaksys.szcore.score.InscorePageMap;
import com.xenaksys.szcore.score.InstrumentBeatTracker;
import com.xenaksys.szcore.score.MaxMspScoreConfig;
import com.xenaksys.szcore.score.OscScript;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.ScoreLoader;
import com.xenaksys.szcore.score.ScoreProcessorDelegator;
import com.xenaksys.szcore.score.StaveFactory;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.delegate.web.unionrose.UnionRoseWebAudienceProcessor;
import com.xenaksys.szcore.score.overlay.OverlayProcessor;
import com.xenaksys.szcore.score.web.WebScore;
import com.xenaksys.szcore.score.web.WebScoreState;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreProcessor;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.score.web.overlay.DefaultWebOverlayFactory;
import com.xenaksys.szcore.scripting.ScoreScriptingEngine;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;
import com.xenaksys.szcore.task.ScheduledEventTask;
import com.xenaksys.szcore.task.ScriptingEngineEventTask;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.task.WebAudienceEventTask;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.util.MathUtil;
import com.xenaksys.szcore.util.ParseUtil;
import com.xenaksys.szcore.util.ThreadUtil;
import com.xenaksys.szcore.util.WebUtil;
import com.xenaksys.szcore.web.WebAudienceStateListener;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebScoreAction;
import com.xenaksys.szcore.web.WebScoreActionType;
import gnu.trove.map.TIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xenaksys.szcore.Consts.CONFIG_BUILDER_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_RND_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_TRANSPOSITION_STRATEGY;
import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NAME;
import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NO;
import static com.xenaksys.szcore.Consts.MAX_BPM;
import static com.xenaksys.szcore.Consts.MIN_BPM;
import static com.xenaksys.szcore.Consts.UNDERSCORE;

public class ScoreProcessorDelegate implements ScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(ScoreProcessorDelegate.class);

    private final TransportFactory transportFactory;
    private final MutableClock clock;
    private final OscPublisher oscPublisher;
    private final WebPublisher webPublisher;
    private final Scheduler scheduler;
    private final EventFactory eventFactory;
    private final TaskFactory taskFactory;
    private final OscDestinationEventListener oscDestinationEventListener;
    private final ScoreProcessorDelegator parentProcessor;
    private final EventReceiver eventReceiver;
    private final Properties props;

    private BasicScore szcore = null;
    private boolean isScoreLoaded = false;
    private boolean isInitDone = true;
    private List<SzcoreEvent> initEvents = new ArrayList<>();
    private final List<SzcoreEvent> baseBeatEventsToProcess = new ArrayList<>();

    private volatile static int instrumentPortRequestCount = 0;
    private volatile int startBaseBeat = 0;
    private final Map<Id, InstrumentBeatTracker> instrumentBeatTrackers = new HashMap<>();
    private final BeatFollowerPositionStrategy beatFollowerPositionStrategy = new BeatFollowerPositionStrategy();
    private final Map<Id, TempoModifier> transportTempoModifiers = new ConcurrentHashMap<>();
    private final List<String> noScoreInstruments = new ArrayList<>();
    private final LatencyCompensator latencyCompensator;

    private WebAudienceScoreProcessor webAudienceScoreProcessor = null;
    private WebScore webScore = null;
    private MaxMspScoreConfig maxMspConfig = null;
    private ScoreScriptingEngine scriptingEngine;
    private OverlayProcessor overlayProcessor;

    protected volatile boolean isUpdateWindowOpen = false;
    protected volatile int currentBeatNo = 0;

    public ScoreProcessorDelegate(TransportFactory transportFactory,
                                  MutableClock clock,
                                  OscPublisher oscPublisher,
                                  WebPublisher webPublisher,
                                  Scheduler scheduler,
                                  EventFactory eventFactory,
                                  TaskFactory taskFactory,
                                  BasicScore szcore,
                                  ScoreProcessorDelegator parent,
                                  EventReceiver eventReceiver,
                                  List<OutgoingWebEventType> latencyCompensatorEventTypeFilter,
                                  Properties props) {
        this.transportFactory = transportFactory;
        this.clock = clock;
        this.oscPublisher = oscPublisher;
        this.webPublisher = webPublisher;
        this.scheduler = scheduler;
        this.eventFactory = eventFactory;
        this.taskFactory = taskFactory;
        this.szcore = szcore;
        this.parentProcessor = parent;
        this.oscDestinationEventListener = new OscDestinationEventListener(parent, eventFactory, clock);
        this.eventReceiver = eventReceiver;
        this.props = props;
        this.latencyCompensator = new LatencyCompensator(this, eventFactory, latencyCompensatorEventTypeFilter, clock);
    }

    @Override
    public void loadAndPrepare(String filePath) throws Exception {
       LOG.error("loadAndPrepare: unexpected call");
    }

    @Override
    public Score loadScore(File file) throws Exception {
        if (scheduler.isActive()) {
            LOG.warn("Scheduler is active, can not perform load score");
            throw new Exception("Scheduler is active, can not perform load score");
        }
        if(szcore == null) {
            throw new Exception("loadScore: invalid score");
        }
        String dir = file.getParent();
        loadStrategyConfig(dir);
        createWebAudienceProcessor();
        initWebAudienceScore(dir);
        initWebScore();
        initMax();
        initScriptingEngine(dir);
        initOverlayProcessor();
        return szcore;
    }

    public void initWebScore() {
        setWebScore(new WebScore(this, eventFactory, clock, props, new DefaultWebOverlayFactory()));
    }

    public void setWebScore(WebScore webScore) {
        this.webScore = webScore;
    }

    public void initMax() {
        maxMspConfig = new MaxMspScoreConfig();
        maxMspConfig.setScoreName(szcore.getName());
    }

    public void initScriptingEngine(String dir) {
        scriptingEngine = new ScoreScriptingEngine(this, eventFactory, clock);
        scriptingEngine.init(dir);
    }

    protected void initOverlayProcessor() {
        this.overlayProcessor = new OverlayProcessor(this, eventFactory, clock);
    }

    public void loadStrategyConfig(String dir) throws Exception {
        Map<String, Object> strategyConfig = StrategyConfigLoader.loadConfig(dir);
        for(String key : strategyConfig.keySet()) {
            switch (key) {
                case CONFIG_RND_STRATEGY:
                    initRandomisationStrategyConfig(strategyConfig);
                    break;
                case CONFIG_BUILDER_STRATEGY:
                    initScoreBuilderStrategyConfig(strategyConfig);
                    break;
                case CONFIG_TRANSPOSITION_STRATEGY:
                    initTranspositionStrategyConfig(strategyConfig);
                    break;
            }
        }
    }

    public void initRandomisationStrategyConfig(Map<String, Object> strategyConfig) throws Exception {
        ScoreRandomisationStrategyConfig randomisationStrategyConfig = StrategyConfigLoader.loadRndStrategyConfig(strategyConfig, szcore);
        if(randomisationStrategyConfig != null) {
            szcore.addStrategyConfig(randomisationStrategyConfig);
        }
    }

    public void initScoreBuilderStrategyConfig(Map<String, Object> strategyConfig) throws Exception {
        ScoreBuilderStrategyConfig builderStrategyConfig = StrategyConfigLoader.loadBuilderStrategyConfig(strategyConfig, szcore);
        if(builderStrategyConfig != null) {
            szcore.addStrategyConfig(builderStrategyConfig);
        }
    }

    public void initTranspositionStrategyConfig(Map<String, Object> strategyConfig) throws Exception {
        TranspositionStrategyConfig transpositionStrategyConfig = StrategyConfigLoader.loadTranspositionConfig(strategyConfig, szcore);
        if(transpositionStrategyConfig != null) {
            szcore.addStrategyConfig(transpositionStrategyConfig);
        }
    }

    protected void createWebAudienceProcessor() {
        setWebAudienceProcessor(new UnionRoseWebAudienceProcessor(this, eventFactory, clock));
    }

    protected void initWebAudienceScore(String dir) {
        this.webAudienceScoreProcessor.init(dir);
    }

    public WebAudienceScoreProcessor getWebAudienceProcessor() {
        return webAudienceScoreProcessor;
    }

    public void setWebAudienceProcessor(WebAudienceScoreProcessor webAudienceScoreProcessor) {
        this.webAudienceScoreProcessor = webAudienceScoreProcessor;
    }

    public void sendClientEvent(ClientEvent clientEvent) {
        if(eventReceiver == null) {
            return;
        }
        eventReceiver.notifyListeners(clientEvent);
    }

    @Override
    public void publishAudienceViewState(boolean isNotesEnabled, boolean isAudioEnabled, boolean isThumbsEnabled, boolean isMeterEnabled, boolean isVoteEnabled) {

    }

    @Override
    public void prepare(Score score) {
        this.szcore = (BasicScore) score;

        // TODO create transports from file
        Transport transport = transportFactory.getTransport(Consts.DEFAULT_TRANSPORT_NAME);
        transport.addListener(new ScoreTransportListener(transport.getId()));
        scheduler.addTransport(transport);

        transportTempoModifiers.put(transport.getId(), new TempoModifier(Consts.ONE_D));

        PageId bpid = new PageId(0, new StrId(Consts.BLANK_PAGE_NAME), score.getId());
        Page blankPage = new BasicPage(bpid, Consts.BLANK_PAGE_NAME, Consts.BLANK_PAGE_FILE);
        szcore.setBlankPage(blankPage);

        Collection<Instrument> instruments = szcore.getInstruments();
        BeatId lastBeat = null;
        for (Instrument instrument : instruments) {
            Id instrumentId = instrument.getId();
            instrumentBeatTrackers.put(instrumentId, new InstrumentBeatTracker(transport, instrumentId));
            lastBeat = prepareInstrument(instrument, transport);
            Page lastPage = szcore.getLastInstrumentPage(instrumentId);

            String contPageName = instrument.getName() + UNDERSCORE + CONTINUOUS_PAGE_NAME;
            PageId contPageId = new PageId(CONTINUOUS_PAGE_NO, instrumentId, lastPage.getScoreId());
            InscorePageMap contInscorePageMap = ScoreLoader.loadPageInscoreMap(contPageId, contPageName);
            BasicPage contPage = new BasicPage(contPageId, contPageName, contPageName, lastPage.getInscorePageMap(), true);
            Collection<Bar> lpBars = lastPage.getBars();
            for (Bar bar : lpBars) {
                contPage.addBar(bar);
            }
            szcore.setContinuousPage(instrumentId, contPage);

            BasicPage endPage = new BasicPage(contPageId, contPageName, contPageName, contInscorePageMap, true);
            szcore.setEndPage(instrumentId, endPage);

            if (szcore.isUseContinuousPage()) {
                prepareContinuousPages(instrumentId, lastPage);
            }
        }

        szcore.initScoreStrategies();

        if (!szcore.isUseContinuousPage()) {
            addStopEvent(lastBeat, transport.getId());
        }
        int precountMillis = 5 * 1000;
        int precountBeatNo = 4;
        szcore.setPrecount(precountMillis, precountBeatNo);

        Collection<Instrument> maxClients = szcore.getOscPlayers();
        oscDestinationEventListener.reloadDestinations(maxClients);

        isScoreLoaded = true;

        webScore.init();
    }

    protected void prepareContinuousPages(Id instrumentId, Page lastPage) {
        for (int i = 0; i < szcore.noContinuousPages; i++) {
            lastPage = prepareContinuousPage(instrumentId, (PageId) lastPage.getId());
        }
    }

    protected void copyInstrument(Instrument fromInstrument, String toInstrumentName, String pageFileName, boolean isCopyScripts) {
        Instrument exitingInst = szcore.getInstrument(toInstrumentName);
        if(exitingInst != null) {
            LOG.warn("copyInstrument: instrument {} already exists, ignoring copy...", toInstrumentName);
            return;
        }

        InstrumentId instrumentId = new InstrumentId(toInstrumentName);
        BasicInstrument instrument = new BasicInstrument(instrumentId, toInstrumentName, fromInstrument.isAv());
        szcore.addInstrument(instrument);

        List<Page> fromPages = new ArrayList<>(szcore.getInstrumentPages(fromInstrument.getId()));
        for(Page fromPg : fromPages) {
            BasicPage fromPage = (BasicPage)fromPg;
            PageId fromPageId = fromPage.getPageId();
            Id scoreId = fromPageId.getScoreId();
            PageId pageId = new PageId(fromPageId.getPageNo(), instrumentId, scoreId);
            String fileName = fromPage.getFileName();
            if(pageFileName != null) {
                fileName = pageFileName;
            }
            BasicPage page = new BasicPage(pageId, fromPage.getPageName(), fileName, fromPage.getInscorePageMap(), fromPage.isSendInscoreMap());
            if (!szcore.containsPage(page)) {
                szcore.addPage(page);
            }
            List<Bar> bars = (List<Bar>) fromPage.getBars();
            for (Bar fromBar : bars) {
                BarId fromBarId = (BarId)fromBar.getId();
                BarId barId = new BarId(fromBarId.getBarNo(), instrumentId, pageId, scoreId);
                BasicBar bar = new BasicBar(barId, fromBar.getBarName(), fromBar.getTempo(), fromBar.getTimeSignature());
                if (szcore.doesNotcontainBar(bar)) {
                    szcore.addBar(bar);
                }
                List<Beat> fromBeats = (List<Beat>) fromBar.getBeats();
                for (Beat fromBeat : fromBeats) {
                    BeatId fromBeatId = fromBeat.getBeatId();
                    BeatId beatId = new BeatId(fromBeatId.getBeatNo(), instrumentId, pageId, scoreId, barId, fromBeatId.getBaseBeatNo());
                    BasicBeat beat = new BasicBeat(beatId, fromBeat.getStartTimeMillis(), fromBeat.getDurationMillis(), fromBeat.getEndTimeMillis(),
                            fromBeat.getBaseBeatUnitsNoAtStart(), fromBeat.getBaseBeatUnitsDuration(), fromBeat.getBaseBeatUnitsNoOnEnd(),
                            fromBeat.getPositionXStartPxl(), fromBeat.getPositionXEndPxl(), fromBeat.getPositionYStartPxl(), fromBeat.getPositionYEndPxl(), fromBeat.isUpbeat());
                    if (!szcore.containsBeat(beat)) {
                        szcore.addBeat(beat);
                    }
                    if(isCopyScripts) {
                        List<Script> fromBeatScripts = szcore.getBeatScripts(fromBeatId);
                        if (fromBeatScripts != null && !fromBeatScripts.isEmpty()) {
                            for (Script fromScript : fromBeatScripts) {
                                Script script = null;
                                if(fromScript instanceof BasicScript) {
                                    IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
                                    script = new BasicScript(id, beatId, fromScript.getContent());
                                } else if(fromScript instanceof BasicTransition) {
                                    IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
                                    BasicTransition fromTransition = (BasicTransition)fromScript;
                                    script = new BasicTransition(id, beatId, fromTransition.getComponent(), fromTransition.getDuration(), fromTransition.getStartValue(), fromTransition.getEndValue(), fromTransition.getFrequency());
                                } else if(fromScript instanceof OscScript) {
                                    IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
                                    OscScript fromOscScript = (OscScript)fromScript;
                                    script = new OscScript(id, beatId, fromOscScript.getTarget(), fromOscScript.getArgs(), fromOscScript.isResetPoint(), fromOscScript.isResetOnly());
                                } else if(fromScript instanceof ScriptingEngineScript) {
                                    IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
                                    ScriptingEngineScript fromSeScript = (ScriptingEngineScript)fromScript;
                                    script = new ScriptingEngineScript(id, beatId, fromSeScript.getContent(), fromSeScript.isResetPoint(), fromSeScript.isResetOnly());
                                } else if(fromScript instanceof WebAudienceScoreScript) {
                                    IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
                                    WebAudienceScoreScript fromWebScript = (WebAudienceScoreScript)fromScript;
                                    script = new WebAudienceScoreScript(id, beatId, fromWebScript.getContent(), fromWebScript.isResetPoint(), fromWebScript.isResetOnly());
                                }
                                if(script != null) {
                                    szcore.addScript(script);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected BeatId prepareInstrument(Instrument instrument, Transport transport) {
        Id transportId = transport.getId();
        boolean isAudioVideoInstrument = instrument.isAv();
        boolean isScoreInstrument = !isAudioVideoInstrument;
        if (isScoreInstrument) {
            createInstrumentStaves(instrument);
        }
        szcore.addInstrumentTransport(instrument, transport);

        List<BeatId> beats = new ArrayList<>(szcore.getInstrumentBeatIds(instrument.getId()));
        Collections.sort(beats);

        int currentBeatNo = -1;
        int currentBarNo = 0;
        int currentPageNo = 0;
        Tempo currentTempo = null;
        TimeSignature currentTimeSig = null;
        BeatId lastBeat = null;
        for (BeatId beatId : beats) {
            lastBeat = beatId;
            Beat beat = szcore.getBeat(beatId);
            szcore.addTransportBeatId(beat, transportId);
            Id instrumentId = beat.getInstrumentId();
            if (!instrument.getId().equals(instrumentId)) {
                LOG.error("Unexpected instrument id: " + instrumentId + " expected: " + instrument.getId());
                return lastBeat;
            }

            int beatNo = beat.getBeatNo();
            BarId barId = (BarId) beat.getBarId();
            int barNo = barId.getBarNo();
            Bar bar = szcore.getBar(barId);
            PageId pageId = (PageId) beat.getPageId();
            int pageNo = pageId.getPageNo();

            BasicStave currentStave = null;
            BasicStave nextStave = null;
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            if (isScoreInstrument) {
                currentStave = (BasicStave) szcore.getCurrentStave(instrumentId);
                nextStave = (BasicStave) szcore.getNextStave(instrumentId);
            }

            Tempo tempo = bar.getTempo();
            TimeSignature timeSignature = bar.getTimeSignature();

            boolean isFirstBeat = (beatNo == 0);
            boolean isFirstPage = (pageNo == 1);
            boolean isNewBeat = (beatNo != currentBeatNo);
            boolean isNewBar = (barNo != currentBarNo);
            boolean isNewPage = (pageNo != currentPageNo);

            if (isNewBeat) {
                currentBeatNo = beatNo;
                if (isFirstBeat && isScoreInstrument) {
                    for (Stave stave : staves) {
                        addClockTickEvents(transport.getId(), stave);
                        addClockBaseBeatEvent(transport.getId(), stave);
                    }
                }
            }
            if (isNewBar) {
                currentBarNo = barNo;
                if (tempo != null && (currentTempo == null || !currentTempo.equals(tempo))) {
                    addTempoChangeEvent(tempo, beatId, Consts.ALL_DESTINATIONS, transport.getId());
                    currentTempo = tempo;
                }

                if (timeSignature != null && (currentTimeSig == null || !currentTimeSig.equals(timeSignature))) {
                    addTimeSigChangeEvent(timeSignature, beatId, transport.getId());
                    currentTimeSig = timeSignature;
                }
            }
            if (isNewPage && isScoreInstrument) {
                currentPageNo = pageNo;
                if (isFirstPage) {
                    currentStave.setActive(true);
                    nextStave.setActive(false);
                } else {
                    currentStave.setActive(false);
                    nextStave.setActive(true);

                    // TODO both staves active between beatId and deactivateBeat so getCurrentStave can return wrong stave
                    Beat executeBeat = szcore.getOffsetBeat(beatId, -1);
                    Beat deactivateBeat = szcore.getOffsetBeat(beatId, 1);
                    Beat pageChangeBeat = szcore.getOffsetBeat(beatId, 4);

                    addPrepStaveChangeEvent(executeBeat.getBeatId(), beatId, deactivateBeat.getBeatId(), pageChangeBeat.getBeatId(), null, transport.getId());
                }
            }

            List<Script> beatScripts = szcore.getBeatScripts(beatId);
            if (beatScripts != null && !beatScripts.isEmpty()) {
                for (Script script : beatScripts) {
                    addScript(beatId, script, transportId);
                }
            }
            addWebScoreBeatScripts(beatId, transportId);
            addScriptingEngineBeatScripts(beatId, transportId);
        }

        return lastBeat;
    }

    protected void addScriptingEngineBeatScripts(BeatId beatId, Id transportId) {
        if(scriptingEngine == null) {
            return;
        }
        List<ScriptingEngineScript> scripts = scriptingEngine.getBeatScripts(beatId);
        if (scripts != null && !scripts.isEmpty()) {
            addScriptingEngineEvent(beatId, scripts, transportId);
        }
    }

    protected void addWebScoreBeatScripts(BeatId beatId, Id transportId) {
        if(webAudienceScoreProcessor == null) {
            return;
        }
        List<WebAudienceScoreScript> scripts = webAudienceScoreProcessor.getBeatScripts(beatId);
        if (scripts != null && !scripts.isEmpty()) {
            addWebScoreEvent(beatId, scripts, transportId);
        }
    }

    protected void addScript(BeatId beatId, Script script, Id transportId) {
        ScriptType type = script.getType();
        switch (type) {
            case JAVASCRIPT:
                addBeatScriptEvent(beatId, script, transportId);
                break;
            case TRANSITION:
                addBeatTransitionEvent(beatId, (BasicTransition) script, transportId);
                break;
            case WEB_AUDIENCE_SCORE:
                webAudienceScoreProcessor.addBeatScript(beatId, (WebAudienceScoreScript) script);
                break;
            case OSC_PLAYER:
                addOscScriptEvent(beatId, (OscScript) script, transportId);
                break;
            case SCRIPT_ENGINE:
                addScriptEngineEvent(beatId, (ScriptingEngineScript) script);
                break;
        }
    }

    private void addOscScriptEvent(BeatId beatId, OscScript script, Id transportId) {

        if (script.isResetPoint()) {
            try {
                addMaxPreset(beatId, script);
                if (!script.isResetOnly()) {
                    addBeatOscScript(beatId, transportId, script);
                }
            } catch (Exception e) {
                LOG.error("addOscScriptEvent: failed to add maxmsp preset: " + script, e);
            }
        } else {
            addBeatOscScript(beatId, transportId, script);
        }
    }

    private void addBeatOscScript(BeatId beatId, Id transportId, OscScript script) {
        OscEvent beatScriptEvent = createOscBeatScriptEvent(script, beatId);
        szcore.addScoreBaseBeatEvent(transportId, beatScriptEvent);
    }

    private void addScriptEngineEvent(BeatId beatId, ScriptingEngineScript script) {
        try {
            if (script.isResetPoint()) {
                scriptingEngine.addResetScript(beatId, script);
                if (!script.isResetOnly()) {
                    scriptingEngine.addBeatScript(beatId, script);
                }
            } else {
                scriptingEngine.addBeatScript(beatId, script);
            }
        } catch (Exception e) {
            LOG.error("addScriptEngineEvent: failed to add scripting preset: " + script, e);
        }
    }

    private void addScriptingEngineEvent(BeatId beatId, List<ScriptingEngineScript> scripts, Id transportId) {
        ScriptingEngineEvent scriptingEngineEvent = createScriptingEngineEvent(scripts, beatId);
        szcore.addScoreBaseBeatEvent(transportId, scriptingEngineEvent);
    }

    public void addMaxPreset(BeatId beatId, OscScript script) {
        if (script == null || !script.isResetPoint()) {
            return;
        }

        int beatNo = beatId.getBaseBeatNo();
        OscEvent beatScriptEvent = createOscBeatScriptEvent(script, beatId);

        ScriptEventPreset scriptPreset = maxMspConfig.getPresetScripts(beatNo);
        if (scriptPreset == null) {
            scriptPreset = new ScriptEventPreset(beatNo);
            maxMspConfig.addPreset(scriptPreset);
        }

        scriptPreset.addScriptEvent(beatScriptEvent);
    }

    private void addBeatScriptEvent(BeatId beatId, Script script, Id transportId) {
        OscEvent beatScriptEvent = createBeatScriptEvent(script, beatId);
        szcore.addScoreBaseBeatEvent(transportId, beatScriptEvent);
    }

    private void addBeatTransitionEvent(BeatId beatId, Transition transition, Id transportId) {
        TransitionEvent transitionEvent = createTransitionEvent(transition, beatId);
        szcore.addScoreBaseBeatEvent(transportId, transitionEvent);
    }

    private void addWebScoreEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, Id transportId) {
        WebAudienceEvent webAudienceEvent = createWebAudienceEvent(scripts, beatId);
        szcore.addScoreBaseBeatEvent(transportId, webAudienceEvent);
    }

    protected void addTransportInitEvents(Tempo tempo, TimeSignature timeSignature, int startBaseBeatNo, int transportBeatNo, int tickNo, long positionMillis, Id transportId, List<SzcoreEvent> initEvents) {
        addTransportPositionEvent(transportId, startBaseBeatNo, transportBeatNo, tickNo, positionMillis, initEvents);
        addTempoChangeInitEvent(tempo, null, transportId, initEvents);
        addTimeSigChangeInitEvent(timeSignature, null, transportId, initEvents);
        addPrecountSetupEvent(szcore.getPrecountBeatNo(), szcore.getPrecountMillis(), szcore.getPrecountBeaterInterval(), transportId, initEvents);
    }

    protected void addPageInitEvents(Transport transport, Instrument instrument, BasicStave currentStave, BasicStave nextStave,
                                   Page page, List<SzcoreEvent> initEvents) {
        addActiveStaveChangeEvent(currentStave.getId(), true, false, null, transport.getId(), instrument, initEvents);
        addActiveStaveChangeEvent(nextStave.getId(), false, false, null, transport.getId(), instrument, initEvents);
        addNewPageEvents(page, currentStave, transport.getId(), initEvents);
        Page nextPage = szcore.getNextPage((PageId) page.getId());
        if (nextPage != null) {
            addNewPageEvents(nextPage, nextStave, transport.getId(), initEvents);
        }
    }

    protected void addInitAvEvent(Transport transport, Instrument instrument, List<SzcoreEvent> initEvents, BeatId startBeatId) {

        OscEvent resetInstrumentEvent = createResetInstrumentEvent(instrument.getName());
        ScriptEventPreset eventPreset = maxMspConfig.getBeatResetScripts(startBeatId);
        List<OscEvent> presetEvents = Collections.emptyList();
        if(eventPreset != null) {
            presetEvents = eventPreset.getScriptEvents();
        }

        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transport.getId(), resetInstrumentEvent);
            for (OscEvent presetEvent : presetEvents) {
                szcore.addScoreBaseBeatEvent(transport.getId(), presetEvent);
            }
        } else {
            initEvents.add(resetInstrumentEvent);
            initEvents.addAll(presetEvents);
        }
    }


    protected void addInitWebAudienceEvent(Transport transport, List<SzcoreEvent> initEvents, BeatId startBeatId) {
        WebAudienceResetEvent webScoreResetEvent = createWebAudienceResetEvent(startBeatId);
        if (webScoreResetEvent == null) {
            return;
        }
        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transport.getId(), webScoreResetEvent);
        } else {
            initEvents.add(webScoreResetEvent);
        }
    }

    protected void addInitWebScoreEvent(Transport transport, BeatId startBeatId, StaveId staveId) {
        Instrument instrument = szcore.getInstrument(staveId.getInstrumentId());
        String destination = instrument.getName();

        String webStaveId = WebUtil.getWebStaveId(staveId);
        OutgoingWebEvent webScoreResetPlayStave = createWebScoreStartEvent(startBeatId, webStaveId, destination);
        if (webScoreResetPlayStave == null) {
            return;
        }
        szcore.addOneOffBaseBeatEvent(transport.getId(), webScoreResetPlayStave);
    }

    protected void addInitScriptingEvent(Transport transport, List<SzcoreEvent> initEvents, BeatId startBeatId) {
        ScriptingEngineResetEvent scriptingEngineResetEvent = createScriptingEngineResetEvent(startBeatId);
        if (scriptingEngineResetEvent == null) {
            return;
        }
        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transport.getId(), scriptingEngineResetEvent);
        } else {
            initEvents.add(scriptingEngineResetEvent);
        }
    }

    public void processPrepStaveChange(Id instrumentId, BeatId activateBeatId, BeatId deactivateBeatId, BeatId pageChangeBeatId, PageId nextPageId) {
        if (szcore == null) {
            return;
        }

        BasicStave currentStave = (BasicStave) szcore.getCurrentStave(instrumentId);
        BasicStave nextStave = (BasicStave) szcore.getNextStave(instrumentId);

        Transport transport = szcore.getInstrumentTransport(instrumentId);
        Id transportId = transport.getId();
        Instrument instrument = szcore.getInstrument(instrumentId);

        //TODO both staves active between activateBeatId and deactivateBeatId - getCurrentStave() might return wrong value
        addOneOffActiveStaveChangeEvent(nextStave.getId(), true, true, activateBeatId, transportId, instrument);
        addOneOffActiveStaveChangeEvent(currentStave.getId(), false, false, deactivateBeatId, transportId, instrument);

        //change next stave page after x beats of the current page
        PageId pageId = (PageId) pageChangeBeatId.getPageId();
        Page nextPage = szcore.getPage(nextPageId);
        if (nextPage == null) {
            nextPage = szcore.getNextPage(pageId);
        }

        if (nextPage == null) {
            if (szcore.isUseContinuousPage()) {
                nextPage = prepareContinuousPage(instrumentId, pageId);
            } else {
                LOG.info("processPrepStaveChange: nextPage is NULL for instrument: {}, assuming score end ...", instrumentId);
                nextPage = szcore.getEndPage(instrumentId);
            }
        }

        if (nextPage == null) {
            LOG.error("processPrepStaveChange: nextPage is NULL, ignoring prep stave change ...");
            return;
        }

        LOG.debug("processPrepStaveChange: nextPage: {}", nextPage);
        boolean isCloseOnMidPage = false;
        ScoreRandomisationStrategy rndStrategy = szcore.getRandomisationStrategy();
        if(rndStrategy != null && rndStrategy.isActive()) {
            isCloseOnMidPage = rndStrategy.isInActiveRange((InstrumentId) instrument.getId(), nextPage);
            if (!szcore.isRandomizeContinuousPageContent()) {
                isCloseOnMidPage = false;
            }
        }

        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if(builderStrategy != null && builderStrategy.isActive()) {
            isCloseOnMidPage = true;
        }

        addOneOffNewPageEvents(nextPage, isCloseOnMidPage, currentStave, pageChangeBeatId, deactivateBeatId, transportId);
    }

    private Page prepareContinuousPage(Id instrumentId, PageId currentPageId) {
        Page continuousPage = szcore.getContinuousPage(instrumentId);
        Page currentPage = szcore.getPage(currentPageId);
        Transport transport = szcore.getInstrumentTransport(instrumentId);

        List<Bar> bars = (List<Bar>) currentPage.getBars();
        Bar lastBar = bars.get(bars.size() - 1);
        int lastBarNo = lastBar.getBarNo();
        List<Beat> beats = (List<Beat>) lastBar.getBeats();
        Beat lastBeat = beats.get(beats.size() - 1);
        int lastBeatNo = lastBeat.getBeatNo();

        Id scoreId = currentPageId.getScoreId();
        int cPageNo = currentPageId.getPageNo() + 1;
        PageId newPageId = new PageId(cPageNo, instrumentId, scoreId);

        InscorePageMap continuousInscorePageMap = continuousPage.getInscorePageMap();
        InscorePageMap newInscorePageMap = new InscorePageMap(newPageId);
        List<BasicBeat> newBeats = new ArrayList<>();

        BasicPage newPage = new BasicPage(newPageId, continuousPage.getPageName(), continuousPage.getFileName(), newInscorePageMap, true);
        if (!szcore.containsPage(newPage)) {
            szcore.addPage(newPage);
        }

        bars = (List<Bar>) continuousPage.getBars();
        BeatId firstBeatId = null;
        for (Bar bar : bars) {
            BarId newBarId = new BarId(lastBarNo, instrumentId, newPageId, scoreId);
            lastBarNo++;
            BasicBar newBar = new BasicBar(newBarId, bar.getBarName(), bar.getTempo(), bar.getTimeSignature());
            if (szcore.doesNotcontainBar(newBar)) {
                szcore.addBar(newBar);
            }

            beats = (List<Beat>) bar.getBeats();

            for (Beat beat : beats) {
                BeatId newBeatId;
                if (beat.isUpbeat()) {
                    newBeatId = new BeatId(lastBeatNo, instrumentId, newPageId, scoreId, newBarId, lastBeat.getBaseBeatUnitsNoAtStart());
                    if (firstBeatId == null) {
                        firstBeatId = newBeatId;
                    }
                    BasicBeat newBeat = new BasicBeat(newBeatId, lastBeat.getStartTimeMillis(), beat.getDurationMillis(), lastBeat.getEndTimeMillis(),
                            lastBeat.getBaseBeatUnitsNoAtStart(), lastBeat.getBaseBeatUnitsDuration(), lastBeat.getBaseBeatUnitsNoOnEnd(),
                            beat.getPositionXStartPxl(), beat.getPositionXEndPxl(), beat.getPositionYStartPxl(), beat.getPositionYEndPxl(), beat.isUpbeat());

                    newBeats.add(newBeat);
                    szcore.addTransportBeatId(newBeat, transport.getId());

                    if (!szcore.containsBeat(newBeat)) {
                        szcore.addBeat(newBeat);
                    }
                    lastBeat = newBeat;
                } else {
                    lastBeatNo++;
                    long startTimeMillis = lastBeat.getEndTimeMillis();
                    int baseBeatUnitsNoAtStart = lastBeat.getBaseBeatUnitsNoOnEnd();
                    newBeatId = new BeatId(lastBeatNo, instrumentId, newPageId, scoreId, newBarId, baseBeatUnitsNoAtStart);
                    if (firstBeatId == null) {
                        firstBeatId = newBeatId;
                    }

                    long durationMillis = beat.getDurationMillis();
                    int baseBeatUnitsDuration = beat.getBaseBeatUnitsDuration();
                    long endTimeMillis = startTimeMillis + durationMillis;
                    int baseBeatUnitsNoOnEnd = baseBeatUnitsNoAtStart + baseBeatUnitsDuration;
                    BasicBeat newBeat = new BasicBeat(newBeatId, startTimeMillis, durationMillis, endTimeMillis, baseBeatUnitsNoAtStart,
                            baseBeatUnitsDuration, baseBeatUnitsNoOnEnd, beat.getPositionXStartPxl(), beat.getPositionXEndPxl(),
                            beat.getPositionYStartPxl(), beat.getPositionYEndPxl(), beat.isUpbeat());
                    newBeats.add(newBeat);
                    szcore.addTransportBeatId(newBeat, transport.getId());
                    if (!szcore.containsBeat(newBeat)) {
                        szcore.addBeat(newBeat);
                    }
                    lastBeat = newBeat;
                }

                //copy scripts
                BeatId beatId = beat.getBeatId();
                List<Script> scripts = szcore.getBeatScripts(beatId);
                if (scripts != null) {
                    for (Script script : scripts) {
                        Script newScript = script.copy(newBeatId);
                        szcore.addScript(newScript);
                    }
                }
            }
        }

        Instrument instrument = szcore.getInstrument(instrumentId);
        if (instrument.isAv()) {
            return newPage;
        }

        //Add stave changes only for score instruments
        List<InscoreMapElement> continuousInscoreMapElements = continuousInscorePageMap.getMapElements();
        for (int i = 0; i < newBeats.size(); i++) {
            BasicBeat newBeat = newBeats.get(i);
            InscoreMapElement continuousMapElement = continuousInscoreMapElements.get(i);
            InscoreMapElement newInscoreMapElement = new InscoreMapElement(
                    newBeat.getPositionXStartPxl(), newBeat.getPositionXEndPxl(),
                    newBeat.getPositionYStartPxl(), newBeat.getPositionYEndPxl(),
                    newBeat.getBaseBeatUnitsNoAtStart(), continuousMapElement.getBeatStartDenom(), newBeat.getBaseBeatUnitsNoOnEnd(), continuousMapElement.getBeatEndDenom());
            newInscorePageMap.addElement(newInscoreMapElement);
        }
        newInscorePageMap.createWebStr();
        newInscorePageMap.createInscoreStr();

        Beat executeBeat = szcore.getOffsetBeat(firstBeatId, -1);
        Beat deactivateBeat = szcore.getOffsetBeat(firstBeatId, 1);
        Beat pageChangeBeat = szcore.getOffsetBeat(firstBeatId, 4);

        addPrepStaveChangeEvent(executeBeat.getBeatId(), firstBeatId, deactivateBeat.getBeatId(), pageChangeBeat.getBeatId(), null, transport.getId());

        return newPage;
    }

    protected void createInstrumentStaves(Instrument instrument) {
        // TODO populate dynamically from engine
        szcore.addInstrumentOscPort(instrument.getId(), getInstrumentOscPort());
        BasicStave stave1 = (BasicStave) StaveFactory.createStave(1, instrument);
        if (stave1 != null) {
            stave1.setActive(true);
            szcore.addStave(stave1);
        }
        BasicStave stave2 = (BasicStave) StaveFactory.createStave(2, instrument);
        if (stave2 != null) {
            stave2.setActive(false);
            szcore.addStave(stave2);
        }
    }

    @Override
    public void onOpenModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        this.isUpdateWindowOpen = true;
        processStrategiesOnOpenModWindow(instId, nextPage, currentPageId);
    }

    protected void processStrategiesOnOpenModWindow(InstrumentId instId, Page nextPage, PageId currentPageId) {
        ScoreRandomisationStrategy rndStrategy = szcore.getRandomisationStrategy();
        if(rndStrategy != null) {
            processRndStrategyOnOpenModWindow(rndStrategy, instId, nextPage, currentPageId);
        }

        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if(builderStrategy != null) {
            processBuilderStrategyOnOpenModWindow(builderStrategy, instId);
        }
    }

    private void processBuilderStrategyOnOpenModWindow(ScoreBuilderStrategy builderStrategy, InstrumentId instId) {
        if(!builderStrategy.isActive()) {
            return;
        }
        String[] instruments = builderStrategy.getDynamicInstruments();
        if(instruments == null || instruments.length < 1) {
            return;
        }
        String instSlotsCsv = ParseUtil.convertToCsv(instruments);
        String section = builderStrategy.getCurrentSection();
        if(section == null) {
            return;
        }
        String inst = instId.getName();
        List<String> clients = builderStrategy.getInstrumentClients(section, inst);
        boolean isSendSlotEvent = isSendInstrumentSlotsEvent(inst);
        if(clients == null || clients.isEmpty()) {
            LOG.debug("processBuilderStrategy OpenModWindow, no clients for instrument: {} section: {}", inst, section);
        } else {
            for(String client : clients) {
                if(isSendSlotEvent) {
                    OscEvent instrumentSlotsEvent = createInstrumentSlotsEvent(client, instSlotsCsv);
                    publishOscEvent(instrumentSlotsEvent);
                } else {
                    OscEvent instrumentSlotsEvent =  createInstrumentResetSlotsEvent(client);
                    publishOscEvent(instrumentSlotsEvent);
                }
            }
        }
    }

    protected boolean isSendInstrumentSlotsEvent(String name) {
        return true;
    }

    private void processRndStrategyOnOpenModWindow(ScoreRandomisationStrategy strategy, InstrumentId instId, Page nextPage, PageId currentPageId) {
        if(!strategy.isActive()) {
            return;
        }
        boolean isNextPageInRndRange = nextPage != null && strategy.isInActiveRange(instId, nextPage);
        LOG.debug("onOpenModWindow: isNextPageInRndRange : {} page: {}", isNextPageInRndRange, nextPage);

        Page currentPage = szcore.getPage(currentPageId);
        boolean isCurrentPageInRndRange = strategy.isInActiveRange(instId, currentPage);

        boolean isRecalcTime = strategy.isRecalcTime() && isNextPageInRndRange;
        if (isRecalcTime) {
            strategy.recalcStrategy(nextPage);
        }
        String destination = szcore.getOscDestination(instId);

        if (isNextPageInRndRange) {
            List<InstrumentId> slotInstrumentIds = strategy.getInstrumentSlotIds();
            String instSlotsCsv = ParseUtil.convertToCsv(slotInstrumentIds);
            LOG.debug("onOpenModWindow: rnd strategy selected instruments: {}", instSlotsCsv);
            OscEvent instrumentSlotsEvent = createInstrumentSlotsEvent(destination, instSlotsCsv);
            if (instrumentSlotsEvent == null) {
                LOG.debug("onOpenModWindow: Invalid instrumentSlotsEvent, isInRndRange: true, destination: {} instSlotsCsv: {}", destination, instSlotsCsv);
                instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination);
                publishOscEvent(instrumentSlotsEvent);
                return;
            }
            publishOscEvent(instrumentSlotsEvent);
        } else {
            OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination);
            publishOscEvent(instrumentSlotsEvent);
        }

        if (isCurrentPageInRndRange) {
            WebAudiencePlayTilesEvent playTilesEvent = eventFactory.createWebAudiencePlayTilesEvent(clock.getSystemTimeMillis());
            processWebAudienceEvent(playTilesEvent);
        }
    }

    @Override
    public void onCloseModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        this.isUpdateWindowOpen = false;
        processStrategiesOnCloseModWindow(instId, stave, nextPage, currentPageId);
    }

    protected void processStrategiesOnCloseModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        ScoreRandomisationStrategy rndStrategy = szcore.getRandomisationStrategy();
        if(rndStrategy != null) {
            processRndStrategyOnCloseModWindow(rndStrategy, stave, instId, nextPage, currentPageId);
        }

        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if(builderStrategy != null) {
            processBuilderStrategyOnCloseModWindow(builderStrategy, instId, nextPage, stave);
        }
    }

    private void processRndStrategyOnCloseModWindow(ScoreRandomisationStrategy strategy, Stave stave, InstrumentId instId, Page nextPage, PageId currentPageId) {
        if(!strategy.isActive()) {
            return;
        }
        LOG.debug("processRndStrategyOn  CloseModWindow: instrument {} next page: {} ", instId.getName(), nextPage);
        boolean isInRange = strategy.isInActiveRange(instId, nextPage);
        if (isInRange) {
            if (strategy.isPageRecalcTime()) {
                LOG.debug("processRndStrategyOn CloseModWindow: next assignment Strategy: {} pageId: {} nextPage: {}  stave: {}", Arrays.toString(strategy.getAssignmentStrategy().toArray()), currentPageId.getPageNo(), nextPage.getPageNo(), stave.getStaveId().getStaveNo());
                processRndStrategyOnModClose(strategy);
            }
            sendRndPageFileUpdate(nextPage, stave, instId);
        } else {
            if (nextPage == null) {
                //Last page should be blank
                nextPage = szcore.getEndPage(instId);
            }
            LOG.debug("processRndStrategyOn CloseModWindow: instrument {} next page: {} stave: {} ", instId.getName(), nextPage.getPageNo(), stave.getStaveId().getStaveNo());
            sendPageFileUpdate(nextPage, stave);
        }
        String destination = szcore.getOscDestination(instId);
        OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination);
        publishOscEvent(instrumentSlotsEvent);
    }

    private void processBuilderStrategyOnCloseModWindow(ScoreBuilderStrategy builderStrategy, InstrumentId instId, Page nextPage, Stave stave) {
        if(!builderStrategy.isActive()) {
            return;
        }
        String[] instruments = builderStrategy.getDynamicInstruments();
        if(instruments == null || instruments.length < 1) {
            return;
        }
        String instSlotsCsv = ParseUtil.convertToCsv(instruments);
        String section = builderStrategy.getCurrentSection();
        if(section == null) {
            return;
        }
        List<String> clients = builderStrategy.getInstrumentClients(section, instId.getName());
        if(clients == null || clients.isEmpty()) {
            String destination = szcore.getOscDestination(instId);
            LOG.debug("processBuilderStrategyOn  CloseModWindow: Invalid instrumentSlotsEvent, isInRndRange: true, destination: {} instSlotsCsv: {}", destination, instSlotsCsv);
            OscEvent instrumentSlotsEvent =  createInstrumentResetSlotsEvent(destination);
            publishOscEvent(instrumentSlotsEvent);
        } else {
            if(builderStrategy.isStopOnSectionEnd()) {
                SectionInfo sectionInfo = builderStrategy.getSectionInfo(section);
                int sectionEndPageNo = sectionInfo.getEndPageNo();
                int nextPageNo = nextPage.getPageNo();
                if(nextPageNo > sectionEndPageNo) {
                    nextPage = szcore.getEndPage(instId);
                }
            }
            if (nextPage == null) {
                //Last page should be blank
                nextPage = szcore.getEndPage(instId);
            }
            LOG.debug("processBuilderStrategyOn CloseModWindow: instrument {} next page: {} stave: {} ", instId.getName(), nextPage.getPageNo(), stave.getStaveId().getStaveNo());
            sendPageFileUpdate(nextPage, stave);
            for(String client : clients) {
                OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(client);
                publishOscEvent(instrumentSlotsEvent);
            }
        }
    }

    public void sendRndPageFileUpdate(Page page, Stave stave, InstrumentId instId) {
        if (page == null) {
            return;
        }
        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
        Page rndPage = strategy.getRandomPageFileName(instId);
        String pageFileName;
        PageId rndPageId;
        if (rndPage == null) {
            pageFileName = page.getFileName();
            rndPageId = null;
            LOG.debug("sendRndPageFileUpdate: Invalid random page file name, using: {} for page: {}", pageFileName, page.getPageNo());
        } else {
            pageFileName = rndPage.getFileName();
            rndPageId = rndPage.getPageId();
            LOG.debug("sendRndPageFileUpdate: Using random page file name: {} for instrument: {}", pageFileName, instId);
        }
        LOG.debug("sendRndPageFileUpdate: page file name: {} for instrument: {}: stave: {}", pageFileName, instId, stave.getStaveId().getStaveNo());
        List<OscEvent> out = createPageChangeEvents(page, pageFileName, rndPageId, (BasicStave) stave);
        publishOscEvents(out);
    }

    public void sendOscInstrumentRndPageUpdate(int bufferNo) {
        LOG.debug("sendOscInstrumentRndPageUpdate: bufferNo: {}", bufferNo);

        Collection<Instrument> oscPlayers = szcore.getOscPlayers();
        if (oscPlayers == null || oscPlayers.isEmpty()) {
            return;
        }

        Instrument instrument = oscPlayers.iterator().next();
        InstrumentId instId = (InstrumentId) instrument.getId();

        Transport transport = szcore.getInstrumentTransport(instId);
        BeatId beatId = szcore.getInstrumentBeatIds(transport.getId(), instId, currentBeatNo);
        PageId pageId = (PageId) beatId.getPageId();
        Page nextPage = szcore.getNextPage(pageId);
        LOG.debug("sendOscInstrumentRndPageUpdate: instrument {} page: {} currentBeat: {}  nextPage: {}", instId.getName(), pageId.getPageNo(), beatId, nextPage.getPageNo());

        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
        boolean isInRange = strategy.isInActiveRange(instId, nextPage);
        Map<Integer, List<InstrumentId>> pageAssignments = strategy.getPageAssigments();
        if (isInRange) {
            sendOscPlayerRndPageUpdate(instId, beatId, pageAssignments.keySet(), nextPage, bufferNo, transport);
        }
    }

    @Override
    public void setUpContinuousTempoChange(int endBpm, int timeInBeats) {
        Collection<Instrument> avInstruments = szcore.getAvInstruments();
        if (avInstruments.isEmpty()) {
            return;
        }
        if (endBpm < MIN_BPM) {
            endBpm = 1;
        }
        if (endBpm > MAX_BPM) {
            endBpm = MAX_BPM;
        }
        if (timeInBeats < 1) {
            timeInBeats = 1;
        }
        Instrument avInstrument = avInstruments.iterator().next();
        InstrumentId acInstId = (InstrumentId) avInstrument.getId();
        Transport transport = szcore.getInstrumentTransport(acInstId);
        Tempo currentTempo = transport.getTempo();
        int currentBpm = currentTempo.getBpm();
        int bpmDelta = (endBpm - currentBpm) / timeInBeats;
        int currentBeat = getCurrentBeatNo();
        BeatId currentBeatId = szcore.getInstrumentBeatIds(transport.getId(), acInstId, currentBeat);
        for (int i = 1; i <= timeInBeats; i++) {
            Beat offsetBeat = szcore.getOffsetBeat(currentBeatId, i);
            if (offsetBeat == null) {
                continue;
            }
            int beatBpmOffset = i * bpmDelta;
            int newBpm = currentBpm + beatBpmOffset;
            if (i == timeInBeats) {
                newBpm = endBpm;
            }
            Tempo newTempo = new TempoImpl(newBpm, currentTempo.getBeatDuration());
            LOG.debug("#### setUpContinuousTempoChange: new tempo{}, beat: {}, bpmDelta: {}, beatBpmOffset: {}", newBpm, offsetBeat.getBeatId(), bpmDelta, beatBpmOffset);
            addTempoChangeEvent(newTempo, offsetBeat.getBeatId(), Consts.ALL_DESTINATIONS, transport.getId());
        }
    }

    @Override
    public void scheduleEvent(SzcoreEvent event, long timeDeltaMs) {
        long elapsedTime = clock.getElapsedTimeMillis();
        long eventTime = elapsedTime + timeDeltaMs;
        ScheduledEventTask task = taskFactory.createScheduledEventTask(eventTime, event, this);
        scheduleTask(task);
    }

    public void scheduleTask(MusicTask task) {
        if (task == null) {
            return;
        }
        scheduler.add(task);
    }

    public void sendOscPlayerRndPageUpdate(InstrumentId instId, BeatId beatId, Collection<Integer> selectedPageIds, Page nextPage, int bufferNo, Transport transport) {
        if (!szcore.isOscPlayer(instId)) {
            return;
        }

        int pageNo = nextPage.getPageNo();
        if (selectedPageIds != null && !selectedPageIds.isEmpty()) {
            pageNo = selectedPageIds.iterator().next();
        }
        LOG.debug("sendOscPlayerRndPageUpdate: MAXMSP Using RND page no: {} for instrument: {}", pageNo, instId);

        //Send setFile in buffer to be played next
        OscScript setFileScript = maxMspConfig.createSetFileInNextBufferScript(beatId, pageNo, bufferNo);
        OscEvent setFileEvent = createOscBeatScriptEvent(setFileScript, beatId);
        publishOscEvent(setFileEvent);

        //Add play buffer event for next page
        if (bufferNo == 0) {
            Beat nextPageBeat = nextPage.getFirstBeat();
            OscScript playNextBufferScript = maxMspConfig.createPlayNextBufferScript(nextPageBeat.getBeatId(), bufferNo);
            OscEvent playBufferEvent = createOscBeatScriptEvent(playNextBufferScript, nextPageBeat.getBeatId());
            szcore.addScoreBaseBeatEvent(transport.getId(), playBufferEvent);
        }
    }

    public void sendPageFileUpdate(Page page, Stave stave) {
        if (page == null) {
            return;
        }
        LOG.debug("sendPageFileUpdate: page: {} for instrument: {}: stave: {}", page.getPageNo(), stave.getStaveId().getInstrumentId(), stave.getStaveId().getStaveNo());
        List<OscEvent> out = createPageChangeEvents(page, page.getFileName(), null, (BasicStave) stave);
        publishOscEvents(out);
    }

    protected void addNewPageInstrumentEvents(Instrument instrument, List<SzcoreEvent> initEvents) {
        String destination = szcore.getOscDestination(instrument.getId());
        OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination);
        initEvents.add(instrumentSlotsEvent);
    }

    protected void addNewPageEvents(Page page, BasicStave stave, Id transportId, List<SzcoreEvent> initEvents) {
        if (page == null || stave == null || transportId == null) {
            return;
        }

        StaveId staveId = stave.getId();
        String pageName = page.getFileName();
        PageId pageId = page.getPageId();

        LOG.debug("addNewPageEvents: instrument {} next page: {} stave: {} ", staveId.getInstrumentId(), pageName, staveId.getStaveNo());
        OscEvent pageDisplayEvent = createDisplayPageEvent(pageId, pageName, null, stave);
        if (initEvents == null) {
            LOG.error("addNewPageEvents: NULL initEvents, should not happen. instrument {} next page: {} stave: {}", staveId.getInstrumentId(), pageName, staveId.getStaveNo());
            //szcore.addScoreBaseBeatEvent(transportId, pageDisplayEvent);
        } else {
            initEvents.add(pageDisplayEvent);
        }

        if (page.getPageName().equals(Consts.BLANK_PAGE_NAME)) {
            return;
        }

        String address = stave.getOscAddress();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());
        InscorePageMap inscorePageMap = page.getInscorePageMap();
        OscEvent pageMapDisplayEvent = createPageMapFileEvent(pageId, staveId, pageName, address, inscorePageMap.getMapElements(), destination);
        if (initEvents == null) {
            LOG.error("addNewPageEvents: pageMapDisplayEvent NULL initEvents, should not happen. instrument {} next page: {} stave: {}", staveId.getInstrumentId(), pageName, staveId.getStaveNo());
            //szcore.addScoreBaseBeatEvent(transportId, pageMapDisplayEvent);
        } else {
            initEvents.add(pageMapDisplayEvent);
        }
    }

    private void removeOneOffModEvents(Id transportId, int baseBeatNo, Id instrumentId) {
        List<SzcoreEvent> oneOffBeatEvents = szcore.getOneOffBaseBeatEvents(transportId, baseBeatNo);
        if (oneOffBeatEvents == null || oneOffBeatEvents.isEmpty()) {
            return;
        }
        List<SzcoreEvent> filteredEvents = new ArrayList<>(5);
        boolean isReplace = false;
        for (SzcoreEvent bev : oneOffBeatEvents) {
            if (bev instanceof ModWindowEvent) {
                ModWindowEvent mev = (ModWindowEvent) bev;
                Id bIns = mev.getCurrentPageId().getInstrumentId();
                if (bIns.equals(instrumentId)) {
                    LOG.debug("removeOneOffModEvents: removing one off Mod event: {}", mev);
                    isReplace = true;
                } else {
                    filteredEvents.add(mev);
                }
            } else {
                filteredEvents.add(bev);
            }
        }
        if (isReplace) {
            szcore.replaceOneOffBaseBeatEvents(transportId, baseBeatNo, filteredEvents);
        }
    }

    private void removeOneOffStopEvents(Id transportId, int baseBeatNo) {
        List<SzcoreEvent> oneOffBeatEvents = szcore.getOneOffBaseBeatEvents(transportId, baseBeatNo);
        if (oneOffBeatEvents == null || oneOffBeatEvents.isEmpty()) {
            return;
        }
        List<SzcoreEvent> filteredEvents = new ArrayList<>(5);
        boolean isReplace = false;
        for (SzcoreEvent bev : oneOffBeatEvents) {
            if (bev instanceof StopEvent) {
                isReplace = true;
            } else {
                filteredEvents.add(bev);
            }
        }
        if (isReplace) {
            szcore.replaceOneOffBaseBeatEvents(transportId, baseBeatNo, filteredEvents);
        }
    }

    protected void addOneOffNewPageEvents(Page page, boolean isCloseOnMidPage, BasicStave stave, BeatId pageChangeBeat, BeatId activatePageBeat, Id transportId) {
        if (transportId == null) {
            return;
        }

        LOG.debug("addOneOffNewPageEvents: Add Open/Close Window Event beatId: {} ", activatePageBeat);
        removeOneOffModEvents(transportId, activatePageBeat.getBaseBeatNo(), page.getInstrumentId());
        ModWindowEvent openWindowEvent = createModWindowEvent(activatePageBeat, page, (PageId) pageChangeBeat.getPageId(), stave, true);
        szcore.addOneOffBaseBeatEvent(transportId, openWindowEvent);

        BeatId closeWindowBeatId = pageChangeBeat;
        if (isCloseOnMidPage) {
            PageId currentPageId = (PageId) activatePageBeat.getPageId();
            Page currentPage = szcore.getPage(currentPageId);

            Beat firstBeat = currentPage.getFirstBeat();
            Beat lastBeat = currentPage.getLastBeat();
            int first = firstBeat.getBeatNo();
            int last = lastBeat.getBeatNo();
            int half = (last - first) / 2 + 1;

            Beat closeWindowBeat = szcore.getOffsetBeat(firstBeat.getBeatId(), half);
            if (closeWindowBeat != null) {
                closeWindowBeatId = closeWindowBeat.getBeatId();
            }
            LOG.debug("Close Window Event page first beat: {} last: {}, calculated beatId: {} ", first, last, closeWindowBeatId);
        }

        LOG.debug("addOneOffNewPageEvents: Add Open/Close Window Event instrument: {} stave: {} ", stave.getStaveId().getInstrumentId(), stave.getStaveId().getStaveNo());
        removeOneOffModEvents(transportId, closeWindowBeatId.getBaseBeatNo(), page.getInstrumentId());
        ModWindowEvent closeWindowEvent = createModWindowEvent(closeWindowBeatId, page, (PageId) pageChangeBeat.getPageId(), stave, false);
        szcore.addOneOffBaseBeatEvent(transportId, closeWindowEvent);
    }

    protected List<OscEvent> createPageChangeEvents(Page nextPage, String nextPageName, PageId rndPageId, BasicStave nextStave) {
        List<OscEvent> out = new ArrayList<>(5);
        if (nextPage == null || nextStave == null) {
            return out;
        }

        StaveId staveId = nextStave.getId();
        PageId nextPageId = nextPage.getPageId();
        OscEvent pageDisplayEvent = createDisplayPageEvent(nextPageId, nextPageName, rndPageId, nextStave);
        out.add(pageDisplayEvent);

        BasicPage basicPage = (BasicPage) nextPage;
        String address = nextStave.getOscAddress();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());
        InscorePageMap inscorePageMap = basicPage.getInscorePageMap();
        if (basicPage.isSendInscoreMap() && basicPage.getInscorePageMap() != null) {
            String inscoreMap = inscorePageMap.getInscoreStr();
            if(inscoreMap == null) {
                inscoreMap = inscorePageMap.toInscoreString();
            }
            OscEvent pageMapDisplayEvent = createPageMapEvent(nextPageId, staveId, nextPageName, address, destination, inscoreMap, inscorePageMap.getMapElements());
            out.add(pageMapDisplayEvent);
        } else {
            OscEvent pageMapDisplayEvent = createPageMapFileEvent(nextPageId, staveId,  nextPage.getFileName(), address, inscorePageMap.getMapElements(), destination);
            out.add(pageMapDisplayEvent);
        }
        return out;
    }

    public void addClockBaseBeatEvent(Id transportId, Stave stave) {
        StaveId id = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(id.getInstrumentId());
        DateTickEvent dateTickEvent = eventFactory.createDateTickEvent(destination, id, 0, clock.getSystemTimeMillis());
        szcore.addClockBaseBeatTickEvent(transportId, dateTickEvent);
    }

    public void addInitClockBaseBeatEvent(Stave stave, int beatNo, List<SzcoreEvent> initEvents) {
        StaveId id = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(id.getInstrumentId());

        DateTickEvent dateTickEvent = eventFactory.createDateTickEvent(destination, id, beatNo, clock.getSystemTimeMillis());
        initEvents.add(dateTickEvent);
    }

    public void addInitStaveStartMarkEvent(Stave stave, int beatNo, List<SzcoreEvent> initEvents) {
        StaveId id = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(id.getInstrumentId());
        String address = stave.getOscAddressScoreStartMark();
        StaveStartMarkEvent staveDateTickEvent = eventFactory.createStaveStartMarkEvent(address, destination, id, beatNo, clock.getSystemTimeMillis());
        ArrayList<Object> args = (ArrayList<Object>) staveDateTickEvent.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        String beat = beatNo + Consts.EIGHTH;
        args.add(1, beat);
        initEvents.add(staveDateTickEvent);
    }

    public void addInitStaveDyEvent(Stave stave, List<SzcoreEvent> initEvents) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());
        String address = stave.getOscAddressScoreBeater();
        StaveDyTickEvent staveDyTickEvent = eventFactory.createStaveDyTickEvent(address, destination, staveId, clock.getSystemTimeMillis());
        ArrayList<Object> args = (ArrayList<Object>) staveDyTickEvent.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, new Float(Consts.OSC_STAVE_BEATER_Y_MIN));
        initEvents.add(staveDyTickEvent);
    }

    public void addClockTickEvents(Id transportId, Stave stave) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        //Beater dy position
        String address = stave.getOscAddressScoreBeater();
        StaveDyTickEvent staveDyTickEvent = eventFactory.createStaveDyTickEvent(address, destination, staveId, clock.getSystemTimeMillis());
        szcore.addClockTickEvent(transportId, staveDyTickEvent);
    }

    public void addTempoChangeInitEvent(Tempo tempo, BeatId beatId, Id transportId, List<SzcoreEvent> initEvents) {
        long now = clock.getSystemTimeMillis();
        List<OscStaveTempoEvent> oscEvents = new ArrayList<>();

        OscStaveTempoEvent oscTempoEvent = eventFactory.createOscStaveTempoEvent(Consts.ALL_DESTINATIONS, tempo.getBpm(), now);
        oscEvents.add(oscTempoEvent);

        TempoChangeEvent tempoChangeEvent = eventFactory.createTempoChangeEvent(tempo, beatId, transportId, oscEvents, clock.getSystemTimeMillis());
        int toRemove = -1;
        for (int i = 0; i < initEvents.size(); i++) {
            SzcoreEvent event = initEvents.get(i);
            if (!(EventType.MUSIC == event.getEventType())) {
                continue;
            }
            MusicEvent musicEvent = (MusicEvent) event;
            if (!(MusicEventType.TEMPO_CHANGE == musicEvent.getMusicEventType())) {
                continue;
            }
            TempoChangeEvent existing = (TempoChangeEvent) musicEvent;
            if (existing.getTransportId().equals(transportId)) {
                LOG.warn("Already have transport Tempo change INIT, removing event: " + existing);
                toRemove = i;
            }
        }
        if (toRemove >= 0) {
            initEvents.remove(toRemove);
        }

        initEvents.add(tempoChangeEvent);
    }

    public void addTransportPositionEvent(Id transportId, int startBaseBeatNo, int transportBeatNo, int tickNo, long positionMillis, List<SzcoreEvent> initEvents) {
        TransportPositionEvent transportPositionEvent = createTransportPositionEvent(transportId, startBaseBeatNo, transportBeatNo, tickNo, positionMillis);

        int toRemove = -1;
        for (int i = 0; i < initEvents.size(); i++) {
            SzcoreEvent event = initEvents.get(i);
            if (!(EventType.MUSIC == event.getEventType())) {
                continue;
            }
            MusicEvent musicEvent = (MusicEvent) event;
            if (!(MusicEventType.TRANSPORT_POSITION == musicEvent.getMusicEventType())) {
                continue;
            }
            TempoChangeEvent existing = (TempoChangeEvent) musicEvent;
            if (existing.getTransportId().equals(transportId)) {
                LOG.warn("Already have transport Tempo change INIT, removing event: " + existing);
                toRemove = i;
            }
        }
        if (toRemove >= 0) {
            initEvents.remove(toRemove);
        }

        initEvents.add(transportPositionEvent);
    }

    public void addTimeSigChangeInitEvent(TimeSignature timeSignature, BeatId beatNo, Id transportId, List<SzcoreEvent> initEvents) {
        TimeSigChangeEvent timeSigChangeEvent = eventFactory.createTimeSigChangeEvent(timeSignature, beatNo, transportId, clock.getSystemTimeMillis());
        int toRemove = -1;
        for (int i = 0; i < initEvents.size(); i++) {
            SzcoreEvent event = initEvents.get(i);
            if (!(EventType.MUSIC == event.getEventType())) {
                continue;
            }
            MusicEvent musicEvent = (MusicEvent) event;
            if (!(MusicEventType.TIMESIG_CHANGE == musicEvent.getMusicEventType())) {
                continue;
            }
            TimeSigChangeEvent existing = (TimeSigChangeEvent) musicEvent;
            if (existing.getTransportId().equals(transportId)) {
                LOG.warn("Already have transport TimeSig change INIT, removing event: " + existing);
                toRemove = i;
            }
        }
        if (toRemove >= 0) {
            initEvents.remove(toRemove);
        }

        initEvents.add(timeSigChangeEvent);
    }

    public void addTempoChangeEvent(Tempo tempo, BeatId beatId, String destination, Id transportId) {
        TempoChangeEvent tempoChangeEvent = createTempoChangeEvent(tempo, beatId, destination, transportId);
        TIntObjectMap<List<SzcoreEvent>> baseBeatEvents = szcore.getScoreBaseBeatEvents(transportId);
        removeExistingBeatTransportEvents(beatId, transportId, baseBeatEvents, MusicEventType.TEMPO_CHANGE);
        szcore.addScoreBaseBeatEvent(transportId, tempoChangeEvent);
    }

    public void addOneOffTempoChangeEvent(Tempo tempo, BeatId beatId, String destination, Id transportId) {
        TempoChangeEvent tempoChangeEvent = createTempoChangeEvent(tempo, beatId, destination, transportId);
        TIntObjectMap<List<SzcoreEvent>> oneOffBeatEvents = szcore.getOneOffBaseBeatEvents(transportId);
        removeExistingBeatTransportEvents(beatId, transportId, oneOffBeatEvents, MusicEventType.TEMPO_CHANGE);
        szcore.addOneOffBaseBeatEvent(transportId, tempoChangeEvent);
    }

    public TempoChangeEvent createTempoChangeEvent(Tempo tempo, BeatId beatId, String destination, Id transportId) {
        long now = clock.getSystemTimeMillis();
        List<OscStaveTempoEvent> oscEvents = new ArrayList<>();
        OscStaveTempoEvent oscTempoEvent = eventFactory.createOscStaveTempoEvent(destination, tempo.getBpm(), now);
        oscEvents.add(oscTempoEvent);
        return eventFactory.createTempoChangeEvent(tempo, beatId, transportId, oscEvents, now);
    }

    public TransportPositionEvent createTransportPositionEvent(Id transportId, int startBaseBeatNo, int transportBeatNo, int tickNo, long positionMillis) {
        long now = clock.getSystemTimeMillis();
        return eventFactory.createTransportPositionEvent(transportId, startBaseBeatNo, transportBeatNo, tickNo, positionMillis, now);
    }

    public void removeExistingBeatTransportEvents(BeatId beatId, Id transportId, TIntObjectMap<List<SzcoreEvent>> baseBeatEvents, MusicEventType type) {
        if (beatId == null) {
            return;
        }
        List<SzcoreEvent> scoreBeatEvents = baseBeatEvents.get(beatId.getBaseBeatNo());
        if (scoreBeatEvents == null) {
            return;
        }

        int toRemove = -1;
        for (int i = 0; i < scoreBeatEvents.size(); i++) {
            SzcoreEvent event = scoreBeatEvents.get(i);
            if (!(EventType.MUSIC == event.getEventType())) {
                continue;
            }
            MusicEvent musicEvent = (MusicEvent) event;
            if (!(type == musicEvent.getMusicEventType())) {
                continue;
            }
            TransportEvent existing = (TransportEvent) musicEvent;
            if (existing.getTransportId().equals(transportId)) {
                LOG.debug("Already have transport event " + type + ", removing event: " + existing);
                toRemove = i;
            }
        }
        if (toRemove >= 0) {
            scoreBeatEvents.remove(toRemove);
        }
    }

    public void addTimeSigChangeEvent(TimeSignature timeSignature, BeatId beatId, Id transportId) {
        TimeSigChangeEvent timeSigChangeEvent = eventFactory.createTimeSigChangeEvent(timeSignature, beatId, transportId, clock.getSystemTimeMillis());
        TIntObjectMap<List<SzcoreEvent>> baseBeatEvents = szcore.getScoreBaseBeatEvents(transportId);
        removeExistingBeatTransportEvents(beatId, transportId, baseBeatEvents, MusicEventType.TIMESIG_CHANGE);
        szcore.addScoreBaseBeatEvent(transportId, timeSigChangeEvent);
    }

    public void addStopEvent(BeatId lastBeat, Id transportId) {
        long now = clock.getSystemTimeMillis();
        StopEvent stopEvent = eventFactory.createStopEvent(lastBeat, transportId, now);
        szcore.addScoreBaseBeatEvent(transportId, stopEvent);
        LOG.debug("Added stop event: " + stopEvent);
    }

    public void addOneOffStopEvent(BeatId lastBeat, Id transportId) {
        long now = clock.getSystemTimeMillis();
        removeOneOffStopEvents(transportId, lastBeat.getBaseBeatNo());
        StopEvent stopEvent = eventFactory.createStopEvent(lastBeat, transportId, now);
        szcore.addOneOffBaseBeatEvent(transportId, stopEvent);
        LOG.debug("Added one off stop event: " + stopEvent);
    }

    public void addPrepStaveChangeEvent(BeatId executeOnBaseBeat, BeatId activateOnBaseBeat, BeatId deactivateOnBaseBeat, BeatId pageChangeOnBaseBeat, PageId nextPageId, Id transportId) {
        PrepStaveChangeEvent event = eventFactory.createPrepStaveChangeEvent(executeOnBaseBeat, activateOnBaseBeat, deactivateOnBaseBeat, pageChangeOnBaseBeat, nextPageId, clock.getSystemTimeMillis());
        szcore.addScoreBaseBeatEvent(transportId, event);
    }

    public void addActiveStaveChangeEvent(StaveId staveId, boolean isActive, boolean isPlayStave, BeatId changeOnBaseBeat, Id transportId, Instrument instrument, List<SzcoreEvent> initEvents) {

        StaveActiveChangeEvent activeStaveChangeEvent = createActiveStaveChangeEvent(staveId, isActive, isPlayStave, changeOnBaseBeat, instrument);

        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transportId, activeStaveChangeEvent);
        } else {
            initEvents.add(activeStaveChangeEvent);
        }

    }

    private StaveActiveChangeEvent createActiveStaveChangeEvent(StaveId staveId, boolean isActive, boolean isPlayStave, BeatId changeOnBaseBeat, Instrument instrument) {
        String destination = szcore.getOscDestination(instrument.getId());
        StaveActiveChangeEvent activeStaveChangeEvent = eventFactory.createActiveStaveChangeEvent(staveId, isActive, isPlayStave, changeOnBaseBeat, destination, clock.getSystemTimeMillis());
        OscStaveActivateEvent oscStaveActivateEvent = activeStaveChangeEvent.getOscStaveActivateEvent();
        List<Object> args = oscStaveActivateEvent.getArguments();
        Stave stave = szcore.getStave(staveId);
        String staveAddress = stave.getOscAddress();
        String jsAddr = Consts.QUOTE + staveAddress + Consts.QUOTE;
        String jsCommand;
        if (isActive) {
            jsCommand = Consts.OSC_JS_ACTIVATE.replace(Consts.ADDR_TOKEN, jsAddr);
        } else {
            jsCommand = Consts.OSC_JS_DEACTIVATE.replace(Consts.ADDR_TOKEN, jsAddr);
        }

        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

        return activeStaveChangeEvent;
    }

    public void addOneOffActiveStaveChangeEvent(StaveId staveId, boolean isActive, boolean isPlayStave, BeatId changeOnBaseBeat, Id transportId, Instrument instrument) {
        StaveActiveChangeEvent activeStaveChangeEvent = createActiveStaveChangeEvent(staveId, isActive, isPlayStave, changeOnBaseBeat, instrument);
        szcore.addOneOffBaseBeatEvent(transportId, activeStaveChangeEvent);
    }

    public void addPrecountSetupEvent(int precountBeatNo, long precountTimeMillis,
                                      long initBeaterInterval, Id transportId, List<SzcoreEvent> initEvents) {
        PrecountBeatSetupEvent activeStaveChangeEvent = eventFactory.createPrecountBeatSetupEvent(precountBeatNo,
                precountTimeMillis, initBeaterInterval, transportId, clock.getSystemTimeMillis());
        initEvents.add(activeStaveChangeEvent);
    }

    //TODO FIX this needs to be dynamicly set
    private OSCPortOut getInstrumentOscPort() {
        try {
            InetAddress address;
            if (instrumentPortRequestCount == 0) {
                address = InetAddress.getLocalHost();
            } else {
                address = null;
            }
            instrumentPortRequestCount++;
            int remotePort = Consts.DEFAULT_OSC_PORT;
            return new OSCPortOut(address, remotePort);
        } catch (Exception e) {
            LOG.error("Failed to create OSCPort", e);
        }
        return null;
    }

    @Override
    public void play() throws Exception {
        LOG.info("##### Play");
        if (szcore == null) {
            LOG.error("Invalid Score NULL");
            return;
        }

        if (!isReadyToPlay()) {
            throw new Exception("System not ready to play. Check score instrumentation and position");
        }

        Collection<Transport> transports = szcore.getTransports();

        for (Transport transport : transports) {
            transport.start();
        }

        LOG.info("Started all transports");
    }

    @Override
    public void stop() {
        LOG.info("##### Stopping Play");
        if (szcore == null) {
            LOG.error("Invalid Score NULL");
            return;
        }

        Collection<Transport> transports = szcore.getTransports();
        for (Transport transport : transports) {
            transport.stop();
        }

        sendStopToClients();

        resetScore();

        LOG.info("Stopped play");
    }

    private void resetScore() {
        szcore.reset();
        scheduler.resetScheduledTasks();
    }

    private void sendStopToClients() {
        if (szcore == null) {
            return;
        }

        OscStopEvent oscStopEvent = eventFactory.createOscStopEvent(Consts.ALL_DESTINATIONS, clock.getSystemTimeMillis());
        publishOscEvent(oscStopEvent);

        WebAudienceStopEvent webStopEvent = eventFactory.createWebAudienceStopEvent(clock.getSystemTimeMillis());
        process(webStopEvent);

        ScoreInfoEvent clientStop = eventFactory.createScoreInfoEvent(getScore().getId(), true, null, clock.getSystemTimeMillis());
        sendClientEvent(clientStop);
    }

    private ModWindowEvent createModWindowEvent(BeatId beatId, Page nextPage, PageId currentPageId, Stave stave, boolean isOpen) {
        return eventFactory.createModWindowEvent(beatId, nextPage, currentPageId, stave, isOpen, clock.getSystemTimeMillis());
    }

    private OscEvent createDisplayPageEvent(PageId pageId, String pageName, PageId rndPageId, BasicStave stave) {
        if (pageName == null || stave == null) {
            return null;
        }

        StaveId staveId = stave.getId();
        String address = stave.getOscAddress();
        String filename = Consts.RSRC_DIR + pageName + Consts.PNG_FILE_EXTENSION;
        ArrayList<Object> args = new ArrayList<>();
        args.add(Consts.OSC_INSCORE_SET);
        args.add(Consts.OSC_INSCORE_FILE);
        args.add(filename);

        String destination = szcore.getOscDestination(staveId.getInstrumentId());
//        LOG.info("createDisplayPageEvent: pageName: {} destination: {} staveId: {} stave address: {}", pageName, destination, staveId.getStaveNo(), address);

        return eventFactory.createPageDisplayEvent(pageId, rndPageId, filename, staveId, address, args, null, destination, clock.getSystemTimeMillis());

    }

    private OscEvent createBeatScriptEvent(Script script, BeatId eventBeatId) {
        if (script == null || eventBeatId == null) {
            return null;
        }

        String destination = szcore.getOscDestination(eventBeatId.getInstrumentId());
        BeatScriptEvent beatScriptEvent = eventFactory.createBeatScriptEvent(destination, eventBeatId, clock.getSystemTimeMillis());
        beatScriptEvent.addCommandArg(script.getContent());

        return beatScriptEvent;
    }

    private OscEvent createOscBeatScriptEvent(Script script, BeatId eventBeatId) {
        if (script == null || eventBeatId == null) {
            return null;
        }

        if (!(script instanceof OscScript)) {
            return null;
        }

        OscScript oscScript = (OscScript) script;

        String destination = szcore.getOscDestination(eventBeatId.getInstrumentId());
        return eventFactory.createOscScriptEvent(destination, eventBeatId, oscScript.getTarget(), oscScript.getArgs(), clock.getSystemTimeMillis());
    }

    private TransitionEvent createTransitionEvent(Transition transition, BeatId eventBeatId) {
        if (transition == null || eventBeatId == null) {
            return null;
        }

        String destination = szcore.getOscDestination(eventBeatId.getInstrumentId());
        return eventFactory.createTransitionEvent(destination, eventBeatId, transition, clock.getSystemTimeMillis());
    }

    private WebAudienceEvent createWebAudienceEvent(List<WebAudienceScoreScript> scripts, BeatId eventBeatId) {
        if (scripts == null || eventBeatId == null) {
            return null;
        }

        return eventFactory.createWebAudienceEvent(eventBeatId, scripts, clock.getSystemTimeMillis());
    }

    private ScriptingEngineEvent createScriptingEngineEvent(List<ScriptingEngineScript> scripts, BeatId eventBeatId) {
        if (scripts == null || eventBeatId == null) {
            return null;
        }

        return eventFactory.createScriptingEngineEvent(eventBeatId, scripts, clock.getSystemTimeMillis());
    }

    private OscEvent createResetScoreEvent(String instrument) {
        if (instrument == null) {
            return null;
        }

        return eventFactory.createResetScoreEvent(instrument, clock.getSystemTimeMillis());
    }

    private OscEvent createResetInstrumentEvent(String instrument) {
        if (instrument == null) {
            return null;
        }

        return eventFactory.createResetInstrumentEvent(instrument, clock.getSystemTimeMillis());
    }

    private WebAudienceResetEvent createWebAudienceResetEvent(BeatId beatId) {
        if (beatId == null) {
            return null;
        }

        List<WebAudienceScoreScript> scripts = webAudienceScoreProcessor.getBeatResetScripts(beatId);
        LOG.debug("createWebAudienceResetEvent: beat: {} scripts {}", beatId.getBeatNo(), scripts);
        if (scripts == null || scripts.isEmpty()) {
            return null;
        }
        return eventFactory.createWebAudienceResetEvent(beatId, scripts, clock.getSystemTimeMillis());
    }

    private ScriptingEngineResetEvent createScriptingEngineResetEvent(BeatId beatId) {
        if (beatId == null) {
            return null;
        }

        List<ScriptingEngineScript> scripts = scriptingEngine.getBeatResetScripts(beatId);
        LOG.debug("createWebAudienceResetEvent: beat: {} scripts {}", beatId.getBeatNo(), scripts);
        if (scripts == null || scripts.isEmpty()) {
            return null;
        }
        return eventFactory.createScriptingEngineResetEvent(beatId, scripts, clock.getSystemTimeMillis());
    }

    private PageMapDisplayEvent createPageMapFileEvent(PageId pageId, StaveId staveId, String pageName, String address, List<InscoreMapElement> mapElements, String destination) {
        if (pageName == null || address == null) {
            return null;
        }

        String mapFilename = Consts.RSRC_DIR + pageName + Consts.INSCORE_FILE_SUFFIX + Consts.TXT_FILE_EXTENSION;
        ArrayList<Object> mapargs = new ArrayList<>();
        mapargs.add(Consts.OSC_INSCORE_MAPF);
        mapargs.add(mapFilename);

        return eventFactory.createPageMapDisplayEvent(pageId, staveId, address, mapargs, mapElements, null, destination, clock.getSystemTimeMillis());

    }

    private PageMapDisplayEvent createPageMapEvent(PageId pageId, StaveId staveId, String pageName, String address, String destination, String inscoreMap, List<InscoreMapElement> mapElements) {
        if (pageName == null || address == null) {
            return null;
        }

        ArrayList<Object> mapargs = new ArrayList<>();
        mapargs.add(Consts.OSC_INSCORE_MAP);
        mapargs.add(inscoreMap);

        return eventFactory.createPageMapDisplayEvent(pageId, staveId, address, mapargs, mapElements, null, destination, clock.getSystemTimeMillis());
    }

    private OscEvent createInstrumentSlotsEvent(String destination, String instrumentsCsv) {
        if (instrumentsCsv == null || instrumentsCsv.length() < 1) {
            return null;
        }
        return eventFactory.createInstrumentSlotsEvent(instrumentsCsv, destination, clock.getSystemTimeMillis(), null);
    }

    public OscEvent createInstrumentResetSlotsEvent(String destination) {
        return eventFactory.createResetInstrumentSlotsEvent(destination, clock.getSystemTimeMillis(), null);
    }

    private void resetClients() {
        if (szcore == null) {
            return;
        }

        LOG.info("Resetting clients ...");
        Collection<Instrument> instruments = szcore.getInstruments();

        for (Instrument instrument : instruments) {
            publishOscEvent(createResetScoreEvent(instrument.getName()));
        }
    }

    @Override
    public void setPosition(long millis) {
        LOG.debug("setPosition time: " + millis);
        if (isInitDone) {
            resetTasks();
            resetScore();
        }

        isInitDone = false;
        Collection<Transport> transports = szcore.getTransports();
        Transport transport = transports.iterator().next();

        BeatId beatId = szcore.getBeatForTime(millis, transport.getId());
        if (beatId == null) {
            LOG.error("Failed to find beat for time: " + millis);
            return;
        }

        Beat startBeat = szcore.getBeat(beatId);
        if (startBeat == null) {
            LOG.error("Invalid start beat NULL ");
            return;
        }

        Beat upbeat = szcore.getUpbeat(beatId);
        if (upbeat != null) {
            startBeat = upbeat;
        }

        BeatId startBeatId = startBeat.getBeatId();

        long startMillis = szcore.getBeatTime(startBeatId);

        scheduler.setElapsedTimeMillis(startMillis);
        if (szcore.isPrecount()) {
            scheduler.setPrecountTimeMillis(szcore.getPrecountMillis());
        }

        this.startBaseBeat = startBeatId.getBaseBeatNo();

        processStrategyOnPosition(startBeatId);

        initEvents = createRequiredEventsForNewPosition(beatId.getBaseBeatNo());

        processInitEvents(initEvents);
    }

    private void processStrategyOnPosition(BeatId startBeatId) {
        if(startBeatId == null) {
            return;
        }
        PageId startPageId = (PageId)startBeatId.getPageId();
        ScoreBuilderStrategy strategy = szcore.getScoreBuilderStrategy();
        if (strategy == null || !strategy.isActive() || !strategy.isReady()) {
            return;
        }
        String currentSection = strategy.getCurrentSection();
        List<String> pageSections = strategy.getSectionsForPageNo(startPageId.getPageNo());
        if(!pageSections.contains(currentSection)) {
            currentSection = pageSections.get(0);
            strategy.setCurrentSection(currentSection);
        }
        Map<String, List<String>> instrumentClients = strategy.getInstrumentClientsMap(currentSection);
        webScore.setSectionInstrumentClients(instrumentClients);
    }

    private void resetTasks() {
        scheduler.resetScheduledTasks();
        initEvents.clear();
    }

    public List<SzcoreEvent> createRequiredEventsForNewPosition(int beatNo) {
        List<SzcoreEvent> initEvents = new ArrayList<>();
        if (szcore == null) {
            LOG.error("Invalid NULL score");
            return initEvents;
        }
        Collection<Instrument> instruments = szcore.getInstruments();
        List<Id> transportIds = new ArrayList<>();
        Instrument lastInstrument = null;
        for (Instrument instrument : instruments) {
            InstrumentId instrumentId = (InstrumentId) instrument.getId();
            boolean isAudioOrVideo = instrument.isAv();
            boolean isScoreInstrument = !isAudioOrVideo;
            Transport transport = szcore.getInstrumentTransport(instrumentId);
            Id transportId = transport.getId();
            BeatId instrumentBeatId = szcore.getInstrumentBeatIds(transport.getId(), instrumentId, beatNo);
            if (instrumentBeatId == null) {
                continue;
            }

            Beat startBeat = szcore.getBeat(instrumentBeatId);
            int startBaseBeatNo = startBeat.getBaseBeatUnitsNoAtStart();

            Beat upbeat = szcore.getUpbeat(instrumentBeatId);
            if (upbeat != null) {
                startBeat = upbeat;
            }
            BeatId startBeatId = startBeat.getBeatId();
            int upbeatBaseBeatNo = startBeat.getBaseBeatUnitsNoAtStart();
            long startMillis = szcore.getBeatTime(startBeatId);

            InstrumentBeatTracker instrumentBeatTracker = instrumentBeatTrackers.get(instrumentId);
            if (instrumentBeatTracker == null) {
                instrumentBeatTracker = new InstrumentBeatTracker(transport, instrumentId);
                instrumentBeatTrackers.put(instrumentId, instrumentBeatTracker);
            }
            instrumentBeatTracker.setCurrentBeat(startBeat);

            Bar bar = szcore.getBar(instrumentBeatId.getBarId());
            if (bar == null) {
                continue;
            }

            Tempo tempo = bar.getTempo();
            TempoModifier tempoModifier = transportTempoModifiers.get(transportId);
            if (tempoModifier != null) {
                tempo = new TempoImpl(tempo, tempoModifier);
            }
            TimeSignature timeSignature = bar.getTimeSignature();
            Page page = szcore.getPage(bar.getPageId());

            BasicStave currentStave = null;
            BasicStave nextStave = null;
            if (isScoreInstrument) {
                List<Stave> instrumentStaves = szcore.getInstrumentStaves(instrumentId);
                int minStaveNo = Integer.MAX_VALUE;
                for (Stave stave : instrumentStaves) {
                    BasicStave bs = (BasicStave) stave;
                    if (bs.getId().getStaveNo() < minStaveNo) {
                        if (currentStave != null) {
                            nextStave = currentStave;
                        }
                        currentStave = bs;
                        minStaveNo = currentStave.getId().getStaveNo();
                    } else {
                        nextStave = bs;
                    }
                }

                if(currentStave != null) {
                    currentStave.setActive(true);
                }
                if(nextStave != null) {
                    nextStave.setActive(false);
                }
            }

            // add one off events, not instrument specific
            if (!transportIds.contains(transportId)) {
                //set beatNo -1 as transport increments on startup
                LOG.debug("Adding transport and time start events, upbeatBaseBeatNo: " + (upbeatBaseBeatNo - 1) + "  startBeatId: " + startBeatId);
                int transportStartBeat = upbeatBaseBeatNo - 1;
                int transportStartTick = 0;
                addTransportInitEvents(tempo, timeSignature, upbeatBaseBeatNo, transportStartBeat, transportStartTick, startMillis, transportId, initEvents);
                //add start tempo
                addOneOffTempoChangeEvent(tempo, startBeatId, Consts.ALL_DESTINATIONS, transport.getId());
                transportIds.add(transportId);
            }

            if (isScoreInstrument && currentStave != null && nextStave != null) {
                addInitWebScoreEvent(transport, startBeatId, currentStave.getStaveId());
                addNewPageEvents(szcore.getBlankPage(), currentStave, transportId, initEvents);
                addNewPageEvents(szcore.getBlankPage(), nextStave, transportId, initEvents);

                addPageInitEvents(transport, instrument, currentStave, nextStave, page, initEvents);
                addInitClockBaseBeatEvent(currentStave, upbeatBaseBeatNo, initEvents);
                addInitStaveStartMarkEvent(currentStave, startBaseBeatNo, initEvents);
                addInitStaveDyEvent(currentStave, initEvents);
                addNewPageInstrumentEvents(instrument, initEvents);

                boolean isCloseOnMidPage = false;
                ScoreRandomisationStrategy rndStrategy = szcore.getRandomisationStrategy();
                if(rndStrategy != null && rndStrategy.isActive()) {
                    boolean isInRndRange = rndStrategy.isInActiveRange(instrumentId, page);
                    if (isInRndRange) {
                        isCloseOnMidPage = true;
                        if (rndStrategy.isPageRecalcTime()) {
                            recalcRndStrategy(rndStrategy, page);
                        }
                        Page rndPage = rndStrategy.getRandomPageFileName(instrumentId);
                        String pageFileName;
                        PageId rndPageId;
                        if (rndPage == null) {
                            pageFileName = page.getFileName();
                            rndPageId = null;
                            LOG.debug("createRequiredEventsForNewPosition: Invalid random page file name, using: {}", pageFileName);
                        } else {
                            pageFileName = rndPage.getFileName();
                            rndPageId = rndPage.getPageId();
                            LOG.info("createRequiredEventsForNewPosition: Using random page file name: {} for instrument: {} page: {}", pageFileName, instrumentId, page.getPageNo());
                        }
                        LOG.debug("createRequiredEventsForNewPosition: page: {} for instrument: {}: stave: {}", pageFileName, currentStave.getStaveId().getInstrumentId(), currentStave.getStaveId().getStaveNo());
                        List<OscEvent> pageChangeEvents = createPageChangeEvents(page, pageFileName, rndPageId, currentStave);
                        initEvents.addAll(pageChangeEvents);
                    }
                }
                ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
                if(builderStrategy != null && builderStrategy.isActive()) {
                    isCloseOnMidPage = true;
                }

                BeatId firstBeat = page.getFirstBeat().getBeatId();
                BeatId activatePageBeat = szcore.getOffsetBeat(firstBeat, 1).getBeatId();
                BeatId pageChangeBeat = szcore.getOffsetBeat(activatePageBeat, 3).getBeatId();
                Page nextPage = szcore.getNextPage(page.getPageId());
                if (nextPage != null) {
                    addOneOffNewPageEvents(nextPage, isCloseOnMidPage, nextStave, pageChangeBeat, activatePageBeat, transportId);
                }
            } else {
                addInitAvEvent(transport, instrument, initEvents, startBeatId);
                addInitWebAudienceEvent(transport, initEvents, startBeatId);
                addInitScriptingEvent(transport, initEvents, startBeatId);
            }
            if(isScoreInstrument) {
                lastInstrument = instrument;
            }
        }

        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if (builderStrategy != null && builderStrategy.isActive()) {
            ScoreSectionInfoEvent sectionInfoEvent = createSectionInfoEvent(builderStrategy);
            if (sectionInfoEvent != null) {
                initEvents.add(sectionInfoEvent);
            }
            if (lastInstrument != null) {
                prepareSectionEvents(builderStrategy, lastInstrument, szcore.getInstrumentTransport(lastInstrument.getId()));
            }
        }

        return initEvents;
    }

    public void recalcRndStrategy(ScoreRandomisationStrategy strategy, Page page) {
        if (strategy == null || page == null) {
            return;
        }
        strategy.recalcStrategy(page);
        int pageQuantity = strategy.getNumberOfRequiredPages();
        UnionRoseWebAudienceProcessor audienceProcessor = (UnionRoseWebAudienceProcessor) getWebAudienceProcessor();
        List<Integer> pageIds = audienceProcessor.prepareNextTilesToPlay(pageQuantity);
        strategy.setPageSelection(pageIds);
    }

    public void processRndStrategyOnModClose(ScoreRandomisationStrategy strategy) {
        if (strategy == null) {
            return;
        }
        int pageQuantity = strategy.getNumberOfRequiredPages();
        LOG.debug("processRndStrategyOnModClose: pageQuantity {} ", pageQuantity);
        UnionRoseWebAudienceProcessor urWebAudienceScore = (UnionRoseWebAudienceProcessor) getWebAudienceProcessor();
        List<Integer> selectedPageIds = urWebAudienceScore.prepareNextTilesToPlay(pageQuantity);
        strategy.setPageSelection(selectedPageIds);
    }

    @Override
    public Score getScore() {
        return szcore;
    }

    public BasicScore getBasicScore() {
        return szcore;
    }

    public void setScore(BasicScore score) {
        this.szcore = score;
    }

    @Override
    public void reset() throws Exception {
        if (scheduler.isActive()) {
            LOG.warn("Scheduler is active, can not perform reset");
            throw new Exception("Scheduler is active, can not perform reset");
        }

        isInitDone = false;
        resetClients();
        if (szcore != null) {
            for (Instrument instrument : szcore.getInstruments()) {
                oscPublisher.removeDestination(instrument.getName());
            }
        }
        szcore = null;
        isScoreLoaded = false;
        Transport transport = getTransportFactory().getTransport(Consts.DEFAULT_TRANSPORT_NAME);
        transport.reset();
        scheduler.reset();
    }

    @Override
    public void subscribe(SzcoreEngineEventListener eventListener) {
        LOG.warn("Unexpected call: subscribe SzcoreEngineEventListener");
    }

    @Override
    public void subscribe(WebAudienceStateListener eventListener) {
        LOG.warn("Unexpected call: subscribe WebAudienceStateListener");
    }

    @Override
    public void setTempoModifier(Id transportId, TempoModifier tempoModifier) {
        if (transportId == null || tempoModifier == null) {
            return;
        }
        transportTempoModifiers.put(transportId, tempoModifier);
        LOG.debug("Received tempo modifier: " + tempoModifier);
        Transport transport = szcore.getTransport(transportId);
        Tempo currentTempo = transport.getTempo();
        TempoModifier currentModifier = currentTempo.getTempoModifier();
        if (tempoModifier.equals(currentModifier)) {
            return;
        }

        Tempo newTempo = new TempoImpl(currentTempo, tempoModifier);

        TempoChangeEvent tempoChangeEvent = createTempoChangeEvent(newTempo, null, Consts.ALL_DESTINATIONS, transportId);
        process(tempoChangeEvent);
    }

    @Override
    public void setRandomisationStrategy(List<Integer> randomisationStrategy) {
        if (randomisationStrategy == null) {
            return;
        }
        szcore.setRandomisationStrategy(randomisationStrategy);
    }

    @Override
    public void usePageRandomisation(Boolean value) {
        LOG.debug("usePageRandomisation: {} ", value);
        szcore.setRandomizeContinuousPageContent(value);
    }

    @Override
    public void useContinuousPageChange(Boolean value) {
        LOG.debug("useContinuousPageChange: {} ", value);
        szcore.setUseContinuousPage(value);
    }

    @Override
    public void setOverlayValue(OverlayType type, long value, List<Id> instrumentIds) {
        overlayProcessor.setOverlayValue(type, value, instrumentIds);
    }

    @Override
    public void setOverlayText(OverlayType type, String l1, String l2, String l3, boolean isVisible, List<Id> instrumentIds) {
        overlayProcessor.setOverlayText(type, l1, l2, l3, isVisible, instrumentIds);
    }

    @Override
    public void onUseOverlayLine(OverlayType type, Boolean value, List<Id> instrumentIds) {
        overlayProcessor.onUseOverlayLine(type, value, instrumentIds);
    }

    @Override
    public void onUseOverlay(OverlayType type, Boolean value, int alpha, List<Id> instrumentIds) {
        overlayProcessor.onUseOverlay(type, value, alpha, instrumentIds);
    }

    @Override
    public void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent) {

        IncomingWebAudienceEventType type = webEvent.getWebEventType();
        switch (type) {
            case ELEMENT_SELECTED:
                processElementSelected((ElementSelectedAudienceEvent) webEvent);
                break;
            case WEB_START:
                processWebAudienceStart();
                break;
            case VOTE:
                processVote((VoteAudienceEvent) webEvent);
                break;
            default:
                LOG.info("onIncomingWebAudienceEvent: unknown IncomingWebAudienceEventType: {}", type);
        }
    }

    @Override
    public void onIncomingWebScoreEvent(WebScoreInEvent webEvent) {
        if (webScore == null) {
            return;
        }
        webScore.processInEvent(webEvent);
        WebScoreInEventType type = webEvent.getWebScoreEventType();
        switch (type) {
            case CONNECTION:
                webScore.processConnectionEvent((WebScoreConnectionEvent) webEvent);
                break;
            case CONNECTIONS_REMOVE:
                webScore.processRemoveConnectionEvent((WebScoreRemoveConnectionEvent) webEvent);
                break;
            case PART_REG:
                webScore.processPartRegistration((WebScorePartRegEvent) webEvent);
                break;
            case PART_READY:
                webScore.processPartReady((WebScorePartReadyEvent) webEvent);
                break;
            case SELECT_ISLOT:
                webScore.processSelectInstrumentSlot((WebScoreSelectInstrumentSlotEvent) webEvent);
                break;
            case SELECT_SECTION:
                webScore.processSelectSection((WebScoreSelectSectionEvent) webEvent);
                break;
            default:
                LOG.info("onIncomingWebScoreEvent: unknown IncomingWebAudienceEventType: {}", type);
        }
    }

    @Override
    public void onWebAudienceStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport) {
        notifyListeners(webAudienceScoreStateExport);
    }

    @Override
    public void onWebAudienceStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport) {
        notifyListeners(webAudienceScoreStateDeltaExport);
    }

    @Override
    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) {
        latencyCompensator.process(webEvent);
//        publishWebEvent(webEvent);
    }

    protected void processVote(VoteAudienceEvent webEvent) {
        LOG.debug("processVote: ");
    }

    public void processElementSelected(ElementSelectedAudienceEvent webEvent) {
        LOG.debug("processElementSelected: ");
        UnionRoseWebAudienceProcessor urWebAudienceScore = (UnionRoseWebAudienceProcessor) webAudienceScoreProcessor;
        String elementId = webEvent.getElementId();
        boolean isSelected = webEvent.isSelected();
        if (webAudienceScoreProcessor == null) {
            return;
        }
        urWebAudienceScore.setSelectedElement(elementId, isSelected);
    }

    public void processWebAudienceStart() {
        LOG.debug("processWebStart: ");
        webAudienceScoreProcessor.resetState();
        webAudienceScoreProcessor.pushServerState();
        webAudienceScoreProcessor.startScore();
    }

    public boolean isReadyToPlay() {
        return isScoreLoaded && isInitDone && szcore.isReady() && webScore.isReady();
    }

    @Override
    public void process(SzcoreEvent event) {
        process(event, 0);
    }

    public void process(SzcoreEvent event, int beatNo) {
        process(event, beatNo, 0);
    }

    public void process(SzcoreEvent event, int beatNo, int tickNo) {

        EventType type = event.getEventType();

        switch (type) {
            case OSC:
                processOscEvent((OscEvent) event, beatNo);
                break;
            case MUSIC:
                processMusicEvent((MusicEvent) event, beatNo);
                break;
            case WEB_AUDIENCE:
                processWebAudienceEvent((WebAudienceEvent) event);
                break;
            case SCRIPTING_ENGINE:
                processScriptingEngineEvent((ScriptingEngineEvent) event);
                break;
            case ADMIN_OUT:
                //do nothing
                break;
            case WEB_SCORE_OUT:
                processWebScoreOutEvent((OutgoingWebEvent)event);
                break;
            case ADMIN_IN:
                processClientInEvent((ClientInEvent)event);
                break;
            case DELAYED_WEB_OUT:
                processDelayedWebOutEvent((DelayedOutgoingWebEvent)event);
                break;
            default:
                LOG.error("Unknown event type " + type);
        }

        notifyListeners(event, beatNo, tickNo);
    }

    private void notifyListeners(SzcoreEvent event, int beatNo, int tickNo) {
        parentProcessor.notifyListeners(event, beatNo, tickNo);
    }

    private void notifyListeners(WebAudienceScoreStateExport webAudienceScoreStateExport) {
        parentProcessor.notifyListeners(webAudienceScoreStateExport);
    }

    private void notifyListeners(WebAudienceScoreStateDeltaExport deltaExport) {
        parentProcessor.notifyListeners(deltaExport);
    }

    private void notifyListenersOnBeat(Id transportId, int beatNo, int baseBeatNo) {
        parentProcessor.notifyListenersOnBeat(transportId, beatNo, baseBeatNo);
    }

    private void notifyListenersOnTempoChange(Id transportId, Tempo tempo) {
        parentProcessor.notifyListenersOnTempoChange(transportId, tempo);
    }

    private void notifyListenersOnTransportPositionChange(Id transportId, int beatNo) {
        parentProcessor.notifyListenersOnTransportPositionChange(transportId, beatNo);
    }

    private void processScriptingEngineEvent(ScriptingEngineEvent event) {
        ScriptingEngineEventTask task = taskFactory.createScriptingEngineEventTask(0, event, scriptingEngine);
        scheduleTask(task);
    }

    private void processWebScoreOutEvent(OutgoingWebEvent event) {
        try {
            onOutgoingWebEvent(event);
        } catch (Exception e) {
            LOG.error("processWebScoreOutEvent: failed to send webscore event {}", event, e);
        }
    }

    private void processDelayedWebOutEvent(DelayedOutgoingWebEvent event) {
        if(event == null || event.getOutgoingWebEvent() == null) {
            return;
        }
        try {
            latencyCompensator.process(event);
        } catch (Exception e) {
            LOG.error("processDelayedWebOutEvent: failed to send delayed web event {}", event, e);
        }
    }

    private void processClientInEvent(ClientInEvent event) {
        try {
            ClientInEventType clientInEventType = event.getClientEventType();
            switch (clientInEventType) {
                case STRATEGY:
                    processStrategyEvent((StrategyEvent)event);
                    break;
            }

        } catch (Exception e) {
            LOG.error("processWebScoreOutEvent: failed to send webscore event {}", event, e);
        }

    }

    private void processStrategyEvent(StrategyEvent event) throws Exception {
        StrategyEventType strategyType = event.getStrategyEventType();
        switch (strategyType) {
            case ASSIGN_OWNERS_RND:
                assignStrategyOwnersRandom();
                break;
            case RESET:
                resetStrategy();
                break;
            case SET_SECTION:
                setSectionName(event.getSectionName());
                break;
            case SHUFFLE_ORDER:
                shuffleSectionOrder();
                break;
            default:
                LOG.error("processStrategyEvent: unknown event type: {}", strategyType);
        }
    }

    private void shuffleSectionOrder() {
        ScoreBuilderStrategy strategy = szcore.getScoreBuilderStrategy();
        if(strategy == null) {
            return;
        }
        strategy.shuffleSectionOrder();
        updateClients(strategy);
    }

    private void setSectionName(String sectionName) {
        if(sectionName == null) {
            return;
        }
        ScoreBuilderStrategy strategy = szcore.getScoreBuilderStrategy();
        if(strategy == null) {
            return;
        }
        strategy.setCurrentSection(sectionName);
    }

    protected void resetStrategy() throws Exception {
        ScoreBuilderStrategy strategy = szcore.getScoreBuilderStrategy();
        if(strategy == null) {
            return;
        }
        strategy.reset();
        strategy.init();
        webScore.createOrUpdateWebBuilderStrategy(strategy);
        webScore.sendStrategyInfo();
    }

    protected void assignStrategyOwnersRandom() throws Exception {
        ScoreBuilderStrategy strategy = szcore.getScoreBuilderStrategy();
        if(strategy == null) {
            return;
        }
        List<WebClientInfo> scoreClients = webScore.getScoreClients();
        if(scoreClients == null || scoreClients.isEmpty()) {
            return;
        }
        List<String> orphanSections = strategy.getOrphanSections();
        List<String> sectionOrder = strategy.getSectionOrder();
        for(String section : orphanSections) {
            if(sectionOrder.contains(section)) {
                LOG.warn("assignStrategyOwnersRandom: unexpected section {}, already set in sectionOrder, removing ...", section);
                sectionOrder.remove(section);
            }
            int index = MathUtil.getRandomInRange(0, scoreClients.size() - 1);
            WebClientInfo clientInfo = scoreClients.get(index);
            processSelectSection(section, clientInfo);
            if(strategy.isReady()) {
                break;
            }
        }
        webScore.createOrUpdateWebBuilderStrategy(strategy);
        webScore.sendStrategyInfo();
    }

    private void processWebAudienceEvent(WebAudienceEvent event) {
        WebAudienceEventType eventType = event.getWebAudienceEventType();

        switch (eventType) {
            case PRECOUNT:
            case RESET:
            case STOP:
            case PLAY_TILES:
            case SELECT_TILES:
            case INSTRUCTIONS:
            case AUDIO:
            case STATE_UPDATE:
                webAudienceScoreProcessor.processWebAudienceEvent(event);
                break;
            case SCRIPT:
            default:
                WebAudienceEventTask task = taskFactory.createWebAudienceEventTask(0, event, webAudienceScoreProcessor);
                scheduleTask(task);
        }
    }

    private void processMusicEvent(MusicEvent event, int beatNo) {
        MusicEventType type = event.getMusicEventType();

        MusicTask task = null;
        //TODO use playtime
//        long playTime = getBeatPlayTime(event.getEventBaseBeat(), beatNo);
        switch (type) {
            case TEMPO_CHANGE:
                TempoChangeEvent tempoChangeEvent = (TempoChangeEvent) event;
                Transport transport = transportFactory.getTransport(tempoChangeEvent.getTransportId());
                TempoModifier tempoModifier = transportTempoModifiers.get(transport.getId());
                boolean isSchedulerRunning = scheduler.isActive();
                task = taskFactory.createTempoChangeTask(tempoChangeEvent, 0, transport, oscPublisher, this, tempoModifier, isSchedulerRunning);
                break;
            case TIMESIG_CHANGE:
                TimeSigChangeEvent timeSigChangeEvent = (TimeSigChangeEvent) event;
                transport = transportFactory.getTransport(timeSigChangeEvent.getTransportId());
                task = taskFactory.createTimeSigChangeTask(timeSigChangeEvent, 0, transport);
                break;
            case TRANSPORT_POSITION:
                TransportPositionEvent transportPositionEvent = (TransportPositionEvent) event;
                transport = transportFactory.getTransport(transportPositionEvent.getTransportId());
                task = taskFactory.createTransportPositionTask(transportPositionEvent, 0, transport);
                break;
            case STAVE_ACTIVE_CHANGE:
                StaveActiveChangeEvent staveActiveChangeEvent = (StaveActiveChangeEvent) event;
                LOG.debug("Processing StaveActiveChangeEvent beatNo: " + beatNo + " event: " + event);
                StaveId staveId = staveActiveChangeEvent.getStaveId();
                Stave instrumentStave = szcore.getStave(staveId);
                task = taskFactory.createActiveStaveChangeTask(staveActiveChangeEvent, 0, instrumentStave, oscPublisher);
                break;
            case PREP_STAVE_ACTIVE_CHANGE:
                PrepStaveChangeEvent prepStaveChangeEvent = (PrepStaveChangeEvent) event;
                LOG.debug("Processing PrepStaveChangeEvent beatNo: " + beatNo + " startBaseBeat: " + startBaseBeat + " event: " + event);
                if (beatNo == startBaseBeat) {
                    task = null;
                    LOG.debug("Ignoring PrepStaveChangeEvent as its starting position beatNo: " + beatNo + " startBaseBeat: " + startBaseBeat);
                    break;
                }
                task = taskFactory.createPrepStaveChangeTask(prepStaveChangeEvent, 0, this);
                break;
            case PRECOUNT_BEAT_SETUP:
                PrecountBeatSetupEvent precountBeatSetupEvent = (PrecountBeatSetupEvent) event;
                Id transportId = precountBeatSetupEvent.getInstrumentId();
                transport = szcore.getTransport(transportId);
                task = taskFactory.createPrecountBeatSetupTask(precountBeatSetupEvent, Consts.ALL_DESTINATIONS, transport, this, oscPublisher, eventFactory, taskFactory, webAudienceScoreProcessor, clock);
                break;
            case TRANSITION:
                TransitionEvent transitionEvent = (TransitionEvent) event;
                task = taskFactory.createTransitionSetupTask(transitionEvent, transitionEvent.getDestination(), this, oscPublisher, eventFactory, clock);
                break;
            case STOP:
                StopEvent stopEvent = (StopEvent) event;
                long elapsedTime = clock.getElapsedTimeMillis();
                transport = szcore.getTransport(stopEvent.getTransportId());
                int duration = transport.getCurrentBeatDuration();
                long stopTime = elapsedTime + duration;
                LOG.debug("processMusicEvent: STOP time event beat duration: {} elapsedTime: {} stopTime: {}", duration, elapsedTime, stopTime);
                task = taskFactory.createStopPlayTask(stopEvent, stopTime, this);
                break;
            case SCORE_SECTION:
                task = processScoreSectionEvent((ScoreSectionEvent)event);
                break;
            case MOD_WINDOW:
                ModWindowEvent modWindowEvent = (ModWindowEvent) event;
                task = taskFactory.createModWindowTask(modWindowEvent, 0, this);
                break;
            default:
                LOG.error("Unknown event: " + event);

        }
        scheduleTask(task);
    }

    private MusicTask processScoreSectionEvent(ScoreSectionEvent sectionEvent) {
        if(sectionEvent == null) {
            return null;
        }

        ScoreSectionEventType eventType = sectionEvent.getSectionEventType();
        switch (eventType) {
            case START:
                long execTime = clock.getElapsedTimeMillis() + 25;
                return taskFactory.createScoreSectionTask(sectionEvent, execTime, this);
            case STOP:
                long elapsedTime = clock.getElapsedTimeMillis();
                Transport transport = szcore.getTransport(sectionEvent.getTransportId());
                int duration = transport.getCurrentBeatDuration();
                execTime = elapsedTime + duration - 50;
                return taskFactory.createScoreSectionTask(sectionEvent, execTime, this);
        }
        return null;
    }

    private void processOscEvent(OscEvent event, int beatNo) {
        OscEventType type = event.getOscEventType();
        switch (type) {
            case STAVE_CLOCK_TICK:
                processClockTickEvent((StaveClockTickEvent) event);
                break;
            case STAVE_TICK_DY:
                processDyTickEvent((StaveDyTickEvent) event, beatNo);
                break;
            case STAVE_Y_POSITION:
                processStaveYPosition((StaveYPositionEvent) event);
                break;
            case STAVE_DATE_TICK:
            case STAVE_TEMPO:
                processStaveDateTickEvent((StaveDateTickEvent) event, beatNo);
                break;
            case STAVE_START_MARK:
                processStaveDateTickEvent((StaveStartMarkEvent) event, beatNo);
                break;
            case DATE_TICK:
                processDateTickEvent((DateTickEvent) event, beatNo);
                break;
            case BEAT_SCRIPT:
                processBeatScriptEvent((BeatScriptEvent) event);
                break;
            case OSC_SCRIPT:
                processOscScriptEvent((OscScriptEvent) event);
                break;
            case ELEMENT_ALPHA:
            case ELEMENT_COLOR:
            case ELEMENT_Y_POSITION:
            case RESET_INSTRUMENT:
            case RESET_SCORE:
            case RESET_STAVES:
            case INSTRUMENT_SLOTS:
            case INSTRUMENT_RESET_SLOTS:
            case PAGE_DISPLAY:
            case PAGE_MAP_DISPLAY:
            case OVERLAY_TEXT:
            case VOTE:
            case GENERIC:
                publishOscEvent(event);
                break;
            default:
                LOG.error("Unknown OSC Event Type: " + type);

        }
    }


    private void processStaveDateTickEvent(StaveDateTickEvent event, int beatNo) {
        StaveId staveId = event.getStaveId();
        Stave stave = szcore.getStave(staveId);
        if (stave == null) {
            LOG.error("Invalid NULL stave for event: " + event);
            return;
        }

        if (!stave.isActive()) {
            return;
        }

        int beatToSend = beatNo;
        int eventBeat = event.getBeatNo();
        if (eventBeat > 0) {
            beatToSend = eventBeat;
        }

        //TODO Needs more efficient way to set beat argument
        ArrayList<Object> args = (ArrayList<Object>) event.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        String beat = beatToSend + Consts.EIGHTH;
        args.add(1, beat);


        publishOscEvent(event);
    }

    private void processDateTickEvent(DateTickEvent event, int beatNo) {
        StaveId staveId = event.getStaveId();
        Stave stave = szcore.getStave(staveId);
        if(!stave.isActive()) {
            return;
        }
        int beatToSend = beatNo;
        if (beatNo == 0) {
            beatToSend = event.getBeatNo();
        }
        event.setBeatNo(beatToSend);
        event.addCommandArg();
        publishOscEvent(event);
    }


    private void processBeatScriptEvent(BeatScriptEvent event) {
        if (event == null) {
            return;
        }
        publishOscEvent(event);
    }

    private void processOscScriptEvent(OscScriptEvent event) {
        if (event == null) {
            return;
        }
        publishOscEvent(event);
    }

    private void processClockTickEvent(StaveClockTickEvent event) {
        StaveId staveId = event.getStaveId();
        Stave stave = szcore.getStave(staveId);
        if (stave == null) {
            LOG.error("Invalid NULL stave for event: " + event);
            return;
        }

        if (!stave.isActive()) {
            return;
        }

        publishOscEvent(event);
    }

    private void processDyTickEvent(StaveDyTickEvent event, int beatNo) {

        StaveId staveId = event.getStaveId();
        Stave stave = szcore.getStave(staveId);
        if (stave == null) {
            LOG.error("Invalid NULL stave for event: " + event);
            return;
        }

        if (!stave.isActive()) {
            return;
        }

        Id instrumentId = staveId.getInstrumentId();
        Transport transport = szcore.getInstrumentTransport(instrumentId);
        boolean isRunning = transport.isRunning();
        InstrumentBeatTracker beatTracker = instrumentBeatTrackers.get(instrumentId);
        int trackerBeat = beatTracker.getStartBaseBeatNo();
        if (isRunning && (beatNo < trackerBeat)) {
            LOG.debug("Not publishing DY, trackerBeat: " + trackerBeat + " beatNo: " + beatNo);
            return;
        }

        int beatPercComplete = beatTracker.getPercentCompleted();
        double y = stave.getBeaterYPositionMin();
        double dyMax = stave.getBeaterYPositionDelta();
        int dyPerc = beatFollowerPositionStrategy.getYPercent(beatPercComplete);
        double dy = dyMax * dyPerc / 100.0;
        if (y > 0.0) {
            y += dy;
        } else {
            y -= dy;
        }

        ArrayList<Object> args = (ArrayList<Object>) event.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, new Float(y));

        publishOscEvent(event);
    }

    private void processStaveYPosition(StaveYPositionEvent event) {
//        LOG.debug("Processing beat: " + beatNo + " tickNo: " + tickNo);
        StaveId staveId = event.getStaveId();
        Stave stave = szcore.getStave(staveId);
        if (stave == null) {
            LOG.error("Invalid NULL stave for event: " + event);
            return;
        }

        if (!stave.isActive()) {
            return;
        }

        Id instrumentId = staveId.getInstrumentId();
        InstrumentBeatTracker beatTracker = instrumentBeatTrackers.get(instrumentId);
        int beatPercComplete = beatTracker.getPercentCompleted();
        double y = stave.getBeaterYPositionMin();
        double dyMax = stave.getBeaterYPositionDelta();
        double dy = dyMax * beatPercComplete / 100.0;
        if (y > 0.0) {
            y += dy;
        } else {
            y -= dy;
        }

        ArrayList<Object> args = (ArrayList<Object>) event.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        Float fl = new Float(y);
        args.add(1, fl);

        publishOscEvent(event);
    }

    public void publishOscEvents(List<OscEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        for (OscEvent event : events) {
            publishOscEvent(event);
        }
    }

    public void publishOscEvent(OscEvent event) {
        oscPublisher.process(event);
    }

    public void sendWebScoreState(String target, WebScoreTargetType targetType, WebScoreState scoreState) {
        sendWebScoreState(target, targetType, scoreState, OutgoingWebEventType.PUSH_SCORE_STATE);
    }

    public void sendWebScorePing(String target, WebScoreTargetType targetType, WebScoreState scoreState) {
        sendWebScoreState(target, targetType, scoreState, OutgoingWebEventType.PING);
    }

    public void sendWebScoreState(String target, WebScoreTargetType targetType, WebScoreState scoreState, OutgoingWebEventType eventType) {
        OutgoingWebEvent outEvent = eventFactory.createWebScoreOutEvent(null, null, eventType, false, clock.getSystemTimeMillis());
        outEvent.addData(Consts.WEB_DATA_SCORE_STATE, scoreState);
        outEvent.addData(Consts.WEB_DATA_TARGET, target);
        outEvent.addData(Consts.WEB_DATA_TARGET_TYPE, targetType);
        onOutgoingWebEvent(outEvent);
    }

    public OutgoingWebEvent createWebScoreStateEvent(BeatId beatId, String target, WebScoreTargetType targetType, WebScoreState scoreState) {
        OutgoingWebEvent outEvent = eventFactory.createWebScoreOutEvent(beatId, null, OutgoingWebEventType.PUSH_SCORE_STATE, false, clock.getSystemTimeMillis());
        outEvent.addData(Consts.WEB_DATA_SCORE_STATE, scoreState);
        outEvent.addData(Consts.WEB_DATA_TARGET, target);
        outEvent.addData(Consts.WEB_DATA_TARGET_TYPE, targetType);
        return outEvent;
    }

    public void sendWebScorePing(long sendTime) {
        WebScoreState scoreState = getOrCreateWebScoreState();
        Map<String, Object> params = new HashMap<>(1);
        params.put(Consts.WEB_ACTION_PARAM_SEND_TIME_MS, sendTime);
        WebScoreAction action = getOrCreateWebScoreAction(WebScoreActionType.PING, null, params);
        scoreState.addAction(action);
        sendWebScorePing(WebScoreTargetType.ALL.name(), WebScoreTargetType.ALL, scoreState);
    }

    public OutgoingWebEvent createWebScoreStartEvent(BeatId beatId, String staveId, String instrument) {
        WebScoreState scoreState = getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(staveId);
        WebScoreAction action = getOrCreateWebScoreAction(WebScoreActionType.START, targets, null);
        scoreState.addAction(action);
        return createWebScoreStateEvent(beatId, instrument, WebScoreTargetType.INSTRUMENT, scoreState);
    }

    public WebScoreState getOrCreateWebScoreState() {
        //TODO create pool?
        return new WebScoreState();
    }

    @Override
    public void onInterceptedOscOutEvent(OscEvent event) {
        String destination = event.getDestination();
        boolean isWebScoreDestination = Consts.ALL_DESTINATIONS.equals(destination) || (webScore != null && webScore.isDestination(destination));
        if (isWebScoreDestination) {
            publishToWebScore(event);
        }
    }

    @Override
    public List<WebClientInfo> getWebScoreInstrumentClients(String instrument) {
        return webScore.getInstrumentClients(instrument);
    }

    public WebScoreAction getOrCreateWebScoreAction(WebScoreActionType actionType, List<String> targets, Map<String, Object> params) {
        //TODO create pool?
        return new WebScoreAction(actionType, targets, params);
    }

    //HACK - replace with separate events
    private void publishToWebScore(OscEvent event) {
        OscEventType oscEventType = event.getOscEventType();
        try {
            switch (oscEventType) {
                case PING:
                    sendWebScorePing(event.getCreationTime());
                    break;
                case GENERIC:
                    LOG.error("Received generic OSC event: {}", event);
                    break;
                default:
                    if (webScore == null) {
                        break;
                    }
                    webScore.processInterceptedOscEvent(event);
            }
        } catch (Exception e) {
            LOG.error("publishToWebScoreHack: failed to publish web score event", e);
        }
    }

    public void publishWebEvent(OutgoingWebEvent event) {
        webPublisher.process(event);
    }

    private void processInitEvents(List<SzcoreEvent> initEvents) {
        if (initEvents == null) {
            return;
        }

        int threadSleepMillis = Consts.DEFAULT_THREAD_SLEEP_MILLIS;
        for (SzcoreEvent initEvent : initEvents) {
            LOG.debug("Processing init event: " + initEvent);
            process(initEvent);
            ThreadUtil.doSleep(Thread.currentThread(), threadSleepMillis);
        }

        scheduler.setPublishSleep(threadSleepMillis);
        scheduler.processQueue();
        scheduler.setPublishSleep(0);
        isInitDone = true;
    }

    private void processClockTick(Id transportId, int beatNo, int tickNo) {
        if (szcore == null) {
            LOG.error("Invalid NULL score");
            return;
        }

        List<BeatId> beatIds = szcore.findBeatIds(transportId, beatNo);
        List<Id> instrumentIds = szcore.getTransportInstrumentIds(transportId);
        if (instrumentIds != null) {
            for (Id instrumentId : instrumentIds) {
                InstrumentBeatTracker instrumentBeatTracker = instrumentBeatTrackers.get(instrumentId);
                if (instrumentBeatTracker == null) {
                    continue;
                }
                Beat beat = null;
                if (beatIds != null) {
                    for (BeatId beatId : beatIds) {
                        if (instrumentId.equals(beatId.getInstrumentId())) {
                            beat = szcore.getBeat(beatId);
                            break;
                        }
                    }
                }

                if (beat != null) {
                    Beat currentBeat = instrumentBeatTracker.getCurrent();
                    if (currentBeat == null || currentBeat.getBeatNo() < beat.getBeatNo()) {
                        instrumentBeatTracker.setCurrentBeat(beat);
                    }
                }
                if (tickNo == 0 && instrumentBeatTracker.getTick() != 0) {
                    instrumentBeatTracker.setTick(-1);
                } else {
                    instrumentBeatTracker.incrementTick(beatNo);
                }
            }
        }

        List<SzcoreEvent> clockTickEvents = szcore.getClockTickEvents(transportId);
        if (clockTickEvents != null) {
            for (SzcoreEvent clockEvent : clockTickEvents) {
                process(clockEvent, beatNo, tickNo);
            }
        }


        LinkedBlockingQueue<SzcoreEvent> oneOffClockTickEvents = szcore.getOneOffClockTickEvents(transportId);
        if (oneOffClockTickEvents != null) {
            while (oneOffClockTickEvents.size() != 0) {
                SzcoreEvent event = oneOffClockTickEvents.poll();
                if (event == null) {
                    continue;
                }
                process(event, beatNo, tickNo);
            }
        }

//        notifyListenersOnTick(transportId, currentBeatNo, currentBaseBeatNo, currentTick);
    }

    private void processBaseBeat(int beatNo, Id transportId) {
        baseBeatEventsToProcess.clear();
        currentBeatNo = beatNo;
//        LOG.debug("###### Processing beat: " + beatNo);
        if (szcore == null) {
            LOG.error("Invalid NULL score");
            return;
        }

        int currentBeat = 0;
        int currentBaseBeat = 0;
        List<BeatId> beatIds = szcore.findBeatIds(transportId, beatNo);
        if (beatIds != null && !beatIds.isEmpty()) {
            for (BeatId beatId : beatIds) {
                Beat beat = szcore.getBeat(beatId);
                Id instrumentId = beatId.getInstrumentId();
                InstrumentBeatTracker instrumentBeatTracker = instrumentBeatTrackers.get(instrumentId);
                if (instrumentBeatTracker == null) {
                    continue;
                }
                instrumentBeatTracker.setCurrentBeat(beat);
                currentBeat = beatId.getBeatNo();
                currentBaseBeat = beatId.getBaseBeatNo();
            }

        }
        notifyListenersOnBeat(transportId, currentBeat, currentBaseBeat);

        List<SzcoreEvent> clockBaseBeatEvents = szcore.getClockBaseBeatEvents(transportId);
        if (clockBaseBeatEvents != null) {
            for (SzcoreEvent clockEvent : clockBaseBeatEvents) {
//                LOG.debug("###  Processing clock beat: " + beatNo + " event: " + clockEvent);
                process(clockEvent, beatNo);
            }
        }

        TIntObjectMap<List<SzcoreEvent>> scoreBaseBeatEvents = szcore.getScoreBaseBeatEvents(transportId);
        List<SzcoreEvent> beatEvents = scoreBaseBeatEvents.get(beatNo);

        if (beatEvents != null && !beatEvents.isEmpty()) {
            baseBeatEventsToProcess.addAll(beatEvents);
        }

        List<SzcoreEvent> oneOffBeatEvents = szcore.getOneOffBaseBeatEvents(transportId, beatNo);
        if (oneOffBeatEvents != null) {
            baseBeatEventsToProcess.addAll(oneOffBeatEvents);
            szcore.removeOneOffBeatEvents(transportId, beatNo);
        }

        if (baseBeatEventsToProcess.isEmpty()) {
            return;
        }

        for (SzcoreEvent beatEvent : baseBeatEventsToProcess) {
//            LOG.debug("###  Processing base Beat event: " + beatEvent);
            process(beatEvent, beatNo);
        }
    }

    //use with care - will be processed for each base beat;
    public void addBeatEventToProcess(SzcoreEvent event) {
        baseBeatEventsToProcess.add(event);
    }

    @Override
    public void processSelectSection(String section, WebClientInfo clientInfo) {
        if(section == null || clientInfo == null) {
            return;
        }
        ScoreBuilderStrategy scoreBuilderStrategy = szcore.getScoreBuilderStrategy();
        String clientId = clientInfo.getClientId();
        if(clientId == null) {
            LOG.warn("processSelectSection: unknown clientId, using source addr as id");
            clientId = clientInfo.getClientAddr();
        }
        scoreBuilderStrategy.appendSection(section, clientId);

        if(scoreBuilderStrategy.isReady()) {
            onScoreBuilderStrategyReady();
        }
        updateClients(scoreBuilderStrategy);
    }

    protected void updateClients(ScoreBuilderStrategy scoreBuilderStrategy) {
        if(scoreBuilderStrategy == null) {
            return;
        }
        ScoreSectionInfoEvent sectionInfoEvent = createSectionInfoEvent(scoreBuilderStrategy);
        if(sectionInfoEvent != null) {
            sendClientEvent(sectionInfoEvent);
        }
    }

    protected ScoreSectionInfoEvent createSectionInfoEvent(ScoreBuilderStrategy scoreBuilderStrategy) {
        if(scoreBuilderStrategy == null) {
            return null;
        }
        Id scoreId = getScore().getId();
        List<SectionInfo> sectionInfos = scoreBuilderStrategy.getSectionInfos();
        List<String> sectionOrder = scoreBuilderStrategy.getSectionOrder();
        boolean isReady = scoreBuilderStrategy.isReady();
        String currentSection = scoreBuilderStrategy.getCurrentSection();
        String nextSection = scoreBuilderStrategy.getNextSection();
        long creationTime = clock.getSystemTimeMillis();
        return eventFactory.createScoreSectionInfoEvent(scoreId, sectionInfos, sectionOrder, isReady, currentSection, nextSection, creationTime);
    }


    public void prepareSectionEvents(ScoreBuilderStrategy scoreBuilderStrategy, Instrument instrument, Transport transport) {
        String section = scoreBuilderStrategy.getCurrentSection();
        SectionInfo sectionInfo = scoreBuilderStrategy.getSectionInfo(section);
        int startPageNo = sectionInfo.getStartPageNo();
        Page sectionStartPage = null;
        if(instrument != null) {
            sectionStartPage = getBasicScore().getPageNo(startPageNo, (InstrumentId)instrument.getId());
        }
        if(sectionStartPage != null) {
            Beat sectionFirstBeat = sectionStartPage.getFirstBeat();
            addProcessSectionsEvent(sectionFirstBeat.getBeatId(), section, ScoreSectionEventType.START, transport.getId());
        }

        int endPageNo = sectionInfo.getEndPageNo();
        Page sectionEndPage = null;
        if(instrument != null) {
            sectionEndPage = getBasicScore().getPageNo(endPageNo, (InstrumentId)instrument.getId());
        }
        if(sectionEndPage != null) {
            Beat sectionLastBeat = sectionEndPage.getLastBeat();
            if(scoreBuilderStrategy.isStopOnSectionEnd()) {
                addOneOffStopEvent(sectionLastBeat.getBeatId(), transport.getId());
            }
            addProcessSectionsEvent(sectionLastBeat.getBeatId(), section, ScoreSectionEventType.STOP, transport.getId());
        }
    }

    public void addProcessSectionsEvent(BeatId beatId, String section, ScoreSectionEventType eventType, Id transportId) {
        long now = getClock().getSystemTimeMillis();
        ScoreSectionEvent sectionEvent = getEventFactory().createScoreSectionEvent(beatId, section, eventType, transportId, now);
        getBasicScore().addOneOffBaseBeatEvent(transportId, sectionEvent);
    }

    protected void onScoreBuilderStrategyReady() {
        ScoreBuilderStrategy scoreBuilderStrategy = szcore.getScoreBuilderStrategy();
        if(scoreBuilderStrategy == null || !scoreBuilderStrategy.isReady()) {
            return;
        }
        scoreBuilderStrategy.setPageOrder();
        List<String> sectionOrder = scoreBuilderStrategy.getSectionOrder();
        if(sectionOrder != null && sectionOrder.size() > 0) {
            scoreBuilderStrategy.setCurrentSection(sectionOrder.get(0));
        }
    }

    public InstrumentBeatTracker getInstrumentBeatTracker(Id instrumentId) {
        return instrumentBeatTrackers.get(instrumentId);
    }

    @Override
    public void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst, WebClientInfo clientInfo) {
        if (slotInstrument == null || sourceInst == null) {
            LOG.error("processSelectInstrumentSlot: Invalid slot: {} or source {} instrument", slotInstrument, sourceInst);
            return;
        }

        if (!isUpdateWindowOpen) {
            LOG.warn("processSelectInstrumentSlot: update window is not open, ignoring  request");
            return;
        }

        Instrument inst = szcore.getInstrument(sourceInst);
        if (inst == null) {
            LOG.error("processSelectInstrumentSlot: could not find instrument for source value: {}", sourceInst);
            return;
        }

        Instrument replaceInst = szcore.getInstrument(slotInstrument);
        if (replaceInst == null) {
            LOG.error("processSelectInstrumentSlot: could not find instrument for source value: {}", sourceInst);
            return;
        }
        processInstrumentReplace(sourceInst, slotInstrument, inst, replaceInst, clientInfo);
    }

    protected void processInstrumentReplace(String sourceInst, String slotInstrument, Instrument currentInst, Instrument replaceInst, WebClientInfo clientInfo) {
        boolean isOptOut = slotInstrument.equals(sourceInst);

        ScoreRandomisationStrategy randomisationStrategy = szcore.getRandomisationStrategy();
        randomisationStrategy.optOutInstrument(currentInst, replaceInst, isOptOut);

        List<InstrumentId> slotInstrumentIds = randomisationStrategy.getInstrumentSlotIds();
        String instSlotsCsv = ParseUtil.convertToCsv(slotInstrumentIds);

        Collection<Instrument> instruments = szcore.getScoreInstruments();

        for (Instrument instrument : instruments) {
            LOG.debug("processSelectInstrumentSlot: prepare sending csv: {} to instrument {}", instSlotsCsv, instrument.getName());
            String destination = szcore.getOscDestination(instrument.getId());
            OscEvent instrumentSlotsEvent = createInstrumentSlotsEvent(destination, instSlotsCsv);
            publishOscEvent(instrumentSlotsEvent);
        }
    }

    private void processTempoChange(Id transportId, Tempo tempo) {
        notifyListenersOnTempoChange(transportId, tempo);
    }


    private void processTransportPositionChange(Id transportId, int beatNo) {
        notifyListenersOnTransportPositionChange(transportId, beatNo);
    }




    public void addNoScoreInstrument(String instrument) {
        if(!this.noScoreInstruments.contains(instrument)) {
            this.noScoreInstruments.add(instrument);
        }
    }

    public boolean isNoScoreInstrument(String instrument) {
        return this.noScoreInstruments.contains(instrument);
    }

    @Override
    public void onSectionStart(String section) {
        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if(builderStrategy == null) {
            return;
        }
        String current = builderStrategy.getCurrentSection();
        if(current != null && !current.equals(section)) {
            return;
        }
        if(section != null) {
           builderStrategy.onSectionStart(section);
        }
        updateClients(builderStrategy);
    }

    @Override
    public void onSectionStop(String section) {
        ScoreBuilderStrategy builderStrategy = szcore.getScoreBuilderStrategy();
        if(builderStrategy == null) {
            return;
        }
        String current = builderStrategy.getCurrentSection();
        if(current != null && !current.equals(section)) {
            builderStrategy.onSectionEnd();;
        } else if(section != null) {
            builderStrategy.onSectionEnd(section);
        }
        updateClients(builderStrategy);
    }

    @Override
    public void setWebDelayMs(long delayMs) {
        if(delayMs < 0L) {
            return;
        }
        LOG.info("setWebDelayMs: wetting web event delay set to {}ms", delayMs);
        latencyCompensator.setWebPublishDelayMs(delayMs);
    }

    public boolean isSchedulerRunning() {
        return scheduler.isActive();
    }

    public int getCurrentBeatNo() {
        return currentBeatNo;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setSzcore(BasicScore szcore) {
        this.szcore = szcore;
    }

    public TransportFactory getTransportFactory() {
        return transportFactory;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public ScoreProcessorDelegator getParentProcessor() {
        return parentProcessor;
    }

    public Map<Id, InstrumentBeatTracker> getInstrumentBeatTrackers() {
        return instrumentBeatTrackers;
    }

    public Map<Id, TempoModifier> getTransportTempoModifiers() {
        return transportTempoModifiers;
    }

    public ScoreScriptingEngine getScriptingEngine() {
        return scriptingEngine;
    }

    public void setScoreLoaded(boolean scoreLoaded) {
        isScoreLoaded = scoreLoaded;
    }

    public MutableClock getClock() {
        return clock;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public Properties getProps() {
        return props;
    }

    public WebScore getWebScore() {
        return webScore;
    }

    class ScoreTransportListener implements TransportListener {

        private final Id transportId;

        public ScoreTransportListener(Id transportId) {
            this.transportId = transportId;
        }

        @Override
        public void onClockTick(int beatNo, int tickNo) {
            processClockTick(transportId, beatNo, tickNo);
        }

        @Override
        public void onBaseBeat(int beatNo) {
            processBaseBeat(beatNo, transportId);
        }

        @Override
        public void onTempoChange(Tempo tempo) {
            processTempoChange(transportId, tempo);
        }

        @Override
        public void onPositionChange(int beatNo) {
            processTransportPositionChange(transportId, beatNo);
        }
    }
}
