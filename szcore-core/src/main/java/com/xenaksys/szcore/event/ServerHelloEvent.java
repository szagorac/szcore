package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;

import java.util.List;

public class ServerHelloEvent extends OscJavascriptEvent {

    ServerHelloEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }

    public void addCommandArg(String inetAddr) {
        String jsCommand = Consts.OSC_JS_SERVER_HELLO.replace(Consts.INET_ADDR_TOKEN, inetAddr);
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, jsCommand);

    }

    public OscEventType getOscEventType() {
        return OscEventType.SERVER_HELLO;
    }
}
