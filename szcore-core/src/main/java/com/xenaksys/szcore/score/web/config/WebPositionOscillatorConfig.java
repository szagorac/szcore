package com.xenaksys.szcore.score.web.config;

import com.xenaksys.szcore.model.OscillatorType;

import java.beans.PropertyChangeSupport;
import java.util.Map;

import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_END_LFO;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQUENCY_LFO;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_POSITION_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_START_LFO;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.model.OscillatorType.TRIANGLE;

public class WebPositionOscillatorConfig extends WebOscillatorConfig {
    private static final double MIN_MIN_VALUE = 0.0;
    private static final double MAX_MIN_VALUE = 10E7;
    private static final double MIN_MAX_VALUE = 10.0;
    private static final double MAX_MAX_VALUE = 10E7;
    private static final double MIN_FREQUENCY = 10E-7;
    private static final double MAX_FREQUENCY = 10E7;

    private static final double DEFAULT_MIN_VALUE = 500.0;
    private static final double DEFAULT_MAX_VALUE = 4500.0;
    private static final OscillatorType DEFAULT_TYPE = TRIANGLE;
    private static final double DEFAULT_FREQUENCY = 0.1;

    private WebPositionFrequencyLfoConfig frequencyLfoConfig;
    private WebPositionStartLfoConfig startLfoConfig;
    private WebPositionEndLfoConfig endLfoConfig;

    public WebPositionOscillatorConfig(PropertyChangeSupport pcs) {
        super(pcs, WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_POSITION_OSCILLATOR, WEB_CONFIG_POSITION_OSCILLATOR + DOT);
        this.minValue = DEFAULT_MIN_VALUE;
        this.maxValue = DEFAULT_MAX_VALUE;
        this.type = DEFAULT_TYPE;
        this.frequency = DEFAULT_FREQUENCY;
        this.frequencyLfoConfig = new WebPositionFrequencyLfoConfig(pcs);
        this.startLfoConfig = new WebPositionStartLfoConfig(pcs);
        this.endLfoConfig = new WebPositionEndLfoConfig(pcs);
    }

    public WebPositionFrequencyLfoConfig getFrequencyLfoConfig() {
        return frequencyLfoConfig;
    }

    public void setFrequencyLfoConfig(WebPositionFrequencyLfoConfig frequencyLfoConfig) {
        this.frequencyLfoConfig = frequencyLfoConfig;
    }

    public WebPositionStartLfoConfig getStartLfoConfig() {
        return startLfoConfig;
    }

    public void setStartLfoConfig(WebPositionStartLfoConfig startLfoConfig) {
        this.startLfoConfig = startLfoConfig;
    }

    public WebPositionEndLfoConfig getEndLfoConfig() {
        return endLfoConfig;
    }

    public void setEndLfoConfig(WebPositionEndLfoConfig endLfoConfig) {
        this.endLfoConfig = endLfoConfig;
    }

    public boolean validate() {
        if (getMinValue() < MIN_MIN_VALUE) {
            LOG.info("validate: invalid getMinValue, setting to {}", MIN_MIN_VALUE);
            setMinValue(MIN_MIN_VALUE);
        }
        if (getMinValue() > MAX_MIN_VALUE) {
            LOG.info("validate: invalid getMinValue, setting to {}", MAX_MIN_VALUE);
            setMinValue(MAX_MIN_VALUE);
        }
        if (getMaxValue() < MIN_MAX_VALUE) {
            LOG.info("validate: invalid getMaxValue, setting to {}", MIN_MAX_VALUE);
            setMinValue(MIN_MAX_VALUE);
        }
        if (getMaxValue() > MAX_MAX_VALUE) {
            LOG.info("validate: invalid getMaxValue, setting to {}", MAX_MAX_VALUE);
            setMinValue(MAX_MAX_VALUE);
        }
        if (getFrequency() < MIN_FREQUENCY) {
            LOG.info("validate: invalid getFrequency, setting to {}", MIN_FREQUENCY);
            setMinValue(MIN_FREQUENCY);
        }
        if (getFrequency() > MAX_FREQUENCY) {
            LOG.info("validate: invalid getFrequency, setting to {}", MAX_FREQUENCY);
            setMinValue(MAX_FREQUENCY);
        }
        if (getType() == null) {
            LOG.info("validate: invalid type, setting to {}", DEFAULT_TYPE);
            setType(DEFAULT_TYPE);
        }

        return frequencyLfoConfig.validate() && startLfoConfig.validate() && endLfoConfig.validate();
    }

    public void update(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return;
        }
        super.update(config);
        if (config.containsKey(WEB_CONFIG_FREQUENCY_LFO)) {
            frequencyLfoConfig.update((Map<String, Object>) config.get(WEB_CONFIG_FREQUENCY_LFO));
        }
        if (config.containsKey(WEB_CONFIG_START_LFO)) {
            startLfoConfig.update((Map<String, Object>) config.get(WEB_CONFIG_START_LFO));
        }
        if (config.containsKey(WEB_CONFIG_END_LFO)) {
            endLfoConfig.update((Map<String, Object>) config.get(WEB_CONFIG_END_LFO));
        }
    }

    public WebPositionOscillatorConfig copy(WebPositionOscillatorConfig to) {
        WebPositionOscillatorConfig out = (WebPositionOscillatorConfig) super.copy(to);
        this.frequencyLfoConfig.copy(to.frequencyLfoConfig);
        this.startLfoConfig.copy(to.startLfoConfig);
        this.endLfoConfig.copy(to.endLfoConfig);
        return out;
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = super.toJsMap();
        config.putAll(this.frequencyLfoConfig.toJsMap());
        config.putAll(this.startLfoConfig.toJsMap());
        config.putAll(this.endLfoConfig.toJsMap());
        return config;
    }
}
