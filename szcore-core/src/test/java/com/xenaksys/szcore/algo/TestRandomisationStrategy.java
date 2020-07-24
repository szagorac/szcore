package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.TstFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestRandomisationStrategy {
    static final Logger LOG = LoggerFactory.getLogger(TestRandomisationStrategy.class);

    BasicScore score;
    ScoreRandomisationStrategy scoreRandomisationStrategy;

    Instrument violin1;
    Instrument violin2;
    Instrument viola;
    Instrument cello;

    String testPageFileName1;
    String testPageFileName2;

    @Before
    public void init() {
        StrId scoreId = new StrId("testScore");
        score = new BasicScore(scoreId);
        scoreRandomisationStrategy = new ScoreRandomisationStrategy(score);

        violin1 = TstFactory.createInstrument("Violin1", false);
        score.addInstrument(violin1);
        violin2 = TstFactory.createInstrument("Violin2", false);
        score.addInstrument(violin2);
        viola = TstFactory.createInstrument("Viola", false);
        score.addInstrument(viola);
        cello = TstFactory.createInstrument("Cello", false);
        score.addInstrument(cello);

        testPageFileName1 = "testPageFileName1";
        Page vln1p1 = TstFactory.createPage(1, violin1.getId(), scoreId, testPageFileName1);
        score.addPage(vln1p1);
        Page vln2p1 = TstFactory.createPage(1, violin2.getId(), scoreId, testPageFileName1);
        score.addPage(vln2p1);
        Page vlap1 = TstFactory.createPage(1, viola.getId(), scoreId, testPageFileName1);
        score.addPage(vlap1);
        Page vcp1 = TstFactory.createPage(1, cello.getId(), scoreId, testPageFileName1);
        score.addPage(vcp1);

        testPageFileName2 = "testPageFileName2";
        Page vln1p2 = TstFactory.createPage(2, violin1.getId(), scoreId, testPageFileName2);
        score.addPage(vln1p2);
        Page vln2p2 = TstFactory.createPage(2, violin2.getId(), scoreId, testPageFileName2);
        score.addPage(vln2p2);
        Page vlap2 = TstFactory.createPage(2, viola.getId(), scoreId, testPageFileName2);
        score.addPage(vlap2);
        Page vcp2 = TstFactory.createPage(2, cello.getId(), scoreId, testPageFileName2);
        score.addPage(vcp2);

        score.setContinuousPage(violin1.getId(), vln1p2);
        score.setContinuousPage(violin2.getId(), vln2p2);
        score.setContinuousPage(viola.getId(), vlap2);
        score.setContinuousPage(cello.getId(), vcp2);

        scoreRandomisationStrategy.init();
    }

    @Test
    public void testOptOutStrategy() {
        List<Integer> strategy = new ArrayList<>();
        strategy.add(2);
        scoreRandomisationStrategy.setAssignmentStrategy(strategy);

        Map<InstrumentId, Integer> initAssignments = new HashMap<>();
        initAssignments.put((InstrumentId) violin1.getId(), 1);
        initAssignments.put((InstrumentId) cello.getId(), 1);
        initAssignments.put((InstrumentId) violin2.getId(), 0);
        initAssignments.put((InstrumentId) viola.getId(), 0);
        scoreRandomisationStrategy.setInstrumentAssignments(initAssignments);

        scoreRandomisationStrategy.optOutInstrument(violin1, true);

        Map<InstrumentId, Integer> assignments = scoreRandomisationStrategy.getInstrumentAssignments();
        Integer pageNo = assignments.get((InstrumentId) violin1.getId());

        assertEquals(0, pageNo.intValue());

        int countAssigned = 0;
        for (Integer page : assignments.values()) {
            if (page != 0) {
                countAssigned++;
            }
        }

        assertEquals(2, countAssigned);
    }

    @Test
    public void testOptInStrategy() {
        List<Integer> strategy = new ArrayList<>();
        strategy.add(2);
        scoreRandomisationStrategy.setAssignmentStrategy(strategy);

        Map<InstrumentId, Integer> initAssignments = new HashMap<>();
        initAssignments.put((InstrumentId) violin1.getId(), 0);
        initAssignments.put((InstrumentId) cello.getId(), 0);
        initAssignments.put((InstrumentId) violin2.getId(), 1);
        initAssignments.put((InstrumentId) viola.getId(), 1);
        scoreRandomisationStrategy.setInstrumentAssignments(initAssignments);

        scoreRandomisationStrategy.optOutInstrument(violin1, false);

        Map<InstrumentId, Integer> assignments = scoreRandomisationStrategy.getInstrumentAssignments();
        Integer pageNo = assignments.get((InstrumentId) violin1.getId());

        assertNotEquals(0, pageNo.intValue());

        int countAssigned = 0;
        for (Integer page : assignments.values()) {
            if (page != 0) {
                countAssigned++;
            }
        }

        assertEquals(2, countAssigned);
    }
}
