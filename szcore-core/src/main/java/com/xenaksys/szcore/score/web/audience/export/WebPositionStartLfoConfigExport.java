package com.xenaksys.szcore.score.web.audience.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebPositionStartLfoConfigExport extends WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebPositionStartLfoConfigExport.class);

    @Override
    public String toString() {
        return "WebPositionStartLfoConfigExport{" +
                "minValue=" + getMinValue() +
                ", maxValue=" + getMaxValue() +
                ", type='" + getType() + '\'' +
                ", frequency=" + getFrequency() +
                ", parentConfigPrefix='" + getParentConfigPrefix() + '\'' +
                '}';
    }
}
