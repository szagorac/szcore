package com.xenaksys.szcore.event;

import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

public class ZScoreEvent implements SzcoreEvent {

    @Override
    public EventType getEventType() {
        return EventType.UNKNOWN;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return null;
    }

    @Override
    public long getCreationTime() {
        return 0L;
    }


}
