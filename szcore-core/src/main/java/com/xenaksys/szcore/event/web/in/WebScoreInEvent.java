package com.xenaksys.szcore.event.web.in;


import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.web.WebClientInfo;

abstract public class WebScoreInEvent extends IncomingWebEvent {
    private final String clientId;
    private final WebClientInfo webClientInfo;
    public WebScoreInEvent(String clientId, String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.clientId = clientId;
        this.webClientInfo = webClientInfo;
    }

    public String getClientId() {
        return clientId;
    }

    public WebClientInfo getWebClientInfo() {
        return webClientInfo;
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
