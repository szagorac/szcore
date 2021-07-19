package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.net.osc.OSCMessage;
import com.xenaksys.szcore.util.TimeUtil;

import java.net.InetAddress;
import java.util.Arrays;


public class IncomingOscEvent extends OscEvent {
    private int port;
    private int localPort;
    private InetAddress inetAddress;

    public IncomingOscEvent() {
    }

    public IncomingOscEvent(OSCMessage message, long time) {
        super(message.getAddress(), message.getArguments(), time);
        this.port = message.getPort();
        this.localPort = message.getLocalPort();
        this.inetAddress = message.getInetAddress();
    }

    public int getPort() {
        return port;
    }

    public int getLocalPort() {
        return localPort;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public String toString() {
        return "IncomingOscEvent {" +
                " time=" + TimeUtil.formatTime(getCreationTime()) +
                ", address='" + getAddress() + '\'' +
                ", arguments=" + Arrays.toString(getArguments().toArray()) +
                ", destination='" + getDestination() + '\'' +
                ", eventType='" + getEventType() + '\'' +
                ", oscEventType='" + getOscEventType() + '\'' +
                ", port=" + getPort() +
                ", localPort=" + getLocalPort() +
                ", inetAddress=" + getInetAddress() +
                ", eventBaseBeat=" + getEventBaseBeat() +
                '}';
    }
}


