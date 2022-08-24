package com.xenaksys.szcore.event.web.in;

import com.xenaksys.szcore.web.WebClientInfo;

public class WebScorePartRegEvent extends WebScoreInEvent {
    private final String part;

    public WebScorePartRegEvent(String clientId, String eventId, String sourceAddr, String part, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
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
        return "WebScorePartRegEvent{" +
                "part=" + part +
                '}';
    }
}
