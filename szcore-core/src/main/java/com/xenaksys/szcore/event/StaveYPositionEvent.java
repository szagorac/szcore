package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class StaveYPositionEvent extends OscEvent {
    private final StaveId staveId;

    public StaveYPositionEvent(String address, List<Object> arguments, String destination, StaveId staveId, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.STAVE_Y_POSITION;
    }
}
