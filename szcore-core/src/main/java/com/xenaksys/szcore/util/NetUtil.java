package com.xenaksys.szcore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NetUtil {
    static final Logger LOG = LoggerFactory.getLogger(NetUtil.class);

    public static final long MEGABYTE = 1024L * 1024L;

    public static InetAddress getHostAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }
            List<InterfaceAddress> addrs = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress ia : addrs) {
                InetAddress inetAddress = ia.getAddress();
                if(inetAddress instanceof Inet4Address) {
                    return inetAddress;
                }
            }
        }
        return null;
    }

    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastAddrs = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }
            networkInterface.getInterfaceAddresses().stream()
                    .map(InterfaceAddress::getBroadcast)
                    .filter(Objects::nonNull)
                    .forEach(broadcastAddrs::add);
        }
        return broadcastAddrs;
    }

    public static List<InetAddress> getConnectedClients() throws Exception {
        List<InetAddress> connectedClients = new ArrayList<>();
        int timeout = 500;
        String currentIP = InetAddress.getLocalHost().toString();
        String subnet = getSubnet(currentIP);
        LOG.info("subnet: " + subnet);

        for (int i = 1; i < 254; i++) {
            String host = subnet + i;
            LOG.info("Checking :" + host);
            InetAddress client = InetAddress.getByName(host);
            if (client.isReachable(timeout)) {
                LOG.info(host + " is reachable");
                connectedClients.add(client);
            }
        }

        return connectedClients;
    }

    public static String getSubnet(String currentIP) {
        int firstSeparator = currentIP.lastIndexOf("/");
        int lastSeparator = currentIP.lastIndexOf(".");
        return currentIP.substring(firstSeparator + 1, lastSeparator + 1);
    }

    public static List<NetworkDevice> discoverConnectedDevices() throws Exception {
        byte[] localHostIp = InetAddress.getLocalHost().getAddress();
        List<NetworkDevice> networkDevices = new ArrayList<>();
        for (int i = 1; i < 255; i++) {
            // Assuming IPV4
            localHostIp[3] = (byte) i;
            networkDevices.add(new NetworkDevice(
                    InetAddress.getByAddress(localHostIp).getHostAddress()));
        }

        return parallelDiscover(networkDevices);
    }

    public static List<NetworkDevice> parallelDiscover(List<NetworkDevice> networkDevices) {
        long start = System.currentTimeMillis();

        List<NetworkDevice> discoveredDevices = networkDevices
                .parallelStream()
                .filter(NetworkDevice::Discover)
                .collect(Collectors.toList());
        long end = System.currentTimeMillis();
        LOG.info("Parallel network node discover elapsed: " + (end - start));
        return discoveredDevices;
    }

    public static class NetworkDevice {
        private String hostIp;
        private String hostName;
        private InetAddress addr;

        public NetworkDevice(String hostIp) {
            this.hostIp = hostIp;
        }

        public boolean Discover() {
            try {
                addr = InetAddress.getByName(hostIp);
                LOG.info("Parallel network node discover: " + hostIp);
                if (addr.isReachable(500)) {
                    hostName = addr.getHostName();
                    return true;
                }
            } catch (IOException ioe) {
            }
            return false;
        }

        public InetAddress getAddr() {
            return addr;
        }

        public String getHostIp() {
            return hostIp;
        }

        public String getHostName() {
            return hostName;
        }

        @Override
        public String toString() {
            return String.format("IP: %s \t Name: %s", hostIp, hostName);
        }
    }


    public static double bytesToMbyte(long bytes) {
        return 1.0 * bytes / MEGABYTE;
    }
}
