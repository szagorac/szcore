package com.xenaksys.szcore.event;


public class WebStartEvent extends IncomingWebEvent {

    public WebStartEvent(String sourceAddr, String requestPath, String eventId,
                         long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public IncomingWebEventType getWebEventType() {
        return IncomingWebEventType.WEB_START;
    }

}
