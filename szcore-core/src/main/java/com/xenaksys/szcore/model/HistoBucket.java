package com.xenaksys.szcore.model;

import java.util.concurrent.atomic.AtomicInteger;

public class HistoBucket {
    private final AtomicInteger count = new AtomicInteger(0);
    private final long startTimeMs;

    public HistoBucket(long startTimeMs) {
        this.startTimeMs = startTimeMs;
    }

    public int getCount() {
        return count.get();
    }

    public int incrementCount() {
        return count.incrementAndGet();
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }
}
