package com.xenaksys.szcore.event;


public class WebScorePrecountEvent extends WebScoreEvent {

    private int count;
    private boolean isOn;
    private int colourId;

    public WebScorePrecountEvent(int count, boolean isOn, int colourId, long creationTime) {
        super(null, null, creationTime);
        this.count = count;
        this.isOn = isOn;
        this.colourId = colourId;
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.PRECOUNT;
    }

    public int getCount() {
        return count;
    }

    public boolean getIsOn() {
        return isOn;
    }

    public int getColourId() {
        return colourId;
    }

    @Override
    public String toString() {
        return "WebScorePrecountEvent{" +
                "count=" + count +
                ", isOn=" + isOn +
                ", colourId=" + colourId +
                '}';
    }
}
