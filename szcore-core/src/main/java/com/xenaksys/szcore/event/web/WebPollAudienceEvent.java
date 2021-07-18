package com.xenaksys.szcore.event.web;


public class WebPollAudienceEvent extends IncomingWebAudienceEvent {
    public WebPollAudienceEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.POLL;
    }

}
