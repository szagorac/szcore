package com.xenaksys.szcore.event.web;


import com.xenaksys.szcore.event.EventType;

abstract public class WebScoreInEvent extends IncomingWebEvent {

    public WebScoreInEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_SCORE_IN;
    }

    abstract public WebScoreInEventType getWebScoreEventType();

}
