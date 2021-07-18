package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventFactory;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;

public class WebEventFactory implements EventFactory<OutgoingWebEvent> {

    @Override
    public OutgoingWebEvent newInstance() {
        return new OutgoingWebEvent();
    }
}

