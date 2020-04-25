package com.xenaksys.szcore.web;

import java.util.Objects;

public class WebConnection {
    private final String clientAddr;
    private final WebConnectionType connectionType;

    public WebConnection(String clientAddr, WebConnectionType connectionType) {
        this.clientAddr = clientAddr;
        this.connectionType = connectionType;
    }

    public String getClientAddr() {
        return clientAddr;
    }

    public WebConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebConnection that = (WebConnection) o;
        return clientAddr.equals(that.clientAddr) &&
                connectionType == that.connectionType;
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
                '}';
    }
}
