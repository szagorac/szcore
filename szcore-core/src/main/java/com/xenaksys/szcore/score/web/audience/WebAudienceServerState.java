package com.xenaksys.szcore.score.web.audience;

import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.List;

import static com.xenaksys.szcore.Consts.*;

public class WebAudienceServerState {
    static final Logger LOG = LoggerFactory.getLogger(WebAudienceServerState.class);

    private final List<WebAudienceAction> actions;
    private final PropertyChangeSupport pcs;

    private final WebTextState instructions;
    private volatile WebGranulatorConfig granulatorConfig;
    private volatile WebSpeechSynthConfig speechSynthConfig;
    private volatile WebSpeechSynthState speechSynthState;

    public WebAudienceServerState(List<WebAudienceAction> currentActions, WebTextState instructions, WebGranulatorConfig granulatorConfig,
                                  WebSpeechSynthConfig speechSynthConfig, WebSpeechSynthState speechSynthState, PropertyChangeSupport pcs) {
        this.actions = currentActions;
        this.instructions = instructions;
        this.granulatorConfig = granulatorConfig;
        this.speechSynthConfig = speechSynthConfig;
        this.speechSynthState = speechSynthState;
        this.pcs = pcs;
    }

    public void resetDelta() {
        clearActions();
    }

    public List<WebAudienceAction> getActions() {
        return actions;
    }

    public void clearActions() {
        actions.clear();
        pcs.firePropertyChange(WEB_OBJ_ACTIONS, WEB_OBJ_ACTIONS, null);
    }

    public void addAction(WebAudienceAction action) {
        if (action == null) {
            return;
        }
        LOG.debug("WebAudienceServerState addAction: {}", action);
        actions.add(action);
        pcs.firePropertyChange(WEB_OBJ_ACTIONS, action.getId(), action);
    }

    public WebTextState getInstructions() {
        return instructions;
    }

    public void setInstructions(String value, int lineNo) {
        if (value == null) {
            value = EMPTY;
        }
        String old = instructions.getLine(lineNo);
        instructions.setLine(value, lineNo);
        if (!value.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_INSTRUCTIONS, instructions.getId(), instructions);
        }
    }

    public void setInstructionsVisible(boolean isVisible) {
        boolean old = this.instructions.isVisible();
        instructions.setVisible(isVisible);
        if (old != isVisible) {
            pcs.firePropertyChange(WEB_OBJ_INSTRUCTIONS, instructions.getId(), instructions);
        }
    }

    public void setInstructionsColour(String colour) {
        String old = instructions.getColour();
        instructions.setColour(colour);
        if (!colour.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_INSTRUCTIONS, instructions.getId(), instructions);
        }
    }

    public WebGranulatorConfig getGranulatorConfig() {
        return granulatorConfig;
    }

    public WebSpeechSynthConfig getSpeechSynthConfig() {
        return speechSynthConfig;
    }

    public WebSpeechSynthState getSpeechSynthState() {
        return speechSynthState;
    }

    public void setGranulatorConfig(WebGranulatorConfig granulatorConfig) {
        this.granulatorConfig = granulatorConfig;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_OBJ_CONFIG_GRANULATOR, granulatorConfig);
    }

    public void setSpeechSynthConfig(WebSpeechSynthConfig speechSynthConfig) {
        this.speechSynthConfig = speechSynthConfig;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_SPEECH_SYNTH, WEB_OBJ_CONFIG_SPEECH_SYNTH, speechSynthConfig);
    }

    public void setSpeechSynthState(WebSpeechSynthState speechSynthState) {
        this.speechSynthState = speechSynthState;
        pcs.firePropertyChange(WEB_OBJ_STATE_SPEECH_SYNTH, WEB_OBJ_STATE_SPEECH_SYNTH, speechSynthState);
    }

    public PropertyChangeSupport getPcs() {
        return pcs;
    }
}
