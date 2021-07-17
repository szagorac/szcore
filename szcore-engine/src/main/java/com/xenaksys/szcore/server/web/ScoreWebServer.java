package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
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
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

public class ScoreWebServer extends BaseZsWebServer {
    static final Logger LOG = LoggerFactory.getLogger(ScoreWebServer.class);

    private volatile boolean isServerRunning = false;
    private Undertow udtowScoreServer = null;
    private WebSocketProtocolHandshakeHandler wsHandler = null;
    private ZsSseHandler sseHandler = null;
    private ScheduledExecutorService clientInfoScheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean isClientInfoSchedulerRunning = false;

    private final List<String> bannedHosts = new CopyOnWriteArrayList<>();
    private final Map<String, WebSocketChannel> clients = new ConcurrentHashMap<>();

    private final AtomicBoolean closed = new AtomicBoolean();

    public ScoreWebServer(String staticDataPath, int port, int transferMinSize, long clientPollingIntervalSec, boolean isUseCaching, SzcoreServer szcoreServer) {
        super(staticDataPath, port, transferMinSize, clientPollingIntervalSec, isUseCaching, szcoreServer);
    }

    public boolean isRunning() {
        return isServerRunning;
    }

    public void start() {
        LOG.info("Starting Score Web Server ...");
        if (udtowScoreServer == null) {
            initUndertowScoreServer();
        }

        if (isServerRunning) {
            return;
        }

        udtowScoreServer.start();
        isServerRunning = true;


        if (!isClientInfoSchedulerRunning) {
            clientInfoScheduler.scheduleAtFixedRate(new ClientUpdater(), getClientPollingIntervalSec(), getClientPollingIntervalSec(), TimeUnit.SECONDS);
            isClientInfoSchedulerRunning = true;
        }

        LOG.info("Audience Web Server is Running");
    }

    public void stop() {
        LOG.info("Stopping Web Server ...");
        if (isServerRunning) {
            if (udtowScoreServer != null) {
                udtowScoreServer.stop();
                isServerRunning = false;
                LOG.info("Audience Web Server is Stopped");
            }
        }
    }

    private void initUndertowScoreServer() {
        HttpHandler staticDataHandler = new ZsStaticPathHandler(new ClassPathResourceManager(ScoreWebServer.class.getClassLoader(), ""), getSzcoreServer(), this)
                .addWelcomeFiles(INDEX_HTML);


        this.sseHandler = new ZsSseHandler(new ZsSseConnectionCallback(this));

        WebSocketConnectionCallback wsConnectionCallback = new ZsWsConnectionCallback(this, getSzcoreServer());
        this.wsHandler = websocket(wsConnectionCallback);

        if (getStaticDataPath() != null) {
            Path path = Paths.get(getStaticDataPath());
            Path indexPath = Paths.get(path.toAbsolutePath().toString(), INDEX_HTML);

            if (Files.exists(path) && Files.exists(indexPath)) {
                staticDataHandler = createStaticDataHandler(path);
            }
        }

        udtowScoreServer = Undertow.builder()
                .addHttpListener(getPort(), "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath(WEB_PATH_STATIC, staticDataHandler)
                        .addPrefixPath(WEB_PATH_WEBSOCKETS, wsHandler)
                        .addPrefixPath(WEB_PATH_SSE, sseHandler)
                        .addPrefixPath(WEB_PATH_HTTP, new ZsHttpHandler(getSzcoreServer(), this))
                ).build();

    }

    public void pushToChannel(String data, WebSocketChannel channel) {
        LOG.debug("pushToChannel: ip: {}, isCloseInitiatedByRemotePeer: {}", channel.getSourceAddress(), channel.isCloseInitiatedByRemotePeer());
        WebSockets.sendText(data, channel, null);
    }

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

