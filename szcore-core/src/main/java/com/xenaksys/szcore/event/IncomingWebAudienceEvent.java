package com.xenaksys.szcore.event;


abstract public class IncomingWebAudienceEvent extends IncomingWebEvent {

    public IncomingWebAudienceEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_AUDIENCE_IN;
    }

    abstract public IncomingWebAudienceEventType getWebEventType();

    @Override
    public String toString() {
        return "WebEvent{" +
                "type=" + getWebEventType() +
                '}';
    }
}
