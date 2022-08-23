package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.StrategyType;
import com.xenaksys.szcore.model.Page;

import java.util.ArrayList;
import java.util.List;

public class ScoreRandomisationStrategyConfig  implements StrategyConfig{

    private final List<RndPageRangeConfig> pageRangeConfigs = new ArrayList<>();
    private String scoreName;
    private boolean isActive;

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }


    public List<RndPageRangeConfig> getPageRangeConfigs() {
        return pageRangeConfigs;
    }

    public void addPageRangeConfig(RndPageRangeConfig pageRangeConfig) {
        if (pageRangeConfig == null) {
            return;
        }
        pageRangeConfigs.add(pageRangeConfig);
    }

    public IntRange getSelectionRange(Page page) {
        if (page == null) {
            return null;
        }

        RndPageRangeConfig rangeConfig = getPageRangeConfig(page);
        if (rangeConfig == null) {
            return null;
        }

        return rangeConfig.getSelectionPageRange();
    }

    public RndPageRangeConfig getPageRangeConfig(Page page) {
        if (page == null) {
            return null;
        }

        int pageNo = page.getPageNo();
        for (RndPageRangeConfig rangeConfig : pageRangeConfigs) {
            IntRange range = rangeConfig.getRange();
            if (range.isInRange(pageNo)) {
                return rangeConfig;
            }
        }
        return null;
    }

    public boolean isPageInActiveRange(Page page) {
        if (page == null) {
            return false;
        }

        int pageNo = page.getPageNo();
        for (RndPageRangeConfig rangeConfig : pageRangeConfigs) {
            if (!rangeConfig.isRangeActive()) {
                continue;
            }
            IntRange range = rangeConfig.getRange();
            if (range.isInRange(pageNo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.RND;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}