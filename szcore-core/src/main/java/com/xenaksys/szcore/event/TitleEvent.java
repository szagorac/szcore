package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class TitleEvent extends OscJavascriptEvent {

    TitleEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(String title) {
        String jsCommand = Consts.OSC_JS_SET_TITLE.replace(Consts.TITLE_TOKEN, title);
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.SET_TITLE;
    }


}
