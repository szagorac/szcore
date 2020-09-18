package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.StrategyConfigLoader;
import com.xenaksys.szcore.score.TstFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestRandomStrategyConfig {
    static final Logger LOG = LoggerFactory.getLogger(TestRandomStrategyConfig.class);
    File file;
    BasicScore score;

    Instrument violin1;
    Instrument violin2;
    Instrument viola;
    Instrument cello;

    @Before
    public void init() {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("strategy/strategyConfig.yml").getFile());

        StrId scoreId = new StrId("Test Score");
        score = new BasicScore(scoreId);

        violin1 = TstFactory.createInstrument("Violin1", false);
        score.addInstrument(violin1);
        violin2 = TstFactory.createInstrument("Violin2", false);
        score.addInstrument(violin2);
        viola = TstFactory.createInstrument("Viola", false);
        score.addInstrument(viola);
        cello = TstFactory.createInstrument("Cello", false);
        score.addInstrument(cello);
    }

    @Test
    public void testConfigLoad() throws Exception {
        ScoreRandomisationStrategyConfig config = StrategyConfigLoader.loadStrategyConfig(file, score);
        assertNotNull(config);

        assertEquals(score.getName(), config.getScoreName());

        List<RndPageRangeConfig> pageRangeConfigs = config.getPageRangeConfigs();
        assertEquals(5, pageRangeConfigs.size());

        RndPageRangeConfig first = pageRangeConfigs.get(0);
        List<InstrumentId> instrumentIds = first.getInstruments();
        assertTrue(instrumentIds.contains((InstrumentId) violin1.getId()));
        assertTrue(instrumentIds.contains((InstrumentId) violin2.getId()));
        assertTrue(instrumentIds.contains((InstrumentId) viola.getId()));
        assertTrue(instrumentIds.contains((InstrumentId) cello.getId()));

        IntRange range = first.getRange();
        assertEquals(1, range.getStart());
        assertEquals(8, range.getEnd());

        IntRange selection = first.getSelectionPageRange();
        assertEquals(1, selection.getStart());
        assertEquals(8, selection.getEnd());

        Boolean isActive = first.isRangeActive();
        assertFalse(isActive);

        RndPageRangeConfig second = pageRangeConfigs.get(1);
        range = second.getRange();
        assertEquals(9, range.getStart());
        assertEquals(16, range.getEnd());

        selection = second.getSelectionPageRange();
        assertEquals(1, selection.getStart());
        assertEquals(8, selection.getEnd());

        isActive = second.isRangeActive();
        assertTrue(isActive);

        RndPageRangeConfig fourth = pageRangeConfigs.get(3);
        instrumentIds = fourth.getInstruments();
        assertFalse(instrumentIds.contains((InstrumentId) violin1.getId()));
        assertFalse(instrumentIds.contains((InstrumentId) violin2.getId()));
        assertTrue(instrumentIds.contains((InstrumentId) viola.getId()));
        assertTrue(instrumentIds.contains((InstrumentId) cello.getId()));

        range = fourth.getRange();
        assertEquals(25, range.getStart());
        assertEquals(32, range.getEnd());

        selection = fourth.getSelectionPageRange();
        assertEquals(17, selection.getStart());
        assertEquals(24, selection.getEnd());

        isActive = fourth.isRangeActive();
        assertTrue(isActive);
    }

}
