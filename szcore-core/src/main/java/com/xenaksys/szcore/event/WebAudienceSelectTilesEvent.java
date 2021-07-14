package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;

import java.util.List;

public class WebAudienceSelectTilesEvent extends WebAudienceEvent {
    private final List<String> tileIds;

    public WebAudienceSelectTilesEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, List<String> tileIds, long creationTime) {
        super(beatId, scripts, creationTime);
        this.tileIds = tileIds;
    }

    public List<String> getTileIds() {
        return tileIds;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.SELECT_TILES;
    }

    @Override
    public String toString() {
        return "WebAudienceSelectTilesEvent{ " +
                super.toString() +
                '}';
    }
}
