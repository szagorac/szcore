package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventFactory;
import com.xenaksys.szcore.event.osc.OscEvent;

public class OscEventFactory implements EventFactory<OscEvent> {

    @Override
    public OscEvent newInstance() {
        return new OscEvent();
    }
}

