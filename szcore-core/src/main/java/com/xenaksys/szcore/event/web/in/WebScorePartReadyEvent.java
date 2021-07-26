package com.xenaksys.szcore.event.web.in;

public class WebScorePartReadyEvent extends WebScoreInEvent {
    private final String part;

    public WebScorePartReadyEvent(String eventId, String sourceAddr, String part, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.part = part;
    }

    public String getPart() {
        return part;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.PART_READY;
    }

    @Override
    public String toString() {
        return "WebScoreConnectionEvent{" +
                "part=" + part +
                '}';
    }
}