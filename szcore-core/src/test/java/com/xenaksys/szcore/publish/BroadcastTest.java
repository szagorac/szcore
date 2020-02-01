package com.xenaksys.szcore.publish;

import com.xenaksys.szcore.util.NetUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class BroadcastTest {
    static final Logger LOG = LoggerFactory.getLogger(BroadcastTest.class);

    @Test
    public void testBroadcastAddr(){
        List<InetAddress> broadcastAddrs = new ArrayList<>();
        try {
            broadcastAddrs = NetUtil.listAllBroadcastAddresses();
        } catch (SocketException e) {
            LOG.error("Failed to retrieve broadcast addresses", e);
        }

        Assert.assertFalse(broadcastAddrs.isEmpty());
        LOG.info("Retrieved broadcast addresses:");
        for(InetAddress broadcastAddr : broadcastAddrs) {
            LOG.info(broadcastAddr.getHostAddress());
        }
    }

    @Ignore
    @Test
    public void testConnectedNetworkClients(){
        List<InetAddress> connectedClients = new ArrayList<>();

        try {
            connectedClients = NetUtil.getConnectedClients();
        } catch (Exception e) {
            LOG.error("Failed to retrieve connected clients", e);
        }

        Assert.assertFalse(connectedClients.isEmpty());
        LOG.info("Retrieved connected clients:");
        for(InetAddress broadcastAddr : connectedClients) {
            LOG.info(broadcastAddr.getHostAddress());
        }
    }

    @Test
    public void testParallelConnectedNetworkClients(){
        List<NetUtil.NetworkDevice> connectedClients = new ArrayList<>();

        try {
            connectedClients = NetUtil.discoverConnectedDevices();
        } catch (Exception e) {
            LOG.error("Failed to retrieve connected clients", e);
        }

        Assert.assertFalse(connectedClients.isEmpty());
        LOG.info("Retrieved connected clients:");
        for(NetUtil.NetworkDevice connectedClient : connectedClients) {
            LOG.info("Found: {}",connectedClient);
        }
    }


}
