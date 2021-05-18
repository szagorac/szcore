package com.xenaksys.szcore.server.web.handler;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZsStaticPathHandler extends ResourceHandler {
    static final Logger LOG = LoggerFactory.getLogger(ZsStaticPathHandler.class);

    public ZsStaticPathHandler(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
//        LOG.debug("handleStaticRequest: path: {}", exchange.getRelativePath());
        super.handleRequest(exchange);
    }

}
