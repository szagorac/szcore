package com.xenaksys.szcore.model;

import com.xenaksys.szcore.score.SzcoreEngineEventListener;

import java.io.File;
import java.util.List;

public interface ScoreService {

    void loadScoreAndPrepare(String filePath);

    Score loadScore(File file);

    boolean reset();

    void play(long startMillis);

    void stopPlay();

    void setPosition(long millis);

    void subscribe(SzcoreEngineEventListener eventListener);

    void setTempoModifier(Id transportId, TempoModifier tempoModifier);

    void setRandomisationStrategy(List<Integer> randomisationStrategy);
}
