package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.model.id.BeatId;

public interface SzcoreEvent {

    EventType getEventType();

    BeatId getEventBaseBeat();

    long getCreationTime();

}
