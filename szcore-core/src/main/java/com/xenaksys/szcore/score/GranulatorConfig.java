package com.xenaksys.szcore.score;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_BUFFER_POSITION_PLAY_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_GRAINS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_DURATION_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_START_OFFSET_SEC;

public class GranulatorConfig {
    private double masterGainVal;
    private double playDurationSec;
    private double playStartOffsetSec;
    private int maxGrains;
    private double bufferPositionPlayRate;
    private int audioStopToleranceMs;
    private GrainConfig grain;
    private EnvelopeConfig envelope;
    private PannerConfig panner;

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

    @Override
    public String toString() {
        return "GranulatorConfig{" +
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
