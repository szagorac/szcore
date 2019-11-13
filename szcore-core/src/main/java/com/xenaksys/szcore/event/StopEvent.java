package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.id.BeatId;

public class StopEvent extends MusicEvent {

    private final Id transportId;

    public StopEvent(BeatId lastBeat, Id transportId, long creationTime) {
        super(lastBeat, creationTime);
        this.transportId = transportId;
    }

    public Id getTransportId() {
        return transportId;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.STOP;
    }

    @Override
    public String toString() {
        return "StopEvent{" +
                "beatId=" + getEventBaseBeat().getBeatNo() +
                ", transportId=" + transportId +
                '}';
    }
}
