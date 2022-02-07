package com.xenaksys.szcore.event.osc;


import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEventType;

public class VoteAudienceEvent extends IncomingWebAudienceEvent {
    private final String value;
    private final int usersNo;

    public VoteAudienceEvent(String value, int usersNo, String sourceAddr, String requestPath, String eventId,
                             long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.value = value;
        this.usersNo = usersNo;
    }

    public String getValue() {
        return value;
    }

    public int getUsersNo() {
        return usersNo;
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.VOTE;
    }

    @Override
    public String toString() {
        return "VoteAudienceEvent{" +
                "value='" + value + '\'' +
                ", usersNo=" + usersNo +
                '}';
    }
}
