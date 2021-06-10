package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.IncomingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.score.SzcoreEngineEventListener;
import com.xenaksys.szcore.score.web.WebScore;
import com.xenaksys.szcore.score.web.export.WebScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateExport;
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

    void onIncomingWebEvent(IncomingWebEvent webEvent) throws Exception;

    void onWebScoreStateChange(WebScoreStateExport webScoreStateExport) throws Exception;

    void onWebScoreStateDeltaChange(WebScoreStateDeltaExport webScoreStateDeltaExport) throws Exception;

    void onOutgoingWebEvent(OutgoingWebEvent webEvent) throws Exception;

    WebScore loadWebScore(File file) throws Exception;

    void processSelectInstrumentSlot(int slotNo, String slotInstrument, String sourceInst);

    void onOpenModWindow(InstrumentId instId, Stave nextStave, Page nextPage, PageId currentPageId);

    void onCloseModWindow(InstrumentId instId, Stave nextStave, Page nextPage, PageId currentPageId);

    public int getCurrentBeatNo();

    void sendOscInstrumentRndPageUpdate(int bufferNo);

    void setUpContinuousTempoChange(int endTempo, int timeInBeats);

    void scheduleEvent(SzcoreEvent event, long timeDeltaMs);

    public void scheduleTask(MusicTask task);
}
