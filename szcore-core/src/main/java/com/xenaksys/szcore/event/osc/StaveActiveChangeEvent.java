package com.xenaksys.szcore.event.osc;


import com.xenaksys.szcore.event.music.MusicEvent;
import com.xenaksys.szcore.event.music.MusicEventType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.StaveId;

public class StaveActiveChangeEvent extends MusicEvent {

    private final StaveId staveId;
    private final boolean isActive;
    private final OscStaveActivateEvent oscStaveActivateEvent;

    public StaveActiveChangeEvent(StaveId staveId,
                                  boolean isActive,
                                  BeatId changeOnBaseBeat,
                                  OscStaveActivateEvent oscStaveActivateEvent,
                                  long creationTime) {
        super(changeOnBaseBeat, creationTime);
        this.staveId = staveId;
        this.isActive = isActive;
        this.oscStaveActivateEvent = oscStaveActivateEvent;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public boolean isActive() {
        return isActive;
    }

    public OscStaveActivateEvent getOscStaveActivateEvent() {
        return oscStaveActivateEvent;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.STAVE_ACTIVE_CHANGE;
    }

    @Override
    public String toString() {
        return "StaveActiveChangeEvent{" +
                "staveId=" + staveId +
                ", isActive=" + isActive +
                '}';
    }
}
