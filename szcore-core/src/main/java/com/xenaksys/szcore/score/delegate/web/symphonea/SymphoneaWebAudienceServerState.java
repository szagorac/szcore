package com.xenaksys.szcore.score.delegate.web.symphonea;

import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebCounter;
import com.xenaksys.szcore.score.web.audience.WebTextState;
import com.xenaksys.szcore.score.web.audience.WebViewState;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebPlayerConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.config.WebSynthConfig;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.List;

import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_PLAYER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VIEW_STATE;

public class SymphoneaWebAudienceServerState extends WebAudienceServerState {
    static final Logger LOG = LoggerFactory.getLogger(SymphoneaWebAudienceServerState.class);

    private final WebCounter counter;
    private volatile WebPlayerConfig playerConfig;
    private volatile WebSynthConfig synthConfig;
    private volatile WebViewState viewState;

    public SymphoneaWebAudienceServerState(List<WebAudienceAction> currentActions, WebTextState instructions,
                                           WebGranulatorConfig granulatorConfig, WebSpeechSynthConfig speechSynthConfig,
                                           WebSpeechSynthState speechSynthState, WebPlayerConfig playerConfig, WebSynthConfig synthConfig,
                                           WebCounter counter, WebViewState viewState, PropertyChangeSupport pcs) {
        super(currentActions, instructions, granulatorConfig, speechSynthConfig, speechSynthState, pcs);
        this.counter = counter;
        this.playerConfig = playerConfig;
        this.synthConfig = synthConfig;
        this.viewState = viewState;
    }

    public WebCounter getCounter() {
        return counter;
    }

    public WebPlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public WebSynthConfig getSynthConfig() {
        return synthConfig;
    }

    public void setPlayerConfig(WebPlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
        getPcs().firePropertyChange(WEB_OBJ_CONFIG_PLAYER, WEB_OBJ_CONFIG_PLAYER, playerConfig);
    }

    public void setSynthConfig(WebSynthConfig synthConfig) {
        this.synthConfig = synthConfig;
        getPcs().firePropertyChange(WEB_OBJ_CONFIG_SYNTH, WEB_OBJ_CONFIG_SYNTH, synthConfig);
    }

    public void onPlayerConfigUpdate() {
        getPcs().firePropertyChange(WEB_OBJ_CONFIG_PLAYER, WEB_OBJ_CONFIG_PLAYER, playerConfig);
    }

    public WebViewState getViewState() {
        return viewState;
    }

    public void setViewState(WebViewState viewState) {
        this.viewState = viewState;
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_OBJ_VIEW_STATE, viewState);
    }

    public void onViewStateUpdate() {
        getPcs().firePropertyChange(WEB_OBJ_VIEW_STATE, WEB_OBJ_VIEW_STATE, viewState);
    }
}
