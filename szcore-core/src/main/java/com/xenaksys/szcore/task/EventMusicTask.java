package com.xenaksys.szcore.task;

import com.xenaksys.szcore.model.MusicTask;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMusicTask implements MusicTask {
    static final Logger LOG = LoggerFactory.getLogger(EventMusicTask.class);

    private final long playTime;
    private final SzcoreEvent event;

    public EventMusicTask(long playTime, SzcoreEvent event) {
        this.playTime = playTime;
        this.event = event;
    }

    public long getPlayTime() {
        return playTime;
    }

    public SzcoreEvent getEvent() {
        return event;
    }

    @Override
    public void play() {

    }

    @Override
    public String toString() {
        return "EventMusicTask{" +
                "playTime=" + playTime +
                ", event=" + event +
                '}';
    }
}
