package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.WebScoreScript;

import java.util.List;

public class WebScorePlayTilesEvent extends WebScoreEvent {

    public WebScorePlayTilesEvent(BeatId beatId, List<WebScoreScript> scripts, long creationTime) {
        super(beatId, scripts, creationTime);
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.PLAY_TILES;
    }

    @Override
    public String toString() {
        return "WebScorePlayTilesEvent{ " +
                super.toString() +
                '}';
    }
}
