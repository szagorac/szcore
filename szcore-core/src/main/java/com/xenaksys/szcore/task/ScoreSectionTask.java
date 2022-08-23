package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.music.ScoreSectionEvent;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SzcoreEvent;

public class ScoreSectionTask extends EventMusicTask {
    private final ScoreProcessor scoreProcessor;

    public ScoreSectionTask(long playTime, ScoreSectionEvent event, ScoreProcessor scoreProcessor) {
        super(playTime, event);
        this.scoreProcessor = scoreProcessor;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof ScoreSectionEvent)) {
            return;
        }

        if(scoreProcessor == null){
            return;
        }

        ScoreSectionEvent sectionEvent = (ScoreSectionEvent)event;
        String section = sectionEvent.getSection();
        switch(sectionEvent.getSectionEventType()) {
            case START:
                scoreProcessor.onSectionStart(section);
                break;
            case STOP:
                scoreProcessor.onSectionStop(section);
                break;
        }
    }

    @Override
    public String toString() {
        return "ScoreSectionTask{" +
                "playTime=" + getPlayTime() +
                ", event=" + getEvent() +
                '}';
    }
}
