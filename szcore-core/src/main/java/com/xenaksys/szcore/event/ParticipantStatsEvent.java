package com.xenaksys.szcore.event;

import java.net.InetAddress;

public class ParticipantStatsEvent extends ClientEvent{

    private final InetAddress inetAddress;
    private final String hostAddress;
    private final double pingLatencyMillis;
    private final double oneWayPingLatencyMillis;

    public ParticipantStatsEvent(InetAddress inetAddress, String hostAddress, double pingLatencyMillis, double oneWayPingLatencyMillis, long time) {
        super(time);
        this.inetAddress = inetAddress;
        this.hostAddress = hostAddress;
        this.pingLatencyMillis = pingLatencyMillis;
        this.oneWayPingLatencyMillis = oneWayPingLatencyMillis;
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

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.PARTICIPANT_STATS;
    }

    @Override
    public String toString() {
        return "ParticipantStatsEvent{" +
                "inetAddress=" + inetAddress +
                ", hostAddress='" + hostAddress + '\'' +
                ", pingLatencyMillis=" + pingLatencyMillis +
                ", oneWayPingLatencyMillis=" + oneWayPingLatencyMillis +
                '}';
    }
}
