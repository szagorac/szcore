package com.xenaksys.szcore.score.delegate.web.symphonea;

import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebAudienceStateDeltaTracker;
import com.xenaksys.szcore.score.web.audience.WebCounter;
import com.xenaksys.szcore.score.web.audience.WebViewState;
import com.xenaksys.szcore.score.web.audience.config.WebPlayerConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSynthConfig;
import com.xenaksys.szcore.score.web.audience.export.WebCounterExport;
import com.xenaksys.szcore.score.web.audience.export.WebPlayerConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSynthConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebViewStateExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN_PANNER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_PLAYER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_COUNTER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STATE_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VIEW_STATE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_WEB_TEXT;

public class SymphoneaWebAudienceStateDeltaTracker extends WebAudienceStateDeltaTracker {
    static final Logger LOG = LoggerFactory.getLogger(SymphoneaWebAudienceStateDeltaTracker.class);

    private final SymphoneaWebAudienceServerState state;

    public SymphoneaWebAudienceStateDeltaTracker(SymphoneaWebAudienceServerState state) {
        this.state = state;
    }

    public void processUpdate(String propertyName, String id, Object newValue) {
        if (propertyName == null) {
            LOG.error("processUpdate: invalid propertyName (null) id: {} newValue: {}", id, newValue);
            return;
        }

        switch (propertyName) {
            case WEB_OBJ_WEB_TEXT:
                processWebText(id, newValue);
                break;
            case WEB_OBJ_INSTRUCTIONS:
                processInstructions(id, newValue);
                break;
            case WEB_OBJ_ACTIONS:
                processAction(id, newValue);
                break;
            case WEB_OBJ_CONFIG_GRANULATOR:
            case WEB_OBJ_CONFIG_GRAIN:
            case WEB_OBJ_CONFIG_GRAIN_ENVELOPE:
            case WEB_OBJ_CONFIG_GRAIN_PANNER:
                processGranulatorConfig(id, newValue);
                break;
            case WEB_OBJ_CONFIG_SPEECH_SYNTH:
                processSpeechSynthConfig(id, newValue);
                break;
            case WEB_OBJ_STATE_SPEECH_SYNTH:
                processSpeechSynthState(id, newValue);
                break;
            case WEB_OBJ_COUNTER:
                processCounter(id, newValue);
                break;
            case WEB_OBJ_VIEW_STATE:
                processViewState(id, newValue);
                break;
            case WEB_OBJ_CONFIG_PLAYER:
                processPlayerConfig(id, newValue);
                break;
            case WEB_OBJ_CONFIG_SYNTH:
                processSynthConfig(id, newValue);
                break;
            default:
                LOG.error("processUpdate: Unknown propertyName: {}", propertyName);
        }
    }

    private void processCounter(String id, Object newValue) {
        if (!(newValue instanceof WebCounter)) {
            return;
        }
        WebCounter counter = (WebCounter)newValue;
        WebCounterExport counterExport = new WebCounterExport();
        counterExport.populate(counter);
        addDelta(WEB_OBJ_COUNTER, counterExport);
    }

    private void processViewState(String id, Object newValue) {
        if (!(newValue instanceof WebViewState)) {
            return;
        }
        WebViewState viewState = (WebViewState)newValue;
        WebViewStateExport viewStateExport = new WebViewStateExport();
        viewStateExport.populate(viewState);
        addDelta(WEB_OBJ_VIEW_STATE, viewStateExport);
    }

    protected void processPlayerConfig(String id, Object newValue) {
        WebPlayerConfig config = getDelegateState().getPlayerConfig();
        WebPlayerConfigExport export = new WebPlayerConfigExport();
        export.populate(config);
        addDelta(WEB_OBJ_CONFIG_PLAYER, export);
    }

    protected void processSynthConfig(String id, Object newValue) {
        WebSynthConfig config = getDelegateState().getSynthConfig();
        WebSynthConfigExport export = new WebSynthConfigExport();
        export.populate(config);
        addDelta(WEB_OBJ_CONFIG_SYNTH, export);
    }

    @Override
    public WebAudienceServerState getState() {
        return state;
    }

    public SymphoneaWebAudienceServerState getDelegateState() {
        return state;
    }
}
