package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.score.WebScore;

public class WebScoreEventTask extends EventMusicTask {
    private WebScore webScore;

    public WebScoreEventTask(long playTime, WebScoreEvent event, WebScore webScore) {
        super(playTime, event);
        this.webScore = webScore;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof WebScoreEvent)) {
            return;
        }

        WebScoreEvent webScoreEvent = (WebScoreEvent) event;
        webScore.processWebScoreEvent(webScoreEvent);
    }
}
