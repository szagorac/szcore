package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.xenaksys.szcore.event.osc.IncomingOscEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class IncomingOscEventTranslator implements EventTranslatorOneArg<IncomingOscEvent, IncomingOscEvent> {
    static final Logger LOG = LoggerFactory.getLogger(IncomingOscEventTranslator.class);


    @Override
    public void translateTo(IncomingOscEvent event, long sequence, IncomingOscEvent in) {
        if(in == null|| event == null) {
            return;
        }

        event.setAddress(in.getAddress());
        event.setDestination(in.getDestination());
        event.setEventBaseBeat(in.getEventBaseBeat());
        event.setInetAddress(in.getInetAddress());
        event.setLocalPort(in.getLocalPort());
        event.setPort(in.getPort());

        long creationTime = in.getCreationTime();
        event.setTime(creationTime);

        List<Object> args = in.getArguments();
        if(args ==  null) {
            return;
        }

        List<Object> dargs = event.getArguments();
        if(dargs == null){
            dargs = new ArrayList<>();
            event.setArguments(dargs);
        }

        dargs.clear();

        for(Object arg : args){
            dargs.add(arg);
        }
    }
}

