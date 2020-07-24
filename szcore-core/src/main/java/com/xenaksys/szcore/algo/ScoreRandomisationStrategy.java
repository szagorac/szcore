package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.MutablePageId;
import com.xenaksys.szcore.score.BasicScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ScoreRandomisationStrategy {
    static final Logger LOG = LoggerFactory.getLogger(ScoreRandomisationStrategy.class);
    private static final long RECALC_TIME_LIMIT = 1000 * 5;

    private final BasicScore szcore;
    private final MutablePageId tempPageId = new MutablePageId(0, null, null);

    private long lastRecalc = 0L;
    private final List<InstrumentId> instruments = new ArrayList<>();
    private final List<InstrumentId> optOutInstruments = new ArrayList<>();
    private final Map<InstrumentId, Integer> instrumentPage = new HashMap<>();
    private final List<Integer> assignmentStrategy = new ArrayList<>();
    private int maxPageNo = Integer.MAX_VALUE;
    private final Random rnd = new Random();

    public ScoreRandomisationStrategy(BasicScore szcore) {
        this.szcore = szcore;
    }

    public void init() {
        Collection<Instrument> instruments = szcore.getInstruments();

        for (Instrument instrument : instruments) {
            if (Consts.NAME_FULL_SCORE.equalsIgnoreCase(instrument.getName()) || instrument.isAv()) {
                continue;
            }

            this.instruments.add((InstrumentId) instrument.getId());
            this.instrumentPage.put((InstrumentId) instrument.getId(), 0);

            Page continuousPage = szcore.getContinuousPage(instrument.getId());

            int cPageNo = continuousPage.getPageNo();
            if (cPageNo < maxPageNo) {
                maxPageNo = cPageNo;
            }
        }

        assignmentStrategy.add(2);
    }

    private void reset() {
        for (InstrumentId instrumentId : instrumentPage.keySet()) {
            instrumentPage.put(instrumentId, 0);
        }
        optOutInstruments.clear();
    }

    public void setAssignmentStrategy(List<Integer> strategy) {
        if (strategy == null) {
            return;
        }
        LOG.debug("setAssignmentStrategy() {}", Arrays.toString(strategy.toArray()));
        assignmentStrategy.clear();
        assignmentStrategy.addAll(strategy);
    }

    public String getRandomPageName(InstrumentId instrumentId) {
        long now = System.currentTimeMillis();
        long diff = now - (lastRecalc + RECALC_TIME_LIMIT);
        if (diff > 0) {
            LOG.debug("getRandomPageName() time limit reached diff: {}", diff);
            recalcStrategy();
        }

        if (!instruments.contains(instrumentId)) {
            return null;
        }

        int pageNo = instrumentPage.get(instrumentId);

        if (pageNo == 0) {
            return null;
        }

        Page page = getPage(pageNo, instrumentId);
        if (page != null) {
            return page.getFileName();
        }

        return null;
    }

    private void recalcStrategy() {
        reset();

        int rndNo = assignmentStrategy.size();
        int[] rndNos = new int[rndNo];

        for (int i = 0; i < rndNo; i++) {
            if (maxPageNo == 1) {
                rndNos[i] = 1;
            } else {
                rndNos[i] = ThreadLocalRandom.current().nextInt(1, maxPageNo);
            }
        }

        List<InstrumentId> rndInst = new ArrayList<>(instrumentPage.keySet());
        Collections.shuffle(rndInst, rnd);
        int instStart = 0;
        int instEnd = 0;
        for (int i = 0; i < rndNo; i++) {
            Integer instNo = assignmentStrategy.get(i);
            int pageNo = rndNos[i];
            instEnd = instStart + instNo;
            for (int j = instStart; j < instEnd; j++) {
                if (rndInst.size() < j) {
                    break;
                }
                InstrumentId instrumentId = rndInst.get(j);
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

    public void optOutInstrument(Instrument instrument, boolean isOptOut) {
        InstrumentId instId = (InstrumentId) instrument.getId();
        LOG.info("optOutInstrument: received opt-out: {} instrument: {}", isOptOut, instId);

        Integer pageNo = instrumentPage.get(instId);
        boolean isAssigned = pageNo != 0;

        List<InstrumentId> unassignedInsts = getUnassignedInstruments();
        List<InstrumentId> assignedInsts = getAssignedInstruments();
        if (isOptOut) {
            //Opt-out instrument
            InstrumentId replacementInst = instId;
            optOutInstruments.add(instId);
            if (isAssigned) {
                if (unassignedInsts.isEmpty()) {
                    LOG.info("optOutInstrument: can not opt out instrument: {}, all instruments assigned", instId);
                } else {
                    for (InstrumentId iid : unassignedInsts) {
                        if (!iid.equals(instId) && !optOutInstruments.contains(iid)) {
                            replacementInst = iid;
                            LOG.info("optOutInstrument: assigning replacement instrument: {}", replacementInst);
                            break;
                        }
                    }
                    if (instId.equals(replacementInst) && !optOutInstruments.isEmpty()) {
                        replacementInst = optOutInstruments.remove(0);
                        LOG.info("optOutInstrument: could not find any available instruments, assigning opted-out instrument: {}", replacementInst);
                    }
                }
            } else {
                LOG.info("optOutInstrument: instrument: {}, is not assigned, not doing anything", instId);
            }

            //Do replace
            if (!instId.equals(replacementInst)) {
                instrumentPage.put(replacementInst, pageNo);
                instrumentPage.put(instId, 0);
            }
        } else {
            //Opt-in instrument
            InstrumentId toReplaceInst = instId;
            optOutInstruments.remove(instId);

            if (isAssigned) {
                LOG.info("optOutInstrument: Opt-in instrument: {}, is already assigned, not doing anything", instId);
            } else {
                if (assignedInsts.isEmpty()) {
                    LOG.info("optOutInstrument: Opt-in instrument: {}, can not find any instruments to assign", instId);
                } else {
                    toReplaceInst = assignedInsts.remove(0);
                    Integer replacePageNo = instrumentPage.get(toReplaceInst);
                    instrumentPage.put(instId, replacePageNo);
                    instrumentPage.put(toReplaceInst, 0);
                }
            }
        }
    }

    public List<InstrumentId> getUnassignedInstruments() {
        List<InstrumentId> unassignedInsts = new ArrayList<>();
        for (InstrumentId instId : instrumentPage.keySet()) {
            Integer pNo = instrumentPage.get(instId);
            if (pNo == 0) {
                unassignedInsts.add(instId);
            }
        }
        return unassignedInsts;
    }

    public List<InstrumentId> getAssignedInstruments() {
        List<InstrumentId> unassignedInsts = new ArrayList<>();
        for (InstrumentId instId : instrumentPage.keySet()) {
            Integer pNo = instrumentPage.get(instId);
            if (pNo != 0) {
                unassignedInsts.add(instId);
            }
        }
        return unassignedInsts;
    }

    public void setInstrumentAssignments(Map<InstrumentId, Integer> pageAssignments) {
        instrumentPage.clear();
        instrumentPage.putAll(pageAssignments);
    }

    public Map<InstrumentId, Integer> getInstrumentAssignments() {
        return instrumentPage;
    }

    public List<InstrumentId> getInstrumentSlotIds() {
        List<InstrumentId> ids = getAssignedInstruments();
        Collections.sort(ids);
        return ids;
    }
}
