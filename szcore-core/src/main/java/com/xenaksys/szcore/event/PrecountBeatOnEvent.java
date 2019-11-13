package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class PrecountBeatOnEvent extends OscJavascriptEvent {

    PrecountBeatOnEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(int beatNo, int colourId) {
        String jsCommand = Consts.OSC_JS_BEATER_ON.replace(Consts.BEAT_TOKEN, Integer.toString(beatNo));
        jsCommand = jsCommand.replace(Consts.COLOUR_TOKEN, Integer.toString(colourId));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.PRECOUNT_BEAT_ON;
    }
}
