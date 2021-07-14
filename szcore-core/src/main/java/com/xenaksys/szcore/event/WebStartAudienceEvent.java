package com.xenaksys.szcore.event;


public class WebStartAudienceEvent extends IncomingWebAudienceEvent {

    public WebStartAudienceEvent(String sourceAddr, String requestPath, String eventId,
                                 long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.WEB_START;
    }

}
