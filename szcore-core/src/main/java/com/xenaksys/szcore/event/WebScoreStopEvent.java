package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.WebScoreScript;

import java.util.List;

public class WebScoreStopEvent extends WebScoreEvent {

    public WebScoreStopEvent(BeatId beatId, List<WebScoreScript> scripts, long creationTime) {
        super(beatId, scripts, creationTime);
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.STOP;
    }

    @Override
    public String toString() {
        return "WebScoreStopEvent{ " +
                super.toString() +
                '}';
    }
}
