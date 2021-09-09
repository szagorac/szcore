package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.model.*;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.score.delegate.ScoreProcessorDelegate;
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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreProcessorImpl implements ScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(ScoreProcessorImpl.class);

    private final TransportFactory transportFactory;
    private final MutableClock clock;
    private final OscPublisher oscPublisher;
    private final WebPublisher webPublisher;
    private final Scheduler scheduler;
    private final EventFactory eventFactory;
    private final TaskFactory taskFactory;
    private final Properties props;

    private ScoreProcessor scoreDelegate;
    private final List<SzcoreEngineEventListener> scoreEventListeners = new CopyOnWriteArrayList<>();
    protected final List<WebAudienceStateListener> webAudienceStateListeners = new CopyOnWriteArrayList<>();


    public ScoreProcessorImpl(TransportFactory transportFactory,
                              MutableClock clock,
                              OscPublisher oscPublisher,
                              WebPublisher webPublisher,
                              Scheduler scheduler,
                              EventFactory eventFactory,
                              TaskFactory taskFactory,
                              Properties props) {
        this.transportFactory = transportFactory;
        this.clock = clock;
        this.oscPublisher = oscPublisher;
        this.webPublisher = webPublisher;
        this.scheduler = scheduler;
        this.eventFactory = eventFactory;
        this.taskFactory = taskFactory;
        this.props = props;
    }

    @Override
    public void loadAndPrepare(String filePath) throws Exception {
        if (scoreDelegate == null) {
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

        prepare(scoreDelegate.getScore());
    }

    @Override
    public Score loadScore(File file) throws Exception {
        LOG.info("LOADING SCORE: " + file.getCanonicalPath());
        Score score = ScoreLoader.load(file);
        BasicScore szcore = (BasicScore) score;
        scoreDelegate = initScoreDelegate(szcore);
        return scoreDelegate.loadScore(file);
    }

    public ScoreProcessor initScoreDelegate(BasicScore score) {
        if (score == null) {
            LOG.error("initScoreHandler: invalid score");
            return null;
        }
        String scoreName = score.getName();
        try {
            String scoreConfigName = ParseUtil.removeAllWhitespaces(scoreName).toLowerCase(Locale.ROOT);
            String configName = Consts.SCORE_DELEGATE_CONFIG_PREFIX + scoreConfigName;
            String className = props.getProperty(configName);
            if(className == null) {
                className = Consts.SCORE_DELEGATE_PACKAGE + ParseUtil.removeAllWhitespaces(scoreName) + Consts.SCORE_PROCESSOR_CLS_SUFFIX;
            } else {
                className = Consts.SCORE_DELEGATE_PACKAGE + className;
            }
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(TransportFactory.class, MutableClock.class, OscPublisher.class,
                    WebPublisher.class, Scheduler.class, EventFactory.class, TaskFactory.class, BasicScore.class, ScoreProcessorImpl.class, Properties.class);
            Object instance = constructor.newInstance(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, score, this, props);
            return (ScoreProcessor) instance;
        } catch (Exception e) {
            LOG.warn("initScoreHandler: Failed to initialise score handler for score {}, using ScoreProcessorDelegate", scoreName);
            return new ScoreProcessorDelegate(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, score, this, props);
        }
    }

    @Override
    public void prepare(Score score) {
        if (scoreDelegate == null) {
            LOG.error("prepare: invalid score handler");
            return;
        }
        scoreDelegate.prepare(score);
    }

    @Override
    public void play() throws Exception {
        scoreDelegate.play();
    }

    @Override
    public void stop() {
        scoreDelegate.stop();
    }

    @Override
    public void setPosition(long millis) {
        scoreDelegate.setPosition(millis);
    }

    @Override
    public Score getScore() {
        return scoreDelegate.getScore();
    }

    @Override
    public void reset() throws Exception {
        if(scoreDelegate == null) {
            return;
        }
        scoreDelegate.reset();
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
        scoreDelegate.setTempoModifier(transportId, tempoModifier);
    }

    @Override
    public void setRandomisationStrategy(List<Integer> randomisationStrategy) {
        scoreDelegate.setRandomisationStrategy(randomisationStrategy);
    }

    @Override
    public void usePageRandomisation(Boolean value) {
        scoreDelegate.usePageRandomisation(value);
    }

    @Override
    public void useContinuousPageChange(Boolean value) {
        scoreDelegate.useContinuousPageChange(value);
    }

    @Override
    public void setOverlayValue(OverlayType type, long value, List<Id> instrumentIds) {
        scoreDelegate.setOverlayValue(type, value, instrumentIds);
    }

    @Override
    public void onUseOverlayLine(OverlayType type, Boolean value, List<Id> instrumentIds) {
        scoreDelegate.onUseOverlayLine(type, value, instrumentIds);
    }

    @Override
    public void onUseOverlay(OverlayType type, Boolean value, List<Id> instrumentIds) {
        scoreDelegate.onUseOverlay(type, value, instrumentIds);
    }

    @Override
    public void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent) throws Exception {
        scoreDelegate.onIncomingWebAudienceEvent(webEvent);
    }

    @Override
    public void onWebAudienceStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport) throws Exception {
        scoreDelegate.onWebAudienceStateChange(webAudienceScoreStateExport);
    }

    @Override
    public void onWebAudienceStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport) throws Exception {
        scoreDelegate.onWebAudienceStateDeltaChange(webAudienceScoreStateDeltaExport);
    }

    @Override
    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) throws Exception {
        scoreDelegate.onOutgoingWebEvent(webEvent);
    }

    @Override
    public void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst) {
        scoreDelegate.processSelectInstrumentSlot(slotNo, slotInstrument, sourceInst);
    }

    public void processPrepStaveChange(Id instrumentId, BeatId activateBeatId, BeatId deactivateBeatId, BeatId pageChangeBeatId, PageId nextPageId) {
        scoreDelegate.processPrepStaveChange(instrumentId, activateBeatId, deactivateBeatId, pageChangeBeatId, nextPageId);
    }

    @Override
    public WebScoreAction getOrCreateWebScoreAction(WebScoreActionType actionType, List<String> targets, Map<String, Object> params) {
        return scoreDelegate.getOrCreateWebScoreAction(actionType, targets, params);
    }

    @Override
    public boolean isSchedulerRunning() {
        return scoreDelegate.isSchedulerRunning();
    }

    @Override
    public InstrumentBeatTracker getInstrumentBeatTracker(Id instrumentId) {
        return scoreDelegate.getInstrumentBeatTracker(instrumentId);
    }

    @Override
    public void publishOscEvent(OscEvent event) {
        scoreDelegate.publishOscEvent(event);
    }

    @Override
    public void addBeatEventToProcess(SzcoreEvent event) {
        scoreDelegate.addBeatEventToProcess(event);
    }

    @Override
    public void onOpenModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        scoreDelegate.onOpenModWindow(instId, stave, nextPage, currentPageId);
    }

    @Override
    public void onCloseModWindow(InstrumentId instId, Stave stave, Page nextPage, PageId currentPageId) {
        scoreDelegate.onOpenModWindow(instId, stave, nextPage, currentPageId);
    }

    @Override
    public int getCurrentBeatNo() {
        return scoreDelegate.getCurrentBeatNo();
    }

    public void sendOscInstrumentRndPageUpdate(int bufferNo) {
        scoreDelegate.sendOscInstrumentRndPageUpdate(bufferNo);
    }

    @Override
    public void setUpContinuousTempoChange(int endBpm, int timeInBeats) {
        scoreDelegate.setUpContinuousTempoChange(endBpm, timeInBeats);
    }

    @Override
    public void scheduleEvent(SzcoreEvent event, long timeDeltaMs) {
        scoreDelegate.scheduleEvent(event, timeDeltaMs);
    }

    public void scheduleTask(MusicTask task) {
        scoreDelegate.scheduleTask(task);
    }

    @Override
    public void onIncomingWebScoreEvent(WebScoreInEvent webEvent) throws Exception {
        if (scoreDelegate == null) {
            return;
        }
        scoreDelegate.onIncomingWebScoreEvent(webEvent);
    }

    @Override
    public void sendWebScoreState(String clientAddr, WebScoreTargetType host, WebScoreState scoreState) throws Exception {
        scoreDelegate.sendWebScoreState(clientAddr, host, scoreState);
    }

    @Override
    public WebScoreState getOrCreateWebScoreState() {
        return scoreDelegate.getOrCreateWebScoreState();
    }

    @Override
    public void onInterceptedOscOutEvent(OscEvent event) {
        if(scoreDelegate == null) {
            return;
        }
        scoreDelegate.onInterceptedOscOutEvent(event);
    }

    @Override
    public List<WebClientInfo> getWebScoreInstrumentClients(String instrument) {
        return scoreDelegate.getWebScoreInstrumentClients(instrument);
    }

    @Override
    public void process(SzcoreEvent event) {
        scoreDelegate.process(event);
    }
}
