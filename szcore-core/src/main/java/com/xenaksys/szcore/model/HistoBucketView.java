package com.xenaksys.szcore.model;

public class HistoBucketView {
    private final int count;
    private final String dateLabel;
    private final long startTimeMs;

    public HistoBucketView(long startTimeMs, int count, String dateLabel) {
        this.startTimeMs = startTimeMs;
        this.count = count;
        this.dateLabel = dateLabel;
    }

    public int getCount() {
        return count;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }
}
