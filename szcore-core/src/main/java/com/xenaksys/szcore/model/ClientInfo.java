package com.xenaksys.szcore.model;

import java.net.InetAddress;

public class ClientInfo {
    private final InetAddress addr;
    private final int port;
    private final String host;
    private final String id;
    private String instrument;


    public ClientInfo(String id, InetAddress addr, int port) {
        this.id = id;
        this.addr = addr;
        this.port = port;
        this.host = addr.getHostAddress();
    }

    public String getId() {
        return id;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "id='" + id + '\'' +
                ", port=" + port +
                ", host='" + host + '\'' +
                ", instrument='" + instrument + '\'' +
                '}';
    }
}

