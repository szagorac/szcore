package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.score.BasicScore;

public class ScoreBuilderStrategy implements ScoreStrategy{
    private final BasicScore szcore;
    private final ScoreBuilderStrategyConfig config;

    public ScoreBuilderStrategy(BasicScore szcore, ScoreBuilderStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {

    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public ScoreBuilderStrategyConfig getConfig() {
        return config;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.BUILDER;
    }
}
