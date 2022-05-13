package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayType;

import java.util.ArrayList;

public class OverlayTextEvent extends OscEvent {
    private StaveId staveId;
    private String txt;
    private boolean isVisible;
    private final OverlayType overlayType;


    public OverlayTextEvent(StaveId staveId, String txt, boolean isVisible, OverlayType overlayType, String destination, long time) {
        super(null, null, null, destination, time);
        this.staveId = staveId;
        this.txt = txt;
        this.isVisible = isVisible;
        this.overlayType = overlayType;
    }

    public void setText(String txt) {
        this.txt = txt;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, txt);
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(2, isVisible);
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    public OscEventType getOscEventType() {
        return OscEventType.OVERLAY_TEXT;
    }
}
