package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;

import java.util.List;

public class WebAudienceStopEvent extends WebAudienceEvent {

    public WebAudienceStopEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, long creationTime) {
        super(beatId, scripts, creationTime);
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.STOP;
    }

    @Override
    public String toString() {
        return "WebAudienceStopEvent{ " +
                super.toString() +
                '}';
    }
}
