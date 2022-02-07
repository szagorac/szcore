package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.WebCounter;

public class WebCounterExport {
    private int count;
    private String name;
    private int maxCount;

    public void populate(WebCounter from) {
        if (from == null) {
            return;
        }
        this.count = from.getCounterValue();
        this.name = from.getId();
        this.maxCount = from.getMaxCount();
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public String toString() {
        return "WebCounterExport{" +
                "count=" + count +
                ", name='" + name + '\'' +
                ", maxCount=" + maxCount +
                '}';
    }
}
