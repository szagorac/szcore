package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class OscStaveActivateEvent extends OscJavascriptEvent {
    private final StaveId staveId;
    private final boolean isActive;
    private final boolean isPlayStave;

    public OscStaveActivateEvent(StaveId staveId, List<Object> arguments, String destination, boolean isActive, boolean isPlayStave, long creationTime) {
        super(arguments, null, destination, creationTime);
        this.staveId = staveId;
        this.isActive = isActive;
        this.isPlayStave = isPlayStave;
    }

    public OscEventType getOscEventType() {
        return OscEventType.STAVE_ACTIVATE;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isPlayStave() {
        return isPlayStave;
    }
}
