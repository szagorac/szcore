package com.xenaksys.szcore.score.web.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebPositionEndLfoConfigExport extends WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebPositionEndLfoConfigExport.class);

    @Override
    public String toString() {
        return "WebPositionEndLfoConfigExport{" +
                "minValue=" + getMinValue() +
                ", maxValue=" + getMaxValue() +
                ", type='" + getType() + '\'' +
                ", frequency=" + getFrequency() +
                ", parentConfigPrefix='" + getParentConfigPrefix() + '\'' +
                '}';
    }
}
