package com.xenaksys.szcore.score.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_BUFFER_POSITION_PLAY_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_GRAINS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_DURATION_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_START_OFFSET_SEC;
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
    private static final double CHANGE_THRESHOLD = 10E-3;

    private static final double DEFAULT_GAIN = 0.5;
    private static final double DEFAULT_PLAY_DURATION_SEC = 30.0;
    private static final double DEFAULT_PLAY_START_OFFSET_SEC = 0.0;
    private static final int DEFAULT_GRAINS = 12;
    private static final double DEFAULT_BUFFER_POSITION_PLAY_RATE = 0.2;
    private static final int DEFAULT_AUDIO_STOP_TOLERANCE_MS = 5;

    private double masterGainVal = DEFAULT_GAIN;
    private double playDurationSec = DEFAULT_PLAY_DURATION_SEC;
    private double playStartOffsetSec = DEFAULT_PLAY_START_OFFSET_SEC;
    private int maxGrains = DEFAULT_GRAINS;
    private double bufferPositionPlayRate = DEFAULT_BUFFER_POSITION_PLAY_RATE;
    private int audioStopToleranceMs = DEFAULT_AUDIO_STOP_TOLERANCE_MS;

    private WebGrainConfig grain;
    private WebEnvelopeConfig envelope;
    private WebPannerConfig panner;

    private final PropertyChangeSupport pcs;

    public WebGranulatorConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
        this.grain = new WebGrainConfig(pcs);
        this.envelope = new WebEnvelopeConfig(pcs);
        this.panner = new WebPannerConfig(pcs);
    }

    public double getMasterGainVal() {
        return masterGainVal;
    }

    public void setMasterGainVal(double masterGainVal) {
        double old = this.masterGainVal;
        this.masterGainVal = masterGainVal;
        if (Math.abs(old - this.masterGainVal) > CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_MASTER_GAIN_VAL, masterGainVal);
        }
    }

    public double getPlayDurationSec() {
        return playDurationSec;
    }

    public void setPlayDurationSec(double playDurationSec) {
        double old = this.playDurationSec;
        this.playDurationSec = playDurationSec;
        if (Math.abs(old - this.playDurationSec) > CHANGE_THRESHOLD) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_PLAY_DURATION_SEC, playDurationSec);
        }
    }

    public double getPlayStartOffsetSec() {
        return playStartOffsetSec;
    }

    public void setPlayStartOffsetSec(double playStartOffsetSec) {
        double old = this.playStartOffsetSec;
        this.playStartOffsetSec = playStartOffsetSec;
        if (Math.abs(old - this.playStartOffsetSec) > CHANGE_THRESHOLD) {
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
        if (Math.abs(old - this.bufferPositionPlayRate) > CHANGE_THRESHOLD) {
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
        return grain.validate() && envelope.validate() && panner.validate();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_MASTER_GAIN_VAL, getMasterGainVal());
        config.put(WEB_CONFIG_PLAY_DURATION_SEC, getPlayDurationSec());
        config.put(WEB_CONFIG_PLAY_START_OFFSET_SEC, getPlayStartOffsetSec());
        config.put(WEB_CONFIG_MAX_GRAINS, getMaxGrains());
        config.put(WEB_CONFIG_BUFFER_POSITION_PLAY_RATE, getBufferPositionPlayRate());
        config.put(WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS, getAudioStopToleranceMs());
        config.putAll(grain.toJsMap());
        config.putAll(envelope.toJsMap());
        config.putAll(panner.toJsMap());
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

        to.setGrain(getGrain().copy(to.getGrain()));
        to.setEnvelope(getEnvelope().copy(to.getEnvelope()));
        to.setPanner(getPanner().copy(to.getPanner()));
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
                ", grain=" + grain +
                ", envelope=" + envelope +
                ", panner=" + panner +
                '}';
    }
}
