package com.xenaksys.szcore.event.web.out;


import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.HashMap;
import java.util.Map;

public class OutgoingWebEvent implements SzcoreEvent {

    private BeatId beatId;
    private long creationTime;
    private String eventId;
    private EventType eventType = EventType.WEB_OUT;
    private OutgoingWebEventType outEventType;
    private Map<String, Object> dataMap = new HashMap<>();

    public OutgoingWebEvent() {
        System.currentTimeMillis();
    }

    public OutgoingWebEvent(String eventId, BeatId beatId, EventType eventType, OutgoingWebEventType outEventType, long creationTime) {
        this.eventId = eventId;
        this.beatId = beatId;
        this.creationTime = creationTime;
        this.eventType = eventType;
        this.outEventType = outEventType;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public void addData(String key, Object data) {
        dataMap.put(key, data);
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public BeatId getBeatId() {
        return beatId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public OutgoingWebEventType getOutWebEventType() {
        return outEventType;
    }

    public void setOutEventType(OutgoingWebEventType outEventType) {
        this.outEventType = outEventType;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return beatId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    ;

    public void setBeatId(BeatId beatId) {
        this.beatId = beatId;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "OutgoingWebEvent{" +
                "type=" + getOutWebEventType() +
                '}';
    }

}
