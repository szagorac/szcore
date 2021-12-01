package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.WebCounter;

public class WebCounterExport {
    private int count;
    private String name;

    public void populate(WebCounter from) {
        if (from == null) {
            return;
        }
        this.count = from.getCounterValue();
        this.name = from.getId();
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "WebCounterExport{" +
                "count=" + count +
                ", name='" + name + '\'' +
                '}';
    }
}
