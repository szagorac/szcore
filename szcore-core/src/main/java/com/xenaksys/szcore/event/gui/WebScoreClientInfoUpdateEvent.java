package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.web.WebClientInfo;

import java.util.ArrayList;

public class WebScoreClientInfoUpdateEvent extends ClientEvent {

    private final ArrayList<WebClientInfo> webClientInfos;
    private final boolean isFullUpdate;

    public WebScoreClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, boolean isFullUpdate, long time) {
        super(time);
        this.webClientInfos = webClientInfos;
        this.isFullUpdate = isFullUpdate;
    }

    public ArrayList<WebClientInfo> getWebClientInfos() {
        return webClientInfos;
    }

    public boolean isFullUpdate() {
        return isFullUpdate;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.WEB_SCORE_CLIENT_INFOS;
    }

}
