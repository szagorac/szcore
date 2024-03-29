package com.xenaksys.szcore.server.web.handler;

import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.ZsWebServer;
import com.xenaksys.szcore.util.HttpUtil;
import com.xenaksys.szcore.web.ZsWebRequest;
import com.xenaksys.szcore.web.ZsWebResponse;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_HTTP_HEADER_USER_AGENT;

public class ZsHttpHandler implements HttpHandler {
    static final Logger LOG = LoggerFactory.getLogger(ZsHttpHandler.class);
    private final static HttpString POST_STR = new HttpString("POST");

    private final SzcoreServer szcoreServer;
    private final ZsWebServer webServer;

    public ZsHttpHandler(SzcoreServer szcoreServer, ZsWebServer webServer) {
        this.szcoreServer = szcoreServer;
        this.webServer = webServer;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        long now = System.currentTimeMillis();
        String requestPath = exchange.getRequestPath();
        String sourceId = exchange.getSourceAddress().toString();
        if (webServer.isSourceAddrBanned(sourceId)) {
            return;
        }
        HttpString method = exchange.getRequestMethod();
        String userAgent = EMPTY;
        HeaderValues hv = exchange.getRequestHeaders().get(WEB_HTTP_HEADER_USER_AGENT);
        if (hv != null) {
            userAgent = hv.getFirst();
        }
//        LOG.info("Received {} request {} {} from {}", method, requestPath, exchange.getQueryString(), sourceId);

        if (method.equals(POST_STR)) {
            if (exchange.isInIoThread()) {
                exchange.dispatch(this);
                return;
            }

            ZsWebRequest zsRequest = new ZsWebRequest(requestPath, sourceId, userAgent, false, webServer.isScoreServer(), now);
            FormParserFactory.Builder builder = FormParserFactory.builder();

            final FormDataParser formDataParser = builder.build().createParser(exchange);
            if (formDataParser != null) {
                exchange.startBlocking();
                FormData formData = formDataParser.parseBlocking();

                for (String data : formData) {
                    for (FormData.FormValue formValue : formData.get(data)) {
                        if (formValue.isFile()) {
                            zsRequest.addFilePath(formValue.getPath());
                        } else {
                            zsRequest.addStringParam(data, formValue.getValue());
                        }
                    }
                }
            }

            ZsWebResponse response = szcoreServer.onWebRequest(zsRequest);
            exchange.getResponseSender().send(response.getData());
            exchange.endExchange();

        } else {
            ZsWebRequest zsRequest = new ZsWebRequest(requestPath, sourceId, userAgent, false, webServer.isScoreServer(), now);

            Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
            for(String key : queryParams.keySet()) {
                zsRequest.addStringParam(key, readQueryParam(queryParams.get(key)));
            }
            ZsWebResponse response = szcoreServer.onWebRequest(zsRequest);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, HttpUtil.getMimeType(response));
            exchange.getResponseSender().send(response.getData());
            exchange.endExchange();
        }
    }

    public String readQueryParam(final  Deque<String> values) {
        if(values == null) {
            return null;
        }else if(values.isEmpty()) {
            return "";
        } else if(values.size() == 1) {
            return values.getFirst();
        } else {
            StringBuilder sb = new StringBuilder("[");
            int i = 0;
            for(String s : values) {
                sb.append(s);
                if(++i != values.size()) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
