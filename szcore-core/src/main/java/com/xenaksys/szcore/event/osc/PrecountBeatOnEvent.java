package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class PrecountBeatOnEvent extends OscJavascriptEvent {
    private int beaterNo;
    private int colourId;

    public PrecountBeatOnEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(int beatNo, int colourId) {
        this.beaterNo = beatNo;
        this.colourId = colourId;
        String jsCommand = Consts.OSC_JS_BEATER_ON.replace(Consts.BEAT_TOKEN, Integer.toString(beatNo));
        jsCommand = jsCommand.replace(Consts.COLOUR_TOKEN, Integer.toString(colourId));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);
    }

    public int getBeaterNo() {
        return beaterNo;
    }

    public int getColourId() {
        return colourId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.PRECOUNT_BEAT_ON;
    }
}
