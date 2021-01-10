package com.xenaksys.szcore.score;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.web.WebscorePageRangeAssignmentType;
import com.xenaksys.szcore.score.web.config.WebscoreConfig;
import com.xenaksys.szcore.score.web.config.WebscoreConfigLoader;
import com.xenaksys.szcore.score.web.config.WebscorePageRangeConfig;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestWebscoreConfig {
    static final Logger LOG = LoggerFactory.getLogger(TestWebscoreConfig.class);
    File file;

    @Before
    public void init() {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("webscore/webscoreConfig.yml").getFile());
    }

    @Test
    public void testConfigLoad() throws Exception {
        WebscoreConfig config = WebscoreConfigLoader.load(file);
        assertNotNull(config);

        assertEquals("Test Score", config.getScoreName());

        TIntObjectHashMap<ScriptPreset> presets = config.getPresets();
        assertEquals(3, presets.size());

        ScriptPreset preset1 = config.getPreset(1);
        assertEquals(1, preset1.getId());
        assertEquals(4, preset1.getScripts().size());
        assertEquals("webScore.resetState()", preset1.getScripts().get(0));

        List<WebscorePageRangeConfig> webscorePageRangeConfigs = config.getPageRangeConfigs();
        assertEquals(8, webscorePageRangeConfigs.size());

        WebscorePageRangeConfig first = webscorePageRangeConfigs.get(0);
        assertEquals(WebscorePageRangeAssignmentType.SEQ, first.getAssignmentType());
        assertEquals(new Integer(1), first.getTileRow());
        IntRange pageRange = new IntRange(1, 8);
        assertEquals(pageRange, first.getTileCols());
        assertEquals(pageRange, first.getPageRange());

        int[][] tilePageMap = config.getTilePageMap();
        assertEquals(1, tilePageMap[0][0]);
        assertEquals(2, tilePageMap[0][1]);
        assertEquals(8, tilePageMap[0][7]);

        assertEquals(1, tilePageMap[1][0]);
        assertEquals(2, tilePageMap[1][1]);
        assertEquals(8, tilePageMap[1][7]);

        assertEquals(17, tilePageMap[3][0]);
        assertEquals(18, tilePageMap[3][1]);
        assertEquals(24, tilePageMap[3][7]);

        assertEquals(17, tilePageMap[4][0]);
        assertEquals(18, tilePageMap[4][1]);
        assertEquals(24, tilePageMap[4][7]);

        int pageNo = config.getPageNo(1, 1);
        assertEquals(1, pageNo);
        pageNo = config.getPageNo(1, 5);
        assertEquals(5, pageNo);
        pageNo = config.getPageNo(1, 7);
        assertEquals(7, pageNo);

        pageNo = config.getPageNo(2, 1);
        assertEquals(1, pageNo);
        pageNo = config.getPageNo(2, 5);
        assertEquals(5, pageNo);
        pageNo = config.getPageNo(2, 7);
        assertEquals(7, pageNo);

        pageNo = config.getPageNo(4, 1);
        assertEquals(17, pageNo);
        pageNo = config.getPageNo(4, 5);
        assertEquals(21, pageNo);
        pageNo = config.getPageNo(4, 7);
        assertEquals(23, pageNo);

        pageNo = config.getPageNo(5, 1);
        assertEquals(17, pageNo);
        pageNo = config.getPageNo(5, 5);
        assertEquals(21, pageNo);
        pageNo = config.getPageNo(5, 7);
        assertEquals(23, pageNo);

        pageNo = config.getPageNo(-1, 9);
        assertEquals(-1, pageNo);
    }
}

