package com.xenaksys.szcore.server.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.xenaksys.szcore.util.MathUtil;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.undertow.client.*;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StringReadChannelListener;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.*;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertNotNull;

public class ZscoreTestWebClient {
    static final Logger LOG = LoggerFactory.getLogger(ZscoreTestWebClient.class);

    private static final OptionMap DEFAULT_OPTIONS;

    public static final int BUFFER_SIZE = 1024 * 16 - 20;
    public static final int MAX_POOL_SIZE = 1000;
    public static final int THREAD_LOCAL_CACHE_SIZE = 10;
    public static final int LEAK_DETECTION_PERC = 100;
    private static final AttachmentKey<String> RESPONSE_BODY = AttachmentKey.create(String.class);

    static {
        final OptionMap.Builder builder = OptionMap.builder()
                .set(Options.WORKER_IO_THREADS, 8)
                .set(Options.TCP_NODELAY, true)
                .set(Options.KEEP_ALIVE, true)
                .set(Options.WORKER_NAME, "ZScoreTestClient");

        DEFAULT_OPTIONS = builder.getMap();
    }

    private final Map<String, Integer> responseSizeBytes = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> responseCounter = new ConcurrentHashMap<>();
    private final TLongObjectHashMap<String> wsReceived = new TLongObjectHashMap<>();
    final Xnio xnio = Xnio.getInstance();

    private final String[] stringFiles;
    private final String[] binaryFlies;
    private final String url;
    private final String host;
    private final int clientNo;
    private final boolean isUseWebsocket;
    private final String wsUrl;
    private final int wsMessageNo;

    private UndertowClient client = createClient();
    private XnioWorker worker;
    private DebuggingSlicePool pool;
    private DebuggingSlicePool wspool;
    private List<ClientResponse> responses = new CopyOnWriteArrayList<>();
    private CountDownLatch latch;
    private ClientConnection connection;
    private WebSocketChannel webSocketChannel;
    private URI address;
    private URI wsaddress;
    private volatile boolean isTestComplete = false;
    private volatile boolean isWsTestComplete = false;
    private long testDuration = 0L;
    private volatile long endTime = 0L;
    private AtomicInteger wsCount = new AtomicInteger(0);
    private final JsonParser jsonParser = new JsonParser();

    public ZscoreTestWebClient(int clientNo, String url, String wsUrl, String host, String[] stringFiles, String[] binaryFlies,
                               boolean isUseWebsocket, int wsMessageNo) {
        this.stringFiles = stringFiles;
        this.binaryFlies = binaryFlies;
        this.url = url;
        this.host = host;
        this.clientNo = clientNo;
        this.isUseWebsocket = isUseWebsocket;
        this.wsUrl = wsUrl;
        this.wsMessageNo = wsMessageNo;
    }

    public void init() throws Exception {
        try {
            address = new URI(url);
            worker = xnio.createWorker(null, DEFAULT_OPTIONS);
            pool = new DebuggingSlicePool(new DefaultByteBufferPool(
                    true, BUFFER_SIZE, MAX_POOL_SIZE, THREAD_LOCAL_CACHE_SIZE, LEAK_DETECTION_PERC));
            connection = client.connect(address, worker, pool, OptionMap.EMPTY).get();

            int numberOfRequests = stringFiles.length + binaryFlies.length;
            latch = new CountDownLatch(numberOfRequests);

            if (isUseWebsocket) {
                wsaddress = new URI(wsUrl);
                wspool = new DebuggingSlicePool(new DefaultByteBufferPool(
                        true, BUFFER_SIZE, MAX_POOL_SIZE, THREAD_LOCAL_CACHE_SIZE, LEAK_DETECTION_PERC));

                webSocketChannel = WebSocketClient.connectionBuilder(worker, wspool, wsaddress).connect().get();
                initWebSocket();
            }
        } catch (Exception e) {
            LOG.error("Failed to initalise test", e);
            throw e;
        }
    }

