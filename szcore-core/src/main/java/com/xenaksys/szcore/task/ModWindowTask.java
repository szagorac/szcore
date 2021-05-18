package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.ModWindowEvent;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;

public class ModWindowTask extends EventMusicTask {
    private final ScoreProcessor scoreProcessor;

    public ModWindowTask(long playTime, ModWindowEvent event, ScoreProcessor scoreProcessor) {
        super(playTime, event);
        this.scoreProcessor = scoreProcessor;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof ModWindowEvent)) {
            return;
        }

        if (scoreProcessor == null) {
            return;
        }

        ModWindowEvent modWindowEvent = ((ModWindowEvent) event);
        boolean isOpenWindow = modWindowEvent.isOpenWindow();
        BeatId beatId = modWindowEvent.getEventBaseBeat();
        InstrumentId instId = (InstrumentId) beatId.getInstrumentId();
        Page nextPage = modWindowEvent.getNextPage();
        Stave nextStave = modWindowEvent.getNextStave();
        PageId currentPageId = modWindowEvent.getCurrentPageId();

        if (isOpenWindow) {
            scoreProcessor.onOpenModWindow(instId, nextStave, nextPage, currentPageId);
        } else {
            scoreProcessor.onCloseModWindow(instId, nextStave, nextPage, currentPageId);
        }

    }

    @Override
    public String toString() {
        return "ModWindowTask{" +
                "playTime=" + getPlayTime() +
                ", event=" + getEvent() +
                '}';
    }
}
