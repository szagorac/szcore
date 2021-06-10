package com.xenaksys.szcore.scripting;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.ScriptingEngineEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.model.id.BeatId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.SCRIPTING_ENGINE_ID;
import static com.xenaksys.szcore.Consts.T_ACTION_TEMPO;

public class ScoreScriptingEngine {
    static final Logger LOG = LoggerFactory.getLogger(ScoreScriptingEngine.class);

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Score score;
    private final Clock clock;

    private ScriptingEngineConfig config;
    private final ScriptEngineManager factory = new ScriptEngineManager();
    private final ScriptEngine jsEngine = factory.getEngineByName("nashorn");
    private final Map<BeatId, List<ScriptingEngineScript>> beatScripts = new HashMap<>();
    private final Map<BeatId, List<ScriptingEngineScript>> beatResetScripts = new HashMap<>();

    public ScoreScriptingEngine(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.score = scoreProcessor.getScore();
    }

    public void resetState() {
        LOG.debug("ScoreScriptingEngine: resetState()");
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
        LOG.debug("processScriptingEngineEvent: execute event: {}", event);
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

    public ScriptingEngineConfig getConfig() {
        return config;
    }

    public void addBeatScript(BeatId beatId, ScriptingEngineScript script) {
        if (beatId == null || script == null) {
            return;
        }

        List<ScriptingEngineScript> scripts = beatScripts.computeIfAbsent(beatId, k -> new ArrayList<>());
        scripts.add(script);
    }

    public void addResetScript(BeatId beatId, ScriptingEngineScript script) {
        if (beatId == null || script == null) {
            return;
        }

        List<ScriptingEngineScript> scripts = beatResetScripts.computeIfAbsent(beatId, k -> new ArrayList<>());
        scripts.add(script);
    }

    public List<ScriptingEngineScript> getBeatScripts(BeatId beatId) {
        return beatScripts.get(beatId);
    }

    public List<ScriptingEngineScript> getBeatResetScripts(BeatId beatId) {
        if (beatResetScripts.containsKey(beatId)) {
            return beatResetScripts.get(beatId);
        }

        ArrayList<BeatId> beats = new ArrayList<>(beatResetScripts.keySet());
        if (beats.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.sort(beats);
        int outIndex = Collections.binarySearch(beats, beatId);
        int idx = outIndex;
        if (outIndex < 0) {
            idx += 1;
            idx *= (-1);
            idx -= 1;
        }
        BeatId outId = beats.get(idx);
        return beatResetScripts.get(outId);
    }

    public void sendRndPageUpdates(String target) {
        LOG.debug("sendRndPageUpdates: target: {}", target);
        try {
            if (Consts.MAXMSP_ID.equals(target)) {
                scoreProcessor.sendOscInstrumentRndPageUpdate(0);
            } else {
                LOG.error("sendRndPageUpdates: Unknown target: {}", target);
            }
        } catch (Exception e) {
            LOG.error("Failed to process sendRndPageUpdates()", e);
        }
    }

    public void timedAction(String action, Object endValue, int timeInBeats) {
        LOG.debug("timedAction: action: {}", action);
        if (action == null) {
            return;
        }
        try {
            switch (action) {
                case T_ACTION_TEMPO:
                    if (endValue == null) {
                        return;
                    }
                    int endBpm = (Integer) endValue;
                    scoreProcessor.setUpContinuousTempoChange(endBpm, timeInBeats);
                    break;
                default:
                    LOG.warn("timedAction: Unknown Timed Action: {}", action);
            }
        } catch (Exception e) {
            LOG.error("Failed to process sendRndPageUpdates()", e);
        }
    }


    public void sendMaxMspRndPageUpdates(int buffer) {
        LOG.debug("sendMaxMspRndPageUpdates: buffer: {}", buffer);
        try {
            scoreProcessor.sendOscInstrumentRndPageUpdate(buffer);
        } catch (Exception e) {
            LOG.error("Failed to process sendMaxMspRndPageUpdates()", e);
        }
    }

    public void setRndStrategy(List<Integer> randomisationStrategy) {
        LOG.debug("setRndStrategy: {}", randomisationStrategy);
        try {
            scoreProcessor.setRandomisationStrategy(randomisationStrategy);
        } catch (Exception e) {
            LOG.error("Failed to process setRndStrategy()", e);
        }
    }

}
