package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.WebClientInfo;

import java.util.ArrayList;

public class WebClientInfoUpdateEvent extends ClientEvent {

    private final ArrayList<WebClientInfo> webClientInfos;

    public WebClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, long time) {
        super(time);
        this.webClientInfos = webClientInfos;
    }

    public ArrayList<WebClientInfo> getWebClientInfos() {
        return webClientInfos;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.WEB_CLIENT_INFOS;
    }

}
