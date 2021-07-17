package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.WebClientInfo;

import java.util.ArrayList;

public class WebScoreClientInfoUpdateEvent extends ClientEvent {

    private final ArrayList<WebClientInfo> webClientInfos;

    public WebScoreClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, long time) {
        super(time);
        this.webClientInfos = webClientInfos;
    }

    public ArrayList<WebClientInfo> getWebClientInfos() {
        return webClientInfos;
    }


    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.WEB_SCORE_CLIENT_INFOS;
    }

}
