package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class ResetScoreEvent extends OscJavascriptEvent {

    public ResetScoreEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg() {
        String jsCommand = Consts.OSC_JS_RESET_SCORE;
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.RESET_SCORE;
    }


}
