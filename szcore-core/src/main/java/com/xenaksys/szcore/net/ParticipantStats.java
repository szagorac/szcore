package com.xenaksys.szcore.net;

public class ParticipantStats {

    private final String id;
    private final String ipAddress;
    private double pingLatency;
    private double oneWayPingLatency;

    public ParticipantStats(String id, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
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
