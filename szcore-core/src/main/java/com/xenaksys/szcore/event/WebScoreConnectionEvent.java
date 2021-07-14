package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.WebClientInfo;

public class WebScoreConnectionEvent extends WebScoreInEvent {
    private final WebClientInfo webClientInfo;

    public WebScoreConnectionEvent(WebClientInfo webClientInfo, long creationTime) {
        super(WebScoreInEventType.CONNECTION.toString(), webClientInfo.getClientAddr(), webClientInfo.getConnectionType().toString(), creationTime, creationTime, creationTime);
        this.webClientInfo = webClientInfo;
    }

    public WebClientInfo getWebClientInfo() {
        return webClientInfo;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.CONNECTION;
    }

    @Override
    public String toString() {
        return "WebScoreConnectionEvent{" +
                "webClientInfo=" + webClientInfo +
                '}';
    }
}
