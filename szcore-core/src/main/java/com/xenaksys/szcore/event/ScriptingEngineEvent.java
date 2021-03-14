package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;

import java.util.List;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.SPACE;

public class ScriptingEngineEvent implements SzcoreEvent {

    private final BeatId beatId;
    private final long creationTime;
    private final List<ScriptingEngineScript> scripts;
    private final String content;

    public ScriptingEngineEvent(BeatId beatId, List<ScriptingEngineScript> scripts, long creationTime) {
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
        for (ScriptingEngineScript script : scripts) {
            sb.append(delimiter);
            sb.append(script.getContent());
            delimiter = SPACE;
        }
        return sb.toString();
    }

    @Override
    public EventType getEventType() {
        return EventType.SCRIPTING_ENGINE;
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

    public List<ScriptingEngineScript> getScripts() {
        return scripts;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "ScriptingEngineEvent{" +
                "beatId=" + beatId +
                ", creationTime=" + creationTime +
                ", script='" + content + '\'' +
                '}';
    }
}
