package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.Id;

public class PrecountBeatSetupEvent extends MusicEvent {

    private final boolean isPrecount;
    private final int precountBeatNo;
    private final Id transportId;
    private final long precountTimeMillis;
    private final long initBeaterInterval;

    public PrecountBeatSetupEvent(boolean isPrecount, int precountBeatNo, long precountTimeMillis, long initBeaterInterval,
                                  Id transportId, long creationTime) {
        super(null, creationTime);
        this.isPrecount = isPrecount;
        this.precountBeatNo = precountBeatNo;
        this.transportId = transportId;
        this.precountTimeMillis = precountTimeMillis;
        this.initBeaterInterval = initBeaterInterval;
    }

    public boolean isPrecount() {
        return isPrecount;
    }

    public int getPrecountBeatNo() {
        return precountBeatNo;
    }

    public Id getInstrumentId() {
        return transportId;
    }

    public long getPrecountTimeMillis() {
        return precountTimeMillis;
    }

    public long getInitBeaterInterval() {
        return initBeaterInterval;
    }

    public MusicEventType getMusicEventType() {
        return MusicEventType.PRECOUNT_BEAT_SETUP;
    }

    @Override
    public String toString() {
        return "PrecountBeatSetupEvent{" +
                "isPrecount=" + isPrecount +
                ", precountBeatNo=" + precountBeatNo +
                ", transportId=" + transportId +
                ", precountTimeMillis=" + precountTimeMillis +
                ", initBeaterInterval=" + initBeaterInterval +
                '}';
    }
}
