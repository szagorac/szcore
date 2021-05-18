package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.model.id.InstrumentId;

import java.util.List;

public class RndPageRangeConfig {
    private final List<InstrumentId> instruments;
    private final IntRange range;
    private final IntRange selectionPageRange;
    private final Boolean isActive;

    public RndPageRangeConfig(boolean isActive, List<InstrumentId> instruments, IntRange range, IntRange selectionPageRange) {
        this.instruments = instruments;
        this.range = range;
        this.selectionPageRange = selectionPageRange;
        this.isActive = isActive;
    }

    public List<InstrumentId> getInstruments() {
        return instruments;
    }

    public IntRange getRange() {
        return range;
    }

    public IntRange getSelectionPageRange() {
        return selectionPageRange;
    }

    public Boolean isRangeActive() {
        return isActive;
    }
}
