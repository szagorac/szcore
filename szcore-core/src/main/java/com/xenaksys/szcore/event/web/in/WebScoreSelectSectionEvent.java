package com.xenaksys.szcore.event.web.in;

import com.xenaksys.szcore.web.WebClientInfo;

public class WebScoreSelectSectionEvent extends WebScoreInEvent {
    private final String section;
    public WebScoreSelectSectionEvent(String clientId, String eventId, String sourceAddr, String section, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime, WebClientInfo webClientInfo) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime, webClientInfo);
        this.section = section;
    }

    public String getSection() {
        return section;
    }


    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.SELECT_SECTION;
    }

    @Override
    public String toString() {
        return "WebScoreSelectSectionEvent{" +
                "section=" + section +
                '}';
    }
}
