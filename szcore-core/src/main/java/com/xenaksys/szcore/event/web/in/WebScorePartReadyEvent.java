package com.xenaksys.szcore.event.web.in;

import com.xenaksys.szcore.web.WebClientInfo;

public class WebScorePartReadyEvent extends WebScoreInEvent {
    private final String part;

    public WebScorePartReadyEvent(String clientId, String eventId, String sourceAddr, String part, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
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
        return "WebScorePartReadyEvent{" +
                "part=" + part +
                '}';
    }
}
