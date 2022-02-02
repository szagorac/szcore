package com.xenaksys.szcore.event.osc;


import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEventType;

public class VoteAudienceEvent extends IncomingWebAudienceEvent {
    private final String value;

    public VoteAudienceEvent(String value, String sourceAddr, String requestPath, String eventId,
                             long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.VOTE;
    }

}
