package com.xenaksys.szcore.score.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PITCH_RATE_RANGE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SIZE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_TIME_OFFSET_STEPS_MS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN;

public class WebGrainConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebGrainConfig.class);

    private static final int MIN_GRAIN_SIZE = 5;
    private static final double MIN_PITCH_RATE = 0.0;
    private static final int MIN_POSITION_OFFSET_RANGE = 0;
    private static final double MIN_PITCH_RATE_RANGE = 0;
    private static final double MAX_PITCH_RATE_RANGE = 1.0;
    private static final int MIN_TIME_OFFSET_STEP = 0;
    private static final double CHANGE_THRESHOLD = 10E-5;

    private static final int DEFAULT_GRAIN_SIZE_MS = 70;
    private static final double DEFAULT_PITCH_RATE = 1.0;
    private static final int DEFAULT_MAX_POSITION_OFFSET_RANGE_MS = 10;
    private static final double DEFAULT_MAX_PITCH_RATE_RANGE = 0.02;
    private static final int DEFAULT_TIME_OFFSET_STEP_MS = 10;

    private int sizeMs = DEFAULT_GRAIN_SIZE_MS;
    private double pitchRate = DEFAULT_PITCH_RATE;
    private int maxPositionOffsetRangeMs = DEFAULT_MAX_POSITION_OFFSET_RANGE_MS;
    private double maxPitchRateRange = DEFAULT_MAX_PITCH_RATE_RANGE;
    private int timeOffsetStepMs = DEFAULT_TIME_OFFSET_STEP_MS;

    private final PropertyChangeSupport pcs;

    public WebGrainConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public int getSizeMs() {
        return sizeMs;
    }

    public void setSizeMs(int sizeMs) {
        int old = this.sizeMs;
        this.sizeMs = sizeMs;
        if (old != this.sizeMs) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN, WEB_CONFIG_SIZE_MS, sizeMs);
        }
    }

    public double getPitchRate() {
        return pitchRate;
    }

    public void setPitchRate(double pitchRate) {
        double old = this.pitchRate;
        this.pitchRate = pitchRate;
        if (Math.abs(old - this.pitchRate) > CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN, WEB_CONFIG_PITCH_RATE, pitchRate);
        }
    }

    public int getMaxPositionOffsetRangeMs() {
        return maxPositionOffsetRangeMs;
    }

    public void setMaxPositionOffsetRangeMs(int maxPositionOffsetRangeMs) {
        int old = this.maxPositionOffsetRangeMs;
        this.maxPositionOffsetRangeMs = maxPositionOffsetRangeMs;
        if (old != this.maxPositionOffsetRangeMs) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN, WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS, maxPositionOffsetRangeMs);
        }
    }

    public double getMaxPitchRateRange() {
        return maxPitchRateRange;
    }

    public void setMaxPitchRateRange(double maxPitchRateRange) {
        double old = this.maxPitchRateRange;
        this.maxPitchRateRange = maxPitchRateRange;
        if (Math.abs(old - this.maxPitchRateRange) > CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN, WEB_CONFIG_MAX_PITCH_RATE_RANGE, maxPitchRateRange);
        }
    }

    public int getTimeOffsetStepMs() {
        return timeOffsetStepMs;
    }

    public void setTimeOffsetStepMs(int timeOffsetStepMs) {
        int old = this.timeOffsetStepMs;
        this.timeOffsetStepMs = timeOffsetStepMs;
        if (old != this.timeOffsetStepMs) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN, WEB_CONFIG_TIME_OFFSET_STEPS_MS, timeOffsetStepMs);
        }
    }

    public boolean validate() {
        if (sizeMs < MIN_GRAIN_SIZE) {
            LOG.info("validate: invalid sizeMs, setting to {}", MIN_GRAIN_SIZE);
            setSizeMs(MIN_GRAIN_SIZE);
        }
        if (pitchRate < MIN_PITCH_RATE) {
            LOG.info("validate: invalid pitchRate, setting to {}", MIN_PITCH_RATE);
            setPitchRate(MIN_PITCH_RATE);
        }
        if (maxPositionOffsetRangeMs < MIN_POSITION_OFFSET_RANGE) {
            LOG.info("validate: invalid maxPositionOffsetRangeMs, setting to {}", MIN_POSITION_OFFSET_RANGE);
            setMaxPositionOffsetRangeMs(MIN_POSITION_OFFSET_RANGE);
        }
        if (maxPitchRateRange < MIN_PITCH_RATE_RANGE) {
            LOG.info("validate: invalid maxPitchRateRange, setting to {}", MIN_PITCH_RATE_RANGE);
            setMaxPitchRateRange(MIN_PITCH_RATE_RANGE);
        }
        if (maxPitchRateRange > MAX_PITCH_RATE_RANGE) {
            LOG.info("validate: invalid maxPitchRateRange, setting to {}", MAX_PITCH_RATE_RANGE);
            setMaxPitchRateRange(MAX_PITCH_RATE_RANGE);
        }
        if (timeOffsetStepMs < MIN_TIME_OFFSET_STEP) {
            LOG.info("validate: invalid timeOffsetStepMs, setting to {}", MIN_PITCH_RATE);
            LOG.info("validate: invalid pitchRate, setting to {}", MIN_PITCH_RATE);
            setTimeOffsetStepMs(MIN_TIME_OFFSET_STEP);
        }

        return true;
    }

    public void update(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return;
        }
        if (config.containsKey(WEB_CONFIG_SIZE_MS)) {
            setSizeMs((Integer) config.get(WEB_CONFIG_SIZE_MS));
        }
        if (config.containsKey(WEB_CONFIG_PITCH_RATE)) {
            setPitchRate((Double) config.get(WEB_CONFIG_PITCH_RATE));
        }
        if (config.containsKey(WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS)) {
            setMaxPositionOffsetRangeMs((Integer) config.get(WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS));
        }
        if (config.containsKey(WEB_CONFIG_MAX_PITCH_RATE_RANGE)) {
            setMaxPitchRateRange((Double) config.get(WEB_CONFIG_MAX_PITCH_RATE_RANGE));
        }
        if (config.containsKey(WEB_CONFIG_TIME_OFFSET_STEPS_MS)) {
            setTimeOffsetStepMs((Integer) config.get(WEB_CONFIG_TIME_OFFSET_STEPS_MS));
        }
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

    public WebGrainConfig copy(WebGrainConfig to) {
        if (to == null) {
            to = new WebGrainConfig(pcs);
        }
        to.setSizeMs(this.sizeMs);
        to.setPitchRate(this.pitchRate);
        to.setMaxPositionOffsetRangeMs(this.maxPositionOffsetRangeMs);
        to.setMaxPitchRateRange(this.maxPitchRateRange);
        to.setTimeOffsetStepMs(this.timeOffsetStepMs);
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebGrainConfig that = (WebGrainConfig) o;
        return sizeMs == that.sizeMs && Double.compare(that.pitchRate, pitchRate) == 0 && maxPositionOffsetRangeMs == that.maxPositionOffsetRangeMs && Double.compare(that.maxPitchRateRange, maxPitchRateRange) == 0 && timeOffsetStepMs == that.timeOffsetStepMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sizeMs, pitchRate, maxPositionOffsetRangeMs, maxPitchRateRange, timeOffsetStepMs);
    }

    @Override
    public String toString() {
        return "WebGrainConfig{" +
                "sizeMs=" + sizeMs +
                ", pitchRate=" + pitchRate +
                ", maxPositionOffsetRangeMs=" + maxPositionOffsetRangeMs +
                ", maxPitchRateRange=" + maxPitchRateRange +
                ", timeOffsetStepMs=" + timeOffsetStepMs +
                '}';
    }
}
