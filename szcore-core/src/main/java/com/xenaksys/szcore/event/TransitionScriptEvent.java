package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class TransitionScriptEvent extends OscJavascriptEvent {

    TransitionScriptEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(String component, long alpha) {
        String jsCommand = Consts.OSC_JS_SET_ALPHA.replace(Consts.ADDR_TOKEN, component);
        jsCommand = jsCommand.replace(Consts.ALPHA_VALUE_TOKEN, Long.toString(alpha));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.TRANSITION;
    }
}
