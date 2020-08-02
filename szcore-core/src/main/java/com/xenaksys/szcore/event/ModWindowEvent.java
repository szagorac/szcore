package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.id.BeatId;

public class ModWindowEvent extends MusicEvent {

    private final boolean isOpen;
    private final Page nextPage;
    private final Stave nextStave;

    public ModWindowEvent(BeatId beatId, Page nextPage, Stave nextStave, boolean isOpen, long creationTime) {
        super(beatId, creationTime);
        this.isOpen = isOpen;
        this.nextPage = nextPage;
        this.nextStave = nextStave;
    }

    public boolean isOpenWindow() {
        return isOpen;
    }

    public Page getNextPage() {
        return nextPage;
    }

    public Stave getNextStave() {
        return nextStave;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.MOD_WINDOW;
    }

}
