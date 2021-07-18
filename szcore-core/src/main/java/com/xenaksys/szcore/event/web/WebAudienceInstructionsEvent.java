package com.xenaksys.szcore.event.web;


public class WebAudienceInstructionsEvent extends WebAudienceEvent {

    private final String l1;
    private final String l2;
    private final String l3;
    private final boolean isVisible;

    public WebAudienceInstructionsEvent(String l1, String l2, String l3, boolean isVisible, long creationTime) {
        super(null, null, creationTime);
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.isVisible = isVisible;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.INSTRUCTIONS;
    }

    public String getL1() {
        return l1;
    }

    public String getL2() {
        return l2;
    }

    public String getL3() {
        return l3;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public String toString() {
        return "WebAudienceInstructionsEvent{" +
                "l1='" + l1 + '\'' +
                ", l2='" + l2 + '\'' +
                ", l3='" + l3 + '\'' +
                ", isVisible=" + isVisible +
                '}';
    }
}
