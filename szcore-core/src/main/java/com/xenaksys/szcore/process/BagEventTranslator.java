package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.xenaksys.szcore.event.BagSzcoreEvent;
import com.xenaksys.szcore.event.SzcoreEventParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class BagEventTranslator implements EventTranslatorOneArg<BagSzcoreEvent, BagSzcoreEvent> {
    static final Logger LOG = LoggerFactory.getLogger(BagEventTranslator.class);


    @Override
    public void translateTo(BagSzcoreEvent dsrptBag, long sequence, BagSzcoreEvent inBag) {
        if(inBag == null|| dsrptBag == null) {
            return;
        }

        dsrptBag.reset();

        dsrptBag.setType(inBag.getEventType());
        Set<SzcoreEventParam> paramTypes = inBag.getParamTypes();

        if (paramTypes == null) {
            return;
        }

        for (SzcoreEventParam type : paramTypes) {
            Object value = inBag.getParam(type);
            if (value == null) {
                continue;
            }

            dsrptBag.addParam(type, value);
        }

    }
}

