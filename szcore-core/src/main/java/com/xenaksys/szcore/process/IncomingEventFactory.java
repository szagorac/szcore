package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventFactory;
import com.xenaksys.szcore.event.EventContainer;

public class IncomingEventFactory implements EventFactory<EventContainer> {

    @Override
    public EventContainer newInstance() {
        return new EventContainer();
    }
}

