package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.SzcoreEvent;

public class EventContainer {

    private SzcoreEvent event;

    public EventContainer() {
    }

    public EventContainer(SzcoreEvent event) {
        this.event = event;
    }

    public SzcoreEvent getEvent() {
        return event;
    }

    public void setEvent(SzcoreEvent event) {
        this.event = event;
    }
}
