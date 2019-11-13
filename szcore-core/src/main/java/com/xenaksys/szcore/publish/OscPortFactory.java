package com.xenaksys.szcore.publish;


import com.xenaksys.szcore.net.osc.OSCPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class OscPortFactory {
    static final Logger LOG = LoggerFactory.getLogger(OscPortFactory.class);

    public static OSCPortOut createOutPort(String ip, int outPort) {
        OSCPortOut port = null;
        try {
            InetAddress address = InetAddress.getByName(ip);
            port = new OSCPortOut(address, outPort);
        } catch (Exception e) {
            LOG.error("Failed to create port", e);
        }
        return port;
    }

    public static OSCPortOut createOutPort(InetAddress address, int outPort) {
        OSCPortOut port = null;
        try {
            port = new OSCPortOut(address, outPort);
        } catch (Exception e) {
            LOG.error("Failed to create port", e);
        }
        return port;
    }

    public static OSCPortOut createLocalOutPort(int outPort) {
        OSCPortOut port = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            port = new OSCPortOut(address, outPort);
        } catch (Exception e) {
            LOG.error("Failed to create local port", e);
        }
        return port;
    }
}
