package com.xenaksys.szcore.score;


import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.ScriptingEngineEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.scripting.ScoreScriptingEngine;
import com.xenaksys.szcore.scripting.ScriptingEngineConfig;
import com.xenaksys.szcore.scripting.ScriptingEngineConfigLoader;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;
import com.xenaksys.szcore.time.TstClock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestScriptingEngine {
    private static final String TEST_SCRIPT_PREFIX = "Test script";

    private ScoreScriptingEngine scriptingEngine;
    private File file;

    @Before
    public void init() throws Exception {
        ScoreProcessor scoreProcessor = Mockito.mock(ScoreProcessor.class);
        EventFactory eventFactory = new EventFactory();
        Clock clock = new TstClock();

        scriptingEngine = new ScoreScriptingEngine(scoreProcessor, eventFactory, clock);

        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("scriptingEngine/scriptingEngineConfig.yml").getFile());
        ScriptingEngineConfig config = ScriptingEngineConfigLoader.load(file);
        scriptingEngine.setConfig(config);
        scriptingEngine.init(null);
    }

    @Test
    public void testResetEvent() {
        BeatId beatId = TstScoreUtil.createBeatId(1, 1, 1, 1, 1);
        List<ScriptingEngineScript> scripts = new ArrayList<>();
        String content = "sce.reset(1)";
        ScriptingEngineScript script = new ScriptingEngineScript(new IntId(1), beatId, content, false, false);
        scripts.add(script);
        ScriptingEngineEvent event = new ScriptingEngineEvent(beatId, scripts, 0L);
        scriptingEngine.processEvent(event);
    }

    @Test
    public void testSetRndStrategy() {
        BeatId beatId = TstScoreUtil.createBeatId(1, 1, 1, 1, 1);
        List<ScriptingEngineScript> scripts = new ArrayList<>();
        String content = "sce.setRndStrategy([2,2])";
        ScriptingEngineScript script = new ScriptingEngineScript(new IntId(1), beatId, content, false, false);
        scripts.add(script);
        ScriptingEngineEvent event = new ScriptingEngineEvent(beatId, scripts, 0L);
        scriptingEngine.processEvent(event);
    }

    @Test
    public void testPresets() {
        ScriptPreset preset1 = scriptingEngine.getConfig().getPreset(1);
        assertNotNull(preset1);
        List<String> scripts = preset1.getScripts();
        assertEquals(2, scripts.size());

        ScriptPreset preset2 = scriptingEngine.getConfig().getPreset(2);
        assertNotNull(preset2);
        scripts = preset2.getScripts();
        assertEquals(1, scripts.size());
    }

}
