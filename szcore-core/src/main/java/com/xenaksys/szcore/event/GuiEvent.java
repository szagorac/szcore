package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

abstract public class GuiEvent implements SzcoreEvent {
    private final long creationTime;

    public GuiEvent(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public EventType getEventType() {
        return EventType.GUI;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return null;
    }

    abstract public WebEventType getGuiEventType();

    @Override
    public String toString() {
        return "GuiEvent{" +
                "type=" + getGuiEventType() +
                '}';
    }

    public long getCreationTime() {
        return creationTime;
    };
}
