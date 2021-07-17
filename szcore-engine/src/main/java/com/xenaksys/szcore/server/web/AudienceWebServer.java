package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.handler.ZsHttpHandler;
import com.xenaksys.szcore.server.web.handler.ZsSseConnection;
import com.xenaksys.szcore.server.web.handler.ZsSseConnectionCallback;
import com.xenaksys.szcore.server.web.handler.ZsSseHandler;
import com.xenaksys.szcore.server.web.handler.ZsStaticPathHandler;
import com.xenaksys.szcore.server.web.handler.ZsWsConnectionCallback;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebConnection;
import com.xenaksys.szcore.web.WebConnectionType;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.resource.CachingResourceManager;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.xenaksys.szcore.Consts.INDEX_HTML;
import static com.xenaksys.szcore.Consts.WEB_BUFFER_SLICE_SIZE;
import static com.xenaksys.szcore.Consts.WEB_HTTP_HEADER_USER_AGENT;
import static com.xenaksys.szcore.Consts.WEB_MAX_FILE_SIZE;
import static com.xenaksys.szcore.Consts.WEB_MAX_MEMORY_SIZE;
import static com.xenaksys.szcore.Consts.WEB_METADATA_CACHE_SIZE;
import static com.xenaksys.szcore.Consts.WEB_PATH_HTTP;
import static com.xenaksys.szcore.Consts.WEB_PATH_SSE;
import static com.xenaksys.szcore.Consts.WEB_PATH_STATIC;
import static com.xenaksys.szcore.Consts.WEB_PATH_WEBSOCKETS;
import static com.xenaksys.szcore.Consts.WEB_SLICES_PER_PAGE;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;

public class AudienceWebServer extends BaseZsWebServer {
    static final Logger LOG = LoggerFactory.getLogger(AudienceWebServer.class);

    private volatile boolean isAudienceServerRunning = false;
    private Undertow udtowAudience = null;
    private ZsSseHandler sseHandler = null;
    private WebSocketProtocolHandshakeHandler wsHandler = null;
    private ScheduledExecutorService clientInfoScheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean isClientInfoSchedulerRunning = false;

    private final List<String> bannedHosts = new CopyOnWriteArrayList<>();

    public AudienceWebServer(String staticDataPath, int port, int transferMinSize, long clientPollingIntervalSec, boolean isUseCaching, SzcoreServer szcoreServer) {
        super(staticDataPath, port, transferMinSize, clientPollingIntervalSec, isUseCaching, szcoreServer);
    }

    @Override
    public boolean isServerRunning() {
        return isAudienceServerRunning;
    }

    @Override
    public void start() {
        LOG.info("Starting Audience Web Server ...");
        if (udtowAudience == null) {
            initUndertowAudience();
        }

        if (isAudienceServerRunning) {
            return;
        }

        udtowAudience.start();
        isAudienceServerRunning = true;


        if (!isClientInfoSchedulerRunning) {
            clientInfoScheduler.scheduleAtFixedRate(new ClientUpdater(), getClientPollingIntervalSec(), getClientPollingIntervalSec(), TimeUnit.SECONDS);
            isClientInfoSchedulerRunning = true;
        }

        LOG.info("Audience Web Server is Running");
    }

    @Override
    public void stop() {
        LOG.info("Stopping Web Server ...");
        if (isAudienceServerRunning) {
            if (udtowAudience != null) {
                udtowAudience.stop();
                isAudienceServerRunning = false;
                LOG.info("Audience Web Server is Stopped");
            }
        }
    }

    @Override
    public boolean isScoreServer() {
        return false;
    }

    private void initUndertowAudience() {
        HttpHandler staticDataHandler = new ZsStaticPathHandler(new ClassPathResourceManager(AudienceWebServer.class.getClassLoader(), ""), getSzcoreServer(), this)
                .addWelcomeFiles(INDEX_HTML);

        this.sseHandler = new ZsSseHandler(new ZsSseConnectionCallback(this));

        WebSocketConnectionCallback wsConnectionCallback = new ZsWsConnectionCallback(this, getSzcoreServer());
        this.wsHandler = websocket(wsConnectionCallback);

        if (getStaticDataPath() != null) {
            Path path = Paths.get(getStaticDataPath());
            Path indexPath = Paths.get(path.toAbsolutePath().toString(), INDEX_HTML);

            if (Files.exists(path) && Files.exists(indexPath)) {
                staticDataHandler = createStaticDataHandler(path);

//                DirectBufferCache bufferCache = new DirectBufferCache(1024, 10,1024 * 1024 * 200);
//                staticDataHandler = new CacheHandler(bufferCache, staticPathHandler);

//                staticDataHandler = resource(new PathResourceManager(path, transferMinSize))
//                        .setWelcomeFiles(INDEX_HTML);
            }
        }

        udtowAudience = Undertow.builder()
                .addHttpListener(getPort(), "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath(WEB_PATH_STATIC, staticDataHandler)
                        .addPrefixPath(WEB_PATH_SSE, sseHandler)
                        .addPrefixPath(WEB_PATH_WEBSOCKETS, wsHandler)
                        .addPrefixPath(WEB_PATH_HTTP, new ZsHttpHandler(getSzcoreServer(), this))
                ).build();

    }

