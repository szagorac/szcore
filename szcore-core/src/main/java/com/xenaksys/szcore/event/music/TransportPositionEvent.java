package com.xenaksys.szcore.event.music;


import com.xenaksys.szcore.model.Id;

public class TransportPositionEvent extends TransportEvent {

    private final int transportBeatNo;
    private final int startBaseBeatNo;
    private final int tickNo;
    private final long positionMillis;

    public TransportPositionEvent(Id transportId, int startBaseBeatNo, int transportBeatNo, int tickNo, long positionMillis, long creationTime) {
        super(null, transportId, creationTime);
        this.transportBeatNo = transportBeatNo;
        this.tickNo = tickNo;
        this.positionMillis = positionMillis;
        this.startBaseBeatNo = startBaseBeatNo;
    }

    public int getTransportBeatNo() {
        return transportBeatNo;
    }

    public int getTickNo() {
        return tickNo;
    }

    public long getPositionMillis() {
        return positionMillis;
    }

    public int getStartBaseBeatNo() {
        return startBaseBeatNo;
    }

    @Override
    public MusicEventType getMusicEventType() {
        return MusicEventType.TRANSPORT_POSITION;
    }

    @Override
    public String toString() {
        return "TransportPositionEvent{" +
                "transportBeatNo=" + transportBeatNo +
                ", startBaseBeatNo=" + startBaseBeatNo +
                ", tickNo=" + tickNo +
                ", positionMillis=" + positionMillis +
                '}';
    }
}
