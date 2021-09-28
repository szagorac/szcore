package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.util.TimeUtil;

import java.net.InetAddress;

public class ParticipantEvent extends ClientEvent {

    private final String clientId;
    private final InetAddress inetAddress;
    private final String hostAddress;
    private final int portIn;
    private final int portOut;
    private final int portErr;
    private final int ping;
    private final String instrument;
    private final boolean isReady;
    private final boolean isBanned;

    public ParticipantEvent(String clientId, InetAddress inetAddress, String hostAddress, int portIn, int portOut, int portErr, int ping, String instrument, boolean isReady, boolean isBanned, long time) {
        super(time);
        this.clientId = clientId;
        this.inetAddress = inetAddress;
        this.hostAddress = hostAddress;
        this.portIn = portIn;
        this.portOut = portOut;
        this.portErr = portErr;
        this.ping = ping;
        this.instrument = instrument;
        this.isReady = isReady;
        this.isBanned = isBanned;
    }

    public String getClientId() {
        return clientId;
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

    public boolean isReady() {
        return isReady;
    }

    public boolean isBanned() {
        return isBanned;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.PARTICIPANT;
    }

    @Override
    public String toString() {
        return "ParticipantEvent{" +
                " time='" + TimeUtil.formatTime(getCreationTime()) + '\'' +
                ", clientId=" + clientId +
                ", inetAddress=" + inetAddress +
                ", hostAddress='" + hostAddress + '\'' +
                ", portIn=" + portIn +
                ", portOut=" + portOut +
                ", portErr=" + portErr +
                ", ping=" + ping +
                ", instrument='" + instrument + '\'' +
                ", isReady='" + isReady + '\'' +
                ", isBanned='" + isBanned + '\'' +
                '}';
    }

}
