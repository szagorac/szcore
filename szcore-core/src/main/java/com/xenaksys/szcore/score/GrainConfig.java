package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrainConfig {
    static final Logger LOG = LoggerFactory.getLogger(GrainConfig.class);

    private static final int MIN_GRAIN_SIZE = 5;
    private static final double MIN_PITCH_RATE = 0.0;
    private static final int MIN_POSITION_OFFSET_RANGE = 0;
    private static final double MIN_PITCH_RATE_RANGE = 0;
    private static final double MAX_PITCH_RATE_RANGE = 1.0;
    private static final int MIN_TIME_OFFSET_STEP = 0;

    private int sizeMs;
    private double pitchRate;
    private int maxPositionOffsetRangeMs;
    private double maxPitchRateRange;
    private int timeOffsetStepMs;

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
