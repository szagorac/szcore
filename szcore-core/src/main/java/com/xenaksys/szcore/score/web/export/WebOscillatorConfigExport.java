package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebOscillatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQUENCY;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_VALUE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MIN_VALUE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_TYPE;

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
        config.put(parentConfigPrefix + WEB_CONFIG_MIN_VALUE, getMinValue());
        config.put(parentConfigPrefix + WEB_CONFIG_MAX_VALUE, getMaxValue());
        config.put(parentConfigPrefix + WEB_CONFIG_TYPE, getType());
        config.put(parentConfigPrefix + WEB_CONFIG_FREQUENCY, getFrequency());
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
