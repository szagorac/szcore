package com.xenaksys.szcore.server.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertNotNull;

public class ZscoreTestWebsocketClient implements WsClient {
    static final Logger LOG = LoggerFactory.getLogger(ZscoreTestWebsocketClient.class);

    private static final OptionMap DEFAULT_OPTIONS;

    public static final int BUFFER_SIZE = 1024 * 16 - 20;
    public static final int MAX_POOL_SIZE = 1000;
    public static final int THREAD_LOCAL_CACHE_SIZE = 10;
    public static final int LEAK_DETECTION_PERC = 100;

    static {
        final OptionMap.Builder builder = OptionMap.builder()
                .set(Options.WORKER_IO_THREADS, 8)
                .set(Options.TCP_NODELAY, true)
                .set(Options.KEEP_ALIVE, true)
                .set(Options.WORKER_NAME, "ZScoreTestClient");

        DEFAULT_OPTIONS = builder.getMap();
    }

    private final TLongObjectHashMap<String> wsReceived = new TLongObjectHashMap<>();
    private final Xnio xnio = Xnio.getInstance();

    private final String wsUrl;
    private final int wsMessageNo;

    private XnioWorker worker;
    private DebuggingSlicePool wspool;
    private WebSocketChannel webSocketChannel;
    private URI wsaddress;
    private volatile boolean isWsTestComplete = false;
    private long testDuration = 0L;
    private volatile long endTime = 0L;
    private AtomicInteger wsCount = new AtomicInteger(0);
    private final JsonParser jsonParser = new JsonParser();

    public ZscoreTestWebsocketClient(int clientNo, String wsUrl, int wsMessageNo) {
        this.wsUrl = wsUrl;
        this.wsMessageNo = wsMessageNo;
    }

    public void init() throws Exception {
        try {
            worker = xnio.createWorker(null, DEFAULT_OPTIONS);
            wsaddress = new URI(wsUrl);
            wspool = new DebuggingSlicePool(new DefaultByteBufferPool(
                    true, BUFFER_SIZE, MAX_POOL_SIZE, THREAD_LOCAL_CACHE_SIZE, LEAK_DETECTION_PERC));

            webSocketChannel = WebSocketClient.connectionBuilder(worker, wspool, wsaddress).connect().get();
            initWebSocket();

        } catch (Exception e) {
            LOG.error("Failed to initalise test", e);
            throw e;
        }
    }


    public void closeWebSocket() {
        try {
            webSocketChannel.sendClose();
        } catch (IOException e) {
            LOG.error("Failed to close websocket", e);
        }
    }

    private void initWebSocket() {
        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {

            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                String data = message.getData();
                int c = wsCount.incrementAndGet();
                LOG.debug("Websocket received {} messages", c);
                long now = System.currentTimeMillis();
                wsReceived.put(now, data);

                if (c >= wsMessageNo) {
                    isWsTestComplete = true;
                }
            }

            @Override
            protected void onError(WebSocketChannel channel, Throwable error) {
                super.onError(channel, error);
                LOG.error("Websocket error", error);
                isWsTestComplete = true;
            }
        });
        webSocketChannel.resumeReceives();
    }


    public long getTestDurationMs() {
        return testDuration;
    }

    public TLongObjectHashMap<String> getWsReceived() {
        return wsReceived;
    }

    public boolean isWsTestComplete() {
        return isWsTestComplete;
    }

    public int getWsCount() {
        return wsCount.intValue();
    }

    public long[] getWsLatencies() {
        long[] times = wsReceived.keys();
        long[] latencies = new long[times.length];
        int i = 0;
        for (long time : times) {
            String value = wsReceived.get(time);
            JsonObject response = jsonParser.parse(value).getAsJsonObject();
            assertNotNull(response);
            JsonObject dataBag = response.getAsJsonObject("dataBag");
            JsonPrimitive timeJson = dataBag.getAsJsonPrimitive("t");
            long serverTime = timeJson.getAsLong();
            long diff = time - serverTime;
            latencies[i] = diff;
        }
        return latencies;
    }
}