    public void startTest() {
        if (client == null || worker == null) {
            LOG.error("Client not initialised");
            return;
        }

        long start = System.currentTimeMillis();
        try {
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    for (String htmlFile : stringFiles) {
                        responseCounter.put(htmlFile, new AtomicInteger(0));
                        final ClientRequest request = new ClientRequest().setMethod(Methods.GET).setPath(htmlFile);
                        request.getRequestHeaders().put(Headers.HOST, host);
                        connection.sendRequest(request, createClientCallback(responses, htmlFile));
                    }
                }

            });

            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    for (String binaryFilePath : binaryFlies) {
                        responseCounter.put(binaryFilePath, new AtomicInteger(0));
                        final ClientRequest request = new ClientRequest().setMethod(Methods.GET).setPath(binaryFilePath);
                        request.getRequestHeaders().put(Headers.HOST, host);
                        connection.sendRequest(request, createFileCallback(responses));
                    }
                }

            });

            isTestComplete = latch.await(10, TimeUnit.SECONDS);
            LOG.info("Client No: {}, id Complete: {}", clientNo, isTestComplete);

            long byteCount = 0;
            for (String htmlFile : stringFiles) {
                byteCount += responseSizeBytes.get(htmlFile);
            }
            for (String binaryFile : binaryFlies) {
                byteCount += responseSizeBytes.get(binaryFile);
            }

            testDuration = endTime - start;
            double mb = MathUtil.bytesToMbyte(byteCount);

            LOG.debug("Request size: {}MB, took {} ms", MathUtil.roundTo2DecimalPlaces(mb), testDuration);

        } catch (Exception e) {
            LOG.error("Failed tp execute test", e);
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    private ClientCallback<ClientExchange> createFileCallback(final List<ClientResponse> responses) {
        return new ClientCallback<ClientExchange>() {
            @Override
            public void completed(ClientExchange result) {
                result.setResponseListener(new ClientCallback<ClientExchange>() {
                    @Override
                    public void completed(final ClientExchange result) {
                        String path = result.getRequest().getPath();
                        ClientResponse clientResponse = result.getResponse();
                        FileChannelListener fileChannelListener = new FileChannelListener(result.getConnection().getBufferPool(), path);
                        fileChannelListener.handleEvent(result.getResponseChannel());

                        AtomicInteger byteCounter = responseCounter.get(path);
                        responseSizeBytes.put(path, byteCounter.intValue());
                        byteCounter.set(0);
                        responses.add(clientResponse);

                        setEndTime();
                        latch.countDown();
                    }

                    @Override
                    public void failed(IOException e) {
                        LOG.error("Failed File Response listener completed", e);
                        setEndTime();
                        latch.countDown();
                    }
                });
                try {
                    result.getRequestChannel().shutdownWrites();
                    if (!result.getRequestChannel().flush()) {
                        result.getRequestChannel().getWriteSetter().set(ChannelListeners.<StreamSinkChannel>flushingChannelListener(null, null));
                        result.getRequestChannel().resumeWrites();
                    }
                } catch (IOException e) {
                    LOG.error("Failed FileCallback completed", e);
                    setEndTime();
                    latch.countDown();
                }
            }

            @Override
            public void failed(IOException e) {
                LOG.error("Failed FileCallback", e);
                setEndTime();
                latch.countDown();
            }
        };
    }

    class FileChannelListener implements ChannelListener<StreamSourceChannel> {
        private final ByteBufferPool bufferPool;
        private final String path;

        public FileChannelListener(ByteBufferPool bufferPool, String path) {
            this.bufferPool = bufferPool;
            this.path = path;
        }

        @Override
        public void handleEvent(StreamSourceChannel channel) {
            PooledByteBuffer resource = bufferPool.allocate();
            ByteBuffer buffer = resource.getBuffer();
            try {
                int r = 0;
                do {
                    r = channel.read(buffer);
                    if (r == 0) {
                        channel.getReadSetter().set(this);
                        channel.resumeReads();
                    } else if (r == -1) {
                        IoUtils.safeClose(channel);
                    } else {
                        buffer.flip();
                        AtomicInteger byteCounter = responseCounter.computeIfAbsent(path, v -> new AtomicInteger());
                        int counter = byteCounter.intValue();
                        int newVal = counter + r;
                        while (!byteCounter.compareAndSet(counter, newVal)) {
                            counter = byteCounter.get();
                            newVal = counter + r;
                        }
                    }
                } while (r > 0);
            } catch (IOException e) {
                LOG.error("Failed to read file", e);
            } finally {
                resource.close();
            }
        }
    }

    ;


    private ClientCallback<ClientExchange> createClientCallback(final List<ClientResponse> responses, final String path) {
        return new ClientCallback<ClientExchange>() {
            @Override
            public void completed(ClientExchange result) {
                result.setResponseListener(new ClientCallback<ClientExchange>() {
                    @Override
                    public void completed(final ClientExchange result) {
                        responses.add(result.getResponse());
                        new StringReadChannelListener(result.getConnection().getBufferPool()) {
                            @Override
                            protected void stringDone(String string) {
                                result.getResponse().putAttachment(RESPONSE_BODY, string);

                                int r = string.getBytes().length;
                                AtomicInteger byteCounter = responseCounter.computeIfAbsent(path, v -> new AtomicInteger());
                                int counter = byteCounter.intValue();
                                int newVal = counter + r;
                                while (!byteCounter.compareAndSet(counter, newVal)) {
                                    counter = byteCounter.get();
                                    newVal = counter + r;
                                }

                                responseSizeBytes.put(path, byteCounter.intValue());
                                byteCounter.set(0);

                                setEndTime();
                                latch.countDown();
                            }

                            @Override
                            protected void error(IOException e) {
                                e.printStackTrace();

                                setEndTime();
                                latch.countDown();
                            }
                        }.setup(result.getResponseChannel());
                    }

                    @Override
                    public void failed(IOException e) {
                        LOG.error("Failed setResponseListener", e);
                        setEndTime();
                        latch.countDown();
                    }
                });
                try {
                    result.getRequestChannel().shutdownWrites();
                    if (!result.getRequestChannel().flush()) {
                        result.getRequestChannel().getWriteSetter().set(ChannelListeners.<StreamSinkChannel>flushingChannelListener(null, null));
                        result.getRequestChannel().resumeWrites();
                    }
                } catch (IOException e) {
                    LOG.error("Failed result.getRequestChannel().shutdownWrites()", e);
                    setEndTime();
                    latch.countDown();
                }
            }

            @Override
            public void failed(IOException e) {
                LOG.error("Failed ClientCallback", e);
                setEndTime();
                latch.countDown();
            }
        };
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

    static UndertowClient createClient() {
        return UndertowClient.getInstance();
    }

    private void setEndTime() {
        long now = System.currentTimeMillis();
        if (now > endTime) {
            endTime = now;
        }
    }

    public Map<String, Integer> getResponseSizeBytes() {
        return responseSizeBytes;
    }

    public List<ClientResponse> getResponses() {
        return responses;
    }

    public boolean isTestComplete() {
        return isTestComplete;
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
