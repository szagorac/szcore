package com.xenaksys.szcore.server.web.handler;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ZsSseHandler implements HttpHandler {

    private static final HttpString LAST_EVENT_ID = new HttpString("Last-Event-ID");

    private final ServerSentEventConnectionCallback callback;

    private final Set<ZsSseConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ZsSseHandler(ServerSentEventConnectionCallback callback) {
        this.callback = callback;
    }

    public ZsSseHandler() {
        this.callback = null;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/event-stream; charset=UTF-8");
        exchange.setPersistent(false);
        final StreamSinkChannel sink = exchange.getResponseChannel();
        if(!sink.flush()) {
            sink.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
                @Override
                public void handleEvent(StreamSinkChannel channel) {
                    handleConnect(channel, exchange);
                }
            }, new ChannelExceptionHandler<StreamSinkChannel>() {
                @Override
                public void handleException(StreamSinkChannel channel, IOException exception) {
                    IoUtils.safeClose(exchange.getConnection());
                }
            }));
            sink.resumeWrites();
        } else {
            exchange.dispatch(exchange.getIoThread(), new Runnable() {
                @Override
                public void run() {
                    handleConnect(sink, exchange);
                }
            });
        }
    }

    private void handleConnect(StreamSinkChannel channel, HttpServerExchange exchange) {
        UndertowLogger.REQUEST_LOGGER.debugf("Opened SSE connection to %s", exchange);
        final ZsSseConnection connection = new ZsSseConnection(exchange, channel);
        PathTemplateMatch pt = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        if(pt != null) {
            for(Map.Entry<String, String> p : pt.getParameters().entrySet()) {
                connection.setParameter(p.getKey(), p.getValue());
            }
        }
        connections.add(connection);
        connection.addCloseTask(channel1 -> connections.remove(connection));
        if(callback != null) {
            callback.connected(connection, exchange.getRequestHeaders().getLast(LAST_EVENT_ID));
        }
    }

    public Set<ZsSseConnection> getConnections() {
        return Collections.unmodifiableSet(connections);
    }
}
