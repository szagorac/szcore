package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.model.id.InstrumentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BuilderPageRangeConfig {
    static final Logger LOG = LoggerFactory.getLogger(BuilderPageRangeConfig.class);

    private final List<InstrumentId> instruments;
    private final IntRange range;
    private final String sectionName;

    public BuilderPageRangeConfig(List<InstrumentId> instruments, IntRange range, String sectionName) {
        this.instruments = instruments;
        this.range = range;
        this.sectionName = sectionName;
    }

    public List<InstrumentId> getInstruments() {
        return instruments;
    }

    public IntRange getRange() {
        return range;
    }

    public String getSectionName() {
        return sectionName;
    }
}
