package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.MutablePageId;
import com.xenaksys.szcore.score.BasicScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ScoreRandomisationStrategy {
    static final Logger LOG = LoggerFactory.getLogger(ScoreRandomisationStrategy.class);
    private static final long RECALC_TIME_LIMIT = 1000 * 5;

    private final BasicScore szcore;
    private final MutablePageId tempPageId = new MutablePageId(0, null, null);

    private long lastRecalc = 0L;
    private List<Id> instruments = new ArrayList<>();
    private Map<Id, Integer> instrumentPage = new HashMap<>();
    private List<Integer> assignmentStrategy = new ArrayList<>();
    private int maxPageNo = Integer.MAX_VALUE;
    private Random rnd = new Random();

    public ScoreRandomisationStrategy(BasicScore szcore) {
        this.szcore = szcore;
    }

    public void init() {
        Collection<Instrument> instruments = szcore.getInstruments();

        for(Instrument instrument : instruments) {
            if(Consts.NAME_FULL_SCORE.equalsIgnoreCase(instrument.getName())) {
                continue;
            }

            this.instruments.add(instrument.getId());
            this.instrumentPage.put(instrument.getId(), 0);

            Page continuousPage = szcore.getContinuousPage(instrument.getId());

            int cPageNo = continuousPage.getPageNo();
            if(cPageNo < maxPageNo) {
                maxPageNo = cPageNo;
            }
        }

        assignmentStrategy.add(2);
    }

    private void reset() {
        for(Id instrumentId : instrumentPage.keySet()) {
            instrumentPage.put(instrumentId, 0);
        }
    }

    public void setAssignmentStrategy(List<Integer> strategy) {
        if(strategy == null) {
            return;
        }
        LOG.debug("setAssignmentStrategy() {}", Arrays.toString(strategy.toArray()));
        assignmentStrategy.clear();
        assignmentStrategy.addAll(strategy);
    }

    public String getRandomPageName(Id instrumentId) {
        long now = System.currentTimeMillis();
        long diff = now - (lastRecalc + RECALC_TIME_LIMIT);
        if(diff > 0) {
            LOG.debug("getRandomPageName() time limit reached diff: {}", diff);
            recalcStrategy();
        }

        if(!instruments.contains(instrumentId)) {
            return null;
        }

        int pageNo = instrumentPage.get(instrumentId);

        if(pageNo == 0) {
            return null;
        }

        Page page = getPage(pageNo, instrumentId);
        if(page != null) {
            return page.getFileName();
        }

        return null;
    }

    private void recalcStrategy() {
        reset();

        int rndNo = assignmentStrategy.size();
        int[] rndNos = new int[rndNo];

        for(int i = 0; i < rndNo; i++) {
            rndNos[i] = ThreadLocalRandom.current().nextInt(1, maxPageNo + 1);
        }

        List<Id> rndInst = new ArrayList<>(instrumentPage.keySet());
        Collections.shuffle(rndInst, rnd);
        int instStart = 0;
        int instEnd = 0;
        for(int i = 0; i < rndNo; i++) {
            Integer instNo = assignmentStrategy.get(i);
            int pageNo = rndNos[i];
            instEnd = instStart + instNo;
            for(int j = instStart; j < instEnd; j++) {
                if(rndInst.size() < j) {
                    break;
                }
                Id instrumentId = rndInst.get(j);
                LOG.debug("recalcStrategy() instrumentId: {}, pageNo: {}", instrumentId, pageNo);
                instrumentPage.put(instrumentId, pageNo);
            }
            instStart = instEnd;
        }

        lastRecalc = System.currentTimeMillis();
    }

    public Page getPage(int pageNo, Id instrumentId) {
        tempPageId.setInstrumentId(instrumentId);
        tempPageId.setScoreId(szcore.getId());
        tempPageId.setPageNo(pageNo);
        Page next = szcore.getPage(tempPageId);
        tempPageId.reset();
        return next;
    }
}
