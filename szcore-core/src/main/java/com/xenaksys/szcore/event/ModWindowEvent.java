package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;

public class ModWindowEvent extends MusicEvent {

    private final boolean isOpen;
    private final Page nextPage;
    private final Stave nextStave;
    private final PageId currentPageId;

    public ModWindowEvent(BeatId beatId, Page nextPage, PageId currentPageId, Stave nextStave, boolean isOpen, long creationTime) {
        super(beatId, creationTime);
        this.isOpen = isOpen;
        this.nextPage = nextPage;
        this.nextStave = nextStave;
        this.currentPageId = currentPageId;
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

    public PageId getCurrentPageId() {
        return currentPageId;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.MOD_WINDOW;
    }

}
