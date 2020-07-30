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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        Map<InstrumentId, IntRange> ranges = config.getInstrumentActivePageRanges();
        assertEquals(4, ranges.size());

        IntRange range = ranges.get((InstrumentId) violin1.getId());
        assertEquals(3, range.getStart());
        assertEquals(0, range.getEnd());
    }

}
