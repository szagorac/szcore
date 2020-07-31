package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.model.id.InstrumentId;

import java.util.List;

public class RndPageRangeConfig {
    private final List<InstrumentId> instruments;
    private final IntRange activePageRange;
    private final IntRange selectionPageRange;

    public RndPageRangeConfig(List<InstrumentId> instruments, IntRange activePageRange, IntRange selectionPageRange) {
        this.instruments = instruments;
        this.activePageRange = activePageRange;
        this.selectionPageRange = selectionPageRange;
    }

    public List<InstrumentId> getInstruments() {
        return instruments;
    }

    public IntRange getActivePageRange() {
        return activePageRange;
    }

    public IntRange getSelectionPageRange() {
        return selectionPageRange;
    }
}
