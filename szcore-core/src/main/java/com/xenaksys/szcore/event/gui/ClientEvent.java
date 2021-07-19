package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

abstract public class ClientEvent implements SzcoreEvent {
    private final long time;

    public ClientEvent(long time) {
        this.time = time;
    }

    abstract public ClientEventType getClientEventType();

    @Override
    public EventType getEventType() {
        return EventType.CLIENT;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return null;
    }

    @Override
    public long getCreationTime() {
        return time;
    }

    public boolean isScoreEvent(){return false;};

}
