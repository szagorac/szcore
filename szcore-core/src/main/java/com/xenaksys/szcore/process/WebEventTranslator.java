package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebEventTranslator implements EventTranslatorOneArg<OutgoingWebEvent, OutgoingWebEvent> {
    static final Logger LOG = LoggerFactory.getLogger(WebEventTranslator.class);

    @Override
    public void translateTo(OutgoingWebEvent event, long sequence, OutgoingWebEvent in) {
        if (in == null || event == null) {
            return;
        }

        event.setBeatId(in.getEventBaseBeat());
        event.setEventId(in.getEventId());
        event.setCreationTime(in.getCreationTime());
        event.setEventType(in.getWebEventType());
    }
}

