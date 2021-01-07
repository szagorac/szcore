package com.xenaksys.szcore.score;


import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.time.TstClock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestWebScore {
    private static final String TEST_SCRIPT_PREFIX = "Test script";

    private WebScore webScore;
    private BeatId[] beatIds;

    @Before
    public void init() {
        ScoreProcessor scoreProcessor = Mockito.mock(ScoreProcessor.class);
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

        EnvelopeConfig envelopeConfig = granulatorConfig.getEnvelope();
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

        EnvelopeConfig envelopeConfig = granulatorConfig.getEnvelope();
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
