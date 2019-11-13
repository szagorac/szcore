package com.xenaksys.szcore.task;

import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.MusicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMusicTask implements MusicTask {
    static final Logger LOG = LoggerFactory.getLogger(LogMusicTask.class);

    private final long playTime;
    private final Clock clock;

    public LogMusicTask(long playTime, Clock clock) {
        this.playTime = playTime;
        this.clock = clock;
    }

    public long getPlayTime() {
        return playTime;
    }

    @Override
    public void play() {
        LOG.info("Executing task playTime: " + playTime + " clock elapsed time: " + clock.getElapsedTimeMillis() +
                " clock system time: " + +clock.getSystemTimeMillis());
    }

}
