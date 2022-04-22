package com.xenaksys.szcore.score;


import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.score.delegate.web.dialogs.DialogsWebAudienceProcessor;
import com.xenaksys.szcore.score.delegate.web.dialogs.DialogsWebAudienceStateDeltaTracker;
import com.xenaksys.szcore.score.web.audience.config.WebPlayerConfig;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebPlayerConfigExport;
import com.xenaksys.szcore.time.TstClock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_PLAYER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestDIalogsWebAudienceScore {
    private static final String TEST_SCRIPT_PREFIX = "Test script";

    private DialogsWebAudienceProcessor webAudienceScore;
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

        webAudienceScore = new DialogsWebAudienceProcessor(scoreProcessor, eventFactory, clock);
        beatIds = new BeatId[10];

        webAudienceScore.init(null);
    }

    @Test
    public void testPlayerConfigUpdate() {
        Map<String, Object> params = new HashMap<>();
        WebPlayerConfig playerConfig = webAudienceScore.getDelegateState().getPlayerConfig();
        String[] configFiles = playerConfig.getAudioFiles();
        assertEquals(9, configFiles.length);
        assertEquals("/audio/DialogsPitch1-1.mp3", configFiles[0]);
        int[][] configFileMap = playerConfig.getAudioFileIndexMap();
        assertEquals(4, configFileMap.length);
        assertEquals(0, configFileMap[0].length);
        assertEquals(3, configFileMap[1].length);
        assertEquals(5, configFileMap[2][2]);

        String[] audioFiles = {
                "/audio/DialogsRhythm2-1.wav",
                "/audio/DialogsRhythm2-2.wav",
                "/audio/DialogsRhythm3-1.wav",
                "/audio/DialogsRhythm3-2.wav",
                "/audio/DialogsRhythm4-1.wav",
                "/audio/DialogsRhythm4-2.wav",
        };
        params.put("audioFiles", audioFiles);
        int[][] fileIndexMap = {
                {},
                {0, 1},
                {2, 3},
                {4, 5}
        };
        params.put("audioFilesIndexMap", fileIndexMap);

        playerConfig.update(params);

        configFiles = playerConfig.getAudioFiles();
        assertEquals(6, configFiles.length);
        assertEquals("/audio/DialogsRhythm2-1.wav", configFiles[0]);
        configFileMap = playerConfig.getAudioFileIndexMap();
        assertEquals(4, configFileMap.length);
        assertEquals(0, configFileMap[0].length);
        assertEquals(2, configFileMap[1].length);
        assertEquals(3, configFileMap[2][1]);

        DialogsWebAudienceStateDeltaTracker deltaTracker = webAudienceScore.getDelegateStateDeltaTracker();
        WebAudienceScoreStateDeltaExport deltaExport = deltaTracker.getDeltaExport();
        Map<String, Object> delta = deltaExport.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_CONFIG_PLAYER));
        WebPlayerConfigExport configExport = (WebPlayerConfigExport) delta.get(WEB_OBJ_CONFIG_PLAYER);

        configFiles = configExport.getAudioFiles();
        assertEquals(6, configFiles.length);
        assertEquals("/audio/DialogsRhythm2-1.wav", configFiles[0]);
        configFileMap = configExport.getAudioFileIndexMap();
        assertEquals(4, configFileMap.length);
        assertEquals(0, configFileMap[0].length);
        assertEquals(2, configFileMap[1].length);
        assertEquals(3, configFileMap[2][1]);

    }
}
