package com.xenaksys.szcore.event.gui;

import java.net.InetAddress;

public class ParticipantStatsEvent extends ClientEvent {

    private final InetAddress inetAddress;
    private final String hostAddress;
    private final int port;
    private final double pingLatencyMillis;
    private final double oneWayPingLatencyMillis;
    private final long lastPingMillis;
    private final boolean isExpired;

    public ParticipantStatsEvent(InetAddress inetAddress,
                                 String hostAddress,
                                 int port,
                                 double pingLatencyMillis,
                                 double oneWayPingLatencyMillis,
                                 boolean isExpired,
                                 long lastPingMillis,
                                 long time) {
        super(time);
        this.inetAddress = inetAddress;
        this.hostAddress = hostAddress;
        this.port = port;
        this.pingLatencyMillis = pingLatencyMillis;
        this.oneWayPingLatencyMillis = oneWayPingLatencyMillis;
        this.isExpired = isExpired;
        this.lastPingMillis = lastPingMillis;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public double getPingLatencyMillis() {
        return pingLatencyMillis;
    }

    public double getOneWayPingLatencyMillis() {
        return oneWayPingLatencyMillis;
    }

    public int getPort() {
        return port;
    }

    public long getLastPingMillis() {
        return lastPingMillis;
    }

    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.PARTICIPANT_STATS;
    }

    @Override
    public String toString() {
        return "ParticipantStatsEvent{" +
                "inetAddress=" + inetAddress +
                ", hostAddress='" + hostAddress + '\'' +
                ", port=" + port +
                ", pingLatencyMillis=" + pingLatencyMillis +
                ", oneWayPingLatencyMillis=" + oneWayPingLatencyMillis +
                ", lastPingMillis=" + lastPingMillis +
                ", isExpired=" + isExpired +
                '}';
    }
}
