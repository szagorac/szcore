package com.xenaksys.szcore.event;

import com.xenaksys.szcore.util.TimeUtil;

import java.net.InetAddress;

public class ParticipantEvent extends ClientEvent{

    private final InetAddress inetAddress;
    private final String hostAddress;
    private final int portIn;
    private final int portOut;
    private final int portErr;
    private final int ping;
    private final String instrument;

    public ParticipantEvent(InetAddress inetAddress, String hostAddress, int portIn, int portOut, int portErr, int ping, String instrument, long time) {
        super(time);
        this.inetAddress = inetAddress;
        this.hostAddress = hostAddress;
        this.portIn = portIn;
        this.portOut = portOut;
        this.portErr = portErr;
        this.ping = ping;
        this.instrument = instrument;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getPortIn() {
        return portIn;
    }

    public int getPortOut() {
        return portOut;
    }

    public int getPortErr() {
        return portErr;
    }

    public int getPing() {
        return ping;
    }

    public String getInstrument() {
        return instrument;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.PARTICIPANT;
    }

    @Override
    public String toString() {
        return "ParticipantEvent{" +
                " time='" + TimeUtil.formatTime(getCreationTime()) + '\'' +
                ", inetAddress=" + inetAddress +
                ", hostAddress='" + hostAddress + '\'' +
                ", portIn=" + portIn +
                ", portOut=" + portOut +
                ", portErr=" + portErr +
                ", ping=" + ping +
                ", instrument='" + instrument + '\'' +
                '}';
    }

}
