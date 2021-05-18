package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.handler.ZsHttpHandler;
import com.xenaksys.szcore.server.web.handler.ZsSseConnection;
import com.xenaksys.szcore.server.web.handler.ZsSseConnectionCallback;
import com.xenaksys.szcore.server.web.handler.ZsSseHandler;
import com.xenaksys.szcore.server.web.handler.ZsStaticPathHandler;
import com.xenaksys.szcore.server.web.handler.ZsWsConnectionCallback;
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
import java.util.Set;

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

public class WebServer {
    static final Logger LOG = LoggerFactory.getLogger(WebServer.class);

    private final String staticDataPath;
    private final int port;
    private final int transferMinSize;
    private final boolean isUseCaching;
    private final SzcoreServer szcoreServer;

    private volatile boolean isRunning = false;
    private Undertow undertow = null;
    private ZsSseHandler sseHandler = null;
    private WebSocketProtocolHandshakeHandler wsHandler = null;

    public WebServer(String staticDataPath, int port, int transferMinSize, boolean isUseCaching, SzcoreServer szcoreServer) {
        this.staticDataPath = staticDataPath;
        this.port = port;
        this.transferMinSize = transferMinSize;
        this.isUseCaching = isUseCaching;
        this.szcoreServer = szcoreServer;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void start(){
        LOG.info("Starting Web Server ...");
        if(undertow == null) {
            initUndertow();
        }

        if(isRunning) {
            return;
        }

        undertow.start();
        isRunning = true;
        LOG.info("Web Server is Running");
    }

    public void stop(){
        LOG.info("Stopping Web Server ...");
        if(isRunning && undertow != null) {
            undertow.stop();
            isRunning = false;
            LOG.info("Web Server is Stopped");
        }
    }

    private void initUndertow() {
        HttpHandler staticDataHandler = new ZsStaticPathHandler(new ClassPathResourceManager(WebServer.class.getClassLoader(), ""))
                .addWelcomeFiles(INDEX_HTML);

        this.sseHandler = new ZsSseHandler(new ZsSseConnectionCallback(this));

        WebSocketConnectionCallback wsConnectionCallback = new ZsWsConnectionCallback(this, szcoreServer);
        this.wsHandler = websocket(wsConnectionCallback);

        if(staticDataPath != null) {
            Path path = Paths.get(staticDataPath);
            Path indexPath = Paths.get(path.toAbsolutePath().toString(), INDEX_HTML);

            if(Files.exists(path) && Files.exists(indexPath)) {
                staticDataHandler = createStaticDataHandler(path);

//                DirectBufferCache bufferCache = new DirectBufferCache(1024, 10,1024 * 1024 * 200);
//                staticDataHandler = new CacheHandler(bufferCache, staticPathHandler);

//                staticDataHandler = resource(new PathResourceManager(path, transferMinSize))
//                        .setWelcomeFiles(INDEX_HTML);
            }
        }

        undertow = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(Handlers.path()
                        .addPrefixPath(WEB_PATH_STATIC, staticDataHandler)
                        .addPrefixPath(WEB_PATH_SSE, sseHandler)
                        .addPrefixPath(WEB_PATH_WEBSOCKETS, wsHandler)
                        .addPrefixPath(WEB_PATH_HTTP, new ZsHttpHandler(szcoreServer))
                ).build();

    }

    public void pushToChannel(String data, WebSocketChannel channel) {
        LOG.info("pushToChannel: ip: {}, isCloseInitiatedByRemotePeer: {}", channel.getSourceAddress(), channel.isCloseInitiatedByRemotePeer());
        WebSockets.sendText(data, channel, null);
    }

    public void pushToAll(String data) {
        byte[] bytes = data.getBytes();
        LOG.info("pushToAll:  size {} data: {}", bytes.length, data);
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
        if(channel == null) {
            return;
        }
        try {
            String userAgent = exchange.getRequestHeader(WEB_HTTP_HEADER_USER_AGENT);
            SocketAddress sourceAddr = channel.getPeerAddress();
            onConnection(sourceAddr, WebConnectionType.WS, userAgent);
            updateConnections();
        } catch (Exception e) {
            LOG.error("onWsChannelConnected: faled to process new Websocket connection", e);
        }
    }

    public void updateConnections() {
        Set<WebConnection> connections = new HashSet<>();
        connections.addAll(getWsConnections());
        connections.addAll(getSseConnections());
        szcoreServer.updateWebConnections(connections);
    }

    public Set<WebConnection> getWsConnections() {
        Set<WebConnection> webConnections = new HashSet<>();
        Set<WebSocketChannel>  channels =  wsHandler.getPeerConnections();
        for (WebSocketChannel c : channels) {
            String clientAddr = c.getPeerAddress().toString();
            WebConnection webConnection = new WebConnection(clientAddr, WebConnectionType.WS);
            webConnections.add(webConnection);
        }
        return webConnections;
    }

    public void onSseChannelConnected(ServerSentEventConnection connection) {
        if(!(connection instanceof ZsSseConnection)) {
            return;
        }
        try {
            ZsSseConnection zsConn = (ZsSseConnection) connection;
            SocketAddress sourceAddr = zsConn.getExchange().getSourceAddress();
            String userAgent = zsConn.getExchange().getRequestHeaders().getFirst(HttpString.tryFromString(WEB_HTTP_HEADER_USER_AGENT));
            onConnection(sourceAddr, WebConnectionType.SSE, userAgent);
            updateConnections();
        } catch (Exception e) {
            LOG.error("onSseChannelConnected: faled to process new SSE connection", e);
        }
    }

    public Set<WebConnection> getSseConnections() {
        Set<WebConnection> webConnections = new HashSet<>();
        Set<ZsSseConnection>  connections =  sseHandler.getConnections();
        for (ZsSseConnection c : connections) {
            String clientAddr = c.getExchange().getSourceAddress().toString();
            WebConnection webConnection = new WebConnection(clientAddr, WebConnectionType.SSE);
            webConnections.add(webConnection);
        }
        return webConnections;
    }

    public void onConnection(SocketAddress sourceAddr, WebConnectionType type, String userAgent) {
        if (sourceAddr == null) {
            return;
        }
        String sourceId = sourceAddr.toString();
        szcoreServer.onWebConnection(sourceId, type, userAgent);
    }

    private HttpHandler createStaticDataHandler(Path path) {
        if (isUseCaching) {
            CachingResourceManager cachingResourceManager = new CachingResourceManager(
                    WEB_METADATA_CACHE_SIZE,
                    WEB_MAX_FILE_SIZE,
                    new DirectBufferCache(WEB_BUFFER_SLICE_SIZE, WEB_SLICES_PER_PAGE, WEB_MAX_MEMORY_SIZE),
                    new PathResourceManager(path, transferMinSize),
                    -1);

            return new ZsStaticPathHandler(cachingResourceManager)
                    .setDirectoryListingEnabled(false)
                    .setWelcomeFiles(INDEX_HTML);
        }

        return resource(new PathResourceManager(path, transferMinSize)).setWelcomeFiles(INDEX_HTML);
    }
}
