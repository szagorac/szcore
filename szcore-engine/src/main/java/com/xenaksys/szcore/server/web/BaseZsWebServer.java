package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.web.WebClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseZsWebServer implements ZsWebServer {
    static final Logger LOG = LoggerFactory.getLogger(BaseZsWebServer.class);

    private final String staticDataPath;
    private final int port;
    private final int transferMinSize;
    private final boolean isUseCaching;
    private final SzcoreServer szcoreServer;
    private final long clientPollingIntervalSec;

    private volatile boolean isAudienceServerRunning = false;

    protected final List<String> bannedHosts = new CopyOnWriteArrayList<>();

    public BaseZsWebServer(String staticDataPath, int port, int transferMinSize, long clientPollingIntervalSec, boolean isUseCaching, SzcoreServer szcoreServer) {
        this.staticDataPath = staticDataPath;
        this.port = port;
        this.transferMinSize = transferMinSize;
        this.clientPollingIntervalSec = clientPollingIntervalSec;
        this.isUseCaching = isUseCaching;
        this.szcoreServer = szcoreServer;
    }

    @Override
    public String getStaticDataPath() {
        return staticDataPath;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getTransferMinSize() {
        return transferMinSize;
    }

    @Override
    public boolean isUseCaching() {
        return isUseCaching;
    }

    @Override
    public SzcoreServer getSzcoreServer() {
        return szcoreServer;
    }

    @Override
    public long getClientPollingIntervalSec() {
        return clientPollingIntervalSec;
    }

    @Override
    public boolean isServerRunning() {
        return isAudienceServerRunning;
    }

    @Override
    public abstract void start();

    @Override
    public abstract void stop();

    @Override
    public abstract boolean isScoreServer();

    @Override
    public boolean isHostBanned(String host) {
        return bannedHosts.contains(host);
    }

    @Override
    public boolean isSourceAddrBanned(String sourceAddr) {
        if (sourceAddr == null) {
            return false;
        }
        String[] hostPort = NetUtil.getHostPort(sourceAddr);
        if (hostPort == null || hostPort.length != 2) {
            return isHostBanned(sourceAddr);
        }

        return isHostBanned(hostPort[0]);
    }

    @Override
    public void banWebClient(WebClientInfo clientInfo) {
        if (clientInfo == null) {
            return;
        }
        String host = clientInfo.getHost();
        banWebClient(host);
    }

    @Override
    public void banWebClient(String host) {
        if (host == null) {
            return;
        }
        if (bannedHosts.contains(host)) {
            return;
        }
        LOG.info("banWebClient: host: {}", host);
        bannedHosts.add(host);
    }
}
