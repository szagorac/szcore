package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;

import java.util.List;

public class ScriptingEngineResetEvent extends ScriptingEngineEvent {

    public ScriptingEngineResetEvent(BeatId beatId, List<ScriptingEngineScript> scripts, long creationTime) {
        super(beatId, scripts, creationTime);
    }

    @Override
    public String toString() {
        return "ScriptingEngineResetEvent{ " +
                super.toString() +
                '}';
    }
}
