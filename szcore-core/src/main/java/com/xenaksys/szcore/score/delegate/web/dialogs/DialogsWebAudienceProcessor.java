package com.xenaksys.szcore.score.delegate.web.dialogs;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceEventType;
import com.xenaksys.szcore.event.web.audience.WebAudiencePrecountEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStateUpdateEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStopEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceVoteEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.web.audience.WebAudienceChangeListener;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreProcessor;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;
import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebAudienceStateDeltaTracker;
import com.xenaksys.szcore.score.web.audience.WebCounter;
import com.xenaksys.szcore.score.web.audience.WebTextState;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceInstructionsExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.score.web.audience.export.WebCounterExport;
import com.xenaksys.szcore.score.web.audience.export.WebGranulatorConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthStateExport;
import com.xenaksys.szcore.web.WebAudienceAction;
import com.xenaksys.szcore.web.WebScoreStateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_LOAD_PRESET;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_COUNTER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STATE_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VOTE;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;

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
        WebCounter counter = createDefaultWebCounter();

        DialogsWebAudienceServerState webAudienceServerState = new DialogsWebAudienceServerState(currentActions,
                instructions, granulatorConfig, speechSynthConfig, speechSynthState, counter, getPcs());

        createWebAudienceStateDeltaTracker(webAudienceServerState);
        getPcs().addPropertyChangeListener(new WebAudienceChangeListener(stateDeltaTracker));
        return webAudienceServerState;
    }

    private void createWebAudienceStateDeltaTracker(DialogsWebAudienceServerState webAudienceServerState) {
        this.stateDeltaTracker = new DialogsWebAudienceStateDeltaTracker(webAudienceServerState);
    }

    public void resetState() {
        getState().clearActions();
        getState().setInstructions("Welcome to ZScore", 1);
        getState().setInstructions("<span style='color:blueviolet;'>Dialogs</span>", 2);
        getState().setInstructions("awaiting performance start ...", 3);
        getState().setInstructionsVisible(true);

        getState().setGranulatorConfig(createDefaultGranulatorConfig());
        getState().setSpeechSynthConfig(createDefaultSpeechSynthConfig());
        getState().setSpeechSynthState(createDefaultSpeechSynthState());

        reset(WEB_CONFIG_LOAD_PRESET);
        updateServerState();
        pushServerState();
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
        DialogsWebAudienceServerState state = getDelegateState();
        List<WebAudienceAction> actions = state.getActions();
        WebAudienceInstructionsExport instructions = new WebAudienceInstructionsExport();
        instructions.populate(state.getInstructions());

        WebGranulatorConfigExport granulatorConfig = new WebGranulatorConfigExport();
        granulatorConfig.populate(state.getGranulatorConfig());

        WebSpeechSynthConfigExport speechSynthConfigExport = new WebSpeechSynthConfigExport();
        speechSynthConfigExport.populate(state.getSpeechSynthConfig());

        WebSpeechSynthStateExport speechSynthStateExport = new WebSpeechSynthStateExport();
        speechSynthStateExport.populate(state.getSpeechSynthState());

        WebCounterExport counterExport = new WebCounterExport();
        counterExport.populate(state.getCounter());

        WebAudienceScoreStateExport export = new WebAudienceScoreStateExport();
        export.addState(WEB_OBJ_ACTIONS, actions);
        export.addState(WEB_OBJ_INSTRUCTIONS, instructions);
        export.addState(WEB_OBJ_CONFIG_GRANULATOR, granulatorConfig);
        export.addState(WEB_OBJ_CONFIG_SPEECH_SYNTH, speechSynthConfigExport);
        export.addState(WEB_OBJ_STATE_SPEECH_SYNTH, speechSynthStateExport);
        export.addState(WEB_OBJ_COUNTER, counterExport);
        return export;
    }

    public void updateServerState() {
        try {
            getScoreProcessor().onWebAudienceStateChange(exportState());
        } catch (Exception e) {
            LOG.error("Failed to process updateServerState", e);
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
                case VOTE:
                    isSendStateUpdate = processVote((WebAudienceVoteEvent) event);
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

    public void setInstructions(String l1, String l2, String l3) {
        setInstructions(l1, l2, l3, WEB_TEXT_BACKGROUND_COLOUR, true);
    }

    public void setInstructions(String l1, String l2, String l3, boolean isVisible) {
        setInstructions(l1, l2, l3, WEB_TEXT_BACKGROUND_COLOUR, isVisible);
    }

    public void setInstructions(boolean isVisible) {
        setInstructions(EMPTY, EMPTY, EMPTY, WEB_TEXT_BACKGROUND_COLOUR, isVisible);
    }

    public void setInstructions(String l1, String l2, String l3, String colour, boolean isVisible) {
        getState().setInstructions(l1, 1);
        getState().setInstructions(l2, 2);
        getState().setInstructions(l3, 3);
        getState().setInstructionsColour(colour);
        getState().setInstructionsVisible(isVisible);
    }

    private boolean processVote(WebAudienceVoteEvent event) {
        String value = event.getValue();
        if (value == null) {
            return false;
        }
        try {
            int voteVal = Integer.parseInt(value);
            LOG.info("Received vote: {}", voteVal);
            DialogsWebAudienceServerState state = getDelegateState();
            WebCounter voteCounter = state.getCounter();
            if (voteVal > 0) {
                voteCounter.increment();
            } else {
                voteCounter.decrement();
            }
            voteCounter.setMaxCount(event.getUsersNo());
            getPcs().firePropertyChange(WEB_OBJ_COUNTER, WEB_OBJ_VOTE, voteCounter);
        } catch (NumberFormatException e) {
            LOG.error("Invalid vote value: {}", value);
            return false;
        }
        return true;
    }

    public boolean updateState(WebAudienceStateUpdateEvent event) {
        WebScoreStateType propType = event.getPropertyType();
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
