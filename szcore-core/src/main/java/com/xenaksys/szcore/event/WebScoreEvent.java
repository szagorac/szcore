package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

public class WebScoreEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final String eventId;
    private final long creationTime;
    private final WebScoreEventType eventType;
    private final String script;

    public WebScoreEvent(WebScoreEventType eventType, BeatId beatId, String eventId, String script, long creationTime) {
        this.beatId = beatId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.creationTime = creationTime;
        this.script = script;
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_SCORE;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return beatId;
    }

    public WebScoreEventType getWebEventType() {
        return eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "ScoreWebEvent{" +
                "type=" + getWebEventType() +
                '}';
    }

    public long getCreationTime() {
        return creationTime;
    };
}
