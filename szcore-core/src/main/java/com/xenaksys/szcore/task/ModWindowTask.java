package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.ModWindowEvent;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;

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

        if (isOpenWindow) {
            scoreProcessor.onOpenModWindow(instId, nextPage);
        } else {
            scoreProcessor.onCloseModWindow(instId, nextPage);
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
