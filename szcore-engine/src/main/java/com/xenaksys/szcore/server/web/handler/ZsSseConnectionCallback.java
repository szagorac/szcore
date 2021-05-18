package com.xenaksys.szcore.server.web.handler;

import com.xenaksys.szcore.server.web.WebServer;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZsSseConnectionCallback implements ServerSentEventConnectionCallback {
    static final Logger LOG = LoggerFactory.getLogger(ZsSseConnectionCallback.class);
    private WebServer webServer;

    public ZsSseConnectionCallback(WebServer webServer) {
        this.webServer = webServer;
    }

    @Override
    public void connected(ServerSentEventConnection connection, String lastEventId) {
        webServer.onSseChannelConnected(connection);
    }
}
