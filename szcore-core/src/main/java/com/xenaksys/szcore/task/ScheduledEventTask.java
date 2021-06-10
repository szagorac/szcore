package com.xenaksys.szcore.task;

import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SzcoreEvent;

public class ScheduledEventTask extends EventMusicTask {
    private ScoreProcessor scoreProcessor;

    public ScheduledEventTask(long playTime, SzcoreEvent event, ScoreProcessor scoreProcessor) {
        super(playTime, event);
        this.scoreProcessor = scoreProcessor;
    }

    @Override
    public void play() {
        scoreProcessor.process(getEvent());
    }
}
