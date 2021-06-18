package com.xenaksys.szcore.server.web.handler;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.WebServer;
import com.xenaksys.szcore.web.ZsWebRequest;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.HeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_HTTP_HEADER_USER_AGENT;

public class ZsStaticPathHandler extends ResourceHandler {
    static final Logger LOG = LoggerFactory.getLogger(ZsStaticPathHandler.class);
    private final WebServer webServer;
    private final SzcoreServer szcoreServer;

    public ZsStaticPathHandler(ResourceManager resourceManager, SzcoreServer szcoreServer, WebServer webServer) {
        super(resourceManager);
        this.webServer = webServer;
        this.szcoreServer = szcoreServer;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
//        LOG.debug("handleStaticRequest: path: {}", exchange.getRelativePath());
        long now = System.currentTimeMillis();
        String requestPath = exchange.getRequestPath();
        String sourceId = exchange.getSourceAddress().toString();
        if (webServer.isSourceAddrBanned(sourceId)) {
            return;
        }
        String userAgent = EMPTY;
        HeaderValues hv = exchange.getRequestHeaders().get(WEB_HTTP_HEADER_USER_AGENT);
        if (hv != null) {
            userAgent = hv.getFirst();
        }
        ZsWebRequest zsRequest = new ZsWebRequest(requestPath, sourceId, userAgent, true, now);
        szcoreServer.onWebRequest(zsRequest);
        super.handleRequest(exchange);
    }

}
