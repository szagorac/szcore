package com.xenaksys.szcore.event.web.audience;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;

import java.util.List;

public class WebAudienceVoteEvent extends WebAudienceEvent {
    private final String value;
    private final int usersNo;

    public WebAudienceVoteEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, String value, int usersNo, long creationTime) {
        super(beatId, scripts, creationTime);
        this.value = value;
        this.usersNo = usersNo;
    }

    public String getValue() {
        return value;
    }

    public int getUsersNo() {
        return usersNo;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.VOTE;
    }

    @Override
    public String toString() {
        return "WebAudienceVoteEvent{" +
                "value='" + value + '\'' +
                ", usersNo=" + usersNo +
                '}';
    }
}
