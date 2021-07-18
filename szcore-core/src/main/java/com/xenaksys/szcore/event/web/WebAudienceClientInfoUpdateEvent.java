package com.xenaksys.szcore.event.web;

import com.xenaksys.szcore.event.gui.ClientEvent;
import com.xenaksys.szcore.event.gui.ClientEventType;
import com.xenaksys.szcore.model.HistoBucketView;
import com.xenaksys.szcore.web.WebClientInfo;

import java.util.ArrayList;
import java.util.List;

public class WebAudienceClientInfoUpdateEvent extends ClientEvent {

    private final ArrayList<WebClientInfo> webClientInfos;
    private final List<HistoBucketView> histoBucketViews;
    private final int totalWebHits;

    public WebAudienceClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, List<HistoBucketView> histoBucketViews, int totalWebHits, long time) {
        super(time);
        this.webClientInfos = webClientInfos;
        this.histoBucketViews = histoBucketViews;
        this.totalWebHits = totalWebHits;
    }

    public ArrayList<WebClientInfo> getWebClientInfos() {
        return webClientInfos;
    }

    public List<HistoBucketView> getHistoBucketViews() {
        return histoBucketViews;
    }

    public int getTotalWebHits() {
        return totalWebHits;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.WEB_AUDIENCE_CLIENT_INFOS;
    }

}
