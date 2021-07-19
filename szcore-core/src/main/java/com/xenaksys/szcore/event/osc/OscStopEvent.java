package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OscStopEvent extends OscJavascriptEvent {
    static final Logger LOG = LoggerFactory.getLogger(OscStopEvent.class);


    public OscStopEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
        addCommandArg();
    }

    public void addCommandArg() {
        String jsCommand = Consts.OSC_JS_STOP;
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
//LOG.info("jsCommand: " + jsCommand);
        args.add(1, jsCommand);
    }

    public OscEventType getOscEventType() {
        return OscEventType.STOP;
    }
}
