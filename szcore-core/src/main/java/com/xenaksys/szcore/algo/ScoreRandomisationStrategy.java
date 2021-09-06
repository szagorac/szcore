package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.config.ScoreRandomisationStrategyConfig;
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
import java.util.Optional;
import java.util.Random;

public class ScoreRandomisationStrategy implements ScoreStrategy{
    static final Logger LOG = LoggerFactory.getLogger(ScoreRandomisationStrategy.class);
    private static final long RECALC_TIME_LIMIT = 1000 * 3;

    private final BasicScore szcore;
    private final ScoreRandomisationStrategyConfig config;
    private final MutablePageId tempPageId = new MutablePageId(0, null, null);

    private long lastRecalc = 0L;
    private long lastPageRecalc = 0L;
    private final List<InstrumentId> instruments = new ArrayList<>();
    private final List<InstrumentId> optOutInstruments = new ArrayList<>();
    private final Map<InstrumentId, Integer> instrumentPage = new HashMap<>();
    private final List<Integer> assignmentStrategy = new ArrayList<>();
    private final Random rnd = new Random();

    public ScoreRandomisationStrategy(BasicScore szcore, ScoreRandomisationStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        Collection<Instrument> instruments = szcore.getInstruments();

        for (Instrument instrument : instruments) {
            if (Consts.NAME_FULL_SCORE.equalsIgnoreCase(instrument.getName()) || instrument.isAv()) {
                continue;
            }

            this.instruments.add((InstrumentId) instrument.getId());
            this.instrumentPage.put((InstrumentId) instrument.getId(), 0);
        }

        assignmentStrategy.add(2);
    }

