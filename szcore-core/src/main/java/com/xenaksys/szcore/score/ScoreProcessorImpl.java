package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategy;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategyConfig;
import com.xenaksys.szcore.algo.StrategyConfigLoader;
import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.event.BeatScriptEvent;
import com.xenaksys.szcore.event.DateTickEvent;
import com.xenaksys.szcore.event.ElementAlphaEvent;
import com.xenaksys.szcore.event.ElementColorEvent;
import com.xenaksys.szcore.event.ElementSelectedEvent;
import com.xenaksys.szcore.event.ElementYPositionEvent;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.event.IncomingWebEvent;
import com.xenaksys.szcore.event.IncomingWebEventType;
import com.xenaksys.szcore.event.ModWindowEvent;
import com.xenaksys.szcore.event.MusicEvent;
import com.xenaksys.szcore.event.MusicEventType;
import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.event.OscEventType;
import com.xenaksys.szcore.event.OscScriptEvent;
import com.xenaksys.szcore.event.OscStaveActivateEvent;
import com.xenaksys.szcore.event.OscStaveTempoEvent;
import com.xenaksys.szcore.event.OscStopEvent;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.PrecountBeatSetupEvent;
import com.xenaksys.szcore.event.PrepStaveChangeEvent;
import com.xenaksys.szcore.event.ScriptingEngineEvent;
import com.xenaksys.szcore.event.ScriptingEngineResetEvent;
import com.xenaksys.szcore.event.StaveActiveChangeEvent;
import com.xenaksys.szcore.event.StaveClockTickEvent;
import com.xenaksys.szcore.event.StaveDateTickEvent;
import com.xenaksys.szcore.event.StaveDyTickEvent;
import com.xenaksys.szcore.event.StaveStartMarkEvent;
import com.xenaksys.szcore.event.StaveYPositionEvent;
import com.xenaksys.szcore.event.StopEvent;
import com.xenaksys.szcore.event.TempoChangeEvent;
import com.xenaksys.szcore.event.TimeSigChangeEvent;
import com.xenaksys.szcore.event.TransitionEvent;
import com.xenaksys.szcore.event.TransportEvent;
import com.xenaksys.szcore.event.TransportPositionEvent;
import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.event.WebScoreEventType;
import com.xenaksys.szcore.event.WebScorePlayTilesEvent;
import com.xenaksys.szcore.event.WebScoreResetEvent;
import com.xenaksys.szcore.event.WebScoreStopEvent;
import com.xenaksys.szcore.event.WebStartEvent;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
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
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.model.ScriptType;
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
import com.xenaksys.szcore.score.web.WebScore;
import com.xenaksys.szcore.score.web.WebScoreLoader;
import com.xenaksys.szcore.score.web.WebScoreScript;
import com.xenaksys.szcore.score.web.export.WebScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateExport;
import com.xenaksys.szcore.scripting.ScoreScriptingEngine;
import com.xenaksys.szcore.scripting.ScriptingEngineConfig;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;
import com.xenaksys.szcore.task.ScriptingEngineEventTask;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.task.WebScoreEventTask;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.util.MathUtil;
import com.xenaksys.szcore.util.ParseUtil;
import com.xenaksys.szcore.util.ThreadUtil;
import com.xenaksys.szcore.web.WebScoreStateListener;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xenaksys.szcore.Consts.CONTENT_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NAME;
import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NO;
import static com.xenaksys.szcore.Consts.DYNAMICS_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.MAX_BPM;
import static com.xenaksys.szcore.Consts.MIN_BPM;
import static com.xenaksys.szcore.Consts.POSITION_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.PRESSURE_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.SPEED_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.UNDERSCORE;

