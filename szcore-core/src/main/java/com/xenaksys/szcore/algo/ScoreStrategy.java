package com.xenaksys.szcore.algo;

public interface ScoreStrategy {

    StrategyType getType();

    boolean isReady();

    boolean isActive();
}
