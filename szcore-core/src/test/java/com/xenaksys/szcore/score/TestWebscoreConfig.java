package com.xenaksys.szcore.score;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.MultiIntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.web.audience.WebAudienceScorePageRangeAssignmentType;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebscoreConfig;
import com.xenaksys.szcore.score.web.audience.config.WebscoreConfigLoader;
import com.xenaksys.szcore.score.web.audience.config.WebscorePageRangeConfig;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_SPEECH_SYNTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

        Map<String, Object> presetConfigs = preset1.getConfigs();
        assertFalse(presetConfigs.isEmpty());
        assertTrue(presetConfigs.containsKey(WEB_GRANULATOR));

        Object granulatorConfigObj = presetConfigs.get(WEB_GRANULATOR);
        assertNotNull(granulatorConfigObj);
        Map<String, Object> granulatorConfig = (Map<String, Object>) granulatorConfigObj;

        assertTrue(granulatorConfig.containsKey(WEB_CONFIG_MASTER_GAIN_VAL));
        Object masterGainObj = granulatorConfig.get(WEB_CONFIG_MASTER_GAIN_VAL);
        assertTrue(masterGainObj instanceof Double);
        double masterGain = (Double) masterGainObj;
        assertEquals(0.1, masterGain, 10E-5);

        List<WebscorePageRangeConfig> webscorePageRangeConfigs = config.getPageRangeConfigs();
        assertEquals(8, webscorePageRangeConfigs.size());

        WebscorePageRangeConfig first = webscorePageRangeConfigs.get(0);
        assertEquals(WebAudienceScorePageRangeAssignmentType.SEQ, first.getAssignmentType());
        assertEquals(new Integer(1), first.getTileRow());
        IntRange pageRange = new SequentalIntRange(1, 8);
        List<IntRange> rngs = new ArrayList<>();
        rngs.add(pageRange);
        MultiIntRange multiIntRange = new MultiIntRange(rngs);
        assertEquals(pageRange, first.getTileCols());
        assertEquals(multiIntRange, first.getPageRange());

        WebscorePageRangeConfig last = webscorePageRangeConfigs.get(7);
        assertEquals(WebAudienceScorePageRangeAssignmentType.RND, last.getAssignmentType());
        assertEquals(new Integer(8), last.getTileRow());
        pageRange = last.getPageRange();
        assertEquals(3 * 8, pageRange.getSize());
        assertEquals(8, first.getTileCols().getSize());

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

    @Test
    public void testGranulatorConfigLoad() throws Exception {
        PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        WebGranulatorConfig granulatorConfig = new WebGranulatorConfig(pcs);

        WebscoreConfig config = WebscoreConfigLoader.load(file);
        ScriptPreset preset1 = config.getPreset(1);
        Map<String, Object> presetConfigs = preset1.getConfigs();
        assertFalse(presetConfigs.isEmpty());
        assertTrue(presetConfigs.containsKey(WEB_GRANULATOR));

        Object granulatorConfigObj = presetConfigs.get(WEB_GRANULATOR);
        assertNotNull(granulatorConfigObj);
        Map<String, Object> granulatorConfigMap = (Map<String, Object>) granulatorConfigObj;
        granulatorConfig.update(granulatorConfigMap);

        assertEquals(0.1, granulatorConfig.getMasterGainVal(), 10E-5);
        assertEquals(-30.0, granulatorConfig.getSizeOscillator().getMinValue(), 10E-5);
        assertEquals(0.02, granulatorConfig.getPositionOscillator().getFrequencyLfoConfig().getFrequency(), 10E-5);
        assertEquals(-500.0, granulatorConfig.getPositionOscillator().getStartLfoConfig().getMinValue(), 10E-5);
        assertEquals(500.0, granulatorConfig.getPositionOscillator().getEndLfoConfig().getMaxValue(), 10E-5);
    }

    @Test
    public void testSpeechSynthConfigLoad() throws Exception {
        PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        WebSpeechSynthConfig speechSynthConfig = new WebSpeechSynthConfig(pcs);

        WebscoreConfig config = WebscoreConfigLoader.load(file);
        ScriptPreset preset1 = config.getPreset(1);
        Map<String, Object> presetConfigs = preset1.getConfigs();
        assertFalse(presetConfigs.isEmpty());
        assertTrue(presetConfigs.containsKey(WEB_GRANULATOR));

        Object speechConfigObj = presetConfigs.get(WEB_SPEECH_SYNTH);
        assertNotNull(speechConfigObj);
        Map<String, Object> speechConfigMap = (Map<String, Object>) speechConfigObj;
        speechSynthConfig.update(speechConfigMap);

        assertEquals(1.0, speechSynthConfig.getVolume(), 10E-5);
        assertEquals(250, speechSynthConfig.getInterruptTimeout());
    }
}

