package com.xenaksys.szcore.event.web.audience;


public class WebAudiencePrecountEvent extends WebAudienceEvent {

    private int count;
    private boolean isOn;
    private int colourId;

    public WebAudiencePrecountEvent(int count, boolean isOn, int colourId, long creationTime) {
        super(null, null, creationTime);
        this.count = count;
        this.isOn = isOn;
        this.colourId = colourId;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.PRECOUNT;
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
        return "WebAudiencePrecountEvent{" +
                "count=" + count +
                ", isOn=" + isOn +
                ", colourId=" + colourId +
                '}';
    }
}
