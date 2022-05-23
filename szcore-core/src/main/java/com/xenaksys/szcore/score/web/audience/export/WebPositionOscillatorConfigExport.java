package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebPositionOscillatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WebPositionOscillatorConfigExport extends WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebPositionOscillatorConfigExport.class);

    private WebPositionFrequencyLfoConfigExport frequencyLFO = new WebPositionFrequencyLfoConfigExport();
    private WebPositionStartLfoConfigExport startLFO = new WebPositionStartLfoConfigExport();
    private WebPositionEndLfoConfigExport endLFO = new WebPositionEndLfoConfigExport();

    public WebPositionFrequencyLfoConfigExport getFrequencyLFO() {
        return frequencyLFO;
    }

    public void setFrequencyLFO(WebPositionFrequencyLfoConfigExport frequencyLFO) {
        this.frequencyLFO = frequencyLFO;
    }

    public WebPositionStartLfoConfigExport getStartLFO() {
        return startLFO;
    }

    public void setStartLFO(WebPositionStartLfoConfigExport startLFO) {
        this.startLFO = startLFO;
    }

    public WebPositionEndLfoConfigExport getEndLFO() {
        return endLFO;
    }

    public void setEndLFO(WebPositionEndLfoConfigExport endLFO) {
        this.endLFO = endLFO;
    }

    public void populate(WebPositionOscillatorConfig from) {
        if (from == null) {
            return;
        }
        super.populate(from);
        this.frequencyLFO.populate(from.getFrequencyLfoConfig());
        this.startLFO.populate(from.getStartLfoConfig());
        this.endLFO.populate(from.getEndLfoConfig());
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = super.toJsMap();
        config.putAll(this.frequencyLFO.toJsMap());
        config.putAll(this.startLFO.toJsMap());
        config.putAll(this.endLFO.toJsMap());
        return config;
    }


    @Override
    public String toString() {
        return "WebPositionOscillatorConfigExport{" +
                " {" + super.toString() + "} " +
                ", frequencyLFO=" + frequencyLFO +
                ", startLFO=" + startLFO +
                ", endLFO=" + endLFO +
                '}';
    }
}
