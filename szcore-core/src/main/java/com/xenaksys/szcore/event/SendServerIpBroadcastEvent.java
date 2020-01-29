package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class SendServerIpBroadcastEvent extends OscJavascriptEvent {

    SendServerIpBroadcastEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(String serverIp) {
        String jsCommand = Consts.OSC_JS_SET_SERVER_IP.replace(Consts.SERVER_IP__TOKEN, serverIp);
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.SERVER_IP_BROADCAST;
    }
}
