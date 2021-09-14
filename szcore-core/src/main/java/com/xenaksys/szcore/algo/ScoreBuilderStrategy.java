package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.score.BasicScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBuilderStrategy implements ScoreStrategy {
    private final BasicScore szcore;
    private final ScoreBuilderStrategyConfig config;
    private final Map<String, String> sectionOwner = new ConcurrentHashMap<>();
    private final List<String> sectionOrder = new ArrayList<>();

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

    public List<String> getSections() {
        return config.getSections();
    }

    public boolean isSectionOwned(String section) {
        return sectionOwner.containsKey(section) && sectionOwner.get(section) != null;
    }

    public void appendSection(String section, String owner) {
        if(section == null) {
            return;
        }
        sectionOrder.add(section);
        if(owner != null) {
            sectionOwner.put(section, owner);
        }
    }

    public List<String> getOwnerSections(String owner) {
        List<String> sections = new ArrayList<>();
        for(String section : sectionOwner.keySet()) {
            String o = sectionOwner.get(section);
            if(o.equals(owner)) {
                sections.add(section);
            }
        }
        return sections;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.BUILDER;
    }
}
