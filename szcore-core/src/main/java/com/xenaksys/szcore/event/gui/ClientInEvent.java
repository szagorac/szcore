package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

abstract public class ClientInEvent implements SzcoreEvent {
    private final long time;

    public ClientInEvent(long time) {
        this.time = time;
    }

    abstract public ClientInEventType getClientEventType();

    @Override
    public EventType getEventType() {
        return EventType.ADMIN_IN;
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