    @Override
    public void pushToChannel(String data, WebSocketChannel channel) {
        LOG.debug("pushToChannel: ip: {}, isCloseInitiatedByRemotePeer: {}", channel.getSourceAddress(), channel.isCloseInitiatedByRemotePeer());
        WebSockets.sendText(data, channel, null);
    }

    @Override
    public void pushToAll(String data) {
        byte[] bytes = data.getBytes();
        LOG.debug("pushToAll:  size {} data: {}", bytes.length, data);
        if (wsHandler != null && data != null) {
            Set<WebSocketChannel> channels = wsHandler.getPeerConnections();
            for (WebSocketChannel channel : channels) {
                pushToChannel(data, channel);
            }
        }

        if (sseHandler != null && data != null) {
            Set<ZsSseConnection> connections = sseHandler.getConnections();
            for (ZsSseConnection sseConnection : connections) {
                sseConnection.send(data);
            }
        }
    }

    @Override
    public void onWsChannelConnected(WebSocketChannel channel, WebSocketHttpExchange exchange) {
        if(channel == null) {
            return;
        }
        try {
            String userAgent = exchange.getRequestHeader(WEB_HTTP_HEADER_USER_AGENT);
            SocketAddress sourceAddr = channel.getPeerAddress();
            onConnection(sourceAddr.toString(), WebConnectionType.WS, userAgent, channel.isOpen());
        } catch (Exception e) {
            LOG.error("onWsChannelConnected: failed to process new Websocket connection", e);
        }
    }

    @Override
    public void updateServerStatus() {
        Set<WebConnection> connections = new HashSet<>();
        connections.addAll(getWsConnections());
        connections.addAll(getSseConnections());

//        if (connections.isEmpty()) {
//            return;
//        }
//        long now = System.currentTimeMillis();

        getSzcoreServer().updateAudienceWebServerConnections(connections);
    }

    @Override
    public Set<WebConnection> getWsConnections() {
        Set<WebConnection> webConnections = new HashSet<>();
        Set<WebSocketChannel>  channels =  wsHandler.getPeerConnections();
        for (WebSocketChannel c : channels) {
            SocketAddress socketAddress = c.getPeerAddress();
            String clientAddr = socketAddress.toString();
            WebConnection webConnection = new WebConnection(clientAddr, WebConnectionType.WS, c.isOpen());
            webConnections.add(webConnection);
        }
        return webConnections;
    }

    @Override
    public void onSseChannelConnected(ServerSentEventConnection connection) {
        if(!(connection instanceof ZsSseConnection)) {
            return;
        }
        try {
            ZsSseConnection zsConn = (ZsSseConnection) connection;
            SocketAddress sourceAddr = zsConn.getExchange().getSourceAddress();
            HeaderMap headerMap = zsConn.getExchange().getRequestHeaders();
            String userAgent = headerMap.getFirst(HttpString.tryFromString(WEB_HTTP_HEADER_USER_AGENT));
            onConnection(sourceAddr.toString(), WebConnectionType.SSE, userAgent, zsConn.isOpen());
        } catch (Exception e) {
            LOG.error("onSseChannelConnected: faled to process new SSE connection", e);
        }
    }

    @Override
    public Set<WebConnection> getSseConnections() {
        Set<WebConnection> webConnections = new HashSet<>();
        Set<ZsSseConnection> connections = sseHandler.getConnections();
        for (ZsSseConnection c : connections) {
            String clientAddr = c.getExchange().getSourceAddress().toString();
            WebConnection webConnection = new WebConnection(clientAddr, WebConnectionType.SSE, c.isOpen());
            webConnections.add(webConnection);
        }
        return webConnections;
    }

    @Override
    public void onConnection(String sourceId, WebConnectionType type, String userAgent, boolean isOpen) {
        WebConnection webConnection = new WebConnection(sourceId, type, isOpen);
        webConnection.setUserAgent(userAgent);
        getSzcoreServer().onWebConnection(webConnection);
    }

    private HttpHandler createStaticDataHandler(Path path) {
        if (isUseCaching()) {
            CachingResourceManager cachingResourceManager = new CachingResourceManager(
                    WEB_METADATA_CACHE_SIZE,
                    WEB_MAX_FILE_SIZE,
                    new DirectBufferCache(WEB_BUFFER_SLICE_SIZE, WEB_SLICES_PER_PAGE, WEB_MAX_MEMORY_SIZE),
                    new PathResourceManager(path, getTransferMinSize()),
                    -1);

            return new ZsStaticPathHandler(cachingResourceManager, getSzcoreServer(), this)
                    .setDirectoryListingEnabled(false)
                    .setWelcomeFiles(INDEX_HTML);
        }

        return resource(new PathResourceManager(path, getTransferMinSize())).setWelcomeFiles(INDEX_HTML);
    }

    @Override
    public void banWebClient(WebClientInfo clientInfo) {
        if (clientInfo == null) {
            return;
        }
        String host = clientInfo.getHost();
        if (bannedHosts.contains(host)) {
            return;
        }
        LOG.info("banWebClient: host: {}", host);
        bannedHosts.add(host);
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
    public boolean isHostBanned(String host) {
        return bannedHosts.contains(host);
    }

    class ClientUpdater implements Runnable {

        @Override
        public void run() {
            LOG.debug("ClientUpdater: update client infos");
            updateServerStatus();
        }
    }
}