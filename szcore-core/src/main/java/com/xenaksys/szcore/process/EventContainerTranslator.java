package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.xenaksys.szcore.event.EventContainer;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventContainerTranslator implements EventTranslatorOneArg<EventContainer, SzcoreEvent> {
    static final Logger LOG = LoggerFactory.getLogger(EventContainerTranslator.class);

    @Override
    public void translateTo(EventContainer eventContainer, long sequence, SzcoreEvent in) {
        if(in == null|| eventContainer == null) {
            eventContainer.setEvent(null);
            return;
        }

        eventContainer.setEvent(in);
    }
}

