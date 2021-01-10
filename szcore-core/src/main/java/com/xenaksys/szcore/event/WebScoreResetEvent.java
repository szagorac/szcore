package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.WebScoreScript;

import java.util.List;

public class WebScoreResetEvent extends WebScoreEvent {

    public WebScoreResetEvent(BeatId beatId, List<WebScoreScript> scripts, long creationTime) {
        super(beatId, scripts, creationTime);
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.RESET;
    }

    @Override
    public String toString() {
        return "WebScoreResetEvent{ " +
                super.toString() +
                '}';
    }
}
