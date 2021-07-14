package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebGrainConfig;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PITCH_RATE_RANGE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SIZE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_TIME_OFFSET_STEPS_MS;

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
        config.put(WEB_CONFIG_GRAIN + DOT + WEB_CONFIG_SIZE_MS, getSizeMs());
        config.put(WEB_CONFIG_GRAIN + DOT + WEB_CONFIG_PITCH_RATE, getPitchRate());
        config.put(WEB_CONFIG_GRAIN + DOT + WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS, getMaxPositionOffsetRangeMs());
        config.put(WEB_CONFIG_GRAIN + DOT + WEB_CONFIG_MAX_PITCH_RATE_RANGE, getMaxPitchRateRange());
        config.put(WEB_CONFIG_GRAIN + DOT + WEB_CONFIG_TIME_OFFSET_STEPS_MS, getTimeOffsetStepMs());
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
