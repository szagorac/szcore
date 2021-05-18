package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class OscScriptEvent extends OscEvent {

    public OscScriptEvent(String address, List<Object> arguments, BeatId beatId, String destination, long time) {
        super(address, arguments, beatId, destination, time);
    }

    @Override
    public String toString() {
        return "OscScriptEvent{" + super.toString() + "}";
    }

    public OscEventType getOscEventType() {
        return OscEventType.OSC_SCRIPT;
    }
}