    public void onWsChannelConnected(WebSocketChannel channel, WebSocketHttpExchange exchange) {
        if (channel == null) {
            return;
        }
        try {
            String userAgent = exchange.getRequestHeader(WEB_HTTP_HEADER_USER_AGENT);
            SocketAddress sourceAddr = channel.getPeerAddress();
            String sourceId = sourceAddr.toString();
            clients.put(sourceId, channel);
            onConnection(sourceId, WebConnectionType.WS, userAgent, channel.isOpen());
        } catch (Exception e) {
            LOG.error("onWsChannelConnected: failed to process new Websocket connection", e);
        }
    }

    public void updateServerStatus() {
        Set<WebConnection> connections = new HashSet<>();
        connections.addAll(getWsConnections());
        connections.addAll(getSseConnections());

        getSzcoreServer().updateScoreServerConnections(connections);
    }

    public Set<WebConnection> getWsConnections() {
        Set<WebConnection> webConnections = new HashSet<>();
        Set<WebSocketChannel> channels = wsHandler.getPeerConnections();
        for (WebSocketChannel c : channels) {
            SocketAddress socketAddress = c.getPeerAddress();
            String clientAddr = socketAddress.toString();
            WebConnection webConnection = new WebConnection(clientAddr, WebConnectionType.WS, c.isOpen());
            webConnection.setScoreClient(true);
            webConnections.add(webConnection);
        }
        return webConnections;
    }

    public void onSseChannelConnected(ServerSentEventConnection connection) {
        if (!(connection instanceof ZsSseConnection)) {
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
            webConnection.setScoreClient(true);
            webConnections.add(webConnection);
        }
        return webConnections;
    }

    public void onConnection(String sourceId, WebConnectionType type, String userAgent, boolean isOpen) {
        WebConnection webConnection = new WebConnection(sourceId, type, isOpen);
        webConnection.setScoreClient(true);
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

    public boolean isHostBanned(String host) {
        return bannedHosts.contains(host);
    }

    public void pushData(String target, WebScoreTargetType targetType, String data) {
        if (data == null) {
            return;
        }
        if (target == null) {
            target = Consts.WEB_DATA_TARGET_ALL;
        }
        if (targetType == null) {
            targetType = WebScoreTargetType.ALL;
        }

        if (targetType == WebScoreTargetType.ALL || Consts.WEB_DATA_TARGET_ALL.equals(target)) {
            pushToAll(data);
            return;
        }

        switch (targetType) {
            case HOST:
                if (clients.containsKey(target)) {
                    pushToHost(target, data);
                }
                break;
            case INSTRUMENT:
            default:
                LOG.error("Unsupported tartet type: {}", targetType);
        }
    }

    private void pushToHost(String host, String data) {
        WebSocketChannel channel = clients.get(host);
        if (channel == null) {
            return;
        }
        pushToChannel(data, channel);
    }

    public void closeWsConnection(WebSocketChannel webSocketChannel, CloseReason closeReason) {
        if (closeReason == null) {
            closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, Consts.WEB_WS_CLOSE_REASON_NORMAL);
        }
        try {
            if (!webSocketChannel.isCloseFrameReceived() && !webSocketChannel.isCloseFrameSent()) {
                //if we have already recieved a close frame then the close frame handler
                //will deal with sending back the reason message
                if (closeReason.getCloseCode().getCode() == CloseReason.CloseCodes.NO_STATUS_CODE.getCode()) {
                    webSocketChannel.sendClose();
                } else {
                    WebSockets.sendClose(new CloseMessage(closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase()).toByteBuffer(), webSocketChannel, null);
                }
            }
        } catch (Exception e) {
            LOG.error("closeWsConnection, Failed to close ");
        }
    }

    public void closeConnections(List<String> connectionIds) {
        for (String id : connectionIds) {
            if (!clients.containsKey(id)) {
                continue;
            }
            WebSocketChannel channel = clients.remove(id);
            if (channel != null && channel.isOpen()) {
                closeWsConnection(channel, null);
            }
        }
    }

    class ClientUpdater implements Runnable {

        @Override
        public void run() {
            LOG.debug("ClientUpdater: update client infos");
            updateServerStatus();
        }
    }
}
