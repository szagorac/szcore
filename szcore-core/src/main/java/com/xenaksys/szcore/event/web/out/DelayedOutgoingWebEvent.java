package com.xenaksys.szcore.event.web.out;


import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

import static com.xenaksys.szcore.event.EventType.DELAYED_WEB_OUT;

public class DelayedOutgoingWebEvent implements SzcoreEvent {

    private final OutgoingWebEvent outgoingWebEvent;
    private final long creationTime;

    public DelayedOutgoingWebEvent(OutgoingWebEvent outgoingWebEvent, long creationTime) {
        this.outgoingWebEvent = outgoingWebEvent;
        this.creationTime = creationTime;
    }

    public OutgoingWebEvent getOutgoingWebEvent() {
        return outgoingWebEvent;
    }

    @Override
    public EventType getEventType() {
        return DELAYED_WEB_OUT;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return outgoingWebEvent.getEventBaseBeat();
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "DelayedOutgoingWebEvent{" +
                "outgoingWebEvent=" + outgoingWebEvent +
                ", creationTime=" + creationTime +
                '}';
    }
}
