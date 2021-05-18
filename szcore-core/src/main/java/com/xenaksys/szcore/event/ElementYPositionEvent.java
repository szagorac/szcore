package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.id.StaveId;

import java.util.ArrayList;
import java.util.List;

public class ElementYPositionEvent extends OscEvent {
    private final StaveId staveId;

    public ElementYPositionEvent(String address, List<Object> arguments, String destination, StaveId staveId, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
    }

    public void setYPosition(double yPosition) {
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        Float fl = new Float(yPosition);
        args.add(1, fl);
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.ELEMENT_Y_POSITION;
    }
}
