package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.ScriptPreset;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
        WebscorePresetConfig config = WebscoreConfigLoader.loadWebScorePresets(file);
        assertNotNull(config);

        assertEquals("Test Score", config.getScoreName());

        TIntObjectHashMap<ScriptPreset> presets = config.getPresets();
        assertEquals(3, presets.size());

        ScriptPreset preset1 = config.getPreset(1);
        assertEquals(1, preset1.getId());
        assertEquals(4, preset1.getScripts().size());
        assertEquals("webScore.resetState()", preset1.getScripts().get(0));

    }
}

