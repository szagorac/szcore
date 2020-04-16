package com.xenaksys.szcore.score;

import java.util.HashMap;
import java.util.Map;

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
        config.put("masterGainVal", getMasterGainVal());
        config.put("playDurationSec", getPlayDurationSec());
        config.put("playStartOffsetSec", getPlayStartOffsetSec());
        config.put("maxGrains", getMaxGrains());
        config.put("bufferPositionPlayRate", getBufferPositionPlayRate());
        config.put("audioStopToleranceMs", getAudioStopToleranceMs());
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
