package com.xenaksys.szcore.score.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_BUFFER_POSITION_PLAY_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_POSITION_FREQ_MOD;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_POSITION_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_POSITION_RANGE_MOD;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_SIZE_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_GRAINS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_DURATION_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_START_OFFSET_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_POSITION_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SIZE_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;

public class WebGranulatorConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebGranulatorConfig.class);

    private static final double MIN_GAIN = 0.0;
    private static final double MAX_GAIN = 1.0;
    private static final double MIN_PLAY_DURATION_SEC = 0.0;
    private static final double MAX_PLAY_DURATION_SEC = 60.0 * 30.0; //half an hour
    private static final double MIN_PLAY_START_OFFSET_SEC = 0.0;
    private static final double MAX_PLAY_START_OFFSET_SEC = MAX_PLAY_DURATION_SEC;
    private static final int MIN_GRAINS = 1;
    private static final int MAX_GRAINS = 128;
    private static final double MIN_BUFFER_POSITION_PLAY_RATE = 0.0;
    private static final double MAX_BUFFER_POSITION_PLAY_RATE = 100.0;
    private static final int MIN_AUDIO_STOP_TOLERANCE_MS = 0;
    private static final int MAX_AUDIO_STOP_TOLERANCE_MS = 100;

    private static final double DEFAULT_GAIN = 0.5;
    private static final double DEFAULT_PLAY_DURATION_SEC = 30.0;
    private static final double DEFAULT_PLAY_START_OFFSET_SEC = 0.0;
    private static final int DEFAULT_GRAINS = 12;
    private static final double DEFAULT_BUFFER_POSITION_PLAY_RATE = 0.5;
    private static final int DEFAULT_AUDIO_STOP_TOLERANCE_MS = 5;
    private static final boolean IS_USE_OSCILLATORS = true;

    private double masterGainVal = DEFAULT_GAIN;
    private double playDurationSec = DEFAULT_PLAY_DURATION_SEC;
    private double playStartOffsetSec = DEFAULT_PLAY_START_OFFSET_SEC;
    private int maxGrains = DEFAULT_GRAINS;
    private double bufferPositionPlayRate = DEFAULT_BUFFER_POSITION_PLAY_RATE;
    private int audioStopToleranceMs = DEFAULT_AUDIO_STOP_TOLERANCE_MS;
    private boolean isUsePositionOscillator = IS_USE_OSCILLATORS;
    private boolean isUseSizeOscillator = IS_USE_OSCILLATORS;
    private boolean isUsePositionFrequencyMod = IS_USE_OSCILLATORS;
    private boolean isUsePositionRangeMod = IS_USE_OSCILLATORS;

    private WebGrainConfig grain;
    private WebEnvelopeConfig envelope;
    private WebPannerConfig panner;

    private WebPositionOscillatorConfig positionOscillator;
    private WebSizeOscillatorConfig sizeOscillator;

    private final PropertyChangeSupport pcs;

    public WebGranulatorConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
        this.grain = new WebGrainConfig(pcs);
        this.envelope = new WebEnvelopeConfig(pcs);
        this.panner = new WebPannerConfig(pcs);
        this.positionOscillator = new WebPositionOscillatorConfig(pcs);
        this.sizeOscillator = new WebSizeOscillatorConfig(pcs);
    }

    public double getMasterGainVal() {
        return masterGainVal;
    }

    public void setMasterGainVal(double masterGainVal) {
        double old = this.masterGainVal;
        this.masterGainVal = masterGainVal;
        if (Math.abs(old - this.masterGainVal) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_MASTER_GAIN_VAL, masterGainVal);
        }
    }

    public double getPlayDurationSec() {
        return playDurationSec;
    }

    public void setPlayDurationSec(double playDurationSec) {
        double old = this.playDurationSec;
        this.playDurationSec = playDurationSec;
        if (Math.abs(old - this.playDurationSec) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_PLAY_DURATION_SEC, playDurationSec);
        }
    }

    public double getPlayStartOffsetSec() {
        return playStartOffsetSec;
    }

    public void setPlayStartOffsetSec(double playStartOffsetSec) {
        double old = this.playStartOffsetSec;
        this.playStartOffsetSec = playStartOffsetSec;
        if (Math.abs(old - this.playStartOffsetSec) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_PLAY_START_OFFSET_SEC, playStartOffsetSec);
        }
    }

    public int getMaxGrains() {
        return maxGrains;
    }

    public void setMaxGrains(int maxGrains) {
        int old = this.maxGrains;
        this.maxGrains = maxGrains;
        if (old != this.maxGrains) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_MAX_GRAINS, maxGrains);
        }
    }

    public double getBufferPositionPlayRate() {
        return bufferPositionPlayRate;
    }

    public void setBufferPositionPlayRate(double bufferPositionPlayRate) {
        double old = this.bufferPositionPlayRate;
        this.bufferPositionPlayRate = bufferPositionPlayRate;
        if (Math.abs(old - this.bufferPositionPlayRate) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_BUFFER_POSITION_PLAY_RATE, bufferPositionPlayRate);
        }
    }

    public int getAudioStopToleranceMs() {
        return audioStopToleranceMs;
    }

    public void setAudioStopToleranceMs(int audioStopToleranceMs) {
        int old = this.audioStopToleranceMs;
        this.audioStopToleranceMs = audioStopToleranceMs;
        if (old != this.audioStopToleranceMs) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS, audioStopToleranceMs);
        }
    }

    public boolean isUsePositionOscillator() {
        return isUsePositionOscillator;
    }

    public void setUsePositionOscillator(boolean usePositionOscillator) {
        boolean old = this.isUsePositionOscillator;
        this.isUsePositionOscillator = usePositionOscillator;
        if (old != this.isUsePositionOscillator) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_IS_USE_POSITION_OSCILLATOR, usePositionOscillator);
        }
    }

    public boolean isUseSizeOscillator() {
        return isUseSizeOscillator;
    }

    public void setUseSizeOscillator(boolean useSizeOscillator) {
        boolean old = this.isUseSizeOscillator;
        this.isUseSizeOscillator = useSizeOscillator;
        if (old != this.isUseSizeOscillator) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_IS_USE_SIZE_OSCILLATOR, useSizeOscillator);
        }
    }

    public boolean isUsePositionFrequencyMod() {
        return isUsePositionFrequencyMod;
    }

    public void setUsePositionFrequencyMod(boolean usePositionFrequencyMod) {
        boolean old = this.isUsePositionFrequencyMod;
        this.isUsePositionFrequencyMod = usePositionFrequencyMod;
        if (old != this.isUsePositionFrequencyMod) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_IS_USE_POSITION_FREQ_MOD, usePositionFrequencyMod);
        }
    }

    public boolean isUsePositionRangeMod() {
        return isUsePositionRangeMod;
    }

    public void setUsePositionRangeMod(boolean usePositionRangeMod) {
        boolean old = this.isUsePositionRangeMod;
        this.isUsePositionRangeMod = usePositionRangeMod;
        if (old != this.isUsePositionRangeMod) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_IS_USE_POSITION_RANGE_MOD, usePositionRangeMod);
        }
    }

    public WebGrainConfig getGrain() {
        return grain;
    }

    public void setGrain(WebGrainConfig grain) {
        WebGrainConfig old = new WebGrainConfig(null);
        this.grain.copy(old);
        this.grain = grain;
        if (!this.grain.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_GRAIN, grain);
        }
    }

    public WebEnvelopeConfig getEnvelope() {
        return envelope;
    }

    public void setEnvelope(WebEnvelopeConfig envelope) {
        WebEnvelopeConfig old = new WebEnvelopeConfig(null);
        this.envelope.copy(old);
        this.envelope = envelope;
        if (!this.envelope.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_ENVELOPE, envelope);
        }
    }

    public WebPannerConfig getPanner() {
        return panner;
    }

    public void setPanner(WebPannerConfig panner) {
        WebPannerConfig old = new WebPannerConfig(null);
        this.panner.copy(old);
        this.panner = panner;
        if (!this.panner.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_PANNER, panner);
        }
    }

    public WebSizeOscillatorConfig getSizeOscillator() {
        return sizeOscillator;
    }

    public void setSizeOscillator(WebSizeOscillatorConfig sizeOscillator) {
        WebSizeOscillatorConfig old = new WebSizeOscillatorConfig(null);
        this.sizeOscillator.copy(old);
        this.sizeOscillator = sizeOscillator;
        if (!this.sizeOscillator.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_SIZE_OSCILLATOR, sizeOscillator);
        }
    }

    public WebPositionOscillatorConfig getPositionOscillator() {
        return positionOscillator;
    }

    public void setPositionOscillator(WebPositionOscillatorConfig positionOscillator) {
        WebPositionOscillatorConfig old = new WebPositionOscillatorConfig(null);
        this.positionOscillator.copy(old);
        this.positionOscillator = positionOscillator;
        if (!this.positionOscillator.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_POSITION_OSCILLATOR, positionOscillator);
        }
    }

    public boolean validate() {
        if (masterGainVal > MAX_GAIN) {
            LOG.info("validate: invalid masterGainVal {}, setting to {}", masterGainVal, MAX_GAIN);
            setMasterGainVal(MAX_GAIN);
        }
        if (masterGainVal < MIN_GAIN) {
            LOG.info("validate: invalid masterGainVal: {}, setting to {}", masterGainVal, MIN_GAIN);
            setMasterGainVal(MIN_GAIN);
        }
        if (playDurationSec > MAX_PLAY_DURATION_SEC) {
            LOG.info("validate: invalid playDurationSec {}, setting to {}", playDurationSec, MAX_PLAY_DURATION_SEC);
            setPlayDurationSec(MAX_PLAY_DURATION_SEC);
        }
        if (playDurationSec < MIN_PLAY_DURATION_SEC) {
            LOG.info("validate: invalid playDurationSec {}, setting to {}", playDurationSec, MIN_PLAY_DURATION_SEC);
            setPlayDurationSec(MIN_PLAY_DURATION_SEC);
        }
        if (playStartOffsetSec > MAX_PLAY_START_OFFSET_SEC) {
            LOG.info("validate: invalid playStartOffsetSec {}, setting to {}", playStartOffsetSec, MAX_PLAY_START_OFFSET_SEC);
            setPlayStartOffsetSec(MAX_PLAY_START_OFFSET_SEC);
        }
        if (playStartOffsetSec < MIN_PLAY_START_OFFSET_SEC) {
            LOG.info("validate: invalid playStartOffsetSec {}, setting to {}", playStartOffsetSec, MIN_PLAY_START_OFFSET_SEC);
            setPlayStartOffsetSec(MIN_PLAY_START_OFFSET_SEC);
        }
        if (maxGrains > MAX_GRAINS) {
            LOG.info("validate: invalid maxGrains {}, setting to {}", maxGrains, MAX_GRAINS);
            setMaxGrains(MAX_GRAINS);
        }
        if (maxGrains < MIN_GRAINS) {
            LOG.info("validate: invalid maxGrains {}, setting to {}", maxGrains, MIN_GRAINS);
            setMaxGrains(MIN_GRAINS);
        }
        if (bufferPositionPlayRate > MAX_BUFFER_POSITION_PLAY_RATE) {
            LOG.info("validate: invalid bufferPositionPlayRate {}, setting to {}", bufferPositionPlayRate, MAX_BUFFER_POSITION_PLAY_RATE);
            setBufferPositionPlayRate(MAX_BUFFER_POSITION_PLAY_RATE);
        }
        if (bufferPositionPlayRate < MIN_BUFFER_POSITION_PLAY_RATE) {
            LOG.info("validate: invalid bufferPositionPlayRate {}, setting to {}", bufferPositionPlayRate, MIN_BUFFER_POSITION_PLAY_RATE);
            setBufferPositionPlayRate(MIN_BUFFER_POSITION_PLAY_RATE);
        }
        if (audioStopToleranceMs > MAX_AUDIO_STOP_TOLERANCE_MS) {
            LOG.info("validate: invalid audioStopToleranceMs {}, setting to {}", audioStopToleranceMs, MAX_AUDIO_STOP_TOLERANCE_MS);
            setAudioStopToleranceMs(MAX_AUDIO_STOP_TOLERANCE_MS);
        }
        if (audioStopToleranceMs < MIN_AUDIO_STOP_TOLERANCE_MS) {
            LOG.info("validate: invalid audioStopToleranceMs {}, setting to {}", audioStopToleranceMs, MIN_AUDIO_STOP_TOLERANCE_MS);
            setAudioStopToleranceMs(MIN_AUDIO_STOP_TOLERANCE_MS);
        }
        return grain.validate() && envelope.validate() && panner.validate() && positionOscillator.validate() && sizeOscillator.validate();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_MASTER_GAIN_VAL, getMasterGainVal());
        config.put(WEB_CONFIG_PLAY_DURATION_SEC, getPlayDurationSec());
        config.put(WEB_CONFIG_PLAY_START_OFFSET_SEC, getPlayStartOffsetSec());
        config.put(WEB_CONFIG_MAX_GRAINS, getMaxGrains());
        config.put(WEB_CONFIG_BUFFER_POSITION_PLAY_RATE, getBufferPositionPlayRate());
        config.put(WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS, getAudioStopToleranceMs());
        config.put(WEB_CONFIG_IS_USE_POSITION_OSCILLATOR, isUsePositionOscillator());
        config.put(WEB_CONFIG_IS_USE_SIZE_OSCILLATOR, isUseSizeOscillator());
        config.put(WEB_CONFIG_IS_USE_POSITION_FREQ_MOD, isUsePositionFrequencyMod());
        config.put(WEB_CONFIG_IS_USE_POSITION_RANGE_MOD, isUsePositionRangeMod());
        config.putAll(grain.toJsMap());
        config.putAll(envelope.toJsMap());
        config.putAll(panner.toJsMap());
        config.putAll(positionOscillator.toJsMap());
        config.putAll(sizeOscillator.toJsMap());
        return config;
    }

    public WebGranulatorConfig copy(WebGranulatorConfig to) {
        if (to == null) {
            to = new WebGranulatorConfig(pcs);
        }
        to.setMasterGainVal(this.masterGainVal);
        to.setPlayDurationSec(this.playDurationSec);
        to.setPlayStartOffsetSec(this.playStartOffsetSec);
        to.setMaxGrains(this.maxGrains);
        to.setBufferPositionPlayRate(this.bufferPositionPlayRate);
        to.setAudioStopToleranceMs(this.audioStopToleranceMs);
        to.setUsePositionOscillator(this.isUsePositionOscillator);
        to.setUseSizeOscillator(this.isUseSizeOscillator);
        to.setUsePositionFrequencyMod(this.isUsePositionFrequencyMod);
        to.setUsePositionRangeMod(this.isUsePositionRangeMod);

        to.setGrain(getGrain().copy(to.getGrain()));
        to.setEnvelope(getEnvelope().copy(to.getEnvelope()));
        to.setPanner(getPanner().copy(to.getPanner()));

        to.setPositionOscillator(getPositionOscillator().copy(to.getPositionOscillator()));
        to.setSizeOscillator(getSizeOscillator().copy(to.getSizeOscillator()));
        return to;
    }

    @Override
    public String toString() {
        return "WebGranulatorConfig{" +
                "masterGainVal=" + masterGainVal +
                ", playDurationSec=" + playDurationSec +
                ", playStartOffsetSec=" + playStartOffsetSec +
                ", maxGrains=" + maxGrains +
                ", bufferPositionPlayRate=" + bufferPositionPlayRate +
                ", audioStopToleranceMs=" + audioStopToleranceMs +
                ", isUsePositionOscillator=" + isUsePositionOscillator +
                ", isUseSizeOscillator=" + isUseSizeOscillator +
                ", isUsePositionFrequencyMod=" + isUsePositionFrequencyMod +
                ", isUsePositionRangeMod=" + isUsePositionRangeMod +
                ", grain=" + grain +
                ", envelope=" + envelope +
                ", panner=" + panner +
                ", positionOscillator=" + positionOscillator +
                ", sizeOscillator=" + sizeOscillator +
                '}';
    }
}
