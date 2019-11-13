package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class TempoChangeEvent extends TransportEvent {

    private final Tempo tempo;
    private final List<OscStaveTempoEvent> oscEvents;

    public TempoChangeEvent(Tempo tempo, BeatId changeOnBaseBeat, Id transportId, List<OscStaveTempoEvent> oscEvents, long creationTime) {
        super(changeOnBaseBeat, transportId, creationTime);
        this.tempo = tempo;
        this.oscEvents = oscEvents;
    }

    public Tempo getTempo() {
        return tempo;
    }

    public List<OscStaveTempoEvent> getOscEvents() {
        return oscEvents;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.TEMPO_CHANGE;
    }

    @Override
    public String toString() {
        ;
        return "TempoChangeEvent{" +
                "beatId=" + (getEventBaseBeat() != null?getEventBaseBeat().getBaseBeatNo():"")+
                ", tempo=" + tempo +
                ", transportId=" + getTransportId() +
                ", oscEvents=" + oscEvents.size() +
                '}';
    }
}
