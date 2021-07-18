package com.xenaksys.szcore.event.music;


import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

abstract public class MusicEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final long creationTime;

    public MusicEvent(BeatId beatId, long creationTime) {
        this.beatId = beatId;
        this.creationTime = creationTime;
    }

    @Override
    public EventType getEventType() {
        return EventType.MUSIC;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return beatId;
    }

    abstract public MusicEventType getMusicEventType();

    @Override
    public String toString() {
        return "MusicEvent{" +
                "beatId=" + beatId +
                '}';
    }

    public long getCreationTime() {
        return creationTime;
    };
}
