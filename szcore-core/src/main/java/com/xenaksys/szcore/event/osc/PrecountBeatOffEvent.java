package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class PrecountBeatOffEvent extends OscJavascriptEvent {
    private int beaterNo;

    public PrecountBeatOffEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(int beatNo) {
        this.beaterNo = beatNo;
        String jsCommand = Consts.OSC_JS_BEATER_OFF.replace(Consts.BEAT_TOKEN, Integer.toString(beatNo));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);
    }

    public int getBeaterNo() {
        return beaterNo;
    }

    public OscEventType getOscEventType() {
        return OscEventType.PRECOUNT_BEAT_OFF;
    }


}
