package com.xenaksys.szcore.net;

public class ParticipantStats {

    private final String ipAddress;
    private double pingLatency;
    private double oneWayPingLatency;

    public ParticipantStats(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public double getPingLatency() {
        return pingLatency;
    }

    public void setPingLatency(double pingLatency) {
        this.pingLatency = pingLatency;
    }

    public double getOneWayPingLatency() {
        return oneWayPingLatency;
    }

    public void setOneWayPingLatency(double oneWayPingLatency) {
        this.oneWayPingLatency = oneWayPingLatency;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
