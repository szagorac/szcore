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

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestWebScore {
    private static final String TEST_SCRIPT_PREFIX = "Test script";
    WebScore webScore;
    BeatId[] beatIds;

    @Before
    public void init(){
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

    private void checkResetBeat(int beatNo, int resetBeatNo) {
        BeatId beatId = beatIds[beatNo - 1];
        BeatId resetBeaId = beatIds[resetBeatNo - 1];
        List<WebScoreScript> scripts = webScore.getBeatResetScripts(beatId);
        assertEquals(1, scripts.size());
        WebScoreScript webScoreScript = scripts.get(0);
        String content =  TEST_SCRIPT_PREFIX + resetBeatNo;
        assertEquals(content, webScoreScript.getContent());
        assertEquals(resetBeaId, webScoreScript.getBeatId());
    }

    public void addBeat(int beatNo, boolean isReset) {
        BeatId beatId = TstScoreUtil.createBeatId(beatNo, 1, 1, 1, beatNo);
        beatIds[beatNo - 1] = beatId;
        String content =  TEST_SCRIPT_PREFIX + beatNo;
        WebScoreScript webScoreScript = new WebScoreScript(new IntId(beatNo), beatId, content, isReset, false);
        webScore.addBeatScript(beatId, webScoreScript);
    }

}
