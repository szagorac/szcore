package com.xenaksys.szcore.score.delegate.web.dialogs;

import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebAudienceStateDeltaTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xenaksys.szcore.Consts.*;

public class DialogsWebAudienceStateDeltaTracker extends WebAudienceStateDeltaTracker {
    static final Logger LOG = LoggerFactory.getLogger(DialogsWebAudienceStateDeltaTracker.class);

    private final DialogsWebAudienceServerState state;

    public DialogsWebAudienceStateDeltaTracker(DialogsWebAudienceServerState state) {
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
            default:
                LOG.error("processUpdate: Unknown propertyName: {}", propertyName);
        }
    }

    @Override
    public WebAudienceServerState getState() {
        return state;
    }

    public DialogsWebAudienceServerState getDelegateState() {
        return state;
    }
}