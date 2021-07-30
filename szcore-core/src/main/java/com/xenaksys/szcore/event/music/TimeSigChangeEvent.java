package com.xenaksys.szcore.event.music;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.id.BeatId;

public class TimeSigChangeEvent extends TransportEvent {

    private final TimeSignature timeSignature;

    public TimeSigChangeEvent(TimeSignature timeSignature,
                              BeatId changeOnBaseBeat,
                              Id transportId,
                              long creationTime) {
        super(changeOnBaseBeat, transportId, creationTime);
        this.timeSignature = timeSignature;
    }

    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.TIMESIG_CHANGE;
    }

    @Override
    public String toString() {
        return "TimeSigChangeEvent{" +
                "beatId=" + (getEventBaseBeat() != null?getEventBaseBeat().getBaseBeatNo():"") +
                ", timeSignature=" + timeSignature +
                ", transportId=" + getTransportId() +
                '}';
    }
}
