package com.xenaksys.szcore.algo.config;

import java.util.ArrayList;
import java.util.List;

public class TranspositionPageConfig {
    private final int pageNo;
    private final String part;

    List<TextElementConfig> textConfigs = new ArrayList<>();

    public TranspositionPageConfig(int pageNo, String part) {
        this.pageNo = pageNo;
        this.part = part;
    }

    public void addTextConfig(TextElementConfig textElementConfig) {
        textConfigs.add(textElementConfig);
    }

    public List<TextElementConfig> getTextConfigs() {
        return textConfigs;
    }

    public int getPageNo() {
        return pageNo;
    }

    public String getPart() {
        return part;
    }
}
