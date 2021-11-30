package com.xenaksys.szcore.score.web.audience.delegate;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.audience.*;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.web.audience.*;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.score.web.audience.export.delegate.DialogsWebAudienceExport;
import com.xenaksys.szcore.web.WebAudienceAction;
import com.xenaksys.szcore.web.WebScoreStateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;

public class DialogsWebAudienceProcessor extends WebAudienceScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(DialogsWebAudienceProcessor.class);

    private DialogsAudienceWebscoreConfig audienceWebscoreConfig;
    private final DialogsAudienceConfigLoader configLoader = new DialogsAudienceConfigLoader();
    private DialogsWebAudienceStateDeltaTracker stateDeltaTracker;

    public DialogsWebAudienceProcessor(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        super(scoreProcessor, eventFactory, clock);
    }

    public WebAudienceServerState initState() {
        List<WebAudienceAction> currentActions = new ArrayList<>();
        WebTextState instructions = new WebTextState(WEB_OBJ_INSTRUCTIONS, 3);
        WebGranulatorConfig granulatorConfig = createDefaultGranulatorConfig();
        WebSpeechSynthConfig speechSynthConfig = createDefaultSpeechSynthConfig();
        WebSpeechSynthState speechSynthState = createDefaultSpeechSynthState();


        DialogsWebAudienceServerState webAudienceServerState = new DialogsWebAudienceServerState(currentActions, instructions, granulatorConfig,
                speechSynthConfig, speechSynthState, getPcs());

        createWebAudienceStateDeltaTracker(webAudienceServerState);
        getPcs().addPropertyChangeListener(new WebAudienceChangeListener(stateDeltaTracker));
        return webAudienceServerState;
    }

    private void createWebAudienceStateDeltaTracker(DialogsWebAudienceServerState webAudienceServerState) {
        this.stateDeltaTracker = new DialogsWebAudienceStateDeltaTracker(webAudienceServerState);
    }

    public void resetState() {
        getState().clearActions();
    }

    public void reset(int presetNo) {
        try {
            ScriptPreset preset = audienceWebscoreConfig.getPreset(presetNo);
            if (preset == null) {
                LOG.info("resetState: Unknown preset: {}", presetNo);
                return;
            }

            Map<String, Object> configs = preset.getConfigs();
            if (!configs.isEmpty()) {
                processPresetConfigs(configs);
            }

            List<String> scripts = preset.getScripts();
            if (!scripts.isEmpty()) {
                runScripts(scripts);
            }
        } catch (Exception e) {
            LOG.error("resetState: Failed to run preset: {}", presetNo, e);
        }
    }

    public void processPresetConfigs(Map<String, Object> configs) {
        for (String key : configs.keySet()) {
            switch (key) {
                default:
                    LOG.info("processPresetConfigs: unknown key: {}", key);
            }
        }
    }

    public void loadConfig(String configDir) {
        if (configDir == null) {
            return;
        }
        try {
            audienceWebscoreConfig = configLoader.load(configDir);
        } catch (Exception e) {
            LOG.error("Failed to load WebAudienceScoreProcessor Presets", e);
        }
    }


    public WebAudienceScoreStateExport exportState() {
        WebAudienceServerState state = getState();
        List<WebAudienceAction> actions = state.getActions();
        return new DialogsWebAudienceExport(actions);
    }

    public void updateServerState() {
        try {
            getScoreProcessor().onWebAudienceStateChange(exportState());
        } catch (Exception e) {
            LOG.error("Failed to process updateServerState", e);
        }
    }

    public void updateServerStateDelta() {
        try {
            if (stateDeltaTracker.hasChanges()) {
                getScoreProcessor().onWebAudienceStateDeltaChange(stateDeltaTracker.getDeltaExport());
            }
        } catch (Exception e) {
            LOG.error("Failed to process updateServerStateDelta", e);
        }
    }

    public void processWebAudienceEvent(WebAudienceEvent event) {
        LOG.debug("processWebScoreEvent: execute event: {}", event);
        WebAudienceEventType type = event.getWebAudienceEventType();
        try {
            resetStateDelta();
            boolean isSendStateUpdate = true;
            switch (type) {
                case PRECOUNT:
                    isSendStateUpdate = processPrecountEvent((WebAudiencePrecountEvent) event);
                    break;
                case STOP:
                    isSendStateUpdate = processStopAll((WebAudienceStopEvent) event);
                    break;
                case STATE_UPDATE:
                    isSendStateUpdate = updateState((WebAudienceStateUpdateEvent) event);
                    break;
                case RESET:
                case SCRIPT:
                    List<WebAudienceScoreScript> jsScripts = event.getScripts();
                    if (jsScripts == null) {
                        return;
                    }
                    runWebScoreScripts(jsScripts);
                    break;
                default:
                    LOG.warn("processWebScoreEvent: Ignoring event {}", type);
            }
            if (isSendStateUpdate) {
                updateServerStateAndPush();
            }
        } catch (Exception e) {
            LOG.error("Failed to evaluate script", e);
        }
    }

    public boolean updateState(WebAudienceStateUpdateEvent event) {
        WebScoreStateType propType = event.getPropertyType();
        Object value = event.getPropertyValue();

        switch (propType) {
            default:
                LOG.error("updateState: unknown property type: {}", propType);
        }

        return false;
    }

    public WebAudienceScoreStateDeltaExport getStateDeltaExport() {
        return null;
    }

    public DialogsWebAudienceServerState getDelegateState() {
        return (DialogsWebAudienceServerState) getState();
    }

    @Override
    public WebAudienceStateDeltaTracker getStateDeltaTracker() {
        return stateDeltaTracker;
    }
}
