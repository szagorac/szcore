package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class AddPartsEvent extends OscJavascriptEvent {

    AddPartsEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(String instrumentsCsv) {
        String jsCommand = Consts.OSC_JS_SET_INSTRUMENTS.replace(Consts.CSV_INSTRUMENTS_TOKEN, instrumentsCsv);
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.ADD_PARTS;
    }
}
