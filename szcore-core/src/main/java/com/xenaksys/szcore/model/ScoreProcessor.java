package com.xenaksys.szcore.model;

import com.xenaksys.szcore.score.SzcoreEngineEventListener;

import java.io.File;
import java.util.List;

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

}
