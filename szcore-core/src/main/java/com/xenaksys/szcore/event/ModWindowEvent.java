package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.BeatId;

public class ModWindowEvent extends MusicEvent {

    private final boolean isOpen;
    private final Page nextPage;

    public ModWindowEvent(BeatId beatId, Page nextPage, boolean isOpen, long creationTime) {
        super(beatId, creationTime);
        this.isOpen = isOpen;
        this.nextPage = nextPage;
    }

    public boolean isOpenWindow() {
        return isOpen;
    }

    public Page getNextPage() {
        return nextPage;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.MOD_WINDOW;
    }

}
