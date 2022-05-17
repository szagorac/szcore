package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayType;

import java.util.ArrayList;

public class OverlayTextEvent extends OscEvent {
    private StaveId staveId;
    private String l1;
    private String l2;
    private String l3;
    private boolean isVisible;
    private final OverlayType overlayType;


    public OverlayTextEvent(StaveId staveId, String l1, String l2, String l3, boolean isVisible, OverlayType overlayType, String destination, long time) {
        super(null, null, null, destination, time);
        this.staveId = staveId;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.isVisible = isVisible;
        this.overlayType = overlayType;
    }

    public void setL1(String txt) {
        this.l1 = txt;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, txt);
    }

    public void setL2(String txt) {
        this.l1 = txt;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 3) {
            args.remove(2);
        }
        args.add(2, txt);
    }

    public void setL3(String txt) {
        this.l1 = txt;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 4) {
            args.remove(3);
        }
        args.add(3, txt);
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 5) {
            args.remove(5);
        }
        args.add(4, isVisible);
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public String getL1() {
        return l1;
    }

    public String getL2() {
        return l2;
    }

    public String getL3() {
        return l3;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    public OscEventType getOscEventType() {
        return OscEventType.OVERLAY_TEXT;
    }
}
