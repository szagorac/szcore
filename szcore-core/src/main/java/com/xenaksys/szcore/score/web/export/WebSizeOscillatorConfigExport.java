package com.xenaksys.szcore.score.web.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSizeOscillatorConfigExport extends WebOscillatorConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebSizeOscillatorConfigExport.class);

    @Override
    public String toString() {
        return "WebSizeOscillatorConfigExport{" +
                "minValue=" + getMinValue() +
                ", maxValue=" + getMaxValue() +
                ", type='" + getType() + '\'' +
                ", frequency=" + getFrequency() +
                ", parentConfigPrefix='" + getParentConfigPrefix() + '\'' +
                '}';
    }
}
