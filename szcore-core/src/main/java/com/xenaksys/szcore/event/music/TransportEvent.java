package com.xenaksys.szcore.event.music;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.id.BeatId;

abstract public class TransportEvent extends MusicEvent {
    private final Id transportId;

    public TransportEvent(BeatId changeOnBaseBeat, Id transportId, long creationTime) {
        super(changeOnBaseBeat, creationTime);
        this.transportId = transportId;
    }

    public Id getTransportId() {
        return transportId;
    }
}
