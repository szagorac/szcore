package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayType;

import java.util.ArrayList;
import java.util.List;

public class ElementYPositionEvent extends OscEvent {
    private final StaveId staveId;
    private final long unscaledValue;
    private final OverlayType overlayType;

    public ElementYPositionEvent(String address, List<Object> arguments, long unscaledValue, OverlayType overlayType, String destination, StaveId staveId, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
        this.unscaledValue = unscaledValue;
        this.overlayType = overlayType;
    }

    public void setYPosition(double yPosition) {
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        Float fl = new Float(yPosition);
        args.add(1, fl);
    }

    public long getUnscaledValue() {
        return unscaledValue;
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.ELEMENT_Y_POSITION;
    }
}
