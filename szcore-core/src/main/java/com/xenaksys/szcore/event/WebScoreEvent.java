package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

public class WebScoreEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final long creationTime;
    private final String script;

    public WebScoreEvent(BeatId beatId, String script, long creationTime) {
        this.beatId = beatId;
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

    public BeatId getBeatId() {
        return beatId;
    }

    public String getScript() {
        return script;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "WebScoreEvent{" +
                "beatId=" + beatId +
                ", creationTime=" + creationTime +
                ", script='" + script + '\'' +
                '}';
    }
}
