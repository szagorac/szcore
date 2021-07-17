package com.xenaksys.szcore.server.web.handler;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.ZsWebServer;
import com.xenaksys.szcore.server.web.mappings.ZsMimeMappings;
import com.xenaksys.szcore.web.ZsWebRequest;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.HeaderValues;
import io.undertow.util.MimeMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_HTTP_HEADER_USER_AGENT;

public class ZsStaticPathHandler extends ResourceHandler {
    static final Logger LOG = LoggerFactory.getLogger(ZsStaticPathHandler.class);
    private final ZsWebServer webServer;
    private final SzcoreServer szcoreServer;

    public ZsStaticPathHandler(ResourceManager resourceManager, SzcoreServer szcoreServer, ZsWebServer webServer) {
        super(resourceManager);
        this.webServer = webServer;
        this.szcoreServer = szcoreServer;
        init();
    }

    private void init() {
        MimeMappings.Builder builder = MimeMappings.builder();
        Map<String, String> zsMappings = ZsMimeMappings.ZSCORE_MIME_MAPPINGS;
        for (String key : zsMappings.keySet()) {
            builder.addMapping(key, zsMappings.get(key));
        }

        MimeMappings zsMimeMap = builder.build();
        setMimeMappings(zsMimeMap);
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        LOG.info("handleStaticRequest: path: {}", exchange.getRelativePath());
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
        ZsWebRequest zsRequest = new ZsWebRequest(requestPath, sourceId, userAgent, true, webServer.isScoreServer(), now);
        szcoreServer.onWebRequest(zsRequest);
        super.handleRequest(exchange);
    }

}
