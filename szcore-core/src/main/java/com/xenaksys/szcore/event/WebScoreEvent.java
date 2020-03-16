package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.WebScoreScript;

import java.util.List;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.SPACE;

public class WebScoreEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final long creationTime;
    private final List<WebScoreScript> scripts;
    private final String content;

    public WebScoreEvent(BeatId beatId, List<WebScoreScript> scripts, long creationTime) {
        this.beatId = beatId;
        this.creationTime = creationTime;
        this.scripts = scripts;
        this.content = initContent();
    }

    private String initContent() {
        StringBuilder sb = new StringBuilder();
        String delimiter = EMPTY;
        for(WebScoreScript script : scripts) {
            sb.append(delimiter);
            sb.append(script.getContent());
            delimiter = SPACE;
        }
        return sb.toString();
    }

    @Override
    public EventType getEventType() {
        return EventType.WEB_SCORE;
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.SCRIPT;
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

    public List<WebScoreScript> getScripts() {
        return scripts;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "WebScoreEvent{" +
                "beatId=" + beatId +
                ", creationTime=" + creationTime +
                ", script='" + content + '\'' +
                '}';
    }
}
