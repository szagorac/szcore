package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.script.ScriptingEngineEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.scripting.ScoreScriptingEngine;

public class ScriptingEngineEventTask extends EventMusicTask {
    private ScoreScriptingEngine scriptingEngine;

    public ScriptingEngineEventTask(long playTime, ScriptingEngineEvent event, ScoreScriptingEngine scriptingEngine) {
        super(playTime, event);
        this.scriptingEngine = scriptingEngine;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof ScriptingEngineEvent)) {
            return;
        }

        ScriptingEngineEvent scriptingEngineEvent = (ScriptingEngineEvent) event;
        scriptingEngine.processEvent(scriptingEngineEvent);
    }
}
