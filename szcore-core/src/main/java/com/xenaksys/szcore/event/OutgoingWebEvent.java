package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

public class OutgoingWebEvent implements SzcoreEvent {

    private BeatId beatId;
    private String eventId;
    private long creationTime;
    private OutgoingWebEventType eventType;

    public OutgoingWebEvent() {
    }

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

    public long getCreationTime() {
        return creationTime;
    };

    public void setBeatId(BeatId beatId) {
        this.beatId = beatId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setEventType(OutgoingWebEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "OutgoingWebEvent{" +
                "type=" + getWebEventType() +
                '}';
    }
}
