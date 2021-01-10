package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebGrainConfig;

import java.util.HashMap;
import java.util.Map;

public class WebGrainConfigExport {

    private int sizeMs;
    private double pitchRate;
    private int maxPositionOffsetRangeMs;
    private double maxPitchRateRange;
    private int timeOffsetStepMs;

    public int getSizeMs() {
        return sizeMs;
    }

    public double getPitchRate() {
        return pitchRate;
    }

    public int getMaxPositionOffsetRangeMs() {
        return maxPositionOffsetRangeMs;
    }

    public double getMaxPitchRateRange() {
        return maxPitchRateRange;
    }

    public int getTimeOffsetStepMs() {
        return timeOffsetStepMs;
    }

    public void populate(WebGrainConfig from) {
        if (from == null) {
            return;
        }
        this.sizeMs = from.getSizeMs();
        this.pitchRate = from.getPitchRate();
        this.maxPositionOffsetRangeMs = from.getMaxPositionOffsetRangeMs();
        this.maxPitchRateRange = from.getMaxPitchRateRange();
        this.timeOffsetStepMs = from.getTimeOffsetStepMs();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("grain.sizeMs", getSizeMs());
        config.put("grain.pitchRate", getPitchRate());
        config.put("grain.maxPositionOffsetRangeMs", getMaxPositionOffsetRangeMs());
        config.put("grain.maxPitchRateRange", getMaxPitchRateRange());
        config.put("grain.timeOffsetStepMs", getTimeOffsetStepMs());
        return config;
    }

    @Override
    public String toString() {
        return "WebGrainConfigExport{" +
                "sizeMs=" + sizeMs +
                ", pitchRate=" + pitchRate +
                ", maxPositionOffsetRangeMs=" + maxPositionOffsetRangeMs +
                ", maxPitchRateRange=" + maxPitchRateRange +
                ", timeOffsetStepMs=" + timeOffsetStepMs +
                '}';
    }

}
