package com.xenaksys.szcore.server.web.handler;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZsWsConnectionCallback implements WebSocketConnectionCallback {
    static final Logger LOG = LoggerFactory.getLogger(ZsWsConnectionCallback.class);
    private String lastReceivedMessage;

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        channel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                final String messageData = message.getData();

                String data = message.getData();
                lastReceivedMessage = data;
                LOG.info("Received data: "+data);
                WebSockets.sendText(data, channel, null);
//                for (WebSocketChannel session : channel.getPeerConnections()) {
//                    WebSockets.sendText(messageData, session, null);
//                }
            }
        });
        channel.resumeReceives();
    }
}
