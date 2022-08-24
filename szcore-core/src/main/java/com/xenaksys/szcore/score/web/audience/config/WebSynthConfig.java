package com.xenaksys.szcore.score.web.audience.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_BPM;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DUR_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQ_MULTIPLIER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_OSC1_FREQ;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_OSC2_FREQ;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_OSC3_FREQ;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SYNTH;

public class WebSynthConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebSynthConfig.class);

    static ArrayList<Double> DEFAULT_OSC1_FREQ = new ArrayList<>(Arrays.asList(523.25, 631.62, 739.99));
    static ArrayList<Double> DEFAULT_OSC2_FREQ = new ArrayList<>(Arrays.asList(261.63, 315.81, 369.99));
    static ArrayList<Double> DEFAULT_OSC3_FREQ = new ArrayList<>(Arrays.asList(261.63, 369.99, 523.25, 739.99, 1046.5, 1479.98, 2093.0, 2959.96, 4186.01, 5919.91));

    private static final int DEFAULT_BPM = 80;
    private static final int DEFAULT_DURATION_MULTIPLIER = 8;
    private static final int DEFAULT_FREQ_MULTIPLIER = 1;

    private ArrayList<Double> osc1Freq = DEFAULT_OSC1_FREQ;
    private ArrayList<Double> osc2Freq = DEFAULT_OSC2_FREQ;
    private ArrayList<Double> osc3Freq = DEFAULT_OSC3_FREQ;
    private int bpm = DEFAULT_BPM;
    private double durationMultiplier = DEFAULT_DURATION_MULTIPLIER;
    private double freqMultiplier = DEFAULT_FREQ_MULTIPLIER;

    private final PropertyChangeSupport pcs;

    public WebSynthConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        double old = this.bpm;
        this.bpm = bpm;
        if (Math.abs(old - this.bpm) > 0) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_CONFIG_BPM, bpm);
        }
    }

    public double getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(double durationMultiplier) {
        double old = this.durationMultiplier;
        this.durationMultiplier = durationMultiplier;
        if (Math.abs(old - this.durationMultiplier) > 0) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_CONFIG_DUR_MULTIPLIER, durationMultiplier);
        }
    }

    public double getFreqMultiplier() {
        return freqMultiplier;
    }

    public void setFreqMultiplier(double freqMultiplier) {
        double old = this.freqMultiplier;
        this.freqMultiplier = freqMultiplier;
        if (Math.abs(old - this.freqMultiplier) > 0) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_CONFIG_FREQ_MULTIPLIER, freqMultiplier);
        }
    }

    public ArrayList<Double> getOsc1Freq() {
        return osc1Freq;
    }

    public void setOsc1Freq(ArrayList<Double> freqs) {
        ArrayList<Double> old = this.osc1Freq;
        this.osc1Freq = freqs;
        if (!freqs.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_CONFIG_OSC1_FREQ, freqs);
        }
    }

    public ArrayList<Double> getOsc2Freq() {
        return osc2Freq;
    }

    public void setOsc2Freq(ArrayList<Double> freqs) {
        ArrayList<Double> old = this.osc2Freq;
        this.osc2Freq = freqs;
        if (!freqs.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_CONFIG_OSC2_FREQ, freqs);
        }
    }

    public ArrayList<Double> getOsc3Freq() {
        return osc3Freq;
    }

    public void setOsc3Freq(ArrayList<Double> freqs) {
        ArrayList<Double> old = this.osc3Freq;
        this.osc3Freq = freqs;
        if (!freqs.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_CONFIG_OSC3_FREQ, freqs);
        }
    }

    public boolean validate() {
        return true;
    }

    public void update(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return;
        }
        if (config.containsKey(WEB_CONFIG_BPM)) {
            setBpm((Integer) config.get(WEB_CONFIG_BPM));
        }
        if (config.containsKey(WEB_CONFIG_OSC1_FREQ)) {
            setOsc1Freq((ArrayList<Double>) config.get(WEB_CONFIG_OSC1_FREQ));
        }
        if (config.containsKey(WEB_CONFIG_OSC2_FREQ)) {
            setOsc2Freq((ArrayList<Double>) config.get(WEB_CONFIG_OSC2_FREQ));
        }
        if (config.containsKey(WEB_CONFIG_OSC3_FREQ)) {
            setOsc3Freq((ArrayList<Double>) config.get(WEB_CONFIG_OSC3_FREQ));
        }
        if (config.containsKey(WEB_CONFIG_DUR_MULTIPLIER)) {
            setDurationMultiplier((Double)config.get(WEB_CONFIG_DUR_MULTIPLIER));
        }
        if (config.containsKey(WEB_CONFIG_FREQ_MULTIPLIER)) {
            setFreqMultiplier((Double)config.get(WEB_CONFIG_FREQ_MULTIPLIER));
        }
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

    public WebSynthConfig copy(WebSynthConfig to) {
        if (to == null) {
            to = new WebSynthConfig(pcs);
        }
        to.setBpm(this.bpm);
        to.setOsc1Freq(this.osc1Freq);
        to.setOsc2Freq(this.osc2Freq);
        to.setOsc3Freq(this.osc3Freq);
        to.setDurationMultiplier(this.durationMultiplier);
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSynthConfig that = (WebSynthConfig) o;
        return bpm == that.bpm && durationMultiplier == that.durationMultiplier && Objects.equals(osc1Freq, that.osc1Freq) && Objects.equals(osc2Freq, that.osc2Freq) && Objects.equals(osc3Freq, that.osc3Freq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(osc1Freq, osc2Freq, osc3Freq, bpm, durationMultiplier);
    }

    @Override
    public String toString() {
        return "WebSynthConfig{" +
                "osc1Freq=" + osc1Freq +
                ", osc2Freq=" + osc2Freq +
                ", osc3Freq=" + osc3Freq +
                ", bpm=" + bpm +
                ", durationMultiplier=" + durationMultiplier +
                ", pcs=" + pcs +
                '}';
    }
}
