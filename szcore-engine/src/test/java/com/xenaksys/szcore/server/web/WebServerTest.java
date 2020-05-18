package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.util.MathUtil;
import com.xenaksys.szcore.util.ThreadUtil;
import io.undertow.client.ClientResponse;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WebServerTest {
    static final Logger LOG = LoggerFactory.getLogger(WebServerTest.class);

    public static final String[] STRING_FILES = {
            "/test.html",
            "/js/gsap/minified/gsap.min.js",
            "/js/util/zsUtil.js",
            "/js/net/zsNet.js",
            "/js/svg/zsSvg.js",
            "/js/audio/zsGranulator.js",
            "/js/audio/zsAudio.js",
            "/js/zscore.js",
            "css/zscore.css",
            "img/manifest.json",
    };
    public static final String[] BINARY_FLIES = {
            "/audio/violin-tuning.mp3",
            "/audio/UnionRoseE2m.mp3",
            "/audio/UnionRoseE3.mp3",
            "/audio/UnionRoseE4.mp3",
            "/audio/UnionRoseE5.mp3",
            "/audio/ligetiS1-1.mp3",
            "img/favicon.ico",
            "img/favicon-16x16.png"
    };

    public static final String URL = "http://zscore";
    public static final String WS_URL = "http://zscore/wsoc";
    public static final String HOST = "zscore";


    @Before
    public void init() {

    }

    @Test
    public void testSimpleBasic() throws Exception {
        ZscoreTestWebClient testWebClient = new ZscoreTestWebClient(1, URL, WS_URL, HOST, STRING_FILES, BINARY_FLIES,
                false, 8);
        testWebClient.init();
        testWebClient.startTest();

        assertTrue(testWebClient.isTestComplete());

        int reqeustNo = STRING_FILES.length + BINARY_FLIES.length;
        List<ClientResponse> responses = testWebClient.getResponses();
        assertEquals(reqeustNo, responses.size());

        long downloadSize = 0L;
        Map<String, Integer> responseSizes = testWebClient.getResponseSizeBytes();
        for (String path : responseSizes.keySet()) {
            Integer size = responseSizes.get(path);
            assertTrue(size > 0);
            downloadSize += size;
        }
        double mb = MathUtil.bytesToMbyte(downloadSize);
        LOG.info("Download size = {}MB", MathUtil.roundTo2DecimalPlaces(mb));
    }

    @Test
    public void testMultipleClients() throws Exception {
        int clientNo = 250;
        int naxSleepBetweenRequests = 1000;

        List<ZscoreTestWebClient> clients = new ArrayList<>(clientNo);

        for (int i = 0; i < clientNo; i++) {
            LOG.info("Initialising client: {}", i);
            ZscoreTestWebClient testWebClient = new ZscoreTestWebClient(i, URL, WS_URL, HOST, STRING_FILES, BINARY_FLIES,
                    false, 8);
            testWebClient.init();
            clients.add(testWebClient);
        }

        ThreadUtil.doSleep(Thread.currentThread(), 2000);

        ExecutorService executorService = Executors.newFixedThreadPool(clientNo);

        int no = 1;
        for (ZscoreTestWebClient client : clients) {
            executorService.submit(new ClientRunner(client, no));
            no++;
            long sleep = ThreadLocalRandom.current().nextInt(0, naxSleepBetweenRequests + 1);
            long clNo = ThreadLocalRandom.current().nextInt(1, 6);
            if (no % clNo == 0) {
                ThreadUtil.doSleep(Thread.currentThread(), sleep);
            }
        }

        // Wait for tests to complete
        while (!areTestsComplete(clients)) {
            ThreadUtil.doSleep(Thread.currentThread(), 500);
        }

        LOG.info("### tests Complete");

        long[] durations = new long[clientNo - 1];
        int i = 0;
        for (ZscoreTestWebClient client : clients) {
            assertTrue(client.isTestComplete());

            int reqeustNo = STRING_FILES.length + BINARY_FLIES.length;
            List<ClientResponse> responses = client.getResponses();
            assertEquals(reqeustNo, responses.size());

            long testDuration = client.getTestDurationMs();
            if (i > 0) {
                durations[i - 1] = testDuration;
            }
            i++;

            long downloadSize = 0L;
            Map<String, Integer> responseSizes = client.getResponseSizeBytes();
            for (String path : responseSizes.keySet()) {
                Integer size = responseSizes.get(path);
                assertTrue(size > 0);
                downloadSize += size;
            }

            double mb = MathUtil.bytesToMbyte(downloadSize);

            LOG.info("Test duration: {}ms, Download size = {}MB", testDuration, MathUtil.roundTo2DecimalPlaces(mb));
        }

        long perc90 = MathUtil.percentile(durations, 90);
        long perc95 = MathUtil.percentile(durations, 90);
        long perc100 = MathUtil.percentile(durations, 100);
        long mean = MathUtil.mean(durations);
        long min = MathUtil.min(durations);

        LOG.info("Average Test duration: {}ms, min: {}, max: {} Percentile: 90th: {}, 95th: {}", mean, min, perc100, perc90, perc95);
    }

    @Test
    public void testWebsocketReceive() throws Exception {
        ZscoreTestWebClient testWebClient = new ZscoreTestWebClient(1, URL, WS_URL, HOST, STRING_FILES, BINARY_FLIES,
                true, 8);
        testWebClient.init();
        testWebClient.startTest();

        assertTrue(testWebClient.isTestComplete());

        int reqeustNo = STRING_FILES.length + BINARY_FLIES.length;
        List<ClientResponse> responses = testWebClient.getResponses();
        assertEquals(reqeustNo, responses.size());

        long downloadSize = 0L;
        Map<String, Integer> responseSizes = testWebClient.getResponseSizeBytes();
        for (String path : responseSizes.keySet()) {
            Integer size = responseSizes.get(path);
            assertTrue(size > 0);
            downloadSize += size;
        }
        double mb = MathUtil.bytesToMbyte(downloadSize);
        LOG.info("Download size = {}MB", MathUtil.roundTo2DecimalPlaces(mb));

        while (!testWebClient.isWsTestComplete()) {
            ThreadUtil.doSleep(Thread.currentThread(), 1000);
        }

        long[] latencies = testWebClient.getWsLatencies();
        long perc90 = MathUtil.percentile(latencies, 90);
        long perc95 = MathUtil.percentile(latencies, 90);
        long perc100 = MathUtil.percentile(latencies, 100);
        long mean = MathUtil.mean(latencies);
        long min = MathUtil.min(latencies);

        LOG.info("Websocket Average Test latency: {}ms, min: {}, max: {} Percentile: 90th: {}, 95th: {}", mean, min, perc100, perc90, perc95);
    }

    @Test
    public void testWebsocketMultipleClients() throws Exception {
        int clientNo = 10;
        int naxSleepBetweenRequests = 1000;

        List<ZscoreTestWebClient> clients = new ArrayList<>(clientNo);

        for (int i = 0; i < clientNo; i++) {
            LOG.info("Initialising client: {}", i);
            ZscoreTestWebClient testWebClient = new ZscoreTestWebClient(i, URL, WS_URL, HOST, STRING_FILES, BINARY_FLIES,
                    true, 8);
            testWebClient.init();
            clients.add(testWebClient);
        }

        ThreadUtil.doSleep(Thread.currentThread(), 2000);

        ExecutorService executorService = Executors.newFixedThreadPool(clientNo);

        int no = 1;
        for (ZscoreTestWebClient client : clients) {
            executorService.submit(new ClientRunner(client, no));
            no++;
            long sleep = ThreadLocalRandom.current().nextInt(0, naxSleepBetweenRequests + 1);
            long clNo = ThreadLocalRandom.current().nextInt(1, 6);
            if (no % clNo == 0) {
                ThreadUtil.doSleep(Thread.currentThread(), sleep);
            }
        }

        // Wait for http tests to complete
        while (!areTestsComplete(clients)) {
            ThreadUtil.doSleep(Thread.currentThread(), 500);
        }

        LOG.info("### HTTP tests Complete");

        // Wait for http tests to complete
        while (!areWsTestsComplete(clients)) {
            ThreadUtil.doSleep(Thread.currentThread(), 500);
        }

        for (ZscoreTestWebClient client : clients) {
            long[] latencies = client.getWsLatencies();
            long perc90 = MathUtil.percentile(latencies, 90);
            long perc95 = MathUtil.percentile(latencies, 90);
            long perc100 = MathUtil.percentile(latencies, 100);
            long mean = MathUtil.mean(latencies);
            long min = MathUtil.min(latencies);

            LOG.info("Websocket Average Test latency: {}ms, min: {}, max: {} Percentile: 90th: {}, 95th: {}", mean, min, perc100, perc90, perc95);
        }
    }

    private boolean areTestsComplete(List<ZscoreTestWebClient> clients) {
        for (ZscoreTestWebClient client : clients) {
            if (!client.isTestComplete()) {
                return false;
            }
        }
        return true;
    }

    private boolean areWsTestsComplete(List<ZscoreTestWebClient> clients) {
        for (ZscoreTestWebClient client : clients) {
            if (!client.isWsTestComplete()) {
                return false;
            }
        }
        return true;
    }

    class ClientRunner implements Runnable {

        private int clientNo;
        private final ZscoreTestWebClient client;

        public ClientRunner(ZscoreTestWebClient client, int clientNo) {
            this.client = client;
            this.clientNo = clientNo;
        }

        @Override
        public void run() {
            LOG.info("Starting test for client: {}", clientNo);
            client.startTest();
        }
    }
}
