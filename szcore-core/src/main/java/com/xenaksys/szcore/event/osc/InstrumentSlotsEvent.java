package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class InstrumentSlotsEvent extends OscJavascriptEvent {
    private String instrumentsCsv;

    public InstrumentSlotsEvent(List<Object> arguments, String instrumentsCsv, BeatId eventBaseBeat, String destination, long time) {
        super(arguments, eventBaseBeat, destination, time);
        this.instrumentsCsv = instrumentsCsv;
    }

    public void addCommandArg(String instrumentsCsv) {
        this.instrumentsCsv = instrumentsCsv;
        String jsCommand = Consts.OSC_JS_SET_INSTRUMENT_SLOTS.replace(Consts.CSV_INSTRUMENT_SLOTS_TOKEN, instrumentsCsv);
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);
    }

    public String getInstrumentsCsv() {
        return instrumentsCsv;
    }

    public OscEventType getOscEventType() {
        return OscEventType.INSTRUMENT_SLOTS;
    }
}