    private void reset() {
        instrumentPage.replaceAll((i, v) -> 0);
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

    public List<Integer> getAssignmentStrategy() {
        return assignmentStrategy;
    }

    public int getFirstRandomPageNo() {
        Collection<Integer> pageNos = instrumentPage.values();
        if (pageNos.isEmpty()) {
            return 0;
        }

        Optional<Integer> optPageNo = pageNos.stream().findFirst();
        return optPageNo.get();
    }

    public Page getRandomPageFileName(InstrumentId instrumentId) {
        if (!instruments.contains(instrumentId)) {
            return null;
        }

        int pageNo = instrumentPage.get(instrumentId);

        if (pageNo == 0) {
            return null;
        }

        return getPage(pageNo, instrumentId);
    }

    public boolean isInActiveRange(InstrumentId instId, Page page) {
        if (page == null || instId == null) {
            return false;
        }
        return config.isPageInActiveRange(page);
    }


    public boolean isRecalcTime() {
        long now = System.currentTimeMillis();
        long diff = now - (lastRecalc + RECALC_TIME_LIMIT);
        return diff > 0;
    }

    public boolean isPageRecalcTime() {
        long now = System.currentTimeMillis();
        long diff = now - (lastPageRecalc + RECALC_TIME_LIMIT);
        return diff > 0;
    }

    public void recalcStrategy(Page page) {
        if (page == null) {
            return;
        }
        reset();
        IntRange range = config.getSelectionRange(page);
        if (range == null) {
            return;
        }

        List<InstrumentId> rndInst = new ArrayList<>(instrumentPage.keySet());
        Collections.shuffle(rndInst, rnd);
        int instStart = 0;
        int instEnd;
        for (Integer instNo : assignmentStrategy) {
            int pageNo = range.getRndValueFromRange();
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

    public int getNumberOfRequiredPages() {
        return assignmentStrategy.size();
    }

    public Page getPage(int pageNo, Id instrumentId) {
        tempPageId.setInstrumentId(instrumentId);
        tempPageId.setScoreId(szcore.getId());
        tempPageId.setPageNo(pageNo);
        Page page = szcore.getPage(tempPageId);
        tempPageId.reset();
        return page;
    }

    public void setPageSelection(List<Integer> pageIds) {
        Map<Integer, List<InstrumentId>> pageAssigments = getPageAssigments();
        Map<InstrumentId, Integer> instrumentAssignments = getInstrumentAssignments();
        List<Integer> assignedPages = new ArrayList<>(pageAssigments.keySet());
        for (int i = 0; i < pageIds.size(); i++) {
            if (i >= assignedPages.size()) {
                LOG.error("setPageSelection: unexpected page index {}, assignedPages size: {}", i, assignedPages.size());
                break;
            }
            Integer assignedPage = assignedPages.get(i);
            Integer selectedPage = pageIds.get(i);
            List<InstrumentId> assignedInstruments = pageAssigments.get(assignedPage);
            for (InstrumentId insId : assignedInstruments) {
                Integer asp = instrumentAssignments.get(insId);
                if (!asp.equals(assignedPage)) {
                    LOG.error("setPageSelection: unexpected assigned page {}, expected: {}", asp, assignedPage);
                }
                LOG.debug("setPageSelection: replacing page {}, with page: {} for instrument: {}", asp, selectedPage, insId);
                instrumentAssignments.put(insId, selectedPage);
            }
        }
        lastPageRecalc = System.currentTimeMillis();
    }

    public void optOutInstrument(Instrument instrument, Instrument replaceInst, boolean isOptOut) {
        InstrumentId instId = (InstrumentId) instrument.getId();
        LOG.debug("optOutInstrument: received opt-out: {} instrument: {} replaceInst: {}", isOptOut, instId, replaceInst);

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
                    LOG.debug("optOutInstrument: can not opt out instrument: {}, all instruments assigned", instId);
                } else {
                    for (InstrumentId iid : unassignedInsts) {
                        if (!iid.equals(instId) && !optOutInstruments.contains(iid)) {
                            replacementInst = iid;
                            LOG.debug("optOutInstrument: assigning replacement instrument: {}", replacementInst);
                            break;
                        }
                    }
                    if (instId.equals(replacementInst) && !optOutInstruments.isEmpty()) {
                        replacementInst = optOutInstruments.remove(0);
                        LOG.debug("optOutInstrument: could not find any available instruments, assigning opted-out instrument: {}", replacementInst);
                    }
                }
            } else {
                LOG.debug("optOutInstrument: instrument: {}, is not assigned, not doing anything", instId);
            }

            //Do replace
            if (!instId.equals(replacementInst)) {
                instrumentPage.put(replacementInst, pageNo);
                instrumentPage.put(instId, 0);
            }
        } else {
            //Opt-in instrument
            InstrumentId toReplaceInstId = (InstrumentId) replaceInst.getId();
            optOutInstruments.remove(instId);

            if (isAssigned) {
                LOG.debug("optOutInstrument: Opt-in instrument: {}, is already assigned, not doing anything", instId);
            } else {
                Integer replacePageNo = instrumentPage.get(toReplaceInstId);
                boolean isReplaceAssigned = replacePageNo != 0;

                if (isReplaceAssigned) {
                    instrumentPage.put(instId, replacePageNo);
                    instrumentPage.put(toReplaceInstId, 0);
                } else {
                    if (assignedInsts.isEmpty()) {
                        LOG.debug("optOutInstrument: Opt-in instrument: {}, can not find any instruments to assign", instId);
                    } else {
                        toReplaceInstId = assignedInsts.remove(0);
                        replacePageNo = instrumentPage.get(toReplaceInstId);
                        instrumentPage.put(instId, replacePageNo);
                        instrumentPage.put(toReplaceInstId, 0);
                    }
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
        List<InstrumentId> assignedInsts = new ArrayList<>();
        for (InstrumentId instId : instrumentPage.keySet()) {
            Integer pNo = instrumentPage.get(instId);
            if (pNo != 0) {
                assignedInsts.add(instId);
            }
        }
        return assignedInsts;
    }

    public Map<Integer, List<InstrumentId>> getPageAssigments() {
        Map<Integer, List<InstrumentId>> assignedPages = new HashMap<>();
        for (InstrumentId instId : instrumentPage.keySet()) {
            Integer pageNo = instrumentPage.get(instId);
            if (pageNo != 0) {
                List<InstrumentId> instrumentIds = assignedPages.computeIfAbsent(pageNo, k -> new ArrayList<>());
                if (!instrumentIds.contains(instId)) {
                    instrumentIds.add(instId);
                }
            }
        }
        return assignedPages;
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

    @Override
    public StrategyType getType() {
        return StrategyType.RND;
    }
}
