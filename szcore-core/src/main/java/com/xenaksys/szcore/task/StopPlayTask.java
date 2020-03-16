package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.StopEvent;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SzcoreEvent;

public class StopPlayTask extends EventMusicTask {
    private final ScoreProcessor scoreProcessor;

    public StopPlayTask(long playTime, StopEvent event, ScoreProcessor scoreProcessor) {
        super(playTime, event);
        this.scoreProcessor = scoreProcessor;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof StopEvent)) {
            return;
        }

        if(scoreProcessor == null){
            return;
        }

        scoreProcessor.stop();
    }

    @Override
    public String toString() {
        return "StopPlayTask{" +
                "playTime=" + getPlayTime() +
                ", event=" + getEvent() +
                '}';
    }
}
