package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayElementType;
import com.xenaksys.szcore.score.OverlayType;

import java.util.ArrayList;
import java.util.List;

public class ElementAlphaEvent extends OscEvent {
    private StaveId staveId;
    private boolean isEnabled;
    private int alpha;
    private final OverlayType overlayType;
    private final OverlayElementType overlayElementType;

    public ElementAlphaEvent(StaveId staveId, boolean isEnabled, OverlayType overlayType, OverlayElementType overlayElementType, String address, List<Object> arguments, String destination, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
        this.isEnabled = isEnabled;
        this.overlayType = overlayType;
        this.overlayElementType = overlayElementType;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, alpha);
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public OverlayType getOverlayType() {
        return overlayType;
    }

    public OverlayElementType getOverlayElementType() {
        return overlayElementType;
    }

    public int getAlpha() {
        return alpha;
    }

    public OscEventType getOscEventType() {
        return OscEventType.ELEMENT_ALPHA;
    }
}
