package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;

public class PrepStaveChangeEvent extends MusicEvent {

    private final BeatId activateOnBaseBeat;
    private final BeatId deactivateOnBaseBeat;
    private final BeatId pageChangeOnBaseBeat;

    public PrepStaveChangeEvent(BeatId executeOnBaseBeat,
                                BeatId activateOnBaseBeat,
                                BeatId deactivateOnBaseBeat,
                                BeatId pageChangeOnBaseBeat,
                                long creationTime) {
        super(executeOnBaseBeat, creationTime);
        this.activateOnBaseBeat = activateOnBaseBeat;
        this.deactivateOnBaseBeat = deactivateOnBaseBeat;
        this.pageChangeOnBaseBeat = pageChangeOnBaseBeat;
    }

    public BeatId getActivateBaseBeat() {
        return activateOnBaseBeat;
    }

    public BeatId getDeactivateBaseBeat() {
        return deactivateOnBaseBeat;
    }

    public BeatId getPageChangeOnBaseBeat() {
        return pageChangeOnBaseBeat;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.PREP_STAVE_ACTIVE_CHANGE;
    }

    @Override
    public String toString() {
        return "PrepStaveChangeEvent{" +
                "executeOnBaseBeat=" + getEventBaseBeat() +
                ", activateOnBaseBeat=" + activateOnBaseBeat +
                ", deactivateOnBaseBeat=" + deactivateOnBaseBeat +
                ", pageChangeOnBaseBeat=" + pageChangeOnBaseBeat +
                '}';
    }
}
