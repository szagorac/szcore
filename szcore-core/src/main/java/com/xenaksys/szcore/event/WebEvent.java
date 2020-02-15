package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

abstract public class WebEvent implements SzcoreEvent {

    private final String eventId;
    private final String sourceAddr;
    private final String requestPath;
    private final long creationTime;
    private final long clientEventCreatedTime;
    private final long clientEventSentTime;

    public WebEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        this.eventId = eventId;
        this.sourceAddr = sourceAddr;
        this.requestPath = requestPath;
        this.creationTime = creationTime;
        this.clientEventCreatedTime = clientEventCreatedTime;
        this.clientEventSentTime = clientEventSentTime;
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return null;
    }

    abstract public WebEventType getWebEventType();

    public long getClientEventCreatedTime() {
        return clientEventCreatedTime;
    }

    public long getClientEventSentTime() {
        return clientEventSentTime;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public String toString() {
        return "WebEvent{" +
                "type=" + getWebEventType() +
                '}';
    }

    public long getCreationTime() {
        return creationTime;
    };
}
