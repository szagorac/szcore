package com.xenaksys.szcore.server.web.handler;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import org.xnio.channels.StreamSinkChannel;

public class ZsSseConnection extends ServerSentEventConnection {

    private final HttpServerExchange exchangeLocal;

    public ZsSseConnection(HttpServerExchange exchange, StreamSinkChannel sink) {
        super(exchange, sink);
        this.exchangeLocal = exchange;
    }

    public HttpServerExchange getExchange() {
        return exchangeLocal;
    }
}
