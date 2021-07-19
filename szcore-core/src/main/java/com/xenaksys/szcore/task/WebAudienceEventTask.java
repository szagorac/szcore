package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.score.web.audience.WebAudienceScore;

public class WebAudienceEventTask extends EventMusicTask {
    private WebAudienceScore webAudienceScore;

    public WebAudienceEventTask(long playTime, WebAudienceEvent event, WebAudienceScore webAudienceScore) {
        super(playTime, event);
        this.webAudienceScore = webAudienceScore;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof WebAudienceEvent)) {
            return;
        }

        WebAudienceEvent webAudienceEvent = (WebAudienceEvent) event;
        webAudienceScore.processWebAudienceEvent(webAudienceEvent);
    }
}
