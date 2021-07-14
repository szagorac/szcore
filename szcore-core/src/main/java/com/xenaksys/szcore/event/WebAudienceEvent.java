package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;

import java.util.List;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.SPACE;

public class WebAudienceEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final long creationTime;
    private final List<WebAudienceScoreScript> scripts;
    private final String content;

    public WebAudienceEvent(BeatId beatId, List<WebAudienceScoreScript> scripts, long creationTime) {
        this.beatId = beatId;
        this.creationTime = creationTime;
        this.scripts = scripts;
        this.content = initContent();
    }

    private String initContent() {
        if (scripts == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String delimiter = EMPTY;
        for (WebAudienceScoreScript script : scripts) {
            sb.append(delimiter);
            sb.append(script.getContent());
            delimiter = SPACE;
        }
        return sb.toString();
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_AUDIENCE;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.SCRIPT;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return beatId;
    }

    public BeatId getBeatId() {
        return beatId;
    }

    public String getScript() {
        return content;
    }

    public List<WebAudienceScoreScript> getScripts() {
        return scripts;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "WebAudienceEvent{" +
                "beatId=" + beatId +
                ", creationTime=" + creationTime +
                ", script='" + content + '\'' +
                '}';
    }
}
