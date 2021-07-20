package com.xenaksys.szcore.util;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xenaksys.szcore.Consts.COLON;
import static com.xenaksys.szcore.Consts.EMPTY;

public class NetUtil {
    static final Logger LOG = LoggerFactory.getLogger(NetUtil.class);


    public static String createClientId(InetAddress addr, int port) {
        String a = EMPTY;
        if (addr != null) {
            a = addr.getHostAddress();
        }
        return a + COLON + port;
    }

    public static String createClientId(String addr, int port) {
        String a = EMPTY;
        if (addr != null) {
            a = addr;
        }
        return a + COLON + port;
    }

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
                if (inetAddress instanceof Inet4Address) {
                    return inetAddress;
                } else if (inetAddress instanceof Inet6Address){
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

    public static String[] getHostPort(String inetAddr) {
        if (inetAddr == null || !inetAddr.contains(Consts.COLON)) {
            return null;
        }

        long columnCount = inetAddr.chars().filter(ch -> ch == COLON.charAt(0)).count();
        if (columnCount < 1) {
            return null;
        }

        int lastColumn = inetAddr.lastIndexOf(COLON.charAt(0));
        String port = "-1";
        if (lastColumn < inetAddr.length()) {
            port = inetAddr.substring(lastColumn + 1);
        }
        String host = inetAddr.substring(0, lastColumn);

        if (!ParseUtil.isInteger(port)) {
            port = "-1";
        }
        host = ParseUtil.removeSlashes(host);

        return new String[]{host, port};
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
        private final String hostIp;
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


}
