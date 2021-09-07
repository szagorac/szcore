package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.score.BasicScore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBuilderStrategy implements ScoreStrategy {
    private final BasicScore szcore;
    private final ScoreBuilderStrategyConfig config;
    private Map<String, String> sectionOwner = new ConcurrentHashMap<>();

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

    public boolean isSectionOwned(String section) {
        return sectionOwner.containsKey(section) && sectionOwner.get(section) != null;
    }

    public void addSectionOwner(String section, String owner) {
        sectionOwner.put(section, owner);
    }

    @Override
    public StrategyType getType() {
        return StrategyType.BUILDER;
    }
}
