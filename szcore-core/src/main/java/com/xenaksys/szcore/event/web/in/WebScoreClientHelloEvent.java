package com.xenaksys.szcore.event.web.in;

public class WebScoreClientHelloEvent extends WebScoreInEvent {

    public WebScoreClientHelloEvent(String clientId, String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.HELLO;
    }

    @Override
    public String toString() {
        return "WebScoreClientHelloEvent{" +
                "clientId='" + getClientId() + '\'' +
                '}';
    }
}
