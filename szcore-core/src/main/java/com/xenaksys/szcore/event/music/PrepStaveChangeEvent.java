package com.xenaksys.szcore.event.music;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;

public class PrepStaveChangeEvent extends MusicEvent {

    private final BeatId activateOnBaseBeat;
    private final BeatId deactivateOnBaseBeat;
    private final BeatId pageChangeOnBaseBeat;
    private final PageId nextPageId;

    public PrepStaveChangeEvent(BeatId executeOnBaseBeat,
                                BeatId activateOnBaseBeat,
                                BeatId deactivateOnBaseBeat,
                                BeatId pageChangeOnBaseBeat,
                                PageId nextPageId,
                                long creationTime) {
        super(executeOnBaseBeat, creationTime);
        this.activateOnBaseBeat = activateOnBaseBeat;
        this.deactivateOnBaseBeat = deactivateOnBaseBeat;
        this.pageChangeOnBaseBeat = pageChangeOnBaseBeat;
        this.nextPageId = nextPageId;
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

    public PageId getNextPageId() {
        return nextPageId;
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
                ", nextPageId=" + nextPageId +
                '}';
    }
}
