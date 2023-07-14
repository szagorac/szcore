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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_PLAYER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestDialogsWebAudienceScore {
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

        webAudienceScore.init("webscore");
    }

    @Test
    public void testPlayerConfigUpdate() {
        webAudienceScore.reset(2);
        Map<String, Object> params = new HashMap<>();
        WebPlayerConfig playerConfig = webAudienceScore.getDelegateState().getPlayerConfig();
        ArrayList<String> configFiles = playerConfig.getAudioFiles();
        assertEquals(9, configFiles.size());
        assertEquals("/audio/DialogsPitch1-1.mp3", configFiles.get(0));
        ArrayList<ArrayList<Integer>> configFileMap = playerConfig.getAudioFilesIndexMap();
        assertEquals(4, configFileMap.size());
        assertEquals(0, configFileMap.get(0).size());
        assertEquals(3, configFileMap.get(1).size());
        assertEquals(5, configFileMap.get(2).get(2).intValue());

        ArrayList<String> audioFiles = new ArrayList<>(Arrays.asList(
                "/audio/DialogsRhythm2-1.wav",
                "/audio/DialogsRhythm2-2.wav",
                "/audio/DialogsRhythm3-1.wav",
                "/audio/DialogsRhythm3-2.wav",
                "/audio/DialogsRhythm4-1.wav",
                "/audio/DialogsRhythm4-2.wav"
        ));
        params.put("audioFiles", audioFiles);

        ArrayList<ArrayList<Integer>> fileIndexMap = new ArrayList<>();
        fileIndexMap.add( new ArrayList<>());
        fileIndexMap.add( new ArrayList<>(Arrays.asList(0, 1)));
        fileIndexMap.add( new ArrayList<>(Arrays.asList(2, 3)));
        fileIndexMap.add( new ArrayList<>(Arrays.asList(4, 5)));
        params.put("audioFilesIndexMap", fileIndexMap);

        playerConfig.update(params);

        configFiles = playerConfig.getAudioFiles();
        assertEquals(6, configFiles.size());
        assertEquals("/audio/DialogsRhythm2-1.wav", configFiles.get(0));
        configFileMap = playerConfig.getAudioFilesIndexMap();
        assertEquals(4, configFileMap.size());
        assertEquals(0, configFileMap.get(0).size());
        assertEquals(2, configFileMap.get(1).size());
        assertEquals(3, configFileMap.get(2).get(1).intValue());

        DialogsWebAudienceStateDeltaTracker deltaTracker = webAudienceScore.getDelegateStateDeltaTracker();
        WebAudienceScoreStateDeltaExport deltaExport = deltaTracker.getDeltaExport();
        Map<String, Object> delta = deltaExport.getDelta();
        assertTrue(delta.containsKey(WEB_OBJ_CONFIG_PLAYER));
        WebPlayerConfigExport configExport = (WebPlayerConfigExport) delta.get(WEB_OBJ_CONFIG_PLAYER);

        configFiles = configExport.getAudioFiles();
        assertEquals(6, configFiles.size());
        assertEquals("/audio/DialogsRhythm2-1.wav", configFiles.get(0));
        configFileMap = configExport.getAudioFilesIndexMap();
        assertEquals(4, configFileMap.size());
        assertEquals(0, configFileMap.get(0).size());
        assertEquals(2, configFileMap.get(1).size());
        assertEquals(3, configFileMap.get(2).get(1).intValue());

    }
}
