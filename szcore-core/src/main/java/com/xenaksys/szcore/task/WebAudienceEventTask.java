package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreProcessor;

public class WebAudienceEventTask extends EventMusicTask {
    private WebAudienceScoreProcessor webAudienceScoreProcessor;

    public WebAudienceEventTask(long playTime, WebAudienceEvent event, WebAudienceScoreProcessor webAudienceScoreProcessor) {
        super(playTime, event);
        this.webAudienceScoreProcessor = webAudienceScoreProcessor;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof WebAudienceEvent)) {
            return;
        }

        WebAudienceEvent webAudienceEvent = (WebAudienceEvent) event;
        webAudienceScoreProcessor.processWebAudienceEvent(webAudienceEvent);
    }
}