public class ScoreProcessorImpl implements ScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(ScoreProcessorImpl.class);

    static final int MUSIC_TASK_DELAY = 2;

    private final TransportFactory transportFactory;
    private final MutableClock clock;
    private final OscPublisher oscPublisher;
    private final WebPublisher webPublisher;
    private final Scheduler scheduler;
    private final EventFactory eventFactory;
    private final TaskFactory taskFactory;
    private final OscDestinationEventListener oscDestinationEventListener;

    private BasicScore szcore = null;
    private boolean isScoreLoaded = false;
    private boolean isInitDone = true;
    private List<SzcoreEvent> initEvents = new ArrayList<>();
    private List<SzcoreEvent> baseBeatEventsToProcess = new ArrayList<>();
    private int threadSleepMillis = Consts.DEFAULT_THREAD_SLEEP_MILLIS;

    private volatile static int instrumentPortRequestCount = 0;
    private volatile int startBaseBeat = 0;
    private Map<Id, InstrumentBeatTracker> instrumentBeatTrackers = new HashMap<>();
    private BeatFollowerPositionStrategy beatFollowerPositionStrategy = new BeatFollowerPositionStrategy();
    private List<SzcoreEngineEventListener> scoreEventListeners = new CopyOnWriteArrayList<>();
    protected List<WebScoreStateListener> webScoreStateListeners = new CopyOnWriteArrayList<>();
    private Map<Id, TempoModifier> transportTempoModifiers = new ConcurrentHashMap<>();

    private ValueScaler dynamicsValueScaler = new ValueScaler(0.0, 100.0, 0.0, DYNAMICS_LINE_Y_MAX);
    private ValueScaler dynamicsForteColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private ValueScaler dynamicsPianoColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private ValueScaler pressureLineValueScaler = new ValueScaler(0.0, 100.0, PRESSURE_LINE_Y_MAX, 0.0);
    private ValueScaler pressureColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private ValueScaler speedValueScaler = new ValueScaler(0.0, 100.0, 0.0, SPEED_LINE_Y_MAX);
    private ValueScaler speedFastColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private ValueScaler speedSlowColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private ValueScaler positionValueScaler = new ValueScaler(0.0, 100.0, POSITION_LINE_Y_MAX, 0.0);
    private ValueScaler contentValueScaler = new ValueScaler(0.0, 100.0, 0.0, CONTENT_LINE_Y_MAX);

    private WebScore webScore = null;
    private MaxMspScoreConfig maxMspConfig = null;
    private ScoreScriptingEngine scriptingEngine;

    private volatile boolean isUpdateWindowOpen = false;
    private volatile int currentBeatNo = 0;

    public ScoreProcessorImpl(TransportFactory transportFactory,
                              MutableClock clock,
                              OscPublisher oscPublisher,
                              WebPublisher webPublisher,
                              Scheduler scheduler,
                              EventFactory eventFactory,
                              TaskFactory taskFactory) {
        this.transportFactory = transportFactory;
        this.clock = clock;
        this.oscPublisher = oscPublisher;
        this.webPublisher = webPublisher;
        this.scheduler = scheduler;
        this.eventFactory = eventFactory;
        this.taskFactory = taskFactory;
        this.oscDestinationEventListener = new OscDestinationEventListener(this, eventFactory, clock);
    }

    @Override
    public void loadAndPrepare(String filePath) throws Exception {
        isScoreLoaded = false;

        File scoreFile = new File(filePath);
        if(!scoreFile.exists()) {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(filePath).getFile());
            if(file.exists()) {
                scoreFile = file;
            }
        }

        Score score = loadScore(scoreFile);
        prepare(score);
    }

    @Override
    public Score loadScore(File file) throws Exception {
        if (scheduler.isActive()) {
            LOG.warn("Scheduler is active, can not perform load score");
            throw new Exception("Scheduler is active, can not perform load score");
        }
        LOG.info("LOADING SCORE: " + file.getCanonicalPath());
        reset();
        Score score = ScoreLoader.load(file);
        szcore = (BasicScore) score;

        ScoreRandomisationStrategyConfig randomisationStrategyConfig = StrategyConfigLoader.loadStrategyConfig(file.getParent(), szcore);
        szcore.setRandomisationStrategyConfig(randomisationStrategyConfig);

        webScore = new WebScore(this, eventFactory, clock);
        webScore.init(file.getParent());

        maxMspConfig = new MaxMspScoreConfig();
        maxMspConfig.setScoreName(szcore.getName());

        scriptingEngine = new ScoreScriptingEngine(this, eventFactory, clock);
        scriptingEngine.init(file.getParent());

        return score;
    }

    @Override
    public WebScore loadWebScore(File file) throws Exception {
        if (scheduler.isActive()) {
            LOG.warn("Scheduler is active, can not perform load web score");
            throw new Exception("Scheduler is active, can not perform load web score");
        }
        LOG.info("LOADING WEB SCORE: " + file.getCanonicalPath());
        WebScore score = WebScoreLoader.load(file);
        return null;
    }

    @Override
    public void  prepare(Score score) {
        this.szcore = (BasicScore) score;

        List<Beat> beats = new ArrayList<>(score.getBeats());
        Collections.sort(beats);

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
            BasicPage contPage = new BasicPage(contPageId, contPageName, contPageName, lastPage.getInscorePageMap(), true);
            Collection<Bar> lpBars = lastPage.getBars();
            for (Bar bar : lpBars) {
                contPage.addBar(bar);
            }
            szcore.setContinuousPage(instrumentId, contPage);

            if (szcore.isUseContinuousPage()) {
                prepareContinuousPages(instrumentId, lastPage);
            }
        }

        if (szcore.isRandomizeContinuousPageContent()) {
            szcore.initRandomisation();
        }

        if (!szcore.isUseContinuousPage()) {
            addStopEvent(lastBeat, transport.getId());
        }
        int precountMillis = 5 * 1000;
        int precountBeatNo = 4;
        szcore.setPrecount(precountMillis, precountBeatNo);

        Collection<Instrument> maxClients = szcore.getOscPlayers();
        oscDestinationEventListener.reloadDestinations(maxClients);

        isScoreLoaded = true;
    }

    private void prepareContinuousPages(Id instrumentId, Page lastPage) {
        for (int i = 0; i < szcore.noContinuousPages; i++) {
            lastPage = prepareContinuousPage(instrumentId, (PageId)lastPage.getId());
        }
    }

    private BeatId prepareInstrument(Instrument instrument, Transport transport) {
        Id transportId = transport.getId();
        boolean isAudioVideoInstrument = instrument.isAv();
        boolean isScoreInstrument = !isAudioVideoInstrument;
        if(isScoreInstrument) {
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
            Page page = szcore.getPage(pageId);

            BasicStave currentStave = null;
            BasicStave nextStave = null;
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            if(isScoreInstrument) {
                currentStave = (BasicStave) szcore.getCurrentStave(instrumentId);
                nextStave = (BasicStave) szcore.getNextStave(instrumentId);
            }

            Tempo tempo = bar.getTempo();
            TimeSignature timeSignature = bar.getTimeSignature();

            boolean isFirstBeat = (beatNo == 0);
            boolean isFirstBar = (barNo == 1);
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

                    Beat executeBeat = szcore.getOffsetBeat(beatId, -1);
                    Beat deactivateBeat = szcore.getOffsetBeat(beatId, 1);
                    Beat pageChangeBeat = szcore.getOffsetBeat(beatId, 4);

                    addPrepStaveChangeEvent(executeBeat.getBeatId(), beatId, deactivateBeat.getBeatId(), pageChangeBeat.getBeatId(), null, transport.getId());
                }
            }

            List<Script> beatScripts = szcore.getBeatScripts(beatId);
            if(beatScripts != null && !beatScripts.isEmpty()) {
                 for(Script script : beatScripts) {
                     addScript(beatId, script, transportId);
                 }
            }
            addWebScoreBeatScripts(beatId, transportId);
            addScriptingEngineBeatScripts(beatId, transportId);
        }

        return lastBeat;
    }

    private void addScriptingEngineBeatScripts(BeatId beatId, Id transportId) {
        List<ScriptingEngineScript> scripts = scriptingEngine.getBeatScripts(beatId);
        if (scripts != null && !scripts.isEmpty()) {
            addScriptingEngineEvent(beatId, scripts, transportId);
        }
    }

    private void addWebScoreBeatScripts(BeatId beatId, Id transportId) {
        List<WebScoreScript> scripts = webScore.getBeatScripts(beatId);
        if (scripts != null && !scripts.isEmpty()) {
            addWebScoreEvent(beatId, scripts, transportId);
        }
    }

    private void addScript(BeatId beatId, Script script, Id transportId) {
        ScriptType type = script.getType();
        switch (type) {
            case JAVASCRIPT:
                addBeatScriptEvent(beatId, script, transportId);
                break;
            case TRANSITION:
                addBeatTransitionEvent(beatId, (BasicTransition) script, transportId);
                break;
            case WEB_SCORE:
                webScore.addBeatScript(beatId, (WebScoreScript) script);
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

    public void addMaxPreset(BeatId beatId, OscScript script) throws Exception {
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

    public void addScriptingEnginePreset(BeatId beatId, ScriptingEngineScript script) throws Exception {
        if (script == null || !script.isResetPoint()) {
            return;
        }

        int id = ((IntId) script.getId()).getValue();
        ScriptingEngineConfig config = scriptingEngine.getConfig();
        ScriptPreset scriptPreset = config.getPreset(id);
        if (scriptPreset == null) {
            ;
            scriptPreset = new ScriptPreset(id);
            config.addPreset(scriptPreset);
        }

        scriptPreset.addScript(script.getContent());
    }

    private void addBeatScriptEvent(BeatId beatId, Script script, Id transportId) {
        OscEvent beatScriptEvent = createBeatScriptEvent(script, beatId);
        szcore.addScoreBaseBeatEvent(transportId, beatScriptEvent);
    }

    private void addBeatTransitionEvent(BeatId beatId, Transition transition, Id transportId) {
        TransitionEvent transitionEvent = createTransitionEvent(transition, beatId);
        szcore.addScoreBaseBeatEvent(transportId, transitionEvent);
    }

    private void addWebScoreEvent(BeatId beatId, List<WebScoreScript> scripts, Id transportId) {
        WebScoreEvent webScoreEvent = createWebScoreEvent(scripts, beatId);
        szcore.addScoreBaseBeatEvent(transportId, webScoreEvent);
    }

    private void addTransportInitEvents(Tempo tempo, TimeSignature timeSignature, int startBaseBeatNo, int transportBeatNo, int tickNo, long positionMillis, Id transportId, List<SzcoreEvent> initEvents) {
        addTransportPositionEvent(transportId, startBaseBeatNo, transportBeatNo, tickNo, positionMillis, initEvents);
        addTempoChangeInitEvent(tempo, null, transportId, false, initEvents);
        addTimeSigChangeInitEvent(timeSignature, null, transportId, initEvents);
        addPrecountSetupEvent(szcore.isPrecount(), szcore.getPrecountBeatNo(), szcore.getPrecountMillis(),
                szcore.getPrecountBeaterInterval(), transportId, initEvents);
    }

    private void addPageInitEvents(Transport transport, Instrument instrument, BasicStave currentStave, BasicStave nextStave,
                                   Page page, List<SzcoreEvent> initEvents){
        addActiveStaveChangeEvent(currentStave.getId(), true, null, transport.getId(), instrument, initEvents);
        addActiveStaveChangeEvent(nextStave.getId(), false, null, transport.getId(), instrument, initEvents);
        addNewPageEvents(page, currentStave, null, transport.getId(), initEvents);
        Page nextPage = szcore.getNextPage((PageId)page.getId());
        if (nextPage != null) {
            addNewPageEvents(nextPage, nextStave, null, transport.getId(), initEvents);
        }
    }

    private void addInitAvEvent(Transport transport, Instrument instrument, List<SzcoreEvent> initEvents, BeatId startBeatId) {

        OscEvent resetInstrumentEvent = createResetInstrumentEvent(instrument.getName());
        ScriptEventPreset eventPreset = maxMspConfig.getBeatResetScripts(startBeatId);
        List<OscEvent> presetEvents = eventPreset.getScriptEvents();
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


    private void addInitWebScoreEvent(Transport transport, List<SzcoreEvent> initEvents, BeatId startBeatId) {
        WebScoreResetEvent webScoreResetEvent = createWebScoreResetEvent(startBeatId);
        if (webScoreResetEvent == null) {
            return;
        }
        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transport.getId(), webScoreResetEvent);
        } else {
            initEvents.add(webScoreResetEvent);
        }
    }

    private void addInitScriptingEvent(Transport transport, List<SzcoreEvent> initEvents, BeatId startBeatId) {
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

    public void processPrepStaveChange(Id instrumentId, BeatId currentBeatId, BeatId activateBeatId, BeatId deactivateBeatId, BeatId pageChangeBeatId, PageId nextPageId) {
        if (szcore == null) {
            return;
        }

        BasicStave currentStave = (BasicStave) szcore.getCurrentStave(instrumentId);
        BasicStave nextStave = (BasicStave) szcore.getNextStave(instrumentId);

        Transport transport = szcore.getInstrumentTransport(instrumentId);
        Id transportId = transport.getId();
        Instrument instrument = szcore.getInstrument(instrumentId);
        addOneOffActiveStaveChangeEvent(nextStave.getId(), true, activateBeatId, transportId, instrument);
        addOneOffActiveStaveChangeEvent(currentStave.getId(), false, deactivateBeatId, transportId, instrument);

        //change next stave page after 4 beats of the current page
        PageId pageId = (PageId) pageChangeBeatId.getPageId();
        Page nextPage = szcore.getPage(nextPageId);
        if (nextPage == null) {
            nextPage = szcore.getNextPage(pageId);
        }

        if (nextPage == null && szcore.isUseContinuousPage()) {
            nextPage = prepareContinuousPage(instrumentId, pageId);
        }

        LOG.info("processPrepStaveChange: nextPage: {}", nextPage);
        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
        boolean isInRndRange = strategy.isInActiveRange((InstrumentId) instrument.getId(), nextPage);

        if (!szcore.isRandomizeContinuousPageContent()) {
            isInRndRange = false;
        }

        addOneOffNewPageEvents(nextPage, isInRndRange, currentStave, pageChangeBeatId, activateBeatId, transportId);
    }

    private Page prepareContinuousPage(Id instrumentId, PageId currentPageId) {
        Page continuousPage = szcore.getContinuousPage(instrumentId);
        Page currentPage = szcore.getPage(currentPageId);
        Transport transport = szcore.getInstrumentTransport(instrumentId);

        List<Bar> bars = (List<Bar>)currentPage.getBars();
        Bar lastBar = bars.get(bars.size() - 1);
        int lastBarNo = lastBar.getBarNo();
        List<Beat> beats  = (List<Beat>) lastBar.getBeats();
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

        bars = (List<Bar>)continuousPage.getBars();
        BeatId firstBeatId = null;
        for(Bar bar : bars) {
            BarId newBarId = new BarId(lastBarNo, instrumentId, newPageId, scoreId);
            lastBarNo++;
            BasicBar newBar = new BasicBar(newBarId, bar.getBarName(), bar.getTempo(), bar.getTimeSignature());
            if (!szcore.containsBar(newBar)) {
                szcore.addBar(newBar);
            }

            beats = (List<Beat>) bar.getBeats();

            for(Beat beat : beats) {
                BeatId newBeatId;
                if(beat.isUpbeat()) {
                    newBeatId = new BeatId(lastBeatNo, instrumentId, newPageId, scoreId, newBarId, lastBeat.getBaseBeatUnitsNoAtStart());
                    if(firstBeatId == null) {
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
                    if(firstBeatId == null) {
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

        Beat executeBeat = szcore.getOffsetBeat(firstBeatId, -1);
        Beat deactivateBeat = szcore.getOffsetBeat(firstBeatId, 1);
        Beat pageChangeBeat = szcore.getOffsetBeat(firstBeatId, 4);

        addPrepStaveChangeEvent(executeBeat.getBeatId(), firstBeatId, deactivateBeat.getBeatId(), pageChangeBeat.getBeatId(), null, transport.getId());

        return newPage;
    }

    private void createInstrumentStaves(Instrument instrument) {
        // TODO populate dynamically from engine
        szcore.addInstrumentOscPort(instrument.getId(), getInstrumentOscPort(instrument));
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

    private String getScoreFileName(Instrument instrument) {
        String instrumentName = instrument.getName();
        return instrumentName + Consts.INSCORE_FILE_EXTENSION;
    }

    private String getDefaultScoreFileName() {
        return Consts.DEFAULT_FILE_NAME;
    }

    @Override
    public void onOpenModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {

        LOG.info("onOpenModWindow: page: {}", nextPage);
        this.isUpdateWindowOpen = true;
        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
        Page currentPage = szcore.getPage(currentPageId);

        boolean isInRndRange = strategy.isInActiveRange(instId, currentPage);
        boolean isRecalcTime = strategy.isRecalcTime();
        if (isRecalcTime) {
            strategy.recalcStrategy(nextPage);
        }
        String destination = szcore.getOscDestination(instId);

        if (isInRndRange) {
            List<InstrumentId> slotInstrumentIds = strategy.getInstrumentSlotIds();
            String instSlotsCsv = ParseUtil.convertToCsv(slotInstrumentIds);
            LOG.info("onOpenModWindow: rnd strategy selected instruments: {}", instSlotsCsv);
            if (currentPage != null) {
                OscEvent instrumentSlotsEvent = createInstrumentSlotsEvent(destination, instSlotsCsv, null);
                if (instrumentSlotsEvent == null) {
                    LOG.error("onOpenModWindow: Invalid instrumentSlotsEvent, isInRndRange: true, destination: {} instSlotsCsv: {}", destination, instSlotsCsv);
                    instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination, null);
                    publishOscEvent(instrumentSlotsEvent);
                    return;
                }
                publishOscEvent(instrumentSlotsEvent);
            }

            WebScorePlayTilesEvent playTilesEvent = eventFactory.createWebScorePlayTilesEvent(clock.getSystemTimeMillis());
            processWebScoreEvent(playTilesEvent);
        } else {
            OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination, null);
            publishOscEvent(instrumentSlotsEvent);
        }
    }

    @Override
    public void onCloseModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        this.isUpdateWindowOpen = false;

        //TODO remove
        Transport transport = szcore.getInstrumentTransport(instId);
        BeatId beatId = szcore.getInstrumentBeatIds(transport.getId(), instId, currentBeatNo);
//        LOG.info("onCloseModWindow: instrument {} page: {} currentBeat: {}", instId.getName(), page.getPageNo(), beatId);

        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();

        boolean isInRange = strategy.isInActiveRange(instId, nextPage);
        if (isInRange) {
            List<Integer> selectedPageIds = null;
            if (strategy.isPageRecalcTime()) {
                int pageQuantity = strategy.getNumberOfRequiredPages();
                selectedPageIds = webScore.prepareNextTilesToPlay(pageQuantity);
                strategy.setPageSelection(selectedPageIds);
            }

            sendRndPageFileUpdate(nextPage, stave, instId);
        } else {
            sendPageFileUpdate(nextPage, stave);
        }

        String destination = szcore.getOscDestination(instId);
        OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination, null);
        publishOscEvent(instrumentSlotsEvent);
    }

    private void sendRndPageFileUpdate(Page page, Stave stave, InstrumentId instId) {
        if (page == null) {
            return;
        }
        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
        String pageFileName = strategy.getRandomPageFileName(instId);
        if (pageFileName == null) {
            pageFileName = page.getFileName();
            LOG.info("onCloseModWindow: Invalid random page file name, using: {}", pageFileName);
        } else {
            LOG.info("onCloseModWindow: Using random page file name: {} for instrument: {}", pageFileName, instId);
        }
        List<OscEvent> out = createPageChangeEvents(page, pageFileName, (BasicStave) stave, null);
        publishOscEvents(out);
    }

    public void sendOscInstrumentRndPageUpdate(int bufferNo) {
        LOG.info("sendOscInstrumentRndPageUpdate: bufferNo: {}", bufferNo);

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
        LOG.info("sendOscInstrumentRndPageUpdate: instrument {} page: {} currentBeat: {}  nextPage: {}", instId.getName(), pageId.getPageNo(), beatId, nextPage.getPageNo());

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
            LOG.info("#### setUpContinuousTempoChange: new tempo{}, beat: {}, bpmDelta: {}, beatBpmOffset: {}", newBpm, offsetBeat.getBeatId(), bpmDelta, beatBpmOffset);
            addTempoChangeEvent(newTempo, offsetBeat.getBeatId(), Consts.ALL_DESTINATIONS, transport.getId());
        }
    }

    public void sendOscPlayerRndPageUpdate(InstrumentId instId, BeatId beatId, Collection<Integer> selectedPageIds, Page nextPage, int bufferNo, Transport transport) {
        if (!szcore.isOscPlayer(instId)) {
            return;
        }

//        ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
//        int pageNo = strategy.getFirstRandomPageNo();
        int pageNo = nextPage.getPageNo();
        if (selectedPageIds != null && !selectedPageIds.isEmpty()) {
            pageNo = selectedPageIds.iterator().next();
        }
        LOG.info("sendOscPlayerRndPageUpdate: MAXMSP Using RND page no: {} for instrument: {}", pageNo, instId);

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

    private void sendPageFileUpdate(Page page, Stave stave) {
        if (page == null) {
            return;
        }
        List<OscEvent> out = createPageChangeEvents(page, page.getFileName(), (BasicStave) stave, null);
        publishOscEvents(out);
    }

    private void addNewPageInstrumentEvents(Instrument instrument, BeatId eventBaseBeat, List<SzcoreEvent> initEvents) {
        String destination = szcore.getOscDestination(instrument.getId());
        OscEvent instrumentSlotsEvent = createInstrumentResetSlotsEvent(destination, eventBaseBeat);
        initEvents.add(instrumentSlotsEvent);
    }

    private void addNewPageEvents(Page page, BasicStave stave, BeatId eventBaseBeat, Id transportId, List<SzcoreEvent> initEvents) {
        if (page == null || stave == null || transportId == null) {
            return;
        }

        StaveId staveId = stave.getId();
        String pageName = page.getFileName();

        OscEvent pageDisplayEvent = createDisplayPageEvent(pageName, stave, eventBaseBeat);
        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transportId, pageDisplayEvent);
        } else {
            initEvents.add(pageDisplayEvent);
        }

        if(page.getPageName().equals(Consts.BLANK_PAGE_NAME)){
            return;
        }

        String address = stave.getOscAddress();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());
        OscEvent pageMapDisplayEvent = createPageMapFileEvent(pageName, address, destination, eventBaseBeat);
        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transportId, pageMapDisplayEvent);
        } else {
            initEvents.add(pageMapDisplayEvent);
        }
    }

    private void addOneOffNewPageEvents(Page page, boolean isInRndRange, BasicStave stave, BeatId pageChangeBeat, BeatId activatePageBeat, Id transportId) {
        if (transportId == null) {
            return;
        }

        LOG.info("addOneOffNewPageEvents: Add Open/Close Window Event beatId: {} ", activatePageBeat);
        ModWindowEvent openWindowEvent = createModWindowEvent(activatePageBeat, page, (PageId) pageChangeBeat.getPageId(), stave, true);
        szcore.addOneOffBaseBeatEvent(transportId, openWindowEvent);

        BeatId closeWindowBeatId = pageChangeBeat;

        if (isInRndRange) {
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
            LOG.info("Close Window Event page first beat: {} last: {}, calculated beatId: {} ", first, last, closeWindowBeatId);
        }

        ModWindowEvent closeWindowEvent = createModWindowEvent(closeWindowBeatId, page, (PageId) pageChangeBeat.getPageId(), stave, false);
        szcore.addOneOffBaseBeatEvent(transportId, closeWindowEvent);
    }

    private List<OscEvent> createPageChangeEvents(Page nextPage, String nextPageName, BasicStave nextStave, BeatId changeBeatId) {
        List<OscEvent> out = new ArrayList<>(5);
        if (nextPage == null || nextStave == null) {
            return out;
        }

        StaveId staveId = nextStave.getId();
        OscEvent pageDisplayEvent = createDisplayPageEvent(nextPageName, nextStave, changeBeatId);
        out.add(pageDisplayEvent);

        BasicPage basicPage = (BasicPage) nextPage;
        String address = nextStave.getOscAddress();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());
        if (basicPage.isSendInscoreMap() && basicPage.getInscorePageMap() != null) {
            String inscoreMap = basicPage.getInscorePageMap().toInscoreString();
            OscEvent pageMapDisplayEvent = createPageMapEvent(nextPageName, address, destination, inscoreMap, changeBeatId);
            out.add(pageMapDisplayEvent);
        } else {
            OscEvent pageMapDisplayEvent = createPageMapFileEvent(nextPage.getFileName(), address, destination, changeBeatId);
            out.add(pageMapDisplayEvent);
        }
        return out;
    }

    public void addClockBaseBeatEvent(Id transportId, Stave stave) {
        StaveId id = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(id.getInstrumentId());
        DateTickEvent dateTickEvent = eventFactory.createDateTickEvent(destination, id.getStaveNo(), 0, clock.getSystemTimeMillis());
        szcore.addClockBaseBeatTickEvent(transportId, dateTickEvent);
    }

    public void addInitClockBaseBeatEvent(Stave stave, int beatNo, List<SzcoreEvent> initEvents) {
        StaveId id = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(id.getInstrumentId());

        DateTickEvent dateTickEvent = eventFactory.createDateTickEvent(destination, id.getStaveNo(), beatNo, clock.getSystemTimeMillis());
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
        Float fl = new Float(Consts.OSC_STAVE_BEATER_Y_MIN);
        args.add(1, fl);
        initEvents.add(staveDyTickEvent);
    }

    public void addClockTickEvents(Id transportId, Stave stave) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        //Score Follow Line event
