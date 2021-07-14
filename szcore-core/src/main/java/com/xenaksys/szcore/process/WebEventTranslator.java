package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

        Map<String, Object> dataMap = in.getDataMap();
        if (!dataMap.isEmpty()) {
            for (String key : dataMap.keySet()) {
                event.addData(key, dataMap.get(key));
            }
        }

        OutgoingWebEventType outType = in.getOutWebEventType();
        if (outType != null) {
            event.setOutEventType(outType);
        }

        event.setEventType(in.getEventType());
    }
}

