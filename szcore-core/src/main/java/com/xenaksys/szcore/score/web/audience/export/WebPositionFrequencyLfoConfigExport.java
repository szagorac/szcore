package com.xenaksys.szcore.score.web.audience.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebPositionFrequencyLfoConfigExport extends WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebPositionFrequencyLfoConfigExport.class);

    @Override
    public String toString() {
        return "WebPositionFrequencyLfoConfigExport{" +
                "minValue=" + getMinValue() +
                ", maxValue=" + getMaxValue() +
                ", type='" + getType() + '\'' +
                ", frequency=" + getFrequency() +
                ", parentConfigPrefix='" + getParentConfigPrefix() + '\'' +
                '}';
    }
}
