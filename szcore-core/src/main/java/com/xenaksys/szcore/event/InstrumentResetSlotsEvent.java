package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class InstrumentResetSlotsEvent extends OscJavascriptEvent {

    public InstrumentResetSlotsEvent(List<Object> arguments, BeatId eventBaseBeat, String destination, long time) {
        super(arguments, eventBaseBeat, destination, time);
    }

    public void addCommandArg() {
        String jsCommand = Consts.OSC_JS_RESET_INSTRUMENT_SLOTS;
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.INSTRUMENT_RESET_SLOTS;
    }
}
