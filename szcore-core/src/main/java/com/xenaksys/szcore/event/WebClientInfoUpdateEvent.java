package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.HistoBucketView;
import com.xenaksys.szcore.web.WebClientInfo;

import java.util.ArrayList;
import java.util.List;

public class WebClientInfoUpdateEvent extends ClientEvent {

    private final ArrayList<WebClientInfo> webClientInfos;
    private final List<HistoBucketView> histoBucketViews;
    private final int totalWebHits;

    public WebClientInfoUpdateEvent(ArrayList<WebClientInfo> webClientInfos, List<HistoBucketView> histoBucketViews, int totalWebHits, long time) {
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
        return ClientEventType.WEB_CLIENT_INFOS;
    }

}
