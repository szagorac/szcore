package com.xenaksys.szcore.event.web;

public class WebScorePartRegEvent extends WebScoreInEvent {
    private final String part;

    public WebScorePartRegEvent(String eventId, String sourceAddr, String part, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.part = part;
    }

    public String getPart() {
        return part;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.PART_REG;
    }

    @Override
    public String toString() {
        return "WebScoreConnectionEvent{" +
                "part=" + part +
                '}';
    }
}
