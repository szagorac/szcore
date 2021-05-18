package com.xenaksys.szcore.score;


import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.score.web.WebScore;
import com.xenaksys.szcore.score.web.WebScoreScript;
import com.xenaksys.szcore.score.web.config.WebEnvelopeConfig;
import com.xenaksys.szcore.score.web.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.export.TileExport;
import com.xenaksys.szcore.score.web.export.WebActionExport;
import com.xenaksys.szcore.score.web.export.WebElementStateExport;
import com.xenaksys.szcore.score.web.export.WebGranulatorConfigExport;
import com.xenaksys.szcore.score.web.export.WebInstructionsExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.export.WebSpeechSynthConfigExport;
import com.xenaksys.szcore.score.web.export.WebSpeechSynthStateExport;
import com.xenaksys.szcore.time.TstClock;
import com.xenaksys.szcore.web.WebActionType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_START;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_TEXT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_VOLUME;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CENTRE_SHAPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STATE_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILES;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ZOOM_LEVEL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestWebScore {
    private static final String TEST_SCRIPT_PREFIX = "Test script";

    private WebScore webScore;
    private BeatId[] beatIds;

    @Before
    public void init() {
        ScoreProcessor scoreProcessor = Mockito.mock(ScoreProcessor.class);
        BasicScore score = Mockito.mock(BasicScore.class);
        BasicInstrument instrument = Mockito.mock(BasicInstrument.class);
        List<Instrument> instruments = new ArrayList<>();
        instruments.add(instrument);
        BasicPage page = Mockito.mock(BasicPage.class);

        when(scoreProcessor.getScore()).thenReturn(score);
        when(score.getInstruments()).thenReturn(instruments);
        when(score.getPage(any())).thenReturn(page);
        when(page.getDurationMs()).thenReturn(5000L);

        EventFactory eventFactory = new EventFactory();
        Clock clock = new TstClock();

        webScore = new WebScore(scoreProcessor, eventFactory, clock);
        beatIds = new BeatId[10];

        addBeat(1, true);
        addBeat(2, false);
        addBeat(3, false);
        addBeat(4, true);
        addBeat(5, false);
        addBeat(6, false);
        addBeat(7, false);
        addBeat(8, true);
        addBeat(9, false);
        addBeat(10, false);

        webScore.init(null);
    }

    @Test
    public void testResetBeat() {
        checkResetBeat(1, 1);
        checkResetBeat(3, 1);
        checkResetBeat(4, 4);
        checkResetBeat(5, 4);
        checkResetBeat(7, 4);
        checkResetBeat(8, 8);
        checkResetBeat(9, 8);
        checkResetBeat(10, 8);
    }

    @Test
    public void testGranulatorConfigAsString() {
        Map<String, Object> params = new HashMap<>();
        WebGranulatorConfig granulatorConfig = webScore.getGranulatorConfig();

        params.put("masterGainVal", "0.5");
        params.put("maxGrains", "32");
        params.put("grain.sizeMs", "88");
        params.put("panner.isUsePanner", "true");
        params.put("panner.panningModel", "HRTF");
        params.put("panner.distanceModel", "rubbish");
        params.put("envelope.attackTime", "0.6");
        params.put("envelope.decayTime", "0.0");
        params.put("envelope.sustainTime", "0.0");
        params.put("envelope.releaseTime", "0.4");

        webScore.setGranulatorConfig(params);

        assertEquals(0.5, granulatorConfig.getMasterGainVal(), 10E-5);
        assertEquals(32, granulatorConfig.getMaxGrains());

        int size = granulatorConfig.getGrain().getSizeMs();
        assertEquals(88, size);

        PanningModel panningModel = PanningModel.fromName(granulatorConfig.getPanner().getPanningModel());
        assertEquals(PanningModel.HRTF, panningModel);

        PannerDistanceModel distanceModel = PannerDistanceModel.fromName(granulatorConfig.getPanner().getPanningModel());
        assertEquals(PannerDistanceModel.LINEAR, distanceModel);

        WebEnvelopeConfig envelopeConfig = granulatorConfig.getEnvelope();
        double attack = envelopeConfig.getAttackTime();
        assertEquals(0.6, attack, 10e-5);
        double release = envelopeConfig.getReleaseTime();
        assertEquals(0.4, release, 10e-5);
    }

    @Test
    public void testGranulatorConfigAsObject() {
        Map<String, Object> params = new HashMap<>();
        WebGranulatorConfig granulatorConfig = webScore.getGranulatorConfig();

        params.put("masterGainVal", "0.5");
        params.put("maxGrains", "32");
        params.put("grain.sizeMs", 88);
        params.put("panner.isUsePanner", true);
        params.put("panner.panningModel", "HRTF");
        params.put("panner.distanceModel", "rubbish");
        params.put("envelope.attackTime", 0.6);
        params.put("envelope.decayTime", 0.0);
        params.put("envelope.sustainTime", 0.0);
        params.put("envelope.releaseTime", 0.4);

        webScore.setGranulatorConfig(params);

        assertEquals(0.5, granulatorConfig.getMasterGainVal(), 10E-5);
        assertEquals(32, granulatorConfig.getMaxGrains());

        int size = granulatorConfig.getGrain().getSizeMs();
        assertEquals(88, size);

        PanningModel panningModel = PanningModel.fromName(granulatorConfig.getPanner().getPanningModel());
        assertEquals(PanningModel.HRTF, panningModel);

        PannerDistanceModel distanceModel = PannerDistanceModel.fromName(granulatorConfig.getPanner().getPanningModel());
        assertEquals(PannerDistanceModel.LINEAR, distanceModel);

        WebEnvelopeConfig envelopeConfig = granulatorConfig.getEnvelope();
        double attack = envelopeConfig.getAttackTime();
        assertEquals(0.6, attack, 10e-5);
        double release = envelopeConfig.getReleaseTime();
        assertEquals(0.4, release, 10e-5);
    }

    @Test
    public void testSpeechSynthConfigAsObject() {
        Map<String, Object> params = new HashMap<>();
        params.put("volume", 0.5);
        params.put("pitch", 2.6);
        params.put("rate", 0.2);
        params.put("lang", "rubbish");
        params.put("maxVoiceLoadAttempts", 7);
        params.put("maxUtterances", 44);
        params.put("utteranceTimeoutSec", 40);
        params.put("isInterrupt", true);
        params.put("interruptTimeout", 123);

        webScore.setSpeechSynthConfig(params);
        WebSpeechSynthConfig speechSynthConfigAfter = webScore.getSpeechSynthConfig();
        assertEquals(0.5, speechSynthConfigAfter.getVolume(), 10e-5);
        assertEquals(2.0, speechSynthConfigAfter.getPitch(), 10e-5);
        assertEquals(0.2, speechSynthConfigAfter.getRate(), 10e-5);
        assertEquals("en-GB", speechSynthConfigAfter.getLang());
        assertEquals(5, speechSynthConfigAfter.getMaxUtterances());
        assertEquals(40, speechSynthConfigAfter.getUtteranceTimeoutSec());
        assertTrue(speechSynthConfigAfter.isInterrupt());
        assertEquals(123, speechSynthConfigAfter.getInterruptTimeout());
    }

    @Test
    public void testStateDeltaTiles() {
        int[] rows = {2, 3, 4, 5, 6, 7, 8};
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        webScore.setVisibleRows(rows);
        Map<String, Object> delta = export.getDelta();
        assertEquals(1, delta.size());

        assertTrue(delta.containsKey(WEB_OBJ_TILES));
        Object tilesObj = delta.get(WEB_OBJ_TILES);

        assertTrue(tilesObj instanceof ArrayList);
        ArrayList<TileExport> tiles = (ArrayList<TileExport>) tilesObj;
        assertEquals(8, tiles.size());
        TileExport tile = tiles.iterator().next();
        assertFalse(tile.getState().isVisible());

        webScore.resetStateDelta();
        List<String> playTiles = new ArrayList<>();
        playTiles.add("t1-1");
        webScore.setPlayingNextTiles(playTiles);
        assertEquals(1, delta.size());
        assertTrue(delta.containsKey(WEB_OBJ_TILES));
        tiles = (ArrayList<TileExport>) delta.get(WEB_OBJ_TILES);
        assertEquals(1, tiles.size());
        tile = tiles.iterator().next();
        assertEquals("t1-1", tile.getId());
        assertTrue(tile.getState().isPlayingNext());

        webScore.resetStateDelta();
        webScore.playNextTilesInternal(null);
        assertEquals(2, delta.size());
        assertTrue(delta.containsKey(WEB_OBJ_TILES));
        tiles = (ArrayList<TileExport>) delta.get(WEB_OBJ_TILES);
        assertEquals(1, tiles.size());
        tile = tiles.iterator().next();
        assertEquals("t1-1", tile.getId());
        assertTrue(tile.getState().isPlaying());
        assertFalse(tile.getState().isPlayingNext());
        assertTrue(delta.containsKey(WEB_OBJ_ACTIONS));
        ArrayList<WebActionExport> actions = (ArrayList<WebActionExport>) delta.get(WEB_OBJ_ACTIONS);
        assertEquals(1, actions.size());
        WebActionExport action = actions.iterator().next();
        assertEquals(WEB_ACTION_ID_START, action.getId());
        assertEquals(WebActionType.ALPHA, action.getActionType());
        List<String> actionElements = action.getElementIds();
        assertEquals(1, actionElements.size());
        String elId = actionElements.get(0);
        assertEquals("t1-1", elId);

        webScore.resetStateDelta();
        String[] tileIds = new String[]{"t1-1", "t1-2"};
        String[] values = new String[]{"txt1", "txt2"};
        webScore.setTileTexts(tileIds, values);
        assertTrue(delta.containsKey(WEB_OBJ_TILES));
        tiles = (ArrayList<TileExport>) delta.get(WEB_OBJ_TILES);
        assertEquals(2, tiles.size());
        tile = tiles.iterator().next();
        assertEquals("t1-1", tile.getId());
        assertEquals("txt1", tile.getTileText().getValue());
    }

    @Test
    public void testStateDeltaInstructions() {
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        webScore.setInstructions("Instr1", "Instr2", null);
        Map<String, Object> delta = export.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_INSTRUCTIONS));
        WebInstructionsExport instructions = (WebInstructionsExport) delta.get(WEB_OBJ_INSTRUCTIONS);
        assertEquals("Instr1", instructions.getLine1());
        assertEquals("Instr2", instructions.getLine2());
        assertEquals("", instructions.getLine3());
        assertTrue(instructions.isVisible());
    }

    @Test
    public void testStateDeltaElementState() {
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        String[] elementIds = new String[]{WEB_OBJ_CENTRE_SHAPE};
        webScore.setVisible(elementIds, true);
        Map<String, Object> delta = export.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_CENTRE_SHAPE));
        WebElementStateExport elementStateExport = (WebElementStateExport) delta.get(WEB_OBJ_CENTRE_SHAPE);
        assertEquals(WEB_OBJ_CENTRE_SHAPE, elementStateExport.getId());
        assertTrue(elementStateExport.isVisible());
    }

    @Test
    public void testStateDeltaZoomLevel() {
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        webScore.setZoomLevel(WEB_OBJ_CENTRE_SHAPE);
        Map<String, Object> delta = export.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_ZOOM_LEVEL));
        String zoomLevel = (String) delta.get(WEB_OBJ_ZOOM_LEVEL);
        assertEquals(WEB_OBJ_CENTRE_SHAPE, zoomLevel);
    }

    @Test
    public void testStateDeltaGranulatorConfig() {
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        webScore.setGranulatorConfigParam(WEB_CONFIG_MASTER_GAIN_VAL, 0.2);
        Map<String, Object> delta = export.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_CONFIG_GRANULATOR));
        WebGranulatorConfigExport granulatorConfig = (WebGranulatorConfigExport) delta.get(WEB_OBJ_CONFIG_GRANULATOR);
        assertEquals(0.2, granulatorConfig.getMasterGainVal(), 10E-5);
    }

    @Test
    public void testStateDeltaSpeechConfig() {
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        webScore.setSpeechSynthConfigParam(WEB_CONFIG_VOLUME, 0.2);
        Map<String, Object> delta = export.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_CONFIG_SPEECH_SYNTH));
        WebSpeechSynthConfigExport config = (WebSpeechSynthConfigExport) delta.get(WEB_OBJ_CONFIG_SPEECH_SYNTH);
        assertEquals(0.2, config.getVolume(), 10E-5);
    }

    @Test
    public void testStateDeltaSpeechState() {
        WebScoreStateDeltaExport export = webScore.getStateDeltaExport();
        webScore.resetStateDelta();

        String speakText = "Speak This";
        webScore.setSpeechSynthStateParam(WEB_CONFIG_SPEECH_TEXT, speakText);
        Map<String, Object> delta = export.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_STATE_SPEECH_SYNTH));
        WebSpeechSynthStateExport config = (WebSpeechSynthStateExport) delta.get(WEB_OBJ_STATE_SPEECH_SYNTH);
        assertEquals(speakText, config.getSpeechText());
    }

    private void checkResetBeat(int beatNo, int resetBeatNo) {
        BeatId beatId = beatIds[beatNo - 1];
        BeatId resetBeaId = beatIds[resetBeatNo - 1];
        List<WebScoreScript> scripts = webScore.getBeatResetScripts(beatId);
        assertEquals(1, scripts.size());
        WebScoreScript webScoreScript = scripts.get(0);
        String content = TEST_SCRIPT_PREFIX + resetBeatNo;
        assertEquals(content, webScoreScript.getContent());
        assertEquals(resetBeaId, webScoreScript.getBeatId());
    }

    public void addBeat(int beatNo, boolean isReset) {
        BeatId beatId = TstScoreUtil.createBeatId(beatNo, 1, 1, 1, beatNo);
        beatIds[beatNo - 1] = beatId;
        String content = TEST_SCRIPT_PREFIX + beatNo;
        WebScoreScript webScoreScript = new WebScoreScript(new IntId(beatNo), beatId, content, isReset, false);
        webScore.addBeatScript(beatId, webScoreScript);
    }

}
