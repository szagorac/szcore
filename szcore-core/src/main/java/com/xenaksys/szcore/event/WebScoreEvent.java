package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.ArrayList;
import java.util.List;

public class WebScoreEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final String eventId;
    private final long creationTime;
    private final WebScoreEventType eventType;
    private final List<String> scripts = new ArrayList<>();

    public WebScoreEvent(WebScoreEventType eventType, BeatId beatId, String eventId, List<String> scripts, long creationTime) {
        this.beatId = beatId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.creationTime = creationTime;
        this.scripts.addAll(scripts);
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

    public List<String> getScripts() {
        return scripts;
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
