package com.xenaksys.szcore.web;

import com.xenaksys.szcore.util.NetUtil;

import java.util.Objects;

public class WebConnection {
    private final String clientAddr;
    private WebConnectionType connectionType;

    private final String host;
    private final int port;
    private String userAgent;
    private String instrument;
    private boolean isScoreClient;
    private boolean isOpen;

    public WebConnection(String clientAddr, WebConnectionType connectionType, boolean isOpen) {
        this.clientAddr = clientAddr;
        this.connectionType = connectionType;
        String[] hostPort = NetUtil.getHostPort(clientAddr);
        String h = null;
        int p = 0;
        if (hostPort != null) {
            h = hostPort[0];
            p = Integer.parseInt(hostPort[1]);
        }
        this.host = h;
        this.port = p;
        this.isOpen = isOpen;
    }

    public String getClientAddr() {
        return clientAddr;
    }

    public WebConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(WebConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public boolean isScoreClient() {
        return isScoreClient;
    }

    public void setScoreClient(boolean scoreClient) {
        this.isScoreClient = scoreClient;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebConnection that = (WebConnection) o;
        return clientAddr.equals(that.clientAddr) && connectionType == that.connectionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientAddr, connectionType);
    }

    @Override
    public String toString() {
        return "WebConnection{" +
                "clientAddr='" + clientAddr + '\'' +
                ", connectionType=" + connectionType +
                ", isOpen='" + isOpen + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", userAgent=" + userAgent +
                ", instrument=" + instrument +
                ", isScoreClient=" + isScoreClient +
                '}';
    }
}
