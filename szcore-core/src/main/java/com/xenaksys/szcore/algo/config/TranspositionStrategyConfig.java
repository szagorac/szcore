package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.StrategyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TranspositionStrategyConfig implements StrategyConfig{

    private boolean isActive;
    private String scoreName;
    private double topStaveYRef;
    private double topStaveXRef;
    private double botStaveYRef;
    private double botStaveXRef;
    private double minYdistance;
    private double minXdistance;

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

    public TranspositionPageConfig getPageConfig(String part, int pageNo) {
        if(instrumentPageConfigs.containsKey(part)) {
            return null;
        }
        return instrumentPageConfigs.get(part).get(pageNo);
    }

    public double getTopStaveYRef() {
        return topStaveYRef;
    }

    public void setTopStaveYRef(double topStaveYRef) {
        this.topStaveYRef = topStaveYRef;
    }

    public double getTopStaveXRef() {
        return topStaveXRef;
    }

    public void setTopStaveXRef(double topStaveXRef) {
        this.topStaveXRef = topStaveXRef;
    }

    public double getBotStaveYRef() {
        return botStaveYRef;
    }

    public void setBotStaveYRef(double botStaveYRef) {
        this.botStaveYRef = botStaveYRef;
    }

    public double getBotStaveXRef() {
        return botStaveXRef;
    }

    public void setBotStaveXRef(double botStaveXRef) {
        this.botStaveXRef = botStaveXRef;
    }

    public double getMinYdistance() {
        return minYdistance;
    }

    public void setMinYdistance(double minYdistance) {
        this.minYdistance = minYdistance;
    }

    public double getMinXdistance() {
        return minXdistance;
    }

    public void setMinXdistance(double minXdistance) {
        this.minXdistance = minXdistance;
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