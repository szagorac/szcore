package com.xenaksys.szcore.server.web;

import com.xenaksys.szcore.util.NetUtil;
import io.undertow.client.ClientResponse;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
    public static final String HOST = "zscore";

    @Before
    public void init() {

    }

    @Test
    public void testSimpleBasic() throws Exception {
        //WebSocketClient13TestCase
        //final WebSocketChannel webSocketChannel = WebSocketClient.connectionBuilder(worker, DefaultServer.getBufferPool(), new URI(DefaultServer.getDefaultServerURL())).connect().get();

        ZscoreTestWebClient testWebClient = new ZscoreTestWebClient(URL, HOST, STRING_FILES, BINARY_FLIES);
        testWebClient.init();
        testWebClient.startTest();

        assertTrue(testWebClient.isTestComplete);

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

        LOG.info("Download size = {}", NetUtil.bytesToMbyte(downloadSize));
    }
}
