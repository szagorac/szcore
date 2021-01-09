package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GrainConfig {
    static final Logger LOG = LoggerFactory.getLogger(GrainConfig.class);

    private static final int MIN_GRAIN_SIZE = 5;
    private static final double MIN_PITCH_RATE = 0.0;
    private static final int MIN_POSITION_OFFSET_RANGE = 0;
    private static final double MIN_PITCH_RATE_RANGE = 0;
    private static final double MAX_PITCH_RATE_RANGE = 1.0;
    private static final int MIN_TIME_OFFSET_STEP = 0;

    private static final int DEFAULT_GRAIN_SIZE_MS = 100;
    private static final double DEFAULT_PITCH_RATE = 3.0;
    private static final int DEFAULT_MAX_POSITION_OFFSET_RANGE_MS = 10;
    private static final double DEFAULT_MAX_PITCH_RATE_RANGE = 0.02;
    private static final int DEFAULT_TIME_OFFSET_STEP_MS = 10;

    private int sizeMs = DEFAULT_GRAIN_SIZE_MS;
    private double pitchRate = DEFAULT_PITCH_RATE;
    private int maxPositionOffsetRangeMs = DEFAULT_MAX_POSITION_OFFSET_RANGE_MS;
    private double maxPitchRateRange = DEFAULT_MAX_PITCH_RATE_RANGE;
    private int timeOffsetStepMs = DEFAULT_TIME_OFFSET_STEP_MS;

    public int getSizeMs() {
        return sizeMs;
    }

    public void setSizeMs(int sizeMs) {
        this.sizeMs = sizeMs;
    }

    public double getPitchRate() {
        return pitchRate;
    }

    public void setPitchRate(double pitchRate) {
        this.pitchRate = pitchRate;
    }

    public int getMaxPositionOffsetRangeMs() {
        return maxPositionOffsetRangeMs;
    }

    public void setMaxPositionOffsetRangeMs(int maxPositionOffsetRangeMs) {
        this.maxPositionOffsetRangeMs = maxPositionOffsetRangeMs;
    }

    public double getMaxPitchRateRange() {
        return maxPitchRateRange;
    }

    public void setMaxPitchRateRange(double maxPitchRateRange) {
        this.maxPitchRateRange = maxPitchRateRange;
    }

    public int getTimeOffsetStepMs() {
        return timeOffsetStepMs;
    }

    public void setTimeOffsetStepMs(int timeOffsetStepMs) {
        this.timeOffsetStepMs = timeOffsetStepMs;
    }

    public boolean validate() {
        if (sizeMs < MIN_GRAIN_SIZE) {
            LOG.info("validate: invalid sizeMs, setting to {}", MIN_GRAIN_SIZE);
            sizeMs = MIN_GRAIN_SIZE;
        }
        if (pitchRate < MIN_PITCH_RATE) {
            LOG.info("validate: invalid pitchRate, setting to {}", MIN_PITCH_RATE);
            pitchRate = MIN_PITCH_RATE;
        }
        if (maxPositionOffsetRangeMs < MIN_POSITION_OFFSET_RANGE) {
            LOG.info("validate: invalid maxPositionOffsetRangeMs, setting to {}", MIN_POSITION_OFFSET_RANGE);
            maxPositionOffsetRangeMs = MIN_POSITION_OFFSET_RANGE;
        }
        if (maxPitchRateRange < MIN_PITCH_RATE_RANGE) {
            LOG.info("validate: invalid maxPitchRateRange, setting to {}", MIN_PITCH_RATE_RANGE);
            maxPitchRateRange = MIN_PITCH_RATE_RANGE;
        }
        if (maxPitchRateRange > MAX_PITCH_RATE_RANGE) {
            LOG.info("validate: invalid maxPitchRateRange, setting to {}", MAX_PITCH_RATE_RANGE);
            maxPitchRateRange = MAX_PITCH_RATE_RANGE;
        }
        if (timeOffsetStepMs < MIN_TIME_OFFSET_STEP) {
            LOG.info("validate: invalid timeOffsetStepMs, setting to {}", MIN_PITCH_RATE);
            LOG.info("validate: invalid pitchRate, setting to {}", MIN_PITCH_RATE);
            timeOffsetStepMs = MIN_TIME_OFFSET_STEP;
        }

        return true;
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

    public GrainConfig copy(GrainConfig to) {
        if (to == null) {
            to = new GrainConfig();
        }
        to.setSizeMs(this.sizeMs);
        to.setPitchRate(this.pitchRate);
        to.setMaxPositionOffsetRangeMs(this.maxPositionOffsetRangeMs);
        to.setMaxPitchRateRange(this.maxPitchRateRange);
        to.setTimeOffsetStepMs(this.timeOffsetStepMs);
        return to;
    }

    @Override
    public String toString() {
        return "GrainConfig{" +
                "sizeMs=" + sizeMs +
                ", pitchRate=" + pitchRate +
                ", maxPositionOffsetRangeMs=" + maxPositionOffsetRangeMs +
                ", maxPitchRateRange=" + maxPitchRateRange +
                ", timeOffsetStepMs=" + timeOffsetStepMs +
                '}';
    }

}
