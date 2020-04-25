package com.xenaksys.szcore.event;


public class WebPollEvent extends IncomingWebEvent {
    public WebPollEvent(String eventId, String sourceAddr, String requestPath, long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public IncomingWebEventType getWebEventType() {
        return IncomingWebEventType.POLL;
    }

}
