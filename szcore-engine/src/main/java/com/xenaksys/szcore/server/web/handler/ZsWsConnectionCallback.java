package com.xenaksys.szcore.server.web.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xenaksys.szcore.server.SzcoreServer;
import com.xenaksys.szcore.server.web.ZsWebServer;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.web.ZsWebRequest;
import com.xenaksys.szcore.web.ZsWebResponse;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_HTTP_HEADER_USER_AGENT;

public class ZsWsConnectionCallback implements WebSocketConnectionCallback {
    static final Logger LOG = LoggerFactory.getLogger(ZsWsConnectionCallback.class);

    static final JsonParser JSON_PARSER = new JsonParser();
    private String lastReceivedMessage;
    private ZsWebServer webServer;
    private SzcoreServer szcoreServer;

    public ZsWsConnectionCallback(ZsWebServer webServer, SzcoreServer szcoreServer) {
        this.webServer = webServer;
        this.szcoreServer = szcoreServer;
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        boolean isOk = webServer.onWsChannelConnected(channel, exchange);
        if (isOk) {
            LOG.info("onConnect: connected channel: {}", channel.getSourceAddress());
            channel.getReceiveSetter().set(new AbstractReceiveListener() {
                @Override
                protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                    try {
                        if (channel == null || message == null) {
                            return;
                        }
                        String sourceAddr = NetUtil.getClientId(channel.getPeerAddress());
                        if (webServer.isSourceAddrBanned(sourceAddr)) {
                            return;
                        }
                        long now = System.currentTimeMillis();
                        processRequest(exchange, channel, message, now);
                    } catch (Exception e) {
                        LOG.error("Failed to process WebSocket request", e);
                    }
                }
            });
            channel.resumeReceives();
        } else {
            channel.getReceiveSetter().set(new AbstractReceiveListener() {
                @Override
                protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                    //DO NOTHING
                }
            });
            channel.resumeReceives();
        }

    }

    private void processRequest(WebSocketHttpExchange exchange, WebSocketChannel channel, BufferedTextMessage message, long now) {
        final String messageData = message.getData();
        LOG.debug("onFullTextMessage: received message: {}", messageData);

        // {"id":"GET_SERVER_STATE","time":1587909588704,"propertyBag":{"ev":"GET_SERVER_STATE","evt":1587909588704}}
        JsonObject request = JSON_PARSER.parse(messageData).getAsJsonObject();
        Map<String, String> requestParams = new HashMap<>();
        populateParams(request, requestParams);

        String sourceAddr = NetUtil.getClientId(channel.getPeerAddress());
        String uri = exchange.getRequestURI();

        String userAgent = exchange.getRequestHeader(WEB_HTTP_HEADER_USER_AGENT);
        ZsWebRequest zsRequest = new ZsWebRequest(uri, sourceAddr, userAgent, false, webServer.isScoreServer(), now);
        zsRequest.addAllParams(requestParams);

        ZsWebResponse out = szcoreServer.onWebRequest(zsRequest);
        if (out != null) {
            webServer.pushToChannel(out.getData(), channel);
        }
    }

    private void populateParams(JsonObject jsonObj, Map<String, String> params) {
        for (Map.Entry<String,JsonElement> entry : jsonObj.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(value.isJsonPrimitive()) {
                params.put(key, value.getAsJsonPrimitive().getAsString());
            } else if(value.isJsonNull()) {
                LOG.info("populateParams: found NULL value for key: {}", key);
                params.put(key, EMPTY);
            } else if(value.isJsonArray()) {
                StringBuilder sb = new StringBuilder();
                String delimiter = EMPTY;
                JsonArray jarr = value.getAsJsonArray();
                for(int i = 0; i < jarr.size(); i++){
                    sb.append(delimiter);
                    JsonElement jel = jarr.get(i);
                    sb.append(jel.getAsString());
                    delimiter = COMMA;
                }
            } else if(value.isJsonObject()) {
                populateParams(value.getAsJsonObject(), params);
            }
        }
    }
}
