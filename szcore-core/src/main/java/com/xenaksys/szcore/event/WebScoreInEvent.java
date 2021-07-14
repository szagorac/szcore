package com.xenaksys.szcore.event;


public class WebScoreInEvent extends IncomingWebEvent {

    public WebScoreInEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_SCORE_IN;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.GENERIC;
    }

}
