package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebGranulatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_BUFFER_POSITION_PLAY_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_POSITION_FREQ_MOD;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_POSITION_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_POSITION_RANGE_MOD;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_SIZE_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_GRAINS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_DURATION_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_START_OFFSET_SEC;

public class WebGranulatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebGranulatorConfigExport.class);

    private double masterGainVal;
    private double playDurationSec;
    private double playStartOffsetSec;
    private int maxGrains;
    private double bufferPositionPlayRate;
    private int audioStopToleranceMs;
    private boolean isUsePositionOscillator;
    private boolean isUseSizeOscillator;
    private boolean isUsePositionFrequencyMod;
    private boolean isUsePositionRangeMod;

    private WebGrainConfigExport grain;
    private WebEnvelopeConfigExport envelope;
    private WebPannerConfigExport panner;

    private WebPositionOscillatorConfigExport positionOscillator;
    private WebSizeOscillatorConfigExport sizeOscillator;

    public double getMasterGainVal() {
        return masterGainVal;
    }

    public double getPlayDurationSec() {
        return playDurationSec;
    }

    public double getPlayStartOffsetSec() {
        return playStartOffsetSec;
    }

    public int getMaxGrains() {
        return maxGrains;
    }

    public double getBufferPositionPlayRate() {
        return bufferPositionPlayRate;
    }

    public int getAudioStopToleranceMs() {
        return audioStopToleranceMs;
    }

    public WebGrainConfigExport getGrain() {
        return grain;
    }

    public WebEnvelopeConfigExport getEnvelope() {
        return envelope;
    }

    public WebPannerConfigExport getPanner() {
        return panner;
    }

    public boolean isUsePositionOscillator() {
        return isUsePositionOscillator;
    }

    public boolean isUseSizeOscillator() {
        return isUseSizeOscillator;
    }

    public boolean isUsePositionFrequencyMod() {
        return isUsePositionFrequencyMod;
    }

    public boolean isUsePositionRangeMod() {
        return isUsePositionRangeMod;
    }

    public WebPositionOscillatorConfigExport getPositionOscillator() {
        return positionOscillator;
    }

    public WebSizeOscillatorConfigExport getSizeOscillator() {
        return sizeOscillator;
    }

    public void populate(WebGranulatorConfig from) {
        if (from == null) {
            return;
        }
        this.masterGainVal = from.getMasterGainVal();
        this.playDurationSec = from.getPlayDurationSec();
        this.playStartOffsetSec = from.getPlayStartOffsetSec();
        this.maxGrains = from.getMaxGrains();
        this.bufferPositionPlayRate = from.getBufferPositionPlayRate();
        this.audioStopToleranceMs = from.getAudioStopToleranceMs();
        this.isUsePositionOscillator = from.isUsePositionOscillator();
        this.isUseSizeOscillator = from.isUseSizeOscillator();
        this.isUsePositionRangeMod = from.isUsePositionRangeMod();
        this.isUsePositionFrequencyMod = from.isUsePositionFrequencyMod();

        WebGrainConfigExport grainConfigExport = new WebGrainConfigExport();
        grainConfigExport.populate(from.getGrain());
        this.grain = grainConfigExport;

        WebEnvelopeConfigExport envelopeConfigExport = new WebEnvelopeConfigExport();
        envelopeConfigExport.populate(from.getEnvelope());
        this.envelope = envelopeConfigExport;

        WebPannerConfigExport pannerConfigExport = new WebPannerConfigExport();
        pannerConfigExport.populate(from.getPanner());
        this.panner = pannerConfigExport;

        WebPositionOscillatorConfigExport positionOscillatorConfigExport = new WebPositionOscillatorConfigExport();
        positionOscillatorConfigExport.populate(from.getPositionOscillator());
        this.positionOscillator = positionOscillatorConfigExport;

        WebSizeOscillatorConfigExport sizeOscillatorConfigExport = new WebSizeOscillatorConfigExport();
        sizeOscillatorConfigExport.populate(from.getSizeOscillator());
        this.sizeOscillator = sizeOscillatorConfigExport;
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
        config.put(WEB_CONFIG_IS_USE_POSITION_RANGE_MOD, isUsePositionFrequencyMod());
        config.putAll(grain.toJsMap());
        config.putAll(envelope.toJsMap());
        config.putAll(panner.toJsMap());
        config.putAll(positionOscillator.toJsMap());
        config.putAll(sizeOscillator.toJsMap());
        return config;
    }

    @Override
    public String toString() {
        return "WebGranulatorConfigExport{" +
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
