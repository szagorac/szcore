package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebPositionOscillatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WebPositionOscillatorConfigExport extends WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebPositionOscillatorConfigExport.class);

    private WebPositionFrequencyLfoConfigExport frequencyLfoConfig = new WebPositionFrequencyLfoConfigExport();
    private WebPositionStartLfoConfigExport startLfoConfig = new WebPositionStartLfoConfigExport();
    private WebPositionEndLfoConfigExport endLfoConfig = new WebPositionEndLfoConfigExport();

    public WebPositionFrequencyLfoConfigExport getFrequencyLfoConfig() {
        return frequencyLfoConfig;
    }

    public void setFrequencyLfoConfig(WebPositionFrequencyLfoConfigExport frequencyLfoConfig) {
        this.frequencyLfoConfig = frequencyLfoConfig;
    }

    public WebPositionStartLfoConfigExport getStartLfoConfig() {
        return startLfoConfig;
    }

    public void setStartLfoConfig(WebPositionStartLfoConfigExport startLfoConfig) {
        this.startLfoConfig = startLfoConfig;
    }

    public WebPositionEndLfoConfigExport getEndLfoConfig() {
        return endLfoConfig;
    }

    public void setEndLfoConfig(WebPositionEndLfoConfigExport endLfoConfig) {
        this.endLfoConfig = endLfoConfig;
    }

    public void populate(WebPositionOscillatorConfig from) {
        if (from == null) {
            return;
        }
        super.populate(from);
        this.frequencyLfoConfig.populate(from.getFrequencyLfoConfig());
        this.startLfoConfig.populate(from.getStartLfoConfig());
        this.endLfoConfig.populate(from.getEndLfoConfig());
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = super.toJsMap();
        config.putAll(this.frequencyLfoConfig.toJsMap());
        config.putAll(this.startLfoConfig.toJsMap());
        config.putAll(this.endLfoConfig.toJsMap());
        return config;
    }


    @Override
    public String toString() {
        return "WebPositionOscillatorConfigExport{" +
                " {" + super.toString() + "} " +
                ", frequencyLfoConfig=" + frequencyLfoConfig +
                ", startLfoConfig=" + startLfoConfig +
                ", endLfoConfig=" + endLfoConfig +
                '}';
    }
}
