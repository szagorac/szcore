package com.xenaksys.szcore.score.web.audience;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudiencePrecountEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStateUpdateEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStopEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEventType;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.MutablePageId;
import com.xenaksys.szcore.score.PannerDistanceModel;
import com.xenaksys.szcore.score.PanningModel;
import com.xenaksys.szcore.score.web.audience.config.*;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.web.WebAudienceAction;
import com.xenaksys.szcore.web.WebAudienceActionType;
import com.xenaksys.szcore.web.WebScoreStateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.beans.PropertyChangeSupport;
import java.util.*;

import static com.xenaksys.szcore.Consts.*;

public abstract class WebAudienceScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(WebAudienceScoreProcessor.class);

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Score score;
    private final Clock clock;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final Map<BeatId, List<WebAudienceScoreScript>> beatScripts = new HashMap<>();
    private final Map<BeatId, List<WebAudienceScoreScript>> beatResetScripts = new HashMap<>();

    private final ScriptEngineManager factory = new ScriptEngineManager();
    private final ScriptEngine jsEngine = factory.getEngineByName("nashorn");

    private final MutablePageId tempPageId;
    private WebAudienceServerState state;

    public WebAudienceScoreProcessor(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.score = scoreProcessor.getScore();
        this.tempPageId = createTempPage();
        this.state = initState();
    }

    public MutablePageId createTempPage() {
        int pageNo = 0;
        if (score == null) {
            return null;
        }
        Collection<Instrument> instruments = score.getInstruments();
        Instrument instrument = null;
        if (instruments != null || !instruments.isEmpty()) {
            instrument = instruments.iterator().next();
        }
        return new MutablePageId(pageNo, instrument.getId(), score.getId());
    }

    public ScoreProcessor getScoreProcessor() {
        return scoreProcessor;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public Score getScore() {
        return score;
    }

    public Clock getClock() {
        return clock;
    }

    public PropertyChangeSupport getPcs() {
        return pcs;
    }

    public Map<BeatId, List<WebAudienceScoreScript>> getBeatScripts() {
        return beatScripts;
    }

    public Map<BeatId, List<WebAudienceScoreScript>> getBeatResetScripts() {
        return beatResetScripts;
    }

    public ScriptEngineManager getFactory() {
        return factory;
    }

    public ScriptEngine getJsEngine() {
        return jsEngine;
    }

    public MutablePageId getTempPageId() {
        return tempPageId;
    }

    public WebAudienceServerState getState() {
        return state;
    }

    abstract public WebAudienceStateDeltaTracker getStateDeltaTracker();

    public abstract WebAudienceServerState initState();

    public abstract void resetState();

    public abstract void reset(int presetNo);

    public abstract void processPresetConfigs(Map<String, Object> configs);

    private void updateGranulatorConfig(Map<String, Object> conf) {
        getGranulatorConfig().update(conf);
    }

    private void updateSpeechSynthConfig(Map<String, Object> conf) {
        getSpeechSynthConfig().update(conf);
    }

    public void runScripts(List<String> scripts) {
        for (String js : scripts) {
            runScript(js);
        }
    }

    public void runWebScoreScripts(List<WebAudienceScoreScript> scripts) {
        for (WebAudienceScoreScript js : scripts) {
            runScript(js.getContent());
        }
    }

    public void runScript(String script) {
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
        loadConfig(configDir);
        jsEngine.put(WEB_SCORE_ID, this);
        resetState();
    }

    public abstract void loadConfig(String configDir);

    public void startScore() {
        LOG.info("startScore: ");
    }

    public void updateServerStateAndPush() {
        updateServerState();
        updateServerStateDelta();
        pushServerStateDelta();
    }

    public void resetStateDelta() {
        getState().resetDelta();
        getStateDeltaTracker().reset();
    }

    public void setAction(String actionId, String type, String[] targetIds) {
        setAction(actionId, type, targetIds, new HashMap<>());
    }

    public void setAction(String actionId, String type, String[] targetIds, Map<String, Object> params) {
        LOG.debug("setAction: {} target: {}", actionId, Arrays.toString(targetIds));
        try {
            WebAudienceAction action = createAction(actionId, type, targetIds, params);
            state.addAction(action);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to setAction id: {} type: {}", actionId, type);
        }
    }

    public WebAudienceAction createAction(String actionId, String type, String[] targetIds, Map<String, Object> params) {
        WebAudienceActionType t = WebAudienceActionType.valueOf(type.toUpperCase());
        return new WebAudienceAction(actionId, t, Arrays.asList(targetIds), params);
    }

    public boolean processStopAll(WebAudienceStopEvent event) {
        sendStopAll();
        return true;
    }

    public void sendStopAll() {
        String[] target = {WEB_TARGET_ALL};
        setAction(WEB_ACTION_ID_STOP, WebAudienceActionType.STOP.name(), target, null);
    }

    public void sendGranulatorConfig() {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = state.getGranulatorConfig().toJsMap();
        setAction(WEB_ACTION_ID_CONFIG, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void playGranulator() {
        String[] target = {WEB_GRANULATOR};
        WebAudienceAction action = createAction(WEB_ACTION_ID_PLAY, WebAudienceActionType.AUDIO.name(), target, null);
        state.addAction(action);
    }

    public void stopGranulator() {
        String[] target = {WEB_GRANULATOR};
        setAction(WEB_ACTION_ID_STOP, WebAudienceActionType.AUDIO.name(), target, null);
    }

    public void validateGranulatorConfig() {
        state.getGranulatorConfig().validate();
    }

    public void setStageAlpha(double endValue, double durationSec) {
        setAlpha(WEB_STAGE, endValue, durationSec);
    }

    public void setAlpha(String targetId, double endValue, double durationSec) {
        String[] targetIds = {targetId};
        Map<String, Object> params = new HashMap<>(2);
        params.put(WEB_CONFIG_DURATION, durationSec);
        params.put(WEB_CONFIG_VALUE, endValue);
        setAction(WEB_ACTION_ID_START, WebAudienceActionType.ALPHA.name(), targetIds, params);
        WebAudienceStateUpdateEvent stateUpdateEvent = eventFactory.createWebAudienceStateUpdateEvent(WebScoreStateType.STAGE_ALPHA, endValue, clock.getSystemTimeMillis());
        scoreProcessor.scheduleEvent(stateUpdateEvent, (long) durationSec * Consts.THOUSAND);
//        state.setStageAlpha(endValue);
    }

    public void granulatorRampLinear(String paramName, Object endValue, int durationMs) {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = state.getGranulatorConfig().toJsMap();
        params.put(WEB_CONFIG_PARAM_NAME, paramName);
        params.put(WEB_CONFIG_END_VALUE, endValue);
        params.put(WEB_CONFIG_DURATION, durationMs);
        setAction(WEB_ACTION_ID_RAMP_LINEAR, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void granulatorRampSin(String paramName, Double amplitude, Double frequency, int durationMs) {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = state.getGranulatorConfig().toJsMap();
        params.put(WEB_CONFIG_PARAM_NAME, paramName);
        params.put(WEB_CONFIG_AMPLITUDE, amplitude);
        params.put(WEB_CONFIG_FREQUENCY, frequency);
        params.put(WEB_CONFIG_DURATION, durationMs);
        setAction(WEB_ACTION_ID_RAMP_SIN, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void setGranulatorVolume(Double level, int millis) {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = new HashMap<>();
        params.put(WEB_ACTION_PARAM_LEVEL, level);
        params.put(WEB_ACTION_PARAM_TIME_MS, millis);
        setAction(WEB_ACTION_VOLUME, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void sendSpeechSynthConfig() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthConfig().toJsMap();
        setAction(WEB_ACTION_ID_CONFIG, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void validateSpeechSynthConfig() {
        state.getSpeechSynthConfig().validate();
    }

    public void sendSpeechSynthState() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        setAction(WEB_ACTION_ID_STATE, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void validateSpeechSynthState() {
        state.getSpeechSynthState().validate();
    }

    public void enableSpeechSynth() {
        state.getSpeechSynthState().setPlaySpeechSynthOnClick(true);
        sendSpeechSynthState();
    }

    public void disableSpeechSynth() {
        state.getSpeechSynthState().setPlaySpeechSynthOnClick(false);
        sendSpeechSynthState();
    }

    public void setSpeechText(String text) {
        state.getSpeechSynthState().setSpeechText(text);
        sendSpeechSynthState();
    }

    public void setSpeechVoice(String voice) {
        state.getSpeechSynthState().setSpeechVoice(voice);
        sendSpeechSynthState();
    }

    public void setSpeechInterrupt(boolean isInterrupt) {
        state.getSpeechSynthState().setSpeechIsInterrupt(isInterrupt);
        sendSpeechSynthState();
    }

    public void speak() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        setAction(WEB_ACTION_ID_PLAY, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void speak(String text) {
        if (text != null && !text.isEmpty()) {
            LOG.warn("speak: Invalid text to speak: {}", text);
        }
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        params.put(WEB_CONFIG_SPEECH_TEXT, text);
        setAction(WEB_ACTION_ID_PLAY, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void stopSpeech() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        setAction(WEB_ACTION_ID_STOP, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void rampLinearSpeechParam(String name, Object endValue, double durationSec) {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = new HashMap<>();
        params.put(WEB_CONFIG_PARAM_NAME, name);
        params.put(WEB_CONFIG_END_VALUE, endValue);
        params.put(WEB_CONFIG_DURATION, durationSec);
        setAction(WEB_ACTION_ID_RAMP_LINEAR, WebAudienceActionType.AUDIO.name(), target, params);
    }

    public void setSpeechSynthConfigParam(String name, Object value) {
        try {
            LOG.debug("setSpeechSynthConfigParam: setting config param: {} value: {}", name, value);
            WebSpeechSynthConfig config = state.getSpeechSynthConfig();
            switch (name) {
                case WEB_CONFIG_VOLUME:
                    try {
                        config.setVolume(getDouble(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set volume", e);
                    }
                    break;
                case WEB_CONFIG_PITCH:
                    try {
                        config.setPitch(getDouble(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set pitch", e);
                    }
                    break;
                case WEB_CONFIG_RATE:
                    try {
                        config.setRate(getDouble(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set rate", e);
                    }
                    break;
                case WEB_CONFIG_LANG:
                    try {
                        config.setLang(getString(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set lang", e);
                    }
                    break;
                case WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS:
                    try {
                        config.setMaxVoiceLoadAttempts(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set MaxVoiceLoadAttempts", e);
                    }
                    break;
                case WEB_CONFIG_MAX_UTTERANCES:
                    try {
                        config.setMaxUtterances(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set MaxUtterances", e);
                    }
                    break;
                case WEB_CONFIG_UTTERANCE_TIMEOUT_SEC:
                    try {
                        config.setUtteranceTimeoutSec(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set UtteranceTimeoutSec", e);
                    }
                    break;
                case WEB_CONFIG_IS_INTERRUPT:
                    try {
                        config.setInterrupt(getBoolean(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set isInterrupt", e);
                    }
                    break;
                case WEB_CONFIG_INTERRUPT_TIMEOUT_MS:
                    try {
                        config.setInterruptTimeout(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set InterruptTimeout", e);
                    }
                    break;
                default:
                    LOG.error("setSpeechSynthConfigParam: Invalid Speech Synth Config Param: {}", name);
            }
        } catch (Exception e) {
            LOG.error("setSpeechSynthConfigParam: failed to set speech synth config for name: {}, value: {}", name, value, e);
        }
    }

    public void setSpeechSynthConfig(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setSpeechSynthConfig: invalid params");
            return;
        }
        for (String param : params.keySet()) {
            Object value = params.get(param);
            setSpeechSynthConfigParam(param, value);
        }
        state.getSpeechSynthConfig().validate();
        LOG.debug("setSpeechSynthConfig: new config: {}", state.getSpeechSynthConfig());
    }

    public WebSpeechSynthConfig getSpeechSynthConfig() {
        return state.getSpeechSynthConfig();
    }

    public void setSpeechSynthStateParam(String name, Object value) {
        try {
            LOG.debug("setSpeechSynthStateParam: setting config param: {} value: {}", name, value);
            WebSpeechSynthState speechSynthState = state.getSpeechSynthState();
            switch (name) {
                case WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK:
                    try {
                        speechSynthState.setPlaySpeechSynthOnClick(getBoolean(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set PlaySpeechSynthOnClick", e);
                    }
                    break;
                case WEB_CONFIG_SPEECH_TEXT:
                    try {
                        speechSynthState.setSpeechText(getString(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set SpeechText", e);
                    }
                    break;
                case WEB_CONFIG_SPEECH_VOICE:
                    try {
                        speechSynthState.setSpeechVoice(getString(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set SpeechVoice", e);
                    }
                    break;
                case WEB_CONFIG_SPEECH_IS_INTERRUPT:
                    try {
                        speechSynthState.setSpeechIsInterrupt(getBoolean(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set SpeechIsInterrupt", e);
                    }
                    break;
                default:
                    LOG.error("setSpeechSynthStateParam: Invalid Speech Synth Config Param: {}", name);
            }
        } catch (Exception e) {
            LOG.error("setSpeechSynthStateParam: failed to set speech synth state for name: {}, value: {}", name, value, e);
        }
    }

    public void setSpeechSynthState(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setSpeechSynthState: invalid params");
            return;
        }
        for (String param : params.keySet()) {
            Object value = params.get(param);
            setSpeechSynthStateParam(param, value);
        }
        state.getSpeechSynthState().validate();
        LOG.debug("setSpeechSynthState: new config: {}", state.getSpeechSynthState());
    }

    public void setGranulatorConfigParam(String name, Object value) {
        try {
            LOG.debug("setGranulatorConfig: setting config param: {} value: {}", name, value);
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

    public void setGranulatorBaseConfig(String name, Object value) {
        LOG.debug("setGranulatorBaseConfig: setting config param: {} value: {}", name, value);
        WebGranulatorConfig config = state.getGranulatorConfig();
        switch (name) {
            case WEB_CONFIG_MASTER_GAIN_VAL:
                try {
                    config.setMasterGainVal(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set MasterGainVal", e);
                }
                break;
            case WEB_CONFIG_PLAY_DURATION_SEC:
                try {
                    config.setPlayDurationSec(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set PlayDurationSec", e);
                }
                break;
            case WEB_CONFIG_PLAY_START_OFFSET_SEC:
                try {
                    config.setPlayStartOffsetSec(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set PlayStartOffsetSec", e);
                }
                break;
            case WEB_CONFIG_MAX_GRAINS:
                try {
                    config.setMaxGrains(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set MaxGrains", e);
                }
                break;
            case WEB_CONFIG_BUFFER_POSITION_PLAY_RATE:
                try {
                    config.setBufferPositionPlayRate(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set BufferPositionPlayRate", e);
                }
                break;
            case WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS:
                try {
                    config.setAudioStopToleranceMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set BufferPositionPlayRate", e);
                }
                break;
            default:
                LOG.error("setGranulatorBaseConfig: Invalid Speech Synth Config Param: {}", name);
        }
    }

    public void setGranulatorConfig(String params) {

    }

    public void setGranulatorConfig(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setGranulatorConfig: invalid params");
            return;
        }
        for (String param : params.keySet()) {
            Object value = params.get(param);
            setGranulatorConfigParam(param, value);
        }
        state.getGranulatorConfig().validate();
        LOG.debug("setGranulatorConfig: new config: {}", state.getGranulatorConfig());
    }

    public WebGranulatorConfig getGranulatorConfig() {
        return state.getGranulatorConfig();
    }

    public void setGranulatorPannerConfig(String name, Object value) {
        WebPannerConfig config = state.getGranulatorConfig().getPanner();
        switch (name) {
            case WEB_CONFIG_IS_USE_PANNER:
                try {
                    config.setUsePanner(getBoolean(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set isUsePanner", e);
                }
                break;
            case WEB_CONFIG_PANNING_MODEL:
                try {
                    String v = PanningModel.fromName(getString(value)).getName();
                    config.setPanningModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set panningModel", e);
                }
                break;
            case WEB_CONFIG_DISTANCE_MODEL:
                try {
                    String v = PannerDistanceModel.fromName(getString(value)).getName();
                    config.setDistanceModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set distanceModel", e);
                }
                break;
            case WEB_CONFIG_MAX_PAN_ANGLE:
                try {
                    config.setMaxPanAngle(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set maxPanAngle", e);
                }
                break;
            default:
                LOG.error("setGranulatorPannerConfig: Invalid Granulator Panner param: {}", name);
        }
    }

    public void setGrainConfig(String name, Object value) {
        WebGrainConfig config = state.getGranulatorConfig().getGrain();
        switch (name) {
            case WEB_CONFIG_SIZE_MS:
                try {
                    config.setSizeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set grain size", e);
                }
                break;
            case WEB_CONFIG_PITCH_RATE:
                try {
                    config.setPitchRate(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set pitchRate", e);
                }
                break;
            case WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS:
                try {
                    config.setMaxPositionOffsetRangeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPositionOffsetRangeMs", e);
                }
                break;
            case WEB_CONFIG_MAX_PITCH_RATE_RANGE:
                try {
                    config.setMaxPitchRateRange(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPitchRateRange", e);
                }
                break;
            case WEB_CONFIG_TIME_OFFSET_STEPS_MS:
                try {
                    config.setTimeOffsetStepMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set timeOffsetStepMs", e);
                }
                break;
            default:
                LOG.error("setGrainConfig: Invalid Grain Config Param: {}", name);
        }
    }

    public void setGranulatorEnvelopeConfig(String name, Object value) {
        WebEnvelopeConfig config = state.getGranulatorConfig().getEnvelope();
        switch (name) {
            case WEB_CONFIG_ATTACK_TIME:
                try {
                    config.setAttackTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set attackTime", e);
                }
                break;
            case WEB_CONFIG_DECAY_TIME:
                try {
                    config.setDecayTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set decayTime", e);
                }
                break;
            case WEB_CONFIG_SUSTAIN_TIME:
                try {
                    config.setSustainTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainTime", e);
                }
                break;
            case WEB_CONFIG_RELEASE_TIME:
                try {
                    config.setReleaseTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set releaseTime", e);
                }
                break;
            case WEB_CONFIG_SUSTAIN_LEVEL:
                try {
                    config.setSustainLevel(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainLevel", e);
                }
                break;
            default:
                LOG.error("setGranulatorEnvelopeConfig: Invalid Grain Config Param: {}", name);
        }
    }

    public abstract WebAudienceScoreStateExport exportState();

    public abstract void updateServerState();

    public void updateServerStateDelta() {
        try {
            WebAudienceStateDeltaTracker deltaTracker = getStateDeltaTracker();
            if (deltaTracker.hasChanges()) {
                getScoreProcessor().onWebAudienceStateDeltaChange(deltaTracker.getDeltaExport());
            }
        } catch (Exception e) {
            LOG.error("Failed to process updateServerStateDelta", e);
        }
    }

    public void pushServerState() {
        sendOutgoingWebEvent(OutgoingWebEventType.PUSH_SERVER_STATE);
    }

    public void pushServerStateDelta() {
        sendOutgoingWebEvent(OutgoingWebEventType.PUSH_SERVER_STATE_DELTA);
    }

    public void sendOutgoingWebEvent(OutgoingWebEventType eventType) {
        try {
            long creationTime = clock.getSystemTimeMillis();
            OutgoingWebEvent outgoingWebAudienceEvent = eventFactory.createOutgoingWebAudienceEvent(null, null, eventType, creationTime);
            scoreProcessor.onOutgoingWebEvent(outgoingWebAudienceEvent);
        } catch (Exception e) {
            LOG.error("Failed to process sendOutgoingWebEvent, type: {}", eventType, e);
        }
    }

    public abstract void processWebAudienceEvent(WebAudienceEvent event);

    public abstract boolean updateState(WebAudienceStateUpdateEvent event);

    public void addBeatScript(BeatId beatId, WebAudienceScoreScript webAudienceScoreScript) {
        if (beatId == null || webAudienceScoreScript == null) {
            return;
        }

        List<WebAudienceScoreScript> scripts = beatScripts.computeIfAbsent(beatId, k -> new ArrayList<>());

        if (webAudienceScoreScript.isResetPoint()) {
            addResetScript(beatId, webAudienceScoreScript);
            if (!webAudienceScoreScript.isResetOnly()) {
                scripts.add(webAudienceScoreScript);
            }
        } else {
            scripts.add(webAudienceScoreScript);
        }
    }

    public void addResetScript(BeatId beatId, WebAudienceScoreScript webAudienceScoreScript) {
        if (beatId == null || webAudienceScoreScript == null) {
            return;
        }

        List<WebAudienceScoreScript> scripts = beatResetScripts.computeIfAbsent(beatId, k -> new ArrayList<>());
        scripts.add(webAudienceScoreScript);
    }

    public List<WebAudienceScoreScript> getBeatScripts(BeatId beatId) {
        return beatScripts.get(beatId);
    }

    public List<WebAudienceScoreScript> getBeatResetScripts(BeatId beatId) {
        if (beatResetScripts.containsKey(beatId)) {
            return beatResetScripts.get(beatId);
        }

        ArrayList<BeatId> beats = new ArrayList<>(beatResetScripts.keySet());
        Collections.sort(beats);
        int outIndex = Collections.binarySearch(beats, beatId);
        int idx = outIndex;
        if (outIndex < 0) {
            idx += 1;
            idx *= (-1);
            idx -= 1;
        }
        if(idx < 0 || idx >= beats.size()) {
            return null;
        }
        BeatId outId = beats.get(idx);
        return beatResetScripts.get(outId);
    }

    public WebSpeechSynthConfig createDefaultSpeechSynthConfig() {
        return new WebSpeechSynthConfig(pcs);
    }

    public WebSpeechSynthState createDefaultSpeechSynthState() {
        return new WebSpeechSynthState(pcs);
    }

    public WebCounter createDefaultWebCounter() {
        return new WebCounter(WEB_CONFIG_COUNTER, 0);
    }

    public WebGranulatorConfig createDefaultGranulatorConfig() {
        return new WebGranulatorConfig(pcs);
    }

    public boolean processPrecountEvent(WebAudiencePrecountEvent event) {
        int count = event.getCount();
        boolean isOn = event.getIsOn();
        int colourId = event.getColourId();

        LOG.debug("processPrecountEvent: count: {}, isOn: {}, colId: {}", count, isOn, colourId);
        if (count == 1 && isOn && colourId == 4) {
            reset(WEB_CONFIG_READY_PRESET);
            return true;
        } else if (count == 1 && isOn && colourId == 3) {
            reset(WEB_CONFIG_GO_PRESET);
            return true;
        }

        return false;
    }

    public abstract WebAudienceScoreStateDeltaExport getStateDeltaExport();

    private double getDouble(Object value) {
        double v;
        if (value instanceof String) {
            v = Double.parseDouble((String) value);
        } else {
            v = (Double) value;
        }
        return v;
    }

    private int getInt(Object value) {
        int v;
        if (value instanceof String) {
            v = Integer.parseInt((String) value);
        } else {
            v = (Integer) value;
        }
        return v;
    }

    private String getString(Object value) {
        String v;
        if (value instanceof String) {
            v = (String) value;
        } else {
            v = value.toString();
        }
        return v;
    }

    private boolean getBoolean(Object value) {
        boolean v;
        if (value instanceof String) {
            v = Boolean.parseBoolean((String) value);
        } else {
            v = (Boolean) value;
        }
        return v;
    }
}
