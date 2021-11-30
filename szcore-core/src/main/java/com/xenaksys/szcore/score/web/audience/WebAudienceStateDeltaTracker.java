package com.xenaksys.szcore.score.web.audience;

import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.export.*;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import static com.xenaksys.szcore.Consts.*;

public abstract class WebAudienceStateDeltaTracker {
    static final Logger LOG = LoggerFactory.getLogger(WebAudienceStateDeltaTracker.class);

    private final HashMap<String, Object> delta = new HashMap<>();

    public void addDelta(String key, Object value) {
        delta.put(key, value);
    }

    public WebAudienceScoreStateDeltaExport getDeltaExport() {
        return new WebAudienceScoreStateDeltaExport(delta);
    }

    public void reset() {
        delta.clear();
    }

    public boolean hasChanges() {
        return !delta.isEmpty();
    }

    public abstract void processUpdate(String propertyName, String id, Object newValue);

    protected void processGranulatorConfig(String id, Object newValue) {
        addGranulatorConfig();
    }

    protected void addGranulatorConfig() {
        WebGranulatorConfig config = getState().getGranulatorConfig();
        WebGranulatorConfigExport export = new WebGranulatorConfigExport();
        export.populate(config);
        delta.put(WEB_OBJ_CONFIG_GRANULATOR, export);
    }

    protected void processSpeechSynthConfig(String id, Object newValue) {
        addSpeechSynthConfig();
    }

    protected void addSpeechSynthConfig() {
        WebSpeechSynthConfig config = getState().getSpeechSynthConfig();
        WebSpeechSynthConfigExport export = new WebSpeechSynthConfigExport();
        export.populate(config);
        delta.put(WEB_OBJ_CONFIG_SPEECH_SYNTH, export);
    }

    protected void processSpeechSynthState(String id, Object newValue) {
        addSpeechSynthState();
    }

    protected void addSpeechSynthState() {
        WebSpeechSynthState config = getState().getSpeechSynthState();
        WebSpeechSynthStateExport export = new WebSpeechSynthStateExport();
        export.populate(config);
        delta.put(WEB_OBJ_STATE_SPEECH_SYNTH, export);
    }

    protected void processAction(String id, Object newValue) {
        if (id == null || newValue == null) {
            return;
        }
        if (newValue instanceof WebAudienceAction) {
            addAction((WebAudienceAction) newValue);
        } else {
            LOG.error("processAction: invalid object type: {}", newValue);
        }
    }

    protected void addAction(WebAudienceAction action) {
        if (action == null) {
            return;
        }
        ArrayList<WebAudienceActionExport> actions = (ArrayList<WebAudienceActionExport>) delta.computeIfAbsent(WEB_OBJ_ACTIONS, s -> new ArrayList<TileExport>());
        WebAudienceActionExport actionExport = new WebAudienceActionExport();
        actionExport.populate(action);
        actions.add(actionExport);
    }

    protected void processInstructions(String id, Object newValue) {
        processWebText(id, newValue);
    }

    protected void processWebText(String id, Object newValue) {
        if (id == null) {
            return;
        }
        switch (id) {
            case WEB_OBJ_INSTRUCTIONS:
                addInstructions((WebTextState) newValue);
                break;
            default:
                LOG.error("processWebText: Invalid id: {}", id);
        }
    }

    protected void addInstructions(WebTextState instructions) {
        if (instructions == null) {
            return;
        }
        WebAudienceInstructionsExport textExport = new WebAudienceInstructionsExport();
        textExport.populate(instructions);
        delta.put(WEB_OBJ_INSTRUCTIONS, textExport);
    }

    public abstract WebAudienceServerState getState();

    protected HashMap<String, Object> getDelta() {
        return delta;
    }
}
