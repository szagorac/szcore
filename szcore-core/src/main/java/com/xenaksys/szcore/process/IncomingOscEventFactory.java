package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventFactory;
import com.xenaksys.szcore.event.IncomingOscEvent;

public class IncomingOscEventFactory implements EventFactory<IncomingOscEvent> {

    @Override
    public IncomingOscEvent newInstance() {
        return new IncomingOscEvent();
    }
}

