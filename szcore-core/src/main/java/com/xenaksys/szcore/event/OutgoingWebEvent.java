package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

public class OutgoingWebEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final String eventId;
    private final long creationTime;
    private final OutgoingWebEventType eventType;

    public OutgoingWebEvent(BeatId beatId, String eventId, OutgoingWebEventType eventType, long creationTime) {
        this.beatId = beatId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.creationTime = creationTime;
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_OUT;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return beatId;
    }

    public String getEventId() {
        return eventId;
    }

    public OutgoingWebEventType getWebEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "OutgoingWebEvent{" +
                "type=" + getWebEventType() +
                '}';
    }

    public long getCreationTime() {
        return creationTime;
    };
}
