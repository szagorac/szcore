package com.xenaksys.szcore.score.web.audience.config;

import com.xenaksys.szcore.model.OscillatorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQUENCY;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_VALUE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MIN_VALUE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_TYPE;

public class WebOscillatorConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebOscillatorConfig.class);

    protected double minValue;
    protected double maxValue;
    protected OscillatorType type;
    protected double frequency;

    protected String parentId;
    protected String oscillatorId;
    protected String parentConfigPrefix;

    private final PropertyChangeSupport pcs;

    public WebOscillatorConfig(PropertyChangeSupport pcs, String parentId, String oscillatorId, String parentConfigPrefix) {
        this.pcs = pcs;
        this.parentId = parentId;
        this.oscillatorId = oscillatorId;
        this.parentConfigPrefix = parentConfigPrefix;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        double old = this.minValue;
        this.minValue = minValue;
        if (Math.abs(old - this.minValue) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(parentId, oscillatorId, this);
        }
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        double old = this.maxValue;
        this.maxValue = maxValue;
        if (Math.abs(old - this.maxValue) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(parentId, oscillatorId, this);
        }
    }

    public OscillatorType getType() {
        return type;
    }

    public void setType(OscillatorType type) {
        OscillatorType old = this.type;
        this.type = type;
        if (type != old) {
            pcs.firePropertyChange(parentId, oscillatorId, this);
        }
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        double old = this.frequency;
        this.frequency = frequency;
        if (Math.abs(old - this.frequency) > WEB_CONFIG_DOUBLE_CHANGE_THRESHOLD) {
            pcs.firePropertyChange(parentId, oscillatorId, this);
        }
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getOscillatorId() {
        return oscillatorId;
    }

    public void setOscillatorId(String oscillatorId) {
        this.oscillatorId = oscillatorId;
    }

    public void setParentConfigPrefix(String parentConfigPrefix) {
        this.parentConfigPrefix = parentConfigPrefix;
    }

    public String getParentConfigPrefix() {
        return parentConfigPrefix;
    }


    public void update(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return;
        }
        if (config.containsKey(WEB_CONFIG_MIN_VALUE)) {
            setMinValue((Double) config.get(WEB_CONFIG_MIN_VALUE));
        }
        if (config.containsKey(WEB_CONFIG_MAX_VALUE)) {
            setMaxValue((Double) config.get(WEB_CONFIG_MAX_VALUE));
        }
        if (config.containsKey(WEB_CONFIG_TYPE)) {
            setType(OscillatorType.valueOf((String) config.get(WEB_CONFIG_TYPE)));
        }
        if (config.containsKey(WEB_CONFIG_FREQUENCY)) {
            setFrequency((Double) config.get(WEB_CONFIG_FREQUENCY));
        }
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(parentConfigPrefix + WEB_CONFIG_MIN_VALUE, getMinValue());
        config.put(parentConfigPrefix + WEB_CONFIG_MAX_VALUE, getMaxValue());
        config.put(parentConfigPrefix + WEB_CONFIG_TYPE, getType().name());
        config.put(parentConfigPrefix + WEB_CONFIG_FREQUENCY, getFrequency());
        return config;
    }

    public WebOscillatorConfig copy(WebOscillatorConfig to) {
        if (to == null) {
            to = new WebOscillatorConfig(pcs, parentId, oscillatorId, parentConfigPrefix);
        }
        to.setMinValue(this.minValue);
        to.setMaxValue(this.maxValue);
        to.setType(this.type);
        to.setFrequency(this.frequency);
        to.setParentId(this.parentId);
        to.setOscillatorId(this.oscillatorId);
        to.setParentConfigPrefix(this.parentConfigPrefix);
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebOscillatorConfig that = (WebOscillatorConfig) o;
        return Double.compare(that.minValue, minValue) == 0 && Double.compare(that.maxValue, maxValue) == 0 && Double.compare(that.frequency, frequency) == 0 && type == that.type && parentId.equals(that.parentId) && oscillatorId.equals(that.oscillatorId) && parentConfigPrefix.equals(that.parentConfigPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minValue, maxValue, type, frequency, parentId, oscillatorId, parentConfigPrefix);
    }

    @Override
    public String toString() {
        return "WebOscillatorConfig{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", type='" + type + '\'' +
                ", frequency=" + frequency +
                ", parentId='" + parentId + '\'' +
                ", oscillatorId='" + oscillatorId + '\'' +
                ", parentConfigPrefix='" + parentConfigPrefix + '\'' +
                '}';
    }
}
