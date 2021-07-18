package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.web.WebScoreState;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.WebAudienceScore;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.web.WebScoreStateListener;

import java.io.File;
import java.util.List;

public interface  ScoreProcessor extends Processor {

    void loadAndPrepare(String path) throws Exception;

    Score loadScore(File file) throws Exception;

    void prepare(Score score);

    void play() throws Exception;

    void stop();

    void setPosition(long millis);

    Score getScore();

    void reset() throws Exception;

    void subscribe(SzcoreEngineEventListener eventListener);

    void subscribe(WebScoreStateListener eventListener);

    void setTempoModifier(Id transportId, TempoModifier tempoModifier);

    void setRandomisationStrategy(List<Integer> randomisationStrategy);

    void usePageRandomisation(Boolean value);

    void useContinuousPageChange(Boolean value);

    void setDynamicsValue(long value, List<Id> instrumentIds) throws Exception;

    void onUseDynamicsOverlay(Boolean value, List<Id> instrumentIds) throws Exception;

    void onUseDynamicsLine(Boolean value, List<Id> instrumentIds) throws Exception;

    void setPressureValue(long value, List<Id> instrumentIds) throws Exception;

    void onUsePressureOverlay(Boolean value, List<Id> instrumentIds) throws Exception;

    void onUsePressureLine(Boolean value, List<Id> instrumentIds) throws Exception;

    void setSpeedValue(long value, List<Id> instrumentIds) throws Exception;

    void onUseSpeedOverlay(Boolean value, List<Id> instrumentIds) throws Exception;

    void onUseSpeedLine(Boolean value, List<Id> instrumentIds) throws Exception;

    void setPositionValue(long value, List<Id> instrumentIds) throws Exception;

    void onUsePositionOverlay(Boolean value, List<Id> instrumentIds) throws Exception;

    void onUsePositionLine(Boolean value, List<Id> instrumentIds) throws Exception;

    void setContentValue(long value, List<Id> instrumentIds) throws Exception;

    void onUseContentOverlay(Boolean value, List<Id> instrumentIds) throws Exception;

    void onUseContentLine(Boolean value, List<Id> instrumentIds) throws Exception;

    void onIncomingWebAudienceEvent(IncomingWebAudienceEvent webEvent) throws Exception;

    void onWebAudienceStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport) throws Exception;

    void onWebScoreStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport) throws Exception;

    void onOutgoingWebEvent(OutgoingWebEvent webEvent) throws Exception;

    WebAudienceScore loadWebScore(File file) throws Exception;

    void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst);

    void onOpenModWindow(InstrumentId instId, Stave nextStave, Page nextPage, PageId currentPageId);

    void onCloseModWindow(InstrumentId instId, Stave nextStave, Page nextPage, PageId currentPageId);

    public int getCurrentBeatNo();

    void sendOscInstrumentRndPageUpdate(int bufferNo);

    void setUpContinuousTempoChange(int endTempo, int timeInBeats);

    void scheduleEvent(SzcoreEvent event, long timeDeltaMs);

    public void scheduleTask(MusicTask task);

    void onIncomingWebScoreEvent(WebScoreInEvent webEvent) throws Exception;

    void publishToWebScore(SzcoreEvent event);

    void sendWebScoreState(String clientAddr, WebScoreTargetType host, WebScoreState scoreState) throws Exception;

    WebScoreState getOrCreateWebScoreState();
}
