package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebOscillatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebOscillatorConfigExport.class);

    private double minValue;
    private double maxValue;
    private String type;
    private double frequency;

    protected String parentConfigPrefix;

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public String getType() {
        return type;
    }

    public double getFrequency() {
        return frequency;
    }

    public String getParentConfigPrefix() {
        return parentConfigPrefix;
    }

    public void populate(WebOscillatorConfig from) {
        if (from == null) {
            return;
        }
        this.minValue = from.getMinValue();
        this.maxValue = from.getMaxValue();
        this.type = from.getType().name();
        this.frequency = from.getFrequency();
        this.parentConfigPrefix = from.getParentConfigPrefix();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(parentConfigPrefix + "minValue", getMinValue());
        config.put(parentConfigPrefix + "maxValue", getMaxValue());
        config.put(parentConfigPrefix + "type", getType());
        config.put(parentConfigPrefix + "frequency", getFrequency());
        return config;
    }

    @Override
    public String toString() {
        return "WebOscillatorConfigExport{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", type='" + type + '\'' +
                ", frequency=" + frequency +
                ", parentConfigPrefix='" + parentConfigPrefix + '\'' +
                '}';
    }
}
