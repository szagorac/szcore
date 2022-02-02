package com.xenaksys.szcore.event.web.audience;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;

import java.util.List;

public class WebAudienceVoteEvent extends WebAudienceEvent {
    private final String value;

    public WebAudienceVoteEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, String value, long creationTime) {
        super(beatId, scripts, creationTime);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.VOTE;
    }

    @Override
    public String toString() {
        return "WebAudienceVoteEvent{ " +
                super.toString() +
                '}';
    }
}
