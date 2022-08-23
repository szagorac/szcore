package com.xenaksys.szcore.event.web.in;

import com.xenaksys.szcore.web.WebClientInfo;

public class WebScoreClientHelloEvent extends WebScoreInEvent {

    public WebScoreClientHelloEvent(String clientId, String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
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
