package com.xenaksys.szcore.score;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.ScriptingEngineEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

import static com.xenaksys.szcore.Consts.SCRIPTING_ENGINE_ID;

public class ScoreScriptingEngine {
    static final Logger LOG = LoggerFactory.getLogger(ScoreScriptingEngine.class);

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Score score;
    private final Clock clock;

    private ScriptingEngineConfig config;
    private final ScriptEngineManager factory = new ScriptEngineManager();
    private final ScriptEngine jsEngine = factory.getEngineByName("nashorn");

    public ScoreScriptingEngine(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.score = scoreProcessor.getScore();
    }

    public void resetState() {
        LOG.info("ScoreScriptingEngine: resetState()");
    }

    public void reset(int presetNo) {
        try {
            ScriptPreset preset = config.getPreset(presetNo);
            if (preset == null) {
                LOG.info("resetState: Unknown preset: {}", presetNo);
                return;
            }

            runScripts(preset.getScripts());
        } catch (Exception e) {
            LOG.error("resetState: Failed to run preset: {}", presetNo, e);
        }
    }

    public void processEvent(ScriptingEngineEvent event) {
        LOG.info("processScriptingEngineEvent: execute event: {}", event);
        try {
            List<ScriptingEngineScript> jsScripts = event.getScripts();
            if (jsScripts == null) {
                return;
            }
            runScoreScripts(jsScripts);
        } catch (Exception e) {
            LOG.error("Failed to evaluate script", e);
        }
    }

    public void runScripts(List<String> scripts) {
        for (String js : scripts) {
            runScript(js);
        }
    }

    public void runScoreScripts(List<ScriptingEngineScript> scripts) {
        for (ScriptingEngineScript js : scripts) {
            runScript(js.getContent());
        }
    }

    private void runScript(String script) {
        if (jsEngine == null) {
            return;
        }

        try {
            LOG.debug("runScript: {}", script);
            jsEngine.eval(script);
        } catch (ScriptException e) {
            LOG.error("Failed to execute script: {}", script, e);
        }
    }

    public void init(String configDir) {
        if (config == null) {
            loadConfig(configDir);
        }
        jsEngine.put(SCRIPTING_ENGINE_ID, this);
        resetState();
    }

    private void loadConfig(String configDir) {
        if (configDir == null) {
            return;
        }
        try {
            config = ScriptingEngineConfigLoader.load(configDir);
        } catch (Exception e) {
            LOG.error("Failed to load Scripting Engine Presets", e);
        }
    }

    public void setConfig(ScriptingEngineConfig config) {
        this.config = config;
    }
}
