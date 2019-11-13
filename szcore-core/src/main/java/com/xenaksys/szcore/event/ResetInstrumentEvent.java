package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class ResetInstrumentEvent extends OscJavascriptEvent {

    public ResetInstrumentEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg() {
        String jsCommand = Consts.OSC_JS_RESET_INSTRUMENT;
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.RESET_INSTRUMENT;
    }


}
