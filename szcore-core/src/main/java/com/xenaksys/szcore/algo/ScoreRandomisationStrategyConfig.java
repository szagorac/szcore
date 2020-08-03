package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.model.Page;

import java.util.ArrayList;
import java.util.List;

public class ScoreRandomisationStrategyConfig {

    private List<RndPageRangeConfig> pageRangeConfigs = new ArrayList<>();
    private String scoreName;

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

        RndPageRangeConfig rangeConfig = getActiveRangeConfig(page);
        if (rangeConfig == null) {
            return null;
        }

        return rangeConfig.getSelectionPageRange();
    }

    public RndPageRangeConfig getActiveRangeConfig(Page page) {
        if (page == null) {
            return null;
        }

        int pageNo = page.getPageNo();
        for (RndPageRangeConfig rangeConfig : pageRangeConfigs) {
            IntRange range = rangeConfig.getActivePageRange();
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
            IntRange range = rangeConfig.getActivePageRange();
            if (range.isInRange(pageNo)) {
                return true;
            }
        }
        return false;
    }

}