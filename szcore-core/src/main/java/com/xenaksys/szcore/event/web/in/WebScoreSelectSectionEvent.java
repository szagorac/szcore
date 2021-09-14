package com.xenaksys.szcore.event.web.in;

public class WebScoreSelectSectionEvent extends WebScoreInEvent {
    private final String section;

    public WebScoreSelectSectionEvent(String clientId, String eventId, String sourceAddr, String section, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(clientId, eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
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
