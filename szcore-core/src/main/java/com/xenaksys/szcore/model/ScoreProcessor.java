package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.score.InstrumentBeatTracker;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.web.WebScoreState;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.web.WebAudienceStateListener;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebScoreAction;
import com.xenaksys.szcore.web.WebScoreActionType;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ScoreProcessor extends Processor {

    void loadAndPrepare(String path) throws Exception;

    Score loadScore(File file) throws Exception;

    void prepare(Score score);

    void play() throws Exception;

    void stop();

    void setPosition(long millis);

    Score getScore();

    void reset() throws Exception;

    void subscribe(SzcoreEngineEventListener eventListener);

    void subscribe(WebAudienceStateListener eventListener);

    void setTempoModifier(Id transportId, TempoModifier tempoModifier);

    void setRandomisationStrategy(List<Integer> randomisationStrategy);

    void usePageRandomisation(Boolean value);

    void useContinuousPageChange(Boolean value);

    void setOverlayValue(OverlayType type, long value, List<Id> instrumentIds);

    void onUseOverlayLine(OverlayType type, Boolean value, List<Id> instrumentIds);

    void onUseOverlay(OverlayType type, Boolean value, List<Id> instrumentIds);

    void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent) throws Exception;

    void onWebAudienceStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport) throws Exception;

    void onWebAudienceStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport) throws Exception;

    void onOutgoingWebEvent(OutgoingWebEvent webEvent) throws Exception;

    void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst);

    void onOpenModWindow(InstrumentId instId, Stave nextStave, Page nextPage, PageId currentPageId);

    void onCloseModWindow(InstrumentId instId, Stave nextStave, Page nextPage, PageId currentPageId);

    public int getCurrentBeatNo();

    void sendOscInstrumentRndPageUpdate(int bufferNo);

    void setUpContinuousTempoChange(int endTempo, int timeInBeats);

    void scheduleEvent(SzcoreEvent event, long timeDeltaMs);

    public void scheduleTask(MusicTask task);

    void onIncomingWebScoreEvent(WebScoreInEvent webEvent) throws Exception;

    void sendWebScoreState(String clientAddr, WebScoreTargetType host, WebScoreState scoreState) throws Exception;

    WebScoreState getOrCreateWebScoreState();

    void onInterceptedOscOutEvent(OscEvent event);

    List<WebClientInfo> getWebScoreInstrumentClients(String instrument);

    void processPrepStaveChange(Id instrumentId, BeatId activateBeatId, BeatId deactivateBeatId, BeatId pageChangeBeatId, PageId nextPageId);

    WebScoreAction getOrCreateWebScoreAction(WebScoreActionType actionType, List<String> targets, Map<String, Object> params);

    boolean isSchedulerRunning();

    InstrumentBeatTracker getInstrumentBeatTracker(Id instrumentId);

    void publishOscEvent(OscEvent event);

    void addBeatEventToProcess(SzcoreEvent event);
}
