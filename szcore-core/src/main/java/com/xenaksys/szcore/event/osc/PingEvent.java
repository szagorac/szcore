package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class PingEvent extends OscJavascriptEvent {

    public PingEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(long sendTime) {
        setTime(sendTime);
        String jsCommand = Consts.OSC_JS_PING.replace(Consts.SEND_TIME, String.valueOf(sendTime));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.PING;
    }
}
