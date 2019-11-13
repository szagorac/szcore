package com.xenaksys.szcore.event;

import java.util.List;

public class OscStaveActivateEvent extends OscJavascriptEvent {

    public OscStaveActivateEvent(List<Object> arguments, String destination, long time) {
        super(arguments, null, destination, time);
    }
}
