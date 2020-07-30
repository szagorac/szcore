package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.InstrumentId;

import java.util.HashMap;
import java.util.Map;

public class ScoreRandomisationStrategyConfig {

    private Map<InstrumentId, IntRange> instrumentActivePageRanges = new HashMap<>();
    private String scoreName;

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public Map<InstrumentId, IntRange> getInstrumentActivePageRanges() {
        return instrumentActivePageRanges;
    }

    public void addPageRange(InstrumentId instrumentId, IntRange range) {
        if (instrumentId == null || range == null) {
            return;
        }

        instrumentActivePageRanges.put(instrumentId, range);
    }

    public boolean isPageInActiveRange(Page page) {
        if (page == null) {
            return false;
        }

        InstrumentId instrumentId = (InstrumentId) page.getInstrumentId();
        IntRange range = instrumentActivePageRanges.get(instrumentId);
        if (range == null) {
            return false;
        }

        int pageNo = page.getPageNo();
        return range.isInRange(pageNo);
    }

}