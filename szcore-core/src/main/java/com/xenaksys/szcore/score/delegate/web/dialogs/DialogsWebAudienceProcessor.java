package com.xenaksys.szcore.score.delegate.web.dialogs;

import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceInstructionsEvent;
import com.xenaksys.szcore.event.web.audience.WebAudiencePrecountEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStateUpdateEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStopEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceVoteEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.web.audience.WebAudienceChangeListener;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreProcessor;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;
import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebAudienceStateDeltaTracker;
import com.xenaksys.szcore.score.web.audience.WebCounter;
import com.xenaksys.szcore.score.web.audience.WebTextState;
import com.xenaksys.szcore.score.web.audience.WebViewState;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebPlayerConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceInstructionsExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.score.web.audience.export.WebCounterExport;
import com.xenaksys.szcore.score.web.audience.export.WebGranulatorConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebPlayerConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthStateExport;
import com.xenaksys.szcore.score.web.audience.export.WebViewStateExport;
import com.xenaksys.szcore.web.WebAudienceAction;
import com.xenaksys.szcore.web.WebScoreStateType;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.set.TLongSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.EQUALS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ACTIVE_VIEWS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_LOAD_PRESET;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SECTION_NAME;
import static com.xenaksys.szcore.Consts.WEB_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_PLAYER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_COUNTER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STATE_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VIEW_STATE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VOTE;
import static com.xenaksys.szcore.Consts.WEB_PLAYER;
import static com.xenaksys.szcore.Consts.WEB_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;
import static com.xenaksys.szcore.Consts.WEB_VIEW_AUDIO;
import static com.xenaksys.szcore.Consts.WEB_VIEW_METER;
import static com.xenaksys.szcore.Consts.WEB_VIEW_NOTES;
import static com.xenaksys.szcore.Consts.WEB_VIEW_THUMBS;
import static com.xenaksys.szcore.Consts.WEB_VIEW_VOTE;

