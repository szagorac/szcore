package com.xenaksys.szcore.score.web.config;

import com.xenaksys.szcore.model.OscillatorType;

import java.beans.PropertyChangeSupport;

import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SIZE_OSCILLATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.model.OscillatorType.TRIANGLE;

public class WebSizeOscillatorConfig extends WebOscillatorConfig {
    private static final double MIN_MIN_VALUE = -10.0 * 1000.0;
    private static final double MAX_MIN_VALUE = 10.0 * 1000.0;
    private static final double MIN_MAX_VALUE = -10.0 * 1000.0;
    private static final double MAX_MAX_VALUE = 10.0 * 1000.0;
    private static final double MIN_FREQUENCY = 0.0001;
    private static final double MAX_FREQUENCY = 20.0 * 1000.0;

    private static final double DEFAULT_MIN_VALUE = -30.0;
    private static final double DEFAULT_MAX_VALUE = 500.0;
    private static final OscillatorType DEFAULT_TYPE = TRIANGLE;
    private static final double DEFAULT_FREQUENCY = 0.1;

    public WebSizeOscillatorConfig(PropertyChangeSupport pcs) {
        super(pcs, WEB_OBJ_CONFIG_GRANULATOR, WEB_CONFIG_SIZE_OSCILLATOR, WEB_CONFIG_SIZE_OSCILLATOR + DOT);
        this.minValue = DEFAULT_MIN_VALUE;
        this.maxValue = DEFAULT_MAX_VALUE;
        this.type = DEFAULT_TYPE;
        this.frequency = DEFAULT_FREQUENCY;
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

        return true;
    }

    public WebSizeOscillatorConfig copy(WebSizeOscillatorConfig to) {
        return (WebSizeOscillatorConfig) super.copy(to);
    }
}
