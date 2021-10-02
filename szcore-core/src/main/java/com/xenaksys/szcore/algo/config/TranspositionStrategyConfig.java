package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.StrategyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TranspositionStrategyConfig implements StrategyConfig{

    private boolean isActive;
    private String scoreName;
    private List<TranspositionPageConfig> pageConfigs = new ArrayList<>();
    private HashMap<String, HashMap<Integer, TranspositionPageConfig>> instrumentPageConfigs = new HashMap<>();

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public void addPageConfig(TranspositionPageConfig pageConfig) {
        if(pageConfig == null) {
            return;
        }
        pageConfigs.add(pageConfig);

        String part = pageConfig.getPart();
        HashMap<Integer, TranspositionPageConfig> instrumentConfigs = instrumentPageConfigs.computeIfAbsent(part, k -> new HashMap<>());
        int pageNo = pageConfig.getPageNo();
        instrumentConfigs.put(pageNo, pageConfig);
    }

    public List<TranspositionPageConfig> getPageConfigs() {
        return pageConfigs;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.TRANSPOSITION;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}