package com.xenaksys.szcore.score.web.audience.delegate;

import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebTextState;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.List;

public class DialogsWebAudienceServerState extends WebAudienceServerState {
    static final Logger LOG = LoggerFactory.getLogger(DialogsWebAudienceServerState.class);

    public DialogsWebAudienceServerState(List<WebAudienceAction> currentActions, WebTextState instructions,
                                         WebGranulatorConfig granulatorConfig, WebSpeechSynthConfig speechSynthConfig,
                                         WebSpeechSynthState speechSynthState, PropertyChangeSupport pcs) {
        super(currentActions, instructions, granulatorConfig, speechSynthConfig, speechSynthState, pcs);
    }

}
