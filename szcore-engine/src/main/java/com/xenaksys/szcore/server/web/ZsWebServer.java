package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebConnection;
import com.xenaksys.szcore.web.WebConnectionType;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.util.Set;

public interface ZsWebServer {
    String getStaticDataPath();

    int getPort();

    int getTransferMinSize();

    boolean isUseCaching();

    SzcoreServer getSzcoreServer();

    long getClientPollingIntervalSec();

    boolean isServerRunning();

    void start();

    void stop();

    void pushToChannel(String data, WebSocketChannel channel);

    void pushToAll(String data);

    void onWsChannelConnected(WebSocketChannel channel, WebSocketHttpExchange exchange);

    void updateServerStatus();

    Set<WebConnection> getWsConnections();

    void onSseChannelConnected(ServerSentEventConnection connection);

    Set<WebConnection> getSseConnections();

    void onConnection(String sourceId, WebConnectionType type, String userAgent, boolean isOpen);

    void banWebClient(WebClientInfo clientInfo);

    void banWebClient(String host);

    boolean isSourceAddrBanned(String sourceAddr);

    boolean isHostBanned(String host);

    boolean isScoreServer();
}
