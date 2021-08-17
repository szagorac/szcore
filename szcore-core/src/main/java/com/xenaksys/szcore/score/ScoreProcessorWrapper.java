package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MusicTask;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.score.handler.GenericScoreProcessor;
import com.xenaksys.szcore.score.web.WebScoreState;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.util.ParseUtil;
import com.xenaksys.szcore.web.WebAudienceStateListener;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebScoreAction;
import com.xenaksys.szcore.web.WebScoreActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreProcessorWrapper implements ScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(ScoreProcessorWrapper.class);

    private final TransportFactory transportFactory;
    private final MutableClock clock;
    private final OscPublisher oscPublisher;
    private final WebPublisher webPublisher;
    private final Scheduler scheduler;
    private final EventFactory eventFactory;
    private final TaskFactory taskFactory;

    private ScoreProcessor scoreHandler;
    private final List<SzcoreEngineEventListener> scoreEventListeners = new CopyOnWriteArrayList<>();
    protected final List<WebAudienceStateListener> webAudienceStateListeners = new CopyOnWriteArrayList<>();


    public ScoreProcessorWrapper(TransportFactory transportFactory,
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
    }

    @Override
    public void loadAndPrepare(String filePath) throws Exception {
        if (scoreHandler == null) {
            File scoreFile = new File(filePath);
            if (!scoreFile.exists()) {
                ClassLoader classLoader = getClass().getClassLoader();
                URL fileURL = classLoader.getResource(filePath);
                if (fileURL != null) {
                    File file = new File(fileURL.getFile());
                    if (file.exists()) {
                        scoreFile = file;
                    }
                }
            }
            loadScore(scoreFile);
        }

        prepare(scoreHandler.getScore());
    }

    @Override
    public Score loadScore(File file) throws Exception {
        LOG.info("LOADING SCORE: " + file.getCanonicalPath());
        Score score = ScoreLoader.load(file);
        BasicScore szcore = (BasicScore) score;
        scoreHandler = initScoreHandler(szcore);
        return scoreHandler.loadScore(file);
    }

    public ScoreProcessor initScoreHandler(BasicScore score) {
        if (score == null) {
            LOG.error("initScoreHandler: invalid score");
            return null;
        }
        String scoreName = score.getName();
        try {
            String className = Consts.SCORE_HANDLER_PACKAGE + ParseUtil.removeAllWhitespaces(scoreName) + Consts.SCORE_PROCESSOR_CLS_SUFFIX;
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(TransportFactory.class, MutableClock.class, OscPublisher.class,
                    WebPublisher.class, Scheduler.class, EventFactory.class, TaskFactory.class, BasicScore.class);
            Object instance = constructor.newInstance(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, score);
            return (ScoreProcessor) instance;
        } catch (Exception e) {
            LOG.warn("initScoreHandler: Failed to initialise score handler for score {}, using GenericScoreProcessor", scoreName);
            return new GenericScoreProcessor(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, score, this);
        }
    }

    @Override
    public void prepare(Score score) {
        if (scoreHandler == null) {
            LOG.error("prepare: invalid score handler");
            return;
        }
        scoreHandler.prepare(score);
    }

    @Override
    public void play() throws Exception {
        scoreHandler.play();
    }

    @Override
    public void stop() {
        scoreHandler.stop();
    }

    @Override
    public void setPosition(long millis) {
        scoreHandler.setPosition(millis);
    }

    @Override
    public Score getScore() {
        return scoreHandler.getScore();
    }

    @Override
    public void reset() throws Exception {
        if(scoreHandler == null) {
            return;
        }
        scoreHandler.reset();
    }

    @Override
    public void subscribe(SzcoreEngineEventListener eventListener) {
        scoreEventListeners.add(eventListener);
    }

    @Override
    public void subscribe(WebAudienceStateListener eventListener) {
        webAudienceStateListeners.add(eventListener);
    }

    public void notifyListeners(SzcoreEvent event, int beatNo, int tickNo) {
        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onEvent(event, beatNo, tickNo);
        }
    }

    public void notifyListeners(WebAudienceScoreStateExport webAudienceScoreStateExport) {
        for (WebAudienceStateListener listener : webAudienceStateListeners) {
            listener.onWebAudienceScoreStateChange(webAudienceScoreStateExport);
        }
    }

    public void notifyListeners(WebAudienceScoreStateDeltaExport deltaExport) {
        for (WebAudienceStateListener listener : webAudienceStateListeners) {
            listener.onWebAudienceScoreStateDeltaChange(deltaExport);
        }
    }

    public void notifyListenersOnBeat(Id transportId, int beatNo, int baseBeatNo) {
        LOG.debug("Sending beat event beatNo: " + beatNo + " baseBeatNo: " + baseBeatNo);

        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onTransportBeatEvent(transportId, beatNo, baseBeatNo);
        }
    }

    public void notifyListenersOnTempoChange(Id transportId, Tempo tempo) {
        LOG.debug("Sending tempo change tempo: " + tempo);
        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onTransportTempoChange(transportId, tempo);
        }
    }

    public void notifyListenersOnTransportPositionChange(Id transportId, int beatNo) {
        LOG.debug("Sending transport position change beatNo: " + beatNo);

        for (SzcoreEngineEventListener listener : scoreEventListeners) {
            listener.onTransportPositionChange(transportId, beatNo);
        }
    }

    @Override
    public void setTempoModifier(Id transportId, TempoModifier tempoModifier) {
        scoreHandler.setTempoModifier(transportId, tempoModifier);
    }

    @Override
    public void setRandomisationStrategy(List<Integer> randomisationStrategy) {
        scoreHandler.setRandomisationStrategy(randomisationStrategy);
    }

    @Override
    public void usePageRandomisation(Boolean value) {
        scoreHandler.usePageRandomisation(value);
    }

    @Override
    public void useContinuousPageChange(Boolean value) {
        scoreHandler.useContinuousPageChange(value);
    }

    @Override
    public void setOverlayValue(OverlayType type, long value, List<Id> instrumentIds) {
        scoreHandler.setOverlayValue(type, value, instrumentIds);
    }

    @Override
    public void onUseOverlayLine(OverlayType type, Boolean value, List<Id> instrumentIds) {
        scoreHandler.onUseOverlayLine(type, value, instrumentIds);
    }

    @Override
    public void onUseOverlay(OverlayType type, Boolean value, List<Id> instrumentIds) {
        scoreHandler.onUseOverlay(type, value, instrumentIds);
    }

    @Override
    public void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent) throws Exception {
        scoreHandler.onIncomingWebAudienceEvent(webEvent);
    }

    @Override
    public void onWebAudienceStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport) throws Exception {
        scoreHandler.onWebAudienceStateChange(webAudienceScoreStateExport);
    }

    @Override
    public void onWebAudienceStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport) throws Exception {
        scoreHandler.onWebAudienceStateDeltaChange(webAudienceScoreStateDeltaExport);
    }

    @Override
    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) throws Exception {
        scoreHandler.onOutgoingWebEvent(webEvent);
    }

    @Override
    public void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst) {
        scoreHandler.processSelectInstrumentSlot(slotNo, slotInstrument, sourceInst);
    }

    public void processPrepStaveChange(Id instrumentId, BeatId activateBeatId, BeatId deactivateBeatId, BeatId pageChangeBeatId, PageId nextPageId) {
        scoreHandler.processPrepStaveChange(instrumentId, activateBeatId, deactivateBeatId, pageChangeBeatId, nextPageId);
    }

    @Override
    public WebScoreAction getOrCreateWebScoreAction(WebScoreActionType actionType, List<String> targets, Map<String, Object> params) {
        return scoreHandler.getOrCreateWebScoreAction(actionType, targets, params);
    }

    @Override
    public boolean isSchedulerRunning() {
        return scoreHandler.isSchedulerRunning();
    }

    @Override
    public InstrumentBeatTracker getInstrumentBeatTracker(Id instrumentId) {
        return scoreHandler.getInstrumentBeatTracker(instrumentId);
    }

    @Override
    public void publishOscEvent(OscEvent event) {
        scoreHandler.publishOscEvent(event);
    }

    @Override
    public void addBeatEventToProcess(SzcoreEvent event) {
        scoreHandler.addBeatEventToProcess(event);
    }

    @Override
    public void onOpenModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        scoreHandler.onOpenModWindow(instId, stave, nextPage, currentPageId);
    }

    @Override
    public void onCloseModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        scoreHandler.onOpenModWindow(instId, stave, nextPage, currentPageId);
    }

    @Override
    public int getCurrentBeatNo() {
        return scoreHandler.getCurrentBeatNo();
    }

    public void sendOscInstrumentRndPageUpdate(int bufferNo) {
        scoreHandler.sendOscInstrumentRndPageUpdate(bufferNo);
    }

    @Override
    public void setUpContinuousTempoChange(int endBpm, int timeInBeats) {
        scoreHandler.setUpContinuousTempoChange(endBpm, timeInBeats);
    }

    @Override
    public void scheduleEvent(SzcoreEvent event, long timeDeltaMs) {
        scoreHandler.scheduleEvent(event, timeDeltaMs);
    }

    public void scheduleTask(MusicTask task) {
        scoreHandler.scheduleTask(task);
    }

    @Override
    public void onIncomingWebScoreEvent(WebScoreInEvent webEvent) throws Exception {
        scoreHandler.onIncomingWebScoreEvent(webEvent);
    }

    @Override
    public void sendWebScoreState(String clientAddr, WebScoreTargetType host, WebScoreState scoreState) throws Exception {
        scoreHandler.sendWebScoreState(clientAddr, host, scoreState);
    }

    @Override
    public WebScoreState getOrCreateWebScoreState() {
        return scoreHandler.getOrCreateWebScoreState();
    }

    @Override
    public void onInterceptedOscOutEvent(OscEvent event) {
        if(scoreHandler == null) {
            return;
        }
        scoreHandler.onInterceptedOscOutEvent(event);
    }

    @Override
    public List<WebClientInfo> getWebScoreInstrumentClients(String instrument) {
        return scoreHandler.getWebScoreInstrumentClients(instrument);
    }

    @Override
    public void process(SzcoreEvent event) {
        scoreHandler.process(event);
    }
}
