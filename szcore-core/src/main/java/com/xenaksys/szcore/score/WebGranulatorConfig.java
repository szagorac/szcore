package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_BUFFER_POSITION_PLAY_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_GRAINS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_DURATION_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_START_OFFSET_SEC;

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
    private static final double DEFAULT_BUFFER_POSITION_PLAY_RATE = 0.2;
    private static final int DEFAULT_AUDIO_STOP_TOLERANCE_MS = 5;

    private double masterGainVal = DEFAULT_GAIN;
    private double playDurationSec = DEFAULT_PLAY_DURATION_SEC;
    private double playStartOffsetSec = DEFAULT_PLAY_START_OFFSET_SEC;
    private int maxGrains = DEFAULT_GRAINS;
    private double bufferPositionPlayRate = DEFAULT_BUFFER_POSITION_PLAY_RATE;
    private int audioStopToleranceMs = DEFAULT_AUDIO_STOP_TOLERANCE_MS;

    private GrainConfig grain = new GrainConfig();
    private EnvelopeConfig envelope = new EnvelopeConfig();
    private PannerConfig panner = new PannerConfig();

    public double getMasterGainVal() {
        return masterGainVal;
    }

    public void setMasterGainVal(double masterGainVal) {
        this.masterGainVal = masterGainVal;
    }

    public double getPlayDurationSec() {
        return playDurationSec;
    }

    public void setPlayDurationSec(double playDurationSec) {
        this.playDurationSec = playDurationSec;
    }

    public double getPlayStartOffsetSec() {
        return playStartOffsetSec;
    }

    public void setPlayStartOffsetSec(double playStartOffsetSec) {
        this.playStartOffsetSec = playStartOffsetSec;
    }

    public int getMaxGrains() {
        return maxGrains;
    }

    public void setMaxGrains(int maxGrains) {
        this.maxGrains = maxGrains;
    }

    public double getBufferPositionPlayRate() {
        return bufferPositionPlayRate;
    }

    public void setBufferPositionPlayRate(double bufferPositionPlayRate) {
        this.bufferPositionPlayRate = bufferPositionPlayRate;
    }

    public int getAudioStopToleranceMs() {
        return audioStopToleranceMs;
    }

    public void setAudioStopToleranceMs(int audioStopToleranceMs) {
        this.audioStopToleranceMs = audioStopToleranceMs;
    }

    public GrainConfig getGrain() {
        return grain;
    }

    public void setGrain(GrainConfig grain) {
        this.grain = grain;
    }

    public EnvelopeConfig getEnvelope() {
        return envelope;
    }

    public void setEnvelope(EnvelopeConfig envelope) {
        this.envelope = envelope;
    }

    public PannerConfig getPanner() {
        return panner;
    }

    public void setPanner(PannerConfig panner) {
        this.panner = panner;
    }

    public boolean validate() {
        if (masterGainVal > MAX_GAIN) {
            LOG.info("validate: invalid masterGainVal {}, setting to {}", masterGainVal, MAX_GAIN);
            masterGainVal = MAX_GAIN;
        }
        if (masterGainVal < MIN_GAIN) {
            LOG.info("validate: invalid masterGainVal: {}, setting to {}", masterGainVal, MIN_GAIN);
            masterGainVal = MIN_GAIN;
        }
        if (playDurationSec > MAX_PLAY_DURATION_SEC) {
            LOG.info("validate: invalid playDurationSec {}, setting to {}", playDurationSec, MAX_PLAY_DURATION_SEC);
            playDurationSec = MAX_PLAY_DURATION_SEC;
        }
        if (playDurationSec < MIN_PLAY_DURATION_SEC) {
            LOG.info("validate: invalid playDurationSec {}, setting to {}", playDurationSec, MIN_PLAY_DURATION_SEC);
            playDurationSec = MIN_PLAY_DURATION_SEC;
        }
        if (playStartOffsetSec > MAX_PLAY_START_OFFSET_SEC) {
            LOG.info("validate: invalid playStartOffsetSec {}, setting to {}", playStartOffsetSec, MAX_PLAY_START_OFFSET_SEC);
            playStartOffsetSec = MAX_PLAY_START_OFFSET_SEC;
        }
        if (playStartOffsetSec < MIN_PLAY_START_OFFSET_SEC) {
            LOG.info("validate: invalid playStartOffsetSec {}, setting to {}", playStartOffsetSec, MIN_PLAY_START_OFFSET_SEC);
            playStartOffsetSec = MIN_PLAY_START_OFFSET_SEC;
        }
        if (maxGrains > MAX_GRAINS) {
            LOG.info("validate: invalid maxGrains {}, setting to {}", maxGrains, MAX_GRAINS);
            maxGrains = MAX_GRAINS;
        }
        if (maxGrains < MIN_GRAINS) {
            LOG.info("validate: invalid maxGrains {}, setting to {}", maxGrains, MIN_GRAINS);
            maxGrains = MIN_GRAINS;
        }
        if (bufferPositionPlayRate > MAX_BUFFER_POSITION_PLAY_RATE) {
            LOG.info("validate: invalid bufferPositionPlayRate {}, setting to {}", bufferPositionPlayRate, MAX_BUFFER_POSITION_PLAY_RATE);
            bufferPositionPlayRate = MAX_BUFFER_POSITION_PLAY_RATE;
        }
        if (bufferPositionPlayRate < MIN_BUFFER_POSITION_PLAY_RATE) {
            LOG.info("validate: invalid bufferPositionPlayRate {}, setting to {}", bufferPositionPlayRate, MIN_BUFFER_POSITION_PLAY_RATE);
            bufferPositionPlayRate = MIN_BUFFER_POSITION_PLAY_RATE;
        }
        if (audioStopToleranceMs > MAX_AUDIO_STOP_TOLERANCE_MS) {
            LOG.info("validate: invalid audioStopToleranceMs {}, setting to {}", audioStopToleranceMs, MAX_AUDIO_STOP_TOLERANCE_MS);
            audioStopToleranceMs = MAX_AUDIO_STOP_TOLERANCE_MS;
        }
        if (audioStopToleranceMs < MIN_AUDIO_STOP_TOLERANCE_MS) {
            LOG.info("validate: invalid audioStopToleranceMs {}, setting to {}", audioStopToleranceMs, MIN_AUDIO_STOP_TOLERANCE_MS);
            audioStopToleranceMs = MIN_AUDIO_STOP_TOLERANCE_MS;
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
            to = new WebGranulatorConfig();
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
