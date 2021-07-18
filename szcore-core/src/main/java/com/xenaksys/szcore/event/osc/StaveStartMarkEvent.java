package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class StaveStartMarkEvent extends StaveDateTickEvent {

    public StaveStartMarkEvent(String address, List<Object> arguments, String destination, StaveId staveId, int beatNo, long time) {
        super(address, arguments, destination, staveId, beatNo, time);
    }

    public OscEventType getOscEventType() {
        return OscEventType.STAVE_START_MARK;
    }
}
