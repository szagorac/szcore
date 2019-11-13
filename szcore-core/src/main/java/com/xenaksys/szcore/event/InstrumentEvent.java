package com.xenaksys.szcore.event;

import com.xenaksys.szcore.util.TimeUtil;

import java.net.InetAddress;

public class InstrumentEvent extends ClientEvent{

    private final InetAddress inetAddress;
    private final String hostAddress;
    private final String instrument;

    public InstrumentEvent(InetAddress inetAddress, String instrument, long time) {
        super(time);
        this.inetAddress = inetAddress;
        this.hostAddress = inetAddress.getHostAddress();
        this.instrument = instrument;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getInstrument() {
        return instrument;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.INSTRUMENT;
    }

    @Override
    public String toString() {
        return "ParticipantEvent{" +
                " time='" + TimeUtil.formatTime(getCreationTime()) + '\'' +
                ", inetAddress=" + inetAddress +
                ", hostAddress='" + hostAddress + '\'' +
                ", instrument='" + instrument + '\'' +
                '}';
    }

}
