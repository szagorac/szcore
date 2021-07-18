package com.xenaksys.szcore.event.web;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;

import java.util.List;

public class WebAudiencePlayTilesEvent extends WebAudienceEvent {

    public WebAudiencePlayTilesEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, long creationTime) {
        super(beatId, scripts, creationTime);
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.PLAY_TILES;
    }

    @Override
    public String toString() {
        return "WebAudiencePlayTilesEvent{ " +
                super.toString() +
                '}';
    }
}
