package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebSynthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_BPM;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DUR_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_OSC1_FREQ;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_OSC2_FREQ;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_OSC3_FREQ;

public class WebSynthConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebSynthConfigExport.class);

    private ArrayList<Double> osc1Freq;
    private ArrayList<Double> osc2Freq;
    private ArrayList<Double> osc3Freq;
    private int bpm;
    private int durationMultiplier;

    public ArrayList<Double> getOsc1Freq() {
        return osc1Freq;
    }

    public void setOsc1Freq(ArrayList<Double> osc1Freq) {
        this.osc1Freq = osc1Freq;
    }

    public ArrayList<Double> getOsc2Freq() {
        return osc2Freq;
    }

    public void setOsc2Freq(ArrayList<Double> osc2Freq) {
        this.osc2Freq = osc2Freq;
    }

    public ArrayList<Double> getOsc3Freq() {
        return osc3Freq;
    }

    public void setOsc3Freq(ArrayList<Double> osc3Freq) {
        this.osc3Freq = osc3Freq;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(int durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    public void populate(WebSynthConfig from) {
        if (from == null) {
            return;
        }
        this.bpm = from.getBpm();
        this.osc1Freq = from.getOsc1Freq();
        this.osc2Freq = from.getOsc2Freq();
        this.osc3Freq = from.getOsc3Freq();
        this.durationMultiplier = from.getDurationMultiplier();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_BPM, getBpm());
        config.put(WEB_CONFIG_OSC1_FREQ, getOsc1Freq());
        config.put(WEB_CONFIG_OSC2_FREQ, getOsc2Freq());
        config.put(WEB_CONFIG_OSC3_FREQ, getOsc3Freq());
        config.put(WEB_CONFIG_DUR_MULTIPLIER, getDurationMultiplier());
        return config;
    }
}
