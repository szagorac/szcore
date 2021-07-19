package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.xenaksys.szcore.event.BagZscoreEvent;
import com.xenaksys.szcore.event.ZscoreEventParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class BagEventTranslator implements EventTranslatorOneArg<BagZscoreEvent, BagZscoreEvent> {
    static final Logger LOG = LoggerFactory.getLogger(BagEventTranslator.class);


    @Override
    public void translateTo(BagZscoreEvent dsrptBag, long sequence, BagZscoreEvent inBag) {
        if (inBag == null || dsrptBag == null) {
            return;
        }

        dsrptBag.reset();

        dsrptBag.setType(inBag.getEventType());
        Set<ZscoreEventParam> paramTypes = inBag.getParamTypes();

        if (paramTypes == null) {
            return;
        }

        for (ZscoreEventParam type : paramTypes) {
            Object value = inBag.getParam(type);
            if (value == null) {
                continue;
            }

            dsrptBag.addParam(type, value);
        }

    }
}