//        String address = stave.getOscAddressScoreFollower();
//        String destination = szcore.getOscDestination(id.getInstrumentId());
//        StaveClockTickEvent staveClockTickEvent = eventFactory.createStaveClockTickEvent(address, destination, id, clock.getSystemTimeMillis());
//        szcore.addClockTickEvent(transportId, staveClockTickEvent);
//
//        // Score beater event
//        address = stave.getOscAddressScoreBeater();
//        staveClockTickEvent = eventFactory.createStaveClockTickEvent(address, destination, id, clock.getSystemTimeMillis());
//        szcore.addClockTickEvent(transportId, staveClockTickEvent);

        //Beater dy position
        String address = stave.getOscAddressScoreBeater();
//        StaveDyTickEvent staveDyTickEvent = eventFactory.createStaveDyTickEvent(address, destination, id, clock.getSystemTimeMillis());
        StaveDyTickEvent staveDyTickEvent = eventFactory.createStaveDyTickEvent(address, destination, staveId, clock.getSystemTimeMillis());
        szcore.addClockTickEvent(transportId, staveDyTickEvent);
    }

    public void sendDynamicsYPositionEvent(Id instrumentId, Stave stave, double yDelta) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        //dynamics y position
        String address = stave.getOscAddressScoreDynamicsLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = Consts.DYNAMICS_LINE1_Y_MIN_POSITION - yDelta;
                break;
            case 2:
                y = Consts.DYNAMICS_LINE2_Y_MIN_POSITION - yDelta;
                break;
            default:
                LOG.error("Invalid stave dynamics Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getDynamicsValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * DYNAMICS_LINE_Y_MAX/100.0);
        if(diff < threshold) {
            LOG.debug("addDynamicsYPositionEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.info("addDynamicsYPositionEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent dynYEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, clock.getSystemTimeMillis());
        dynYEvent.setYPosition(y);
        stave.setDynamicsValue(y);

        process(dynYEvent);
    }

    public void sendSpeedYPositionEvent(Id instrumentId, Stave stave, double yDelta) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        //speed y position
        String address = stave.getOscAddressScoreSpeedLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = Consts.SPEED_LINE1_Y_MIN_POSITION - yDelta;
                break;
            case 2:
                y = Consts.SPEED_LINE2_Y_MIN_POSITION - yDelta;
                break;
            default:
                LOG.error("Invalid stave speed Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getSpeedValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * SPEED_LINE_Y_MAX/100.0);
        if(diff < threshold) {
            LOG.debug("sendSpeedYPositionEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.info("sendSpeedYPositionEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent speedEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, clock.getSystemTimeMillis());
        speedEvent.setYPosition(y);
        stave.setSpeedValue(y);

        process(speedEvent);
    }

    public void sendPositionLineYEvent(Id instrumentId, Stave stave, double yDelta) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        //position line y position
        String address = stave.getOscAddressScorePositionLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = Consts.POSITION_LINE1_Y_MIN_POSITION - yDelta;
                break;
            case 2:
                y = Consts.POSITION_LINE2_Y_MIN_POSITION - yDelta;
                break;
            default:
                LOG.error("Invalid stave position Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getPositionValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * POSITION_LINE_Y_MAX/100.0);
        if(diff < threshold) {
            LOG.debug("sendPositionLineYEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.info("sendPositionLineYEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent positionEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, clock.getSystemTimeMillis());
        positionEvent.setYPosition(y);
        stave.setPositionValue(y);

        process(positionEvent);
    }

    public void sendContentLineYEvent(Id instrumentId, Stave stave, double yDelta) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        String address = stave.getOscAddressScoreContentLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = Consts.CONTENT_LINE1_Y_MIN_POSITION - yDelta;
                break;
            case 2:
                y = Consts.CONTENT_LINE2_Y_MIN_POSITION - yDelta;
                break;
            default:
                LOG.error("Invalid stave position Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getContentValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * CONTENT_LINE_Y_MAX/100.0);
        if(diff < threshold) {
            LOG.debug("sendContentLineYEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.info("sendContentLineYEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent contentEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, clock.getSystemTimeMillis());
        contentEvent.setYPosition(y);
        stave.setContentValue(y);

        process(contentEvent);
    }

    public void sendDynamicsBoxAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }

        sendElementAlphaEvent(instrumentId, stave.getOscAddressScoreDynamicsBox(), destination, alpha);
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScoreDynamicsMidLine(), destination, alpha);
    }

    public void sendDynamicsLineAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }

        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScoreDynamicsLine(), destination, alpha);
    }

    public void sendElementPenAlphaEvent(Id instrumentId, String address, String destination, int alpha) {
        ElementAlphaEvent dynYEvent = eventFactory.createElementPenAlphaEvent(address, destination, clock.getSystemTimeMillis());
        dynYEvent.setAlpha(alpha);
        LOG.info("sendElementPenAlphaEvent sending alpha: {} to: {} addr: '{}'", alpha, instrumentId, address);
        process(dynYEvent);
    }

    public void sendElementAlphaEvent(Id instrumentId, String address, String destination, int alpha) {
        ElementAlphaEvent aEvent = eventFactory.createElementAlphaEvent(address, destination, clock.getSystemTimeMillis());
        aEvent.setAlpha(alpha);
        LOG.info("sendElementAlphaEvent sending alpha: {} to: {} addr: '{}'", alpha, instrumentId, address);
        process(aEvent);
    }

    public void sendPressureBoxAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }

        sendElementAlphaEvent(instrumentId, stave.getOscAddressScorePressureBox(), destination, alpha);
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScorePressureMidLine(), destination, alpha);
    }

    public void sendPressureLineAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }

        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScorePressureLine(), destination, alpha);
    }

    public void sendSpeedBoxAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }

        sendElementAlphaEvent(instrumentId, stave.getOscAddressScoreSpeedBox(), destination, alpha);
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScoreSpeedMidLine(), destination, alpha);
    }

    public void sendSpeedLineAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }
       sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScoreSpeedLine(), destination, alpha);
    }

    public void sendPositionBoxAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }
        sendElementAlphaEvent(instrumentId, stave.getOscAddressScorePositionBox(), destination, alpha);
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScorePositionOrdLine(), destination, alpha);
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScorePositionBridgeLine(), destination, alpha);
    }

    public void sendPositionLineAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScorePositionLine(), destination, alpha);
    }

    public void sendContentBoxAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }
        sendElementAlphaEvent(instrumentId, stave.getOscAddressScoreContentBox(), destination, alpha);
    }

    public void sendContentLineAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int alpha = 0;
        if(isEnabled) {
            alpha = 255;
        }
        sendElementPenAlphaEvent(instrumentId, stave.getOscAddressScoreContentLine(), destination, alpha);
    }

    public void sendPressureColorEvent(Id instrumentId, Stave stave, int r, int g, int b) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        String address = stave.getOscAddressScorePressureBox();
        ElementColorEvent colorEvent = eventFactory.createElementColorEvent(address, destination, clock.getSystemTimeMillis());
        colorEvent.setColor(r, g, b);
        LOG.info("sendPressureColorEvent sending r: {}  g: {}  b: {} to: {} addr: '{}'", r, g, b, instrumentId, address);
        process(colorEvent);
    }

    public void sendDynamicsColorEvent(Id instrumentId, Stave stave, int r, int g, int b) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        String address = stave.getOscAddressScoreDynamicsBox();
        ElementColorEvent colorEvent = eventFactory.createElementColorEvent(address, destination, clock.getSystemTimeMillis());
        colorEvent.setColor(r, g, b);
        LOG.info("sendDynamicsColorEvent sending r: {}  g: {}  b: {} to: {} addr: '{}'", r, g, b, instrumentId, address);
        process(colorEvent);
    }

    public void sendSpeedColorEvent(Id instrumentId, Stave stave, int r, int g, int b) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        String address = stave.getOscAddressScoreSpeedBox();
        ElementColorEvent colorEvent = eventFactory.createElementColorEvent(address, destination, clock.getSystemTimeMillis());
        colorEvent.setColor(r, g, b);
        LOG.info("sendSpeedColorEvent sending r: {}  g: {}  b: {} to: {} addr: '{}'", r, g, b, instrumentId, address);
        process(colorEvent);
    }

    public void sendPressureChangeEvent(Id instrumentId, Stave stave, double yDelta) throws Exception {
        StaveId staveId = (StaveId) stave.getId();
        String destination = szcore.getOscDestination(staveId.getInstrumentId());

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = Consts.PRESSURE_LINE1_Y_MIN_POSITION - yDelta;
                break;
            case 2:
                y = Consts.PRESSURE_LINE2_Y_MIN_POSITION - yDelta;
                break;
            default:
                LOG.error("Invalid stave pressure Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getPressureValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * PRESSURE_LINE_Y_MAX/100.0);
        if(diff < threshold) {
            LOG.debug("sendPressureChangeEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        String address = stave.getOscAddressScorePressureLine();
        LOG.info("sendPressureChangeEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent pressureEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, clock.getSystemTimeMillis());
        pressureEvent.setYPosition(y);
        stave.setPressureValue(y);

        process(pressureEvent);
    }

    public void addTempoChangeInitEvent(Tempo tempo, BeatId beatId, Id transportId, boolean isSendOscEvents, List<SzcoreEvent> initEvents) {
        long now = clock.getSystemTimeMillis();
        List<OscStaveTempoEvent> oscEvents = new ArrayList<>();

        if(isSendOscEvents) {
            OscStaveTempoEvent oscTemoEvent = eventFactory.createOscStaveTempoEvent(Consts.ALL_DESTINATIONS, 0, now);
            oscEvents.add(oscTemoEvent);
        }

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

    public TempoChangeEvent createTempoChangeEvent(Tempo tempo, BeatId beatId, String destination, Id transportId){
        long now = clock.getSystemTimeMillis();
        List<OscStaveTempoEvent> oscEvents = new ArrayList<>();
        OscStaveTempoEvent oscTempoEvent = eventFactory.createOscStaveTempoEvent(destination, tempo.getBpm(), now);
        oscEvents.add(oscTempoEvent);
        return eventFactory.createTempoChangeEvent(tempo, beatId, transportId, oscEvents, now) ;
    }

    public TransportPositionEvent createTransportPositionEvent(Id transportId, int startBaseBeatNo, int transportBeatNo, int tickNo, long positionMillis){
        long now = clock.getSystemTimeMillis();
        return eventFactory.createTransportPositionEvent(transportId, startBaseBeatNo, transportBeatNo, tickNo, positionMillis, now);
    }

    public void removeExistingBeatTransportEvents(BeatId beatId, Id transportId, TIntObjectMap<List<SzcoreEvent>> baseBeatEvents, MusicEventType type){
        if(beatId == null){
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
//                    LOG.debug("Already have transport event " + type + ", removing event: " + existing);
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

//        LOG.info("Added stop event: " + stopEvent);
    }

    public void addPrepStaveChangeEvent(BeatId executeOnBaseBeat, BeatId activateOnBaseBeat, BeatId deactivateOnBaseBeat, BeatId pageChangeOnBaseBeat, PageId nextPageId, Id transportId){
        PrepStaveChangeEvent event = eventFactory.createPrepStaveChangeEvent(executeOnBaseBeat, activateOnBaseBeat, deactivateOnBaseBeat, pageChangeOnBaseBeat, nextPageId, clock.getSystemTimeMillis());
        szcore.addScoreBaseBeatEvent(transportId, event);
    }

    public void addActiveStaveChangeEvent(StaveId staveId, boolean isActive, BeatId changeOnBaseBeat, Id transportId, Instrument instrument, List<SzcoreEvent> initEvents) {

        StaveActiveChangeEvent activeStaveChangeEvent = createActiveStateChangeEvent(staveId, isActive, changeOnBaseBeat, instrument);

        if (initEvents == null) {
            szcore.addScoreBaseBeatEvent(transportId, activeStaveChangeEvent);
        } else {
            initEvents.add(activeStaveChangeEvent);
        }

    }

    private StaveActiveChangeEvent createActiveStateChangeEvent(StaveId staveId, boolean isActive, BeatId changeOnBaseBeat, Instrument instrument){
        String destination = szcore.getOscDestination(instrument.getId());
        StaveActiveChangeEvent activeStaveChangeEvent = eventFactory.createActiveStaveChangeEvent(staveId, isActive, changeOnBaseBeat, destination, clock.getSystemTimeMillis());
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

    public void addOneOffActiveStaveChangeEvent(StaveId staveId, boolean isActive, BeatId changeOnBaseBeat, Id transportId, Instrument instrument) {
        StaveActiveChangeEvent activeStaveChangeEvent = createActiveStateChangeEvent(staveId, isActive, changeOnBaseBeat, instrument);
        szcore.addOneOffBaseBeatEvent(transportId, activeStaveChangeEvent);
    }

    public void addPrecountSetupEvent(boolean isPrecount, int precountBeatNo, long precountTimeMillis,
                                      long initBeaterInterval, Id transportId, List<SzcoreEvent> initEvents) {
        if (!isPrecount) {
            return;
        }
        PrecountBeatSetupEvent activeStaveChangeEvent = eventFactory.createPrecountBeatSetupEvent(isPrecount, precountBeatNo,
                precountTimeMillis, initBeaterInterval, transportId, clock.getSystemTimeMillis());
        initEvents.add(activeStaveChangeEvent);
    }

    //TODO FIX this needs to be dynamicly set
    private OSCPortOut getInstrumentOscPort(Instrument instrument) {
        try {
            InetAddress address;
            if (instrumentPortRequestCount == 0) {
                address = InetAddress.getLocalHost();
            } else {
                address = null;
            }
            int newVal = instrumentPortRequestCount + 1;
            instrumentPortRequestCount = newVal;
            int remotePort = Consts.DEFAULT_OSC_PORT;
            return new OSCPortOut(address, remotePort);
        } catch (Exception e) {
            LOG.error("Failed to create OSCPort", e);
        }
        return null;
    }

    @Override
    public void play()  throws Exception {
        LOG.info("##### Play");
        if (szcore == null) {
            LOG.error("Invalid Score NULL");
            return;
        }

        if (!isReadyToPlay()) {
            throw new Exception("System not ready to play. Check score instrumentation and position");
        }

        Collection<Transport>transports = szcore.getTransports();

        for (Transport transport : transports) {
            transport.start();
        }

//        LOG.info("Started all transports");
    }

    @Override
    public void stop() {
        LOG.info("##### Stopping Play");
        if (szcore == null) {
            LOG.error("Invalid Score NULL");
            return;
        }

        Collection<Transport>transports = szcore.getTransports();
        for (Transport transport : transports) {
            transport.stop();
        }

        sendStopToClients();

        LOG.info("Stopped all transports");
    }

    private void sendStopToClients() {
        if (szcore == null) {
            return;
        }

        OscStopEvent oscStopEvent = eventFactory.createOscStopEvent(Consts.ALL_DESTINATIONS, clock.getSystemTimeMillis());
        publishOscEvent(oscStopEvent);

        WebScoreStopEvent webStopEvent = eventFactory.createWebScoreStopEvent(clock.getSystemTimeMillis());
        process(webStopEvent);
    }

    private ModWindowEvent createModWindowEvent(BeatId beatId, Page nextPage, PageId currentPageId, Stave stave, boolean isOpen) {
        return eventFactory.createModWindowEvent(beatId, nextPage, currentPageId, stave, isOpen, clock.getSystemTimeMillis());
    }

    private OscEvent createDisplayPageEvent(String pageName, BasicStave stave, BeatId eventBeatId) {
        if (pageName == null || stave == null) {
            return null;
        }

        StaveId id = stave.getId();
        String address = stave.getOscAddress();
        String filename = Consts.RSRC_DIR + pageName + Consts.PNG_FILE_EXTENSION;
        ArrayList<Object> args = new ArrayList<>();
        args.add(Consts.OSC_INSCORE_SET);
        args.add(Consts.OSC_INSCORE_FILE);
        args.add(filename);

        String destination = szcore.getOscDestination(id.getInstrumentId());
        return eventFactory.createPageDisplayEvent(address, args, eventBeatId, destination, clock.getSystemTimeMillis());

    }

    private OscEvent createBeatScriptEvent(Script script, BeatId eventBeatId){
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

    private WebScoreEvent createWebScoreEvent(List<WebScoreScript> scripts, BeatId eventBeatId) {
        if (scripts == null || eventBeatId == null) {
            return null;
        }

        return eventFactory.createWebScoreEvent(eventBeatId, scripts, clock.getSystemTimeMillis());
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

    private WebScoreResetEvent createWebScoreResetEvent(BeatId beatId) {
        if (beatId == null) {
            return null;
        }

        List<WebScoreScript> scripts = webScore.getBeatResetScripts(beatId);
        LOG.info("createWebScoreResetEvent: beat: {} scripts {}", beatId.getBeatNo(), scripts);
        if (scripts == null || scripts.isEmpty()) {
            return null;
        }
        return eventFactory.createWebScoreResetEvent(beatId, scripts, clock.getSystemTimeMillis());
    }

    private ScriptingEngineResetEvent createScriptingEngineResetEvent(BeatId beatId) {
        if (beatId == null) {
            return null;
        }

        List<ScriptingEngineScript> scripts = scriptingEngine.getBeatResetScripts(beatId);
        LOG.info("createWebScoreResetEvent: beat: {} scripts {}", beatId.getBeatNo(), scripts);
        if (scripts == null || scripts.isEmpty()) {
            return null;
        }
        return eventFactory.createScriptingEngineResetEvent(beatId, scripts, clock.getSystemTimeMillis());
    }

    private OscEvent createPageMapFileEvent(String pageName, String address, String destination, BeatId eventBeatId) {
        if (pageName == null || address == null) {
            return null;
        }

        String mapFilename = Consts.RSRC_DIR + pageName + Consts.INSCORE_FILE_SUFFIX + Consts.TXT_FILE_EXTENSION;
        ArrayList<Object> mapargs = new ArrayList<>();
        mapargs.add(Consts.OSC_INSCORE_MAPF);
        mapargs.add(mapFilename);

        return eventFactory.createPageMapDisplayEvent(address, mapargs, eventBeatId, destination, clock.getSystemTimeMillis());

    }

    private OscEvent createPageMapEvent(String pageName, String address, String destination, String map, BeatId eventBeatId){
        if (pageName == null || address == null) {
            return null;
        }

        ArrayList<Object> mapargs = new ArrayList<>();
        mapargs.add(Consts.OSC_INSCORE_MAP);
        mapargs.add(map);

        return eventFactory.createPageMapDisplayEvent(address, mapargs, eventBeatId, destination, clock.getSystemTimeMillis());

    }

    private OscEvent createInstrumentSlotsEvent(String destination, String instrumentsCsv, BeatId beatId) {
        if (instrumentsCsv == null || instrumentsCsv.length() < 1) {
            return null;
        }
        return eventFactory.createInstrumentSlotsEvent(instrumentsCsv, destination, clock.getSystemTimeMillis(), beatId);
    }

    private OscEvent createInstrumentResetSlotsEvent(String destination, BeatId beatId) {
        return eventFactory.createResetInstrumentSlotsEvent(destination, clock.getSystemTimeMillis(), beatId);
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
        LOG.info("setPosition time: " + millis);
        if(isInitDone){
            resetTasks();
        }

        isInitDone = false;
        Collection<Transport>transports = szcore.getTransports();
        Transport transport = transports.iterator().next();

        BeatId beatId = szcore.getBeatForTime(millis, transport.getId());
        if (beatId == null) {
            LOG.error("Failed to find beat for time: " + millis);
            return;
        }

        Beat startBeat = szcore.getBeat(beatId);
        if(startBeat == null){
            LOG.error("Invalid start beat NULL ");
            return;
        }

        Beat upbeat = szcore.getUpbeat(beatId);
        if(upbeat != null){
            startBeat = upbeat;
        }

        BeatId startBeatId = startBeat.getBeatId();

        long startMillis = szcore.getBeatTime(startBeatId);

        scheduler.setElapsedTimeMillis(startMillis);
        if (szcore.isPrecount()) {
            scheduler.setPrecountTimeMillis(szcore.getPrecountMillis());
        }

        int baseBeatNo = startBeatId.getBaseBeatNo();
        boolean isZeroPosition = baseBeatNo == 0;

        this.startBaseBeat = baseBeatNo;

        initEvents = createRequiredEventsForNewPosition(beatId.getBaseBeatNo());

        processInitEvents(initEvents);
    }

    private void resetTasks() {
        scheduler.resetScheduledTasks();
        initEvents.clear();
    }

    private List<SzcoreEvent> createRequiredEventsForNewPosition(int beatNo) {
        List<SzcoreEvent> initEvents = new ArrayList<>();
        if (szcore == null) {
            LOG.error("Invalid NULL score");
            return initEvents;
        }

        Collection<Instrument> instruments = szcore.getInstruments();
        List<Id> transportIds = new ArrayList<>();
        for (Instrument instrument : instruments) {
            InstrumentId instrumentId = (InstrumentId) instrument.getId();
            boolean isAudioOrVideo = instrument.isAv();
            boolean isScoreInstrument = !isAudioOrVideo;
            Transport transport = szcore.getInstrumentTransport(instrumentId);
            Id transportId = transport.getId();
            BeatId instrumentBeatId = szcore.getInstrumentBeatIds(transport.getId(), instrumentId, beatNo);
            if(instrumentBeatId == null){
                continue;
            }

            Beat beat = szcore.getBeat(instrumentBeatId);
            Beat startBeat = beat;
            int startBaseBeatNo = startBeat.getBaseBeatUnitsNoAtStart();

            Beat upbeat = szcore.getUpbeat(instrumentBeatId);
            if(upbeat != null){
               startBeat = upbeat;
            }
            BeatId startBeatId = startBeat.getBeatId();
            int upbeatBaseBeatNo = startBeat.getBaseBeatUnitsNoAtStart();
            long startMillis = szcore.getBeatTime(startBeatId);

            InstrumentBeatTracker instrumentBeatTracker = instrumentBeatTrackers.get(instrumentId);
            if(instrumentBeatTracker == null){
                instrumentBeatTracker = new InstrumentBeatTracker(transport, instrumentId);
                instrumentBeatTrackers.put(instrumentId, instrumentBeatTracker);
            }
            instrumentBeatTracker.setCurrentBeat(startBeat);

            Bar bar = szcore.getBar(instrumentBeatId.getBarId());
            if(bar == null){
                continue;
            }

            Tempo tempo = bar.getTempo();
            TempoModifier tempoModifier = transportTempoModifiers.get(transportId);
            if(tempoModifier != null) {
                tempo = new TempoImpl(tempo, tempoModifier);
            }
            TimeSignature timeSignature = bar.getTimeSignature();
            Page page = szcore.getPage(bar.getPageId());

            BasicStave currentStave = null;
            BasicStave nextStave = null;
            if(isScoreInstrument) {
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

                currentStave.setActive(true);
                nextStave.setActive(false);
            }


            if(!transportIds.contains(transportId)) {
                //set beatNo -1 as transport increments on startup
//LOG.info("Adding transport and time start events, upbeatBaseBeatNo: "+ (upbeatBaseBeatNo - 1) + "  startBeatId: " + startBeatId);
                int transportStartBeat = upbeatBaseBeatNo - 1;
                int transportStartTick = 0;
                long transportStartMillis = startMillis;
                addTransportInitEvents(tempo, timeSignature, upbeatBaseBeatNo, transportStartBeat, transportStartTick, transportStartMillis, transportId, initEvents);
                //add start tempo
                addOneOffTempoChangeEvent(tempo, startBeatId, Consts.ALL_DESTINATIONS, transport.getId());
                transportIds.add(transportId);
            }

            if(isScoreInstrument) {
                addNewPageEvents(szcore.getBlankPage(), currentStave, null, transportId, initEvents);
                addNewPageEvents(szcore.getBlankPage(), nextStave, null, transportId, initEvents);

                addPageInitEvents(transport, instrument, currentStave, nextStave, page, initEvents);
                addInitClockBaseBeatEvent(currentStave, upbeatBaseBeatNo, initEvents);
                addInitStaveStartMarkEvent(currentStave, startBaseBeatNo, initEvents);
                addInitStaveDyEvent(currentStave, initEvents);
                addNewPageInstrumentEvents(instrument, null, initEvents);

                ScoreRandomisationStrategy strategy = szcore.getRandomisationStrategy();
                boolean isInRange = strategy.isInActiveRange(instrumentId, page);
                if (isInRange) {
                    if (strategy.isPageRecalcTime()) {
                        strategy.recalcStrategy(page);
                        int pageQuantity = strategy.getNumberOfRequiredPages();
                        List<Integer> pageIds = webScore.prepareNextTilesToPlay(pageQuantity);
                        strategy.setPageSelection(pageIds);
                    }
                    String pageFileName = strategy.getRandomPageFileName(instrumentId);
                    if (pageFileName == null) {
                        pageFileName = page.getFileName();
                        LOG.info("createRequiredEventsForNewPosition: Invalid random page file name, using: {}", pageFileName);
                    } else {
                        LOG.info("createRequiredEventsForNewPosition: Using random page file name: {} for instrument: {}", pageFileName, instrumentId);
                    }
                    List<OscEvent> pageChangeEvents = createPageChangeEvents(page, pageFileName, currentStave, null);
                    initEvents.addAll(pageChangeEvents);
                }
            } else {
                addInitAvEvent(transport, instrument, initEvents, startBeatId);
                addInitWebScoreEvent(transport, initEvents, startBeatId);
                addInitScriptingEvent(transport, initEvents, startBeatId);
            }
        }

        return initEvents;
    }

    @Override
    public Score getScore() {
        return szcore;
    }

    @Override
    public void reset() throws Exception{
        if(scheduler.isActive()){
            LOG.warn("Scheduler is active, can not perform reset");
            throw new Exception("Scheduler is active, can not perform reset");
        }

        isInitDone = false;
        isScoreLoaded = false;

        resetClients();

        if(szcore != null){
            for(Instrument instrument : szcore.getInstruments()){
                oscPublisher.removeDestination(instrument.getName());
            }
        }

        szcore = null;

        isScoreLoaded = false;
        if(scheduler != null) {
            scheduler.reset();
        }
    }

    @Override
    public void subscribe(SzcoreEngineEventListener eventListener) {
        scoreEventListeners.add(eventListener);
    }

    @Override
    public void subscribe(WebScoreStateListener eventListener) {
        webScoreStateListeners.add(eventListener);
    }

    @Override
    public void setTempoModifier(Id transportId, TempoModifier tempoModifier) {
        if(transportId == null || tempoModifier == null){
            return;
        }
        transportTempoModifiers.put(transportId, tempoModifier);
//LOG.info("Received tempo modifier: " + tempoModifier);
        Transport transport = szcore.getTransport(transportId);
        Tempo currentTempo = transport.getTempo();
        TempoModifier currentModifier = currentTempo.getTempoModifier();
        if(tempoModifier.equals(currentModifier)){
            return;
        }

        Tempo newTempo = new TempoImpl(currentTempo, tempoModifier);

        TempoChangeEvent tempoChangeEvent = createTempoChangeEvent(newTempo, null, Consts.ALL_DESTINATIONS, transportId);
        process(tempoChangeEvent);
    }

    @Override
    public void setRandomisationStrategy(List<Integer> randomisationStrategy) {
        if(randomisationStrategy == null) {
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
    public void setDynamicsValue(long value, List<Id> instrumentIds) throws Exception {
        LOG.debug("setDynamicsValue: {} ", value);
        onDynamicsValueChange(value, instrumentIds);
    }

    @Override
    public void onUseDynamicsOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUseDynamicsOverlay: {} ", value);
        setDynamicsOverlay(value, instrumentIds);
    }

    @Override
    public void onUseDynamicsLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUseDynamicsLine: {} ", value);
        setDynamicsLine(value, instrumentIds);
    }

    @Override
    public void setPressureValue(long value, List<Id> instrumentIds) throws Exception {
        LOG.debug("setPressureValue: {} ", value);
        onPressureValueChange(value, instrumentIds);
    }

    @Override
    public void onUsePressureOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUsePressureOverlay: {} ", value);
        setPressureOverlay(value, instrumentIds);
    }

    @Override
    public void onUsePressureLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUsePressureLine: {} ", value);
        setPressureLine(value, instrumentIds);
    }

    @Override
    public void setSpeedValue(long value, List<Id> instrumentIds) throws Exception {
        LOG.debug("setSpeedValue: {} ", value);
        onSpeedValueChange(value, instrumentIds);
    }

    @Override
    public void onUseSpeedOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUseSpeedOverlay: {} ", value);
        setSpeedOverlay(value, instrumentIds);
    }

    @Override
    public void onUseSpeedLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUseSpeedLine: {} ", value);
        setSpeedLine(value, instrumentIds);
    }

    @Override
    public void setPositionValue(long value, List<Id> instrumentIds) throws Exception {
        LOG.debug("setPositionValue: {} ", value);
        onPositionValueChange(value, instrumentIds);
    }

    @Override
    public void onUsePositionOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUsePositionOverlay: {} ", value);
        setPositionOverlay(value, instrumentIds);
    }

    @Override
    public void onUsePositionLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUsePositionLine: {} ", value);
        setPositionLine(value, instrumentIds);
    }

    @Override
    public void setContentValue(long value, List<Id> instrumentIds) throws Exception {
        LOG.debug("setContentValue: {} ", value);
        onContentValueChange(value, instrumentIds);
    }

    @Override
    public void onUseContentOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUseContentOverlay: {} ", value);
        setContentOverlay(value, instrumentIds);
    }

    @Override
    public void onUseContentLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.debug("onUseContentLine: {} ", value);
        setContentLine(value, instrumentIds);
    }

    @Override
    public void onIncomingWebEvent(IncomingWebEvent webEvent) throws Exception {

        IncomingWebEventType type = webEvent.getWebEventType();
        switch (type) {
            case ELEMENT_SELECTED:
                processElementSelected((ElementSelectedEvent)webEvent);
                break;
            case WEB_START:
                processWebStart((WebStartEvent)webEvent);
                break;
            default:
                LOG.info("onIncomingWebEvent: unknown IncomingWebEventType: {}", type);
        }
    }

    @Override
    public void onWebScoreStateChange(WebScoreStateExport webScoreStateExport) throws Exception {
        notifyListeners(webScoreStateExport);
    }

    @Override
    public void onWebScoreStateDeltaChange(WebScoreStateDeltaExport webScoreStateDeltaExport) throws Exception {
        notifyListeners(webScoreStateDeltaExport);
    }

    @Override
    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) throws Exception {
        publishWebEvent(webEvent);
    }

    private void processElementSelected(ElementSelectedEvent webEvent) {
        LOG.info("processElementSelected: ");
        String elementId = webEvent.getElementId();
        boolean isSelected = webEvent.isSelected();

        webScore.setSelectedElement(elementId, isSelected);
    }

    private void processWebStart(WebStartEvent webEvent) {
        LOG.info("processWebStart: ");
        webScore.resetState();
        webScore.pushServerState();
        webScore.startScore();
    }

    public boolean isReadyToPlay(){
        return isScoreLoaded && isInitDone;
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
                processOscEvent((OscEvent) event, beatNo, tickNo);
                break;
            case MUSIC:
                processMusicEvent((MusicEvent) event, beatNo, tickNo);
                break;
            case WEB_SCORE:
                processWebScoreEvent((WebScoreEvent) event, beatNo, tickNo);
                break;
            case SCRIPTING_ENGINE:
                processScriptingEngineEvent((ScriptingEngineEvent) event, beatNo, tickNo);
                break;
            default:
                LOG.error("Unknown event type " + type);
        }

        notifyListeners(event, beatNo, tickNo);
    }

    private void notifyListeners(SzcoreEvent event, int beatNo, int tickNo) {
        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onEvent(event, beatNo, tickNo);
        }
    }

    private void notifyListeners(WebScoreStateExport webScoreStateExport) {
        for (WebScoreStateListener listener : webScoreStateListeners) {
            listener.onWebScoreStateChange(webScoreStateExport);
        }
    }

    private void notifyListeners(WebScoreStateDeltaExport deltaExport) {
        for (WebScoreStateListener listener : webScoreStateListeners) {
            listener.onWebScoreStateDeltaChange(deltaExport);
        }
    }

    private void notifyListenersOnBeat(Id transportId, int beatNo, int baseBeatNo) {
//LOG.info("Sending beat event beatNo: " + beatNo + " baseBeatNo: " + baseBeatNo);

        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onTransportBeatEvent(transportId, beatNo, baseBeatNo);
        }
    }

    private void notifyListenersOnTempoChange(Id transportId, Tempo tempo) {
//LOG.info("Sending tempo change tempo: " + tempo);

        for(SzcoreEngineEventListener listener : scoreEventListeners){
            listener.onTransportTempoChange(transportId, tempo);
        }
    }

    private void notifyListenersOnTransportPositionChange(Id transportId, int beatNo) {
//        LOG.info("Sending transport position change beatNo: " + beatNo);

        for(SzcoreEngineEventListener listener : scoreEventListeners){
            listener.onTransportPositionChange(transportId, beatNo);
        }
    }

    private void notifyListenersOnTick(Id transportId, int beatNo, int baseBeatNo, int tickNo) {
        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onTransportTickEvent(transportId, beatNo, baseBeatNo, tickNo);
        }
    }

    private void processScriptingEngineEvent(ScriptingEngineEvent event, int beatNo, int tickNo) {
        ScriptingEngineEventTask task = taskFactory.createScriptingEngineEventTask(0, event, scriptingEngine);
        if (task != null) {
            scheduler.add(task);
        }
    }

    private void processWebScoreEvent(WebScoreEvent event) {
        processWebScoreEvent(event, 0, 0);
    }

    private void processWebScoreEvent(WebScoreEvent event, int beatNo, int tickNo) {
        WebScoreEventType eventType = event.getWebScoreEventType();

        switch (eventType) {
            case PRECOUNT:
            case RESET:
            case STOP:
            case PLAY_TILES:
            case SELECT_TILES:
            case INSTRUCTIONS:
                webScore.processWebScoreEvent(event);
                break;
            case SCRIPT:
            default:
                WebScoreEventTask task = taskFactory.createWebScoreEventTask(0, event, webScore);
                if (task != null) {
                    scheduler.add(task);
                }
        }
    }

    private void processMusicEvent(MusicEvent event, int beatNo, int tickNo) {
        MusicEventType type = event.getMusicEventType();

        MusicTask task = null;
        //TODO use playtime
        long playTime = getBeatPlayTime(event.getEventBaseBeat(), beatNo);
        switch (type) {
            case TEMPO_CHANGE:
                TempoChangeEvent tempoChangeEvent = (TempoChangeEvent) event;
                Transport transport = transportFactory.getTransport(tempoChangeEvent.getTransportId());
                TempoModifier tempoModifier = transportTempoModifiers.get(transport.getId());
                boolean isSchedulerRunning = scheduler.isActive();
                task = taskFactory.createTempoChangeTask(tempoChangeEvent, 0, transport, oscPublisher, tempoModifier, isSchedulerRunning);
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
//                LOG.info("Processing StaveActiveChangeEvent beatNo: " + beatNo + " event: " + event);
                StaveId staveId = staveActiveChangeEvent.getStaveId();
                Stave instrumentStave = szcore.getStave(staveId);
                task = taskFactory.createActiveStaveChangeTask(staveActiveChangeEvent, 0, instrumentStave, oscPublisher);
                break;
            case PREP_STAVE_ACTIVE_CHANGE:
                PrepStaveChangeEvent prepStaveChangeEvent = (PrepStaveChangeEvent) event;
//                LOG.info("Processing PrepStaveChangeEvent beatNo: " + beatNo + " startBaseBeat: " + startBaseBeat + " event: " + event);
                if(beatNo == startBaseBeat){
                    task = null;
                    LOG.info("Ignoring PrepStaveChangeEvent as its starting position beatNo: " + beatNo + " startBaseBeat: " + startBaseBeat);
                    break;
                }
                task = taskFactory.createPrepStaveChangeTask(prepStaveChangeEvent, 0, this);
                break;
            case PRECOUNT_BEAT_SETUP:
                PrecountBeatSetupEvent precountBeatSetupEvent = (PrecountBeatSetupEvent) event;
                Id transportId = precountBeatSetupEvent.getInstrumentId();
                transport = szcore.getTransport(transportId);
                task = taskFactory.createPrecountBeatSetupTask(precountBeatSetupEvent, Consts.ALL_DESTINATIONS, transport, scheduler, oscPublisher, eventFactory, taskFactory, webScore, clock);
                break;
            case TRANSITION:
                TransitionEvent transitionEvent = (TransitionEvent) event;
                task = taskFactory.createTransitionSetupTask(transitionEvent, transitionEvent.getDestination(), scheduler, oscPublisher, eventFactory, clock);
                break;
            case STOP:
                StopEvent stopEvent = (StopEvent) event;
                BeatId beatId = event.getEventBaseBeat();
                long elapsedTime = clock.getElapsedTimeMillis();
                long duration = getBeatDuration(beatId);
                long stopTime = elapsedTime + duration;
                task = taskFactory.createStopPlayTask(stopEvent, stopTime, this);
                break;
            case MOD_WINDOW:
                ModWindowEvent modWindowEvent = (ModWindowEvent) event;
                task = taskFactory.createModWindowTask(modWindowEvent, 0, this);
                break;
            default:
                LOG.error("Unknown event: " + event);

        }

        if (task != null) {
            scheduler.add(task);
        }
    }

    private long getBeatPlayTime(BeatId beatId, int currentBeat) {
        long playTime = 0L;
        if (beatId != null) {
            int onBeatNo = beatId.getBeatNo();
            if (currentBeat == onBeatNo) {
                playTime = clock.getElapsedTimeMillis();
            } else {
                playTime = szcore.getBeatTime(beatId);
            }
        }

        return playTime;
    }

    private long getBeatDuration(BeatId beatId) {
        long duration = 0L;
        if (beatId == null) {
           return duration;
        }

        Beat beat = szcore.getBeat(beatId);
        if(beat != null) {
            duration = beat.getDurationMillis();
        }

        return duration;
    }

    private void processOscEvent(OscEvent event, int beatNo, int tickNo) {
        OscEventType type = event.getOscEventType();
        switch (type) {
            case STAVE_CLOCK_TICK:
                processClockTickEvent((StaveClockTickEvent) event, beatNo);
                break;
            case STAVE_TICK_DY:
                processDyTickEvent((StaveDyTickEvent) event, beatNo, tickNo);
                break;
            case STAVE_Y_POSITION:
                processStaveYPosition((StaveYPositionEvent) event, beatNo, tickNo);
                break;
            case STAVE_DATE_TICK:
                processStaveDateTickEvent((StaveDateTickEvent) event, beatNo);
                break;
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
                processBeatScriptEvent((BeatScriptEvent) event, beatNo);
                break;
            case OSC_SCRIPT:
                processOscScriptEvent((OscScriptEvent) event, beatNo);
                break;
            case ELEMENT_ALPHA:
            case ELEMENT_COLOR:
            case ELEMENT_Y_POSITION:
            case RESET_INSTRUMENT:
            case RESET_SCORE:
            case RESET_STAVES:
            case INSTRUMENT_SLOTS:
            case INSTRUMENT_RESET_SLOTS:
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
        if(eventBeat > 0){
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
        int beatToSend = beatNo;
        if(beatNo == 0){
            beatToSend = event.getBeatNo();
        }
        event.setBeatNo(beatToSend);
        event.addCommandArg();
        publishOscEvent(event);
    }


    private void processBeatScriptEvent(BeatScriptEvent event, int beatNo) {
        if (event == null) {
            return;
        }
        publishOscEvent(event);
    }

    private void processOscScriptEvent(OscScriptEvent event, int beatNo) {
        if (event == null) {
            return;
        }
        publishOscEvent(event);
    }

    private void processClockTickEvent(StaveClockTickEvent event, int beatNo) {
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

    private void processDyTickEvent(StaveDyTickEvent event, int beatNo, int tickNo) {

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
        if(isRunning && (beatNo < trackerBeat)){
//LOG.info("Not publishing DY, trackerBeat: " + trackerBeat + " beatNo: " + beatNo);
            return;
        }

        int beatPercComplete = beatTracker.getPercentCompleted();
        double y = stave.getBeaterYPositionMin();
        double dyMax = stave.getBeaterYPositionDelta();
        int dyPerc = beatFollowerPositionStrategy.getYPercent(beatPercComplete);
        double dy = dyMax * dyPerc/100.0;
        if(y > 0.0){
            y += dy;
        } else {
            y -= dy;
        }

        Collection<Instrument> instruments = szcore.getInstruments();
        Instrument first = instruments.iterator().next();
        int trackerBeatNo = beatTracker.getBeatTickNo();
        int trackerTick = beatTracker.getTick();
        Beat currentBeat = beatTracker.getCurrent();

//        if(first.getId().equals(instrumentId)) {
//            LOG.debug("Calculated dy: " + y + " beatNo: " + beatNo + " tickNo: " + tickNo + " beatPercComplete: " + beatPercComplete + " dyPerc: " + dyPerc + " trackerBeatNo: " + trackerBeatNo + " trackerTick:" + trackerTick + " trCbBaseBeatAtStart: " + currentBeat.getBaseBeatUnitsNoAtStart() + " isUpbeat: " + currentBeat.isUpbeat());
//        }

        ArrayList<Object> args = (ArrayList<Object>) event.getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        Float fl = new Float(y);
        args.add(1, fl);

        publishOscEvent(event);
    }

    private void processStaveYPosition(StaveYPositionEvent event, int beatNo, int tickNo) {
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
        double dy = dyMax * beatPercComplete/100.0;
        if(y > 0.0){
            y += dy;
        } else {
            y -= dy;
        }

//        LOG.debug("Calculated y: " + y + " beatNo: " + beatNo + " tickNo: " + tickNo);

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
        String destination = event.getDestination();
        if (!oscPublisher.isDestination(destination)) {
//            LOG.debug("publishOscEvent: destination {} is not active, ignoring event: {}", destination, event);
            return;
        }

        oscPublisher.process(event);
    }

    private void publishWebEvent(OutgoingWebEvent event) {
        webPublisher.process(event);
    }

    private void processInitEvents(List<SzcoreEvent> initEvents) {
        if (initEvents == null) {
            return;
        }

        for (SzcoreEvent initEvent : initEvents) {
//            LOG.info("Processing init event: " + initEvent);
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
        int currentBeatNo = 0;
        int currentBaseBeatNo = 0;
        int currentTick = 0;
        if(instrumentIds != null){
            for(Id instrumentId : instrumentIds){
                InstrumentBeatTracker instrumentBeatTracker = instrumentBeatTrackers.get(instrumentId);
                if(instrumentBeatTracker == null){
                    continue;
                }
                Beat beat = null;
                if(beatIds != null) {
                    for (BeatId beatId : beatIds) {
                        if (instrumentId.equals(beatId.getInstrumentId())) {
                            beat = szcore.getBeat(beatId);
                            break;
                        }
                    }
                }

                if(beat != null) {
                    Beat currentBeat = instrumentBeatTracker.getCurrent();
                    if (currentBeat == null || currentBeat.getBeatNo() < beat.getBeatNo()) {
                        instrumentBeatTracker.setCurrentBeat(beat);
                    }
//                    currentBeatNo = beat.getBeatNo();
//                    currentBaseBeatNo = beat.getBaseBeatUnitsNoAtStart();
//                    currentTick = instrumentBeatTracker.getTick();
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
                if(event == null) {
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
        if(beatIds != null && !beatIds.isEmpty()){
            for(BeatId beatId : beatIds) {
                Beat beat = szcore.getBeat(beatId);
                Id instrumentId = beatId.getInstrumentId();
                InstrumentBeatTracker instrumentBeatTracker = instrumentBeatTrackers.get(instrumentId);
                if(instrumentBeatTracker == null){
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

    public InstrumentBeatTracker getInstrumentBeatTracker(Id instrumentId) {
        return instrumentBeatTrackers.get(instrumentId);
    }


    @Override
    public void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst) {
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

        boolean isOptOut = false;
        if (slotInstrument.equals(sourceInst)) {
            isOptOut = true;
        }

        ScoreRandomisationStrategy randomisationStrategy = szcore.getRandomisationStrategy();
        randomisationStrategy.optOutInstrument(inst, replaceInst, isOptOut);

        List<InstrumentId> slotInstrumentIds = randomisationStrategy.getInstrumentSlotIds();
        String instSlotsCsv = ParseUtil.convertToCsv(slotInstrumentIds);

        Collection<Instrument> instruments = szcore.getScoreInstruments();

        for (Instrument instrument : instruments) {
            LOG.info("processSelectInstrumentSlot: prepare sending csv: {} to instrument {}", instSlotsCsv, instrument.getName());
            String destination = szcore.getOscDestination(instrument.getId());
            OscEvent instrumentSlotsEvent = createInstrumentSlotsEvent(destination, instSlotsCsv, null);
            publishOscEvent(instrumentSlotsEvent);
        }

        Page defaultInstPage = szcore.getContinuousPage(inst.getId());
        InstrumentId instId = (InstrumentId) inst.getId();
        Stave stave = szcore.getNextStave(instId);
        sendRndPageFileUpdate(defaultInstPage, stave, instId);

        Page defaultReplaceInstPage = szcore.getContinuousPage(replaceInst.getId());
        InstrumentId replaceInstId = (InstrumentId) replaceInst.getId();
        Stave replaceStave = szcore.getNextStave(replaceInstId);
        sendRndPageFileUpdate(defaultReplaceInstPage, replaceStave, replaceInstId);
    }

    private void processTempoChange(Id transportId, Tempo tempo) {
        notifyListenersOnTempoChange(transportId, tempo);
    }


    private void processTransportPositionChange(Id transportId, int beatNo) {
        notifyListenersOnTransportPositionChange(transportId, beatNo);
    }


    private void onDynamicsValueChange(long value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }
        int r = 255;
        int g = 255;
        int b = 255;
        double scaled = dynamicsValueScaler.scaleValue(value);
        LOG.info("onDynamicsValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        if(value > 50) {
            g = (int) Math.round(dynamicsForteColorValueScaler.scaleValue(value));
            b = g;
        } else if(value < 50) {
            r = (int) Math.round(dynamicsPianoColorValueScaler.scaleValue(value));
            g = r;
        }

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendDynamicsColorEvent(instrumentId, stave, r, g, b);
                sendDynamicsYPositionEvent(instrumentId, stave, scaled);
            }
        }
    }

    private void setDynamicsOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setDynamicsOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendDynamicsBoxAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void setDynamicsLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setDynamicsLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendDynamicsLineAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void onPressureValueChange(long value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {

                if(value > 50) {
                    int scaled = (int) Math.round(pressureColorValueScaler.scaleValue(value));
                    LOG.info("onPressureValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));
                    sendPressureColorEvent(instrumentId, stave, scaled, scaled, scaled);
                }

                double scaled = pressureLineValueScaler.scaleValue(value);
                sendPressureChangeEvent(instrumentId, stave, scaled);
            }
        }
    }

    private void setPressureOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }
        LOG.info("setPressureOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendPressureBoxAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void setPressureLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setPressureLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendPressureLineAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void onSpeedValueChange(long value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }

        int r = 255;
        int g = 255;
        int b = 255;
        double scaled = speedValueScaler.scaleValue(value);
        LOG.info("onSpeedValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        if(value > 50) {
            b = (int) Math.round(speedFastColorValueScaler.scaleValue(value));
            r = b;
        } else if(value < 50) {
            g = (int) Math.round(speedSlowColorValueScaler.scaleValue(value));
        }

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendSpeedColorEvent(instrumentId, stave, r, g, b);
                sendSpeedYPositionEvent(instrumentId, stave, scaled);
            }
        }
    }

    private void setSpeedOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }
        LOG.info("setSpeedOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendSpeedBoxAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void setSpeedLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setSpeedLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendSpeedLineAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void onPositionValueChange(long value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }
        double scaled = positionValueScaler.scaleValue(value);
        LOG.info("onPositionValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendPositionLineYEvent(instrumentId, stave, scaled);
            }
        }
    }

    private void setPositionOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }
        LOG.info("setPositionOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendPositionBoxAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void setPositionLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setPositionLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendPositionLineAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void onContentValueChange(long value, List<Id> instrumentIds) throws Exception {
        if(instrumentIds == null) {
            return;
        }
        double scaled = contentValueScaler.scaleValue(value);
        LOG.info("onContentValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendContentLineYEvent(instrumentId, stave, scaled);
            }
        }
    }

    private void setContentOverlay(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setContentOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendContentBoxAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private void setContentLine(Boolean value, List<Id> instrumentIds) throws Exception {
        LOG.info("setContentLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for(Id instrumentId : instrumentIds) {
            Instrument instrument = szcore.getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = szcore.getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendContentLineAlphaEvent(instrumentId, stave, value);
            }
        }
    }

    private boolean inNotOverlayInstrument(Instrument instrument) {
        return instrument.isAv() || Consts.NAME_FULL_SCORE.equalsIgnoreCase(instrument.getName());
    }

    public boolean isSchedulerRunning() {
        return scheduler.isActive();
    }

    public int getCurrentBeatNo() {
        return currentBeatNo;
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