public class DialogsWebAudienceProcessor extends WebAudienceScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(DialogsWebAudienceProcessor.class);

    private static final String[] AVAILABLE_VIEWS = {WEB_VIEW_THUMBS, WEB_VIEW_NOTES, WEB_VIEW_VOTE, WEB_VIEW_AUDIO, WEB_VIEW_METER};

    private DialogsAudienceWebscoreConfig audienceWebscoreConfig;
    private final DialogsAudienceConfigLoader configLoader = new DialogsAudienceConfigLoader();
    private DialogsWebAudienceStateDeltaTracker stateDeltaTracker;
    private String currentSection;
    private long sectionStartTime;

    public DialogsWebAudienceProcessor(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        super(scoreProcessor, eventFactory, clock);
    }

    public WebAudienceServerState initState() {
        List<WebAudienceAction> currentActions = new ArrayList<>();
        WebTextState instructions = new WebTextState(WEB_OBJ_INSTRUCTIONS, 3);
        WebGranulatorConfig granulatorConfig = createDefaultGranulatorConfig();
        WebSpeechSynthConfig speechSynthConfig = createDefaultSpeechSynthConfig();
        WebSpeechSynthState speechSynthState = createDefaultSpeechSynthState();
        WebPlayerConfig playerConfig = createDefaultWebPlayerConfig();
        WebCounter counter = createDefaultWebCounter();
        WebViewState viewState = createDefaultWebViewState();

        DialogsWebAudienceServerState webAudienceServerState = new DialogsWebAudienceServerState(currentActions,
                instructions, granulatorConfig, speechSynthConfig, speechSynthState, playerConfig, counter, viewState, getPcs());

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
        getDelegateState().setPlayerConfig(createDefaultWebPlayerConfig());

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
                case WEB_PLAYER:
                    updatePlayerConfig((Map<String, Object>) configs.get(key));
                    break;
                case WEB_GRANULATOR:
                    updateGranulatorConfig((Map<String, Object>) configs.get(key));
                    break;
                case WEB_SPEECH_SYNTH:
                    updateSpeechSynthConfig((Map<String, Object>) configs.get(key));
                    break;
                default:
                    LOG.info("processPresetConfigs: unknown key: {}", key);
            }
        }
    }

    private void updatePlayerConfig(Map<String, Object> conf) {
        getPlayerConfig().update(conf);
    }

    private void updateGranulatorConfig(Map<String, Object> conf) {
        getGranulatorConfig().update(conf);
    }

    private void updateSpeechSynthConfig(Map<String, Object> conf) {
        getSpeechSynthConfig().update(conf);
    }

    public WebPlayerConfig getPlayerConfig() {
        return getDelegateState().getPlayerConfig();
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

        WebPlayerConfigExport playerConfigExport = new WebPlayerConfigExport();
        playerConfigExport.populate(state.getPlayerConfig());

        WebViewStateExport viewStateExport = new WebViewStateExport();
        viewStateExport.populate(state.getViewState());

        WebAudienceScoreStateExport export = new WebAudienceScoreStateExport();
        export.addState(WEB_OBJ_ACTIONS, actions);
        export.addState(WEB_OBJ_INSTRUCTIONS, instructions);
        export.addState(WEB_OBJ_CONFIG_GRANULATOR, granulatorConfig);
        export.addState(WEB_OBJ_CONFIG_SPEECH_SYNTH, speechSynthConfigExport);
        export.addState(WEB_OBJ_STATE_SPEECH_SYNTH, speechSynthStateExport);
        export.addState(WEB_OBJ_CONFIG_PLAYER, playerConfigExport);
        export.addState(WEB_OBJ_COUNTER, counterExport);
        export.addState(WEB_OBJ_VIEW_STATE, viewStateExport);

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
                case INSTRUCTIONS:
                    isSendStateUpdate = processInstructionsEvent((WebAudienceInstructionsEvent) event);
                    break;
                case PRECOUNT:
                    isSendStateUpdate = processPrecountEvent((WebAudiencePrecountEvent) event);
                    break;
                case STOP:
                    isSendStateUpdate = processStop((WebAudienceStopEvent) event);
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

    public boolean processStop(WebAudienceStopEvent event) {
        sendStopAll();
        processWebCounterOnStop();
        resetVote();
        sendViewOnStop();
        return true;
    }

    private void processWebCounterOnStop() {
        try {
            String section = this.currentSection;
            if(section == null) {
                ScoreBuilderStrategy scoreBuilderStrategy = ((BasicScore) getScore()).getScoreBuilderStrategy();
                section = scoreBuilderStrategy.getCurrentSection();
            }
            DialogsWebAudienceServerState state = getDelegateState();
            WebCounter voteCounter = state.getCounter();
            TLongIntHashMap counterTimeline = voteCounter.getCounterTimeline();
            StringBuilder out = new StringBuilder();
            TLongSet keys = counterTimeline.keySet();
            long[] times = keys.toArray();
            Arrays.sort(times);
            String delimiter = "";
            for(long time : times) {
                out.append(delimiter).append(time - sectionStartTime).append(EQUALS).append(counterTimeline.get(time));
                if(!delimiter.equals(COMMA)) {
                    delimiter = COMMA;
                }
            }
            LOG.info("WebCounter time series for {}::{}", section, out);
            voteCounter.resetCounterTimeline();
        } catch (Exception e) {
            LOG.error("Failed to process web counter", e);
        }
    }

    public void onSectionStart(String section) {
        currentSection = section;
        sectionStartTime = getClock().getElapsedTimeMillis();
        resetVote();
        activateSection(section);
        activateViews(WEB_VIEW_AUDIO, WEB_VIEW_THUMBS, WEB_VIEW_VOTE, WEB_VIEW_METER);
        updateServerStateAndPush();
    }

    public void resetVote() {
        DialogsWebAudienceServerState state = getDelegateState();
        WebCounter voteCounter = state.getCounter();
        voteCounter.resetCounterTimeline();
        voteCounter.resetCounters();
        processVote(voteCounter);
        getPcs().firePropertyChange(WEB_OBJ_COUNTER, WEB_OBJ_VOTE, voteCounter);
    }

    public void onSectionStop(String section) {
        deactivateSection(section);
//        sendViewOnStop();
    }

    public void sendViewOnStop() {
        deactivateViews(WEB_VIEW_AUDIO, WEB_VIEW_THUMBS, WEB_VIEW_VOTE);
        updateServerStateAndPush(true);
    }

    public void setAudienceViewState(boolean isNotesEnabled, boolean isAudioEnabled, boolean isThumbsEnabled, boolean isMeterEnabled, boolean isVoteEnabled) {
        ArrayList<String> enable = new ArrayList<>();
        ArrayList<String> disable = new ArrayList<>();
        if(isNotesEnabled) {
            enable.add(WEB_VIEW_NOTES);
        } else {
            disable.add(WEB_VIEW_NOTES);
        }
        if(isAudioEnabled) {
            enable.add(WEB_VIEW_AUDIO);
        } else {
            disable.add(WEB_VIEW_AUDIO);
        }
        if(isThumbsEnabled) {
            enable.add(WEB_VIEW_THUMBS);
        } else {
            disable.add(WEB_VIEW_THUMBS);
        }
        if(isMeterEnabled) {
            enable.add(WEB_VIEW_METER);
        } else {
            disable.add(WEB_VIEW_METER);
        }
        if(isVoteEnabled) {
            enable.add(WEB_VIEW_VOTE);
        } else {
            disable.add(WEB_VIEW_VOTE);
        }
        deactivateViews(disable.toArray(new String[0]));
        activateViews(enable.toArray(new String[0]));
        updateServerStateAndPush(true);
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
            voteCounter.setVoterNo(event.getUsersNo());
            voteCounter.processTime(getClock().getElapsedTimeMillis());
            if (voteVal > 0) {
                voteCounter.increment();
            } else {
                voteCounter.decrement();
            }
            processVote(voteCounter);
        } catch (NumberFormatException e) {
            LOG.error("Invalid vote value: {}", value);
            return false;
        }
        return true;
    }

    private void processVote(WebCounter voteCounter) {
        String section = this.currentSection;
        ScoreBuilderStrategy scoreBuilderStrategy = ((BasicScore) getScore()).getScoreBuilderStrategy();
        if(section == null) {
            section = scoreBuilderStrategy.getCurrentSection();
        }
        SectionInfo sectionInfo = scoreBuilderStrategy.getSectionInfo(section);
        if(sectionInfo.isActive()) {
            sectionInfo.populateVoteInfo(voteCounter.getCounterValue(), voteCounter.getMin(), voteCounter.getMax(), voteCounter.getAvg(), voteCounter.getVoterNo());
        }
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

    public void setPlayerConfigParam(String name, Object value) {
        try {
            LOG.debug("setPlayerConfigParam: setting config param: {} value: {}", name, value);
            String[] names = name.split("\\.");
            if (names.length == 1) {
                setGranulatorBaseConfig(names[0], value);
                return;
            }
            if (names.length != 2) {
                LOG.error("setGranulatorConfig: invalid param names {}, will not use", Arrays.toString(names));
                return;
            }
            String l1 = names[0];
            String l2 = names[1];
            switch (l1) {
                case WEB_CONFIG_GRAIN:
                    setGrainConfig(l2, value);
                    break;
                case WEB_CONFIG_ENVELOPE:
                    setGranulatorEnvelopeConfig(l2, value);
                    break;
                case WEB_CONFIG_PANNER:
                    setGranulatorPannerConfig(l2, value);
                    break;
            }
        } catch (Exception e) {
            LOG.error("setGranulatorConfigParam: failed to set granulator config for name: {}, value: {}", name, value, e);
        }
    }

    @Override
    public WebAudienceStateDeltaTracker getStateDeltaTracker() {
        return stateDeltaTracker;
    }

    public DialogsWebAudienceStateDeltaTracker getDelegateStateDeltaTracker() {
        return (DialogsWebAudienceStateDeltaTracker) getStateDeltaTracker();
    }

    public void setSection(String id) {
        WebViewState viewState = getDelegateState().getViewState();
        viewState.setSectionName(id);
        viewState.setSectionActive(false);
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_SECTION_NAME, viewState);
    }

    public void activateSection(String id) {
        WebViewState viewState = getDelegateState().getViewState();
        viewState.setSectionName(id);
        viewState.setSectionActive(true);
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_SECTION_NAME, viewState);
    }

    public void deactivateSection(String id) {
        WebViewState viewState = getDelegateState().getViewState();
        viewState.setSectionName(id);
        viewState.setSectionActive(false);
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_SECTION_NAME, viewState);
    }

    public void activateViews(String... views) {
        WebViewState viewState = getDelegateState().getViewState();
        viewState.activateViews(views);
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_ACTIVE_VIEWS, viewState);
    }

    public void deactivateViews(String... views) {
        WebViewState viewState = getDelegateState().getViewState();
        viewState.deactivateViews(views);
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_ACTIVE_VIEWS, viewState);
    }

    public void deactivateAllViews() {
        WebViewState viewState = getDelegateState().getViewState();
        viewState.deactivateAllViews();
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_CONFIG_ACTIVE_VIEWS, viewState);
    }

    private boolean processInstructionsEvent(WebAudienceInstructionsEvent event) {
        setInstructions(event.getL1(), event.getL2(), event.getL3(), event.isVisible());
        return true;
    }
}
