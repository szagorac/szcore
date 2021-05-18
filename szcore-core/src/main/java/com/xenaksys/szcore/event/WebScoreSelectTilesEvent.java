package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.WebScoreScript;

import java.util.List;

public class WebScoreSelectTilesEvent extends WebScoreEvent {
    private final List<String> tileIds;

    public WebScoreSelectTilesEvent(BeatId beatId, List<WebScoreScript> scripts, List<String> tileIds, long creationTime) {
        super(beatId, scripts, creationTime);
        this.tileIds = tileIds;
    }

    public List<String> getTileIds() {
        return tileIds;
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.SELECT_TILES;
    }

    @Override
    public String toString() {
        return "WebScoreSelectTilesEvent{ " +
                super.toString() +
                '}';
    }
}
