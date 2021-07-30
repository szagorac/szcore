package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayType;

import java.util.ArrayList;
import java.util.List;

public class ElementColorEvent extends OscEvent {
    private final StaveId staveId;
    private final OverlayType overlayType;

    private int r;
    private int g;
    private int b;
    private int alpha;

    public ElementColorEvent(StaveId staveId, OverlayType overlayType, String address, List<Object> arguments, String destination, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
        this.overlayType = overlayType;
    }

    public void setColor(int r, int g, int b, int alpha) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = alpha;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        while ( args.size() > 1) {
            args.remove(1);
        }
        args.add(1, r);
        args.add(2, g);
        args.add(3, b);
        args.add(4, alpha);
    }

    public void setColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = 255;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        while ( args.size() > 1) {
            args.remove(1);
        }
        args.add(1, r);
        args.add(2, g);
        args.add(3, b);
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getAlpha() {
        return alpha;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    public OscEventType getOscEventType() {
        return OscEventType.ELEMENT_COLOR;
    }
}
