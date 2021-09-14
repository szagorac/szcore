package com.xenaksys.szcore.event.web.in;


import com.xenaksys.szcore.event.EventType;

abstract public class WebScoreInEvent extends IncomingWebEvent {
    private final String clientId;

    public WebScoreInEvent(String clientId, String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_SCORE_IN;
    }

    abstract public WebScoreInEventType getWebScoreEventType();

    @Override
    public String toString() {
        return "WebScoreInEvent{" +
                "clientId='" + clientId + '\'' +
                '}';
    }
}
