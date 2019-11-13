package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class StaveDyTickEvent extends OscEvent {
    private final StaveId staveId;

    StaveDyTickEvent(String address, List<Object> arguments, String destination, StaveId staveId, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.STAVE_TICK_DY;
    }
}
