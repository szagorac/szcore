package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class InstrumentSlotsEvent extends OscJavascriptEvent {

    public InstrumentSlotsEvent(List<Object> arguments, BeatId eventBaseBeat, String destination, long time) {
        super(arguments, eventBaseBeat, destination, time);
    }

    public void addCommandArg(String instrumentsCsv) {
        String jsCommand = Consts.OSC_JS_SET_INSTRUMENT_SLOTS.replace(Consts.CSV_INSTRUMENT_SLOTS_TOKEN, instrumentsCsv);
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.INSTRUMENT_SLOTS;
    }
}
