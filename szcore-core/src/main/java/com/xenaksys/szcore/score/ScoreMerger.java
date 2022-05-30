package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.util.FileUtil;
import com.xenaksys.szcore.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.RESOURCE_JAVASCRIPT;
import static com.xenaksys.szcore.Consts.RESOURCE_MAXMSP;
import static com.xenaksys.szcore.Consts.RESOURCE_SCRIPT_ENGINE;
import static com.xenaksys.szcore.Consts.RESOURCE_TRANSITION;
import static com.xenaksys.szcore.Consts.RESOURCE_WEB;
import static com.xenaksys.szcore.Consts.SLASH;
import static com.xenaksys.szcore.Consts.TXT_FILE_EXTENSION;
import static com.xenaksys.szcore.score.ResourceType.FILE;
import static com.xenaksys.szcore.score.ResourceType.JAVASCRIPT;
import static com.xenaksys.szcore.score.ResourceType.MAXMSP;
import static com.xenaksys.szcore.score.ResourceType.SCRIPT_ENGINE;
import static com.xenaksys.szcore.score.ResourceType.TRANSITION;
import static com.xenaksys.szcore.score.ResourceType.WEB_AUDIENCE;

public class ScoreMerger {
    static final Logger LOG = LoggerFactory.getLogger(ScoreMerger.class);

    static final String HEADER = "scoreName,instrumentName,pageName,pageNo,barName,barNo,timeSigNum,timeSigDenom,tempoBpm,tempoBeatValue,beatNo,startTimeMillis,durationTimeMillis,endTimeMillis,startBaseBeatUnits,durationBeatUnits,endBaseBeatUnits,xStartPxl,xEndPxl,yStartPxl,yEndPxl,isUpbeat,resource,unitBeatNo";
    static final String IN_DIR = "/Users/slavko/MyHome/Music/scoreExport/Dialogs/export";
    static final String OUT_DIR = "/Users/slavko/MyHome/Music/scoreExport/Dialogs/merged";
    static final String RSRC_DIR = "/Users/slavko/MyHome/Music/scoreExport/Dialogs/rsrc";

    static final String[] SCORES_ORDER = {"DialogsPitch","DialogsRhythm","DialogsMelody","DialogsTimbre"};
    static final String SCORE_NAME = "Dialogs";
    static final String SCRIPTS = "ZScripts";
    static final String SCORE_PREFIX = "1_";
    static final String PAGE_NO_HEADER = "pageNo: ";

    private final List<ScoreElementsContainer> scoreElementsContainers = new ArrayList<>();
    private final ScoreElementsContainer masterContainer = new ScoreElementsContainer(SCORE_NAME, true);
    private final HashMap<String, String> resourceMap = new HashMap<>();
    private final HashMap<String, ScoreElement> lastInstrumentElement = new HashMap<>();
    private final HashMap<String, InstrumentCounter> instrumentCounters = new HashMap<>();
    private final HashMap<String, InstrumentTracker> instrumentTrackers = new HashMap<>();
    private final HashMap<String, HashMap<Integer, Integer>> pageMapper = new HashMap<>();
    private final HashMap<String, List<String>> noteInfos = new HashMap<>();

    private int pageNo;

    private void merge() throws Exception {
        validate();
        load();
        recalc();
        export();
    }

    private void export() throws Exception {
        String outBeatLineFile = OUT_DIR +  Consts.SLASH + SCORE_PREFIX + SCORE_NAME + Consts.BEAT_INFO_FILE_SUFFIX;
        String[] lines = createOutBeatInfoLines();
        FileUtil.writeToFile(lines, outBeatLineFile);
        exportNoteInfos();

        for(String fromResource : resourceMap.keySet()) {
            String toResource = resourceMap.get(fromResource);
            FileUtil.copyFile(fromResource + Consts.BEAT_INFO_FILE_SUFFIX, toResource + Consts.BEAT_INFO_FILE_SUFFIX);
            FileUtil.copyFile(fromResource + Consts.PNG_FILE_EXTENSION, toResource + Consts.PNG_FILE_EXTENSION);
            FileUtil.copyFile(fromResource + Consts.INSCORE_FILE_SUFFIX + TXT_FILE_EXTENSION, toResource + Consts.INSCORE_FILE_SUFFIX + TXT_FILE_EXTENSION);
        }
        FileUtil.copyDirectory(OUT_DIR, RSRC_DIR);

    }

    private void exportNoteInfos() {
        String outNoteInfoFile = OUT_DIR +  Consts.SLASH + SCORE_NAME + Consts.NOTE_INFO_FILE_SUFFIX;
        String[] lines = createOutNoteInfoLines();
        FileUtil.writeToFile(lines, outNoteInfoFile);
    }

    private String[] createOutBeatInfoLines() {
        List<String> lines  = masterContainer.export();
        lines.add(0, HEADER);
        return lines.toArray(new String[0]);
    }

    private String[] createOutNoteInfoLines() {
        List<String> noteInfoOut = new ArrayList<>();
        for(String scoreName : SCORES_ORDER) {
            List<String> noteInfoLines = noteInfos.get(scoreName);
            HashMap<Integer, Integer> pageMap = pageMapper.get(scoreName);
            for(String niLine : noteInfoLines) {
                String out = replaceNoteInfoPageNo(niLine, pageMap);
                if(out != null) {
                    noteInfoOut.add(out);
                }
            }
        }
        return noteInfoOut.toArray(new String[0]);
    }

    private String replaceNoteInfoPageNo(String noteInfoLine, HashMap<Integer, Integer> pageMap) {
        int start = noteInfoLine.indexOf(PAGE_NO_HEADER);
        if(start < 0) {
            return null;
        }
        start += PAGE_NO_HEADER.length();
        int end = noteInfoLine.indexOf(COMMA, start);
        String pNoStr = noteInfoLine.substring(start, end);
        Object pNo = ParseUtil.parseWholeNumber(pNoStr);
        if(!(pNo instanceof Integer)) {
            return null;
        }
        Integer fromNo = (Integer)pNo;
        Integer toNo = pageMap.get(fromNo);
        if(toNo == null) {
            LOG.error("Could not find mapping for pageNo: {} in line: {}", fromNo, noteInfoLine);
            return null;
        }
        if(fromNo.equals(toNo)) {
            return noteInfoLine;
        }
        String from = PAGE_NO_HEADER + pNoStr;
        String to  = PAGE_NO_HEADER + toNo;
        return noteInfoLine.replace(from, to);
    }

    private void recalc() throws Exception {
        for(ScoreElementsContainer container : scoreElementsContainers) {
            HashMap<Integer, HashMap<String, Set<ScoreElement>>> elementsMap = container.getPageElementsMap();
            int sectionPageNo = container.getPageNo();
            for(int i = 1; i <= sectionPageNo; i++) {
                HashMap<String, Set<ScoreElement>> pageElements = elementsMap.get(i);
                if(pageElements == null) {
                    throw new RuntimeException("Invalid page elements for pageNo: " + i + ", container: " + container.getScoreName());
                }
                for(String instr : pageElements.keySet()) {
                    LOG.info("Processing instrument: {}", instr);
                    InstrumentCounter counter = instrumentCounters.computeIfAbsent(instr, InstrumentCounter::new);
                    InstrumentTracker tracker = instrumentTrackers.computeIfAbsent(instr, InstrumentTracker::new);
                    Set<ScoreElement> elements = pageElements.get(instr);
                    for(ScoreElement element : elements) {
                        ScoreElement lastElement = lastInstrumentElement.get(instr);
                        ScoreElement masterElement = ScoreElement.clone(element);
                        String sn = element.getScoreName();
                        String pn = element.getPageName();
                        String bn = element.getBarName();
                        int pNo = element.getPageNo();
                        int bNo = element.getBarNo();
                        int btNo = element.getBeatNo();
                        int sbtuNo = element.getStartBaseBeatUnits();
                        long stMill = element.getStartTimeMillis();
                        boolean isUpbeat = element.isUpbeat();

                        if(lastElement != null) {
                            if (pNo != tracker.getPageNo() ) {
                                counter.incrementPageNo();
                            }

                            int lastBarNo = lastElement.getBarNo();
                            if (bNo != tracker.getBarNo()) {
                                if(isUpbeat) {
                                    counter.setBarNo(lastBarNo);
                                } else {
                                    counter.incrementBarNo();
                                }
                            }

                            int lastBeatNo = lastElement.getBeatNo();
                            int lastStartBeatUnitNo = lastElement.getStartBaseBeatUnits();
                            long lastStartTimeMillis = lastElement.getStartTimeMillis();
                            long lastEndTimeMillis = lastElement.getEndTimeMillis();
                            if (btNo != tracker.getBeatNo()) {
                                if(isUpbeat) {
                                    counter.setBeatNo(lastBeatNo);
                                    counter.setUnitBeatNo(lastStartBeatUnitNo);
                                    counter.setStartTimeMillis(lastStartTimeMillis);
                                } else {
                                    counter.incrementBeatNo();
                                    int diff = sbtuNo - tracker.getUnitBeatNo();
                                    counter.incrementUnitBeatNo(diff);
                                    counter.setStartTimeMillis(lastEndTimeMillis);
                                }
                            }
                        } else {
                            counter.setPageNo(element.getPageNo());
                            counter.setBarNo(element.getBarNo());
                            counter.setBeatNo(element.getBeatNo());
                            counter.setUnitBeatNo(element.getUnitBeatNo());
                            counter.setStartTimeMillis(element.getStartTimeMillis());
                            counter.setEndTimeMillis(element.getEndTimeMillis());
                        }
                        LOG.info("Processing page: {}, bar: {}, beat: {}, unit: {}, startMilli: {}", counter.getPageNo(), counter.getBarNo(), counter.getBeatNo(), counter.getUnitBeatNo(), counter.getStartTimeMillis());

                        replaceScoreName(masterElement);
                        addPageMapping(sn, pNo, counter.getPageNo());
                        if(counter.getPageNo() == pNo) {
                            replaceResource(masterElement, sn, pn, pn);
                        } else {
                            replacePage(masterElement, pNo, pn, counter.getPageNo());
                            replaceResource(masterElement, sn, pn, masterElement.getPageName());
                        }
                        if(counter.getBarNo() != bNo) {
                            replaceBar(masterElement, bNo, bn, counter.getBarNo());
                        }
                        if(counter.getBeatNo() != btNo) {
                            replaceBeat(masterElement, counter.getBeatNo());
                        }
                        if(counter.getUnitBeatNo() != sbtuNo) {
                            replaceBeatUnits(masterElement, counter.getUnitBeatNo());
                        }
                        if(counter.getStartTimeMillis() != stMill) {
                            replaceTimeMillis(masterElement, counter.getStartTimeMillis());
                        }
                        masterContainer.addScoreElement(masterElement);
                        lastInstrumentElement.put(instr, masterElement);
                        tracker.setPageNo(pNo);
                        tracker.setBarNo(bNo);
                        tracker.setBeatNo(btNo);
                        tracker.setUnitBeatNo(sbtuNo);
                    }
                }
            }
        }
    }

    private void replaceTimeMillis(ScoreElement masterElement, long startTimeMillis) {
        masterElement.setStartTimeMillis(startTimeMillis);
        masterElement.setEndTimeMillis(startTimeMillis + masterElement.getDurationTimeMillis());
    }

    private void replaceBeatUnits(ScoreElement masterElement, int currentBeatUnits) {
        masterElement.setStartBaseBeatUnits(currentBeatUnits);
        masterElement.setUnitBeatNo(currentBeatUnits);
        masterElement.setEndBaseBeatUnits(currentBeatUnits + masterElement.getDurationBeatUnits());
    }

    private void replaceBeat(ScoreElement masterElement, int currentBeat) {
        masterElement.setBeatNo(currentBeat);
    }

    private void replaceBar(ScoreElement masterElement, int bNo, String bn, int currentBar) {
        masterElement.setBarNo(currentBar);
        String masterBarName = bn.replace(""+bNo, ""+currentBar);
        masterElement.setBarName(masterBarName);
    }

    private void addPageMapping(String scoreName, int fromPageNo, int toPageNo) {
        HashMap<Integer, Integer> pMap = pageMapper.computeIfAbsent(scoreName, s -> new HashMap<>());
        pMap.put(fromPageNo, toPageNo);
    }

    private void replacePage(ScoreElement masterElement, int pNo, String pn, int currentPage) {
        masterElement.setPageNo(currentPage);
        String masterPageName = pn.replace(""+pNo, ""+currentPage);
        masterElement.setPageName(masterPageName);
    }

    private void replaceScoreName(ScoreElement se) {
        se.setScoreName(SCORE_NAME);
    }

    private void replaceResource(ScoreElement se, String replaceScoreName, String replacePageName, String pageName) {
        String resource = se.getResource();
        ResourceType resourceType = getResourceType(resource);
        if(FILE != resourceType) {
            return;
        }
        Path p = Paths.get(resource);
        String file = p.getFileName().toString();
        String masterResource = file.replace(replaceScoreName, SCORE_NAME);
        if( !pageName.equals(replacePageName) ) {
            masterResource = masterResource.replace(replacePageName, pageName);
        }
        masterResource = OUT_DIR + SLASH + masterResource;
        se.setResource(masterResource);
        resourceMap.put(resource, masterResource);
    }

    private void load() throws Exception {
        for(String scoreName : SCORES_ORDER) {
            ScoreElementsContainer scoreElementsContainer = new ScoreElementsContainer(scoreName, false);
            scoreElementsContainers.add(scoreElementsContainer);

            String fileName = IN_DIR + Consts.SLASH + scoreName + Consts.BEAT_INFO_FILE_SUFFIX;
            if(!FileUtil.exists(fileName)) {
                throw new RuntimeException("Invalid BeatInfo file for score: " + scoreName);
            }
            File beatInfoFile = new File(fileName);
            List<ScoreElement> scoreElements = ScoreLoader.loadScoreElements(beatInfoFile);
            if(scoreElements == null) {
                throw new RuntimeException("Invalid score elements for score: " + scoreName);
            }
            for(ScoreElement scoreElement : scoreElements) {
//                LOG.info("load: element: {}", scoreElement);
                scoreElementsContainer.addScoreElement(scoreElement);
            }
            loadNoteInfo(scoreName);
            pageNo += scoreElementsContainer.getPageNo();
        }
    }

    private void loadNoteInfo(String scoreName) throws Exception {
        String fileName = IN_DIR + Consts.SLASH + scoreName + Consts.NOTE_INFO_FILE_SUFFIX;
        if(!FileUtil.exists(fileName)) {
            return;
        }
        File noteInfoFile = new File(fileName);
        List<String> lines = FileUtil.loadFile(noteInfoFile);
        noteInfos.put(scoreName, lines);
    }

    private void validate() throws Exception {
        if(!FileUtil.exists(IN_DIR)) {
            throw new RuntimeException("Invalid IN_DIR");
        }
        if(!FileUtil.exists(OUT_DIR)) {
            FileUtil.getOrCreateDir(OUT_DIR);
        }
        if(SCORES_ORDER == null || SCORES_ORDER.length <= 0) {
            throw new RuntimeException("Invalid Score Order list");
        }
    }

    private static ResourceType getResourceType(String resource) {
        if (resource == null) {
            return FILE;
        }
        if (resource.startsWith(RESOURCE_JAVASCRIPT)) {
            return JAVASCRIPT;
        } else if (resource.startsWith(RESOURCE_TRANSITION)) {
            return TRANSITION;
        } else if (resource.startsWith(RESOURCE_WEB)) {
            return WEB_AUDIENCE;
        } else if (resource.startsWith(RESOURCE_MAXMSP)) {
            return MAXMSP;
        } else if (resource.startsWith(RESOURCE_SCRIPT_ENGINE)) {
            return SCRIPT_ENGINE;
        }
        return FILE;
    }

    public static void main(String[] args) {
        try {
            ScoreMerger merger = new ScoreMerger();
            merger.merge();
        } catch (Exception e) {
            LOG.error("Failed to merge", e);
        }
    }

    static class InstrumentTracker {
        private final String name;
        private int pageNo = 0;
        private int barNo = 0;
        private int beatNo= 0;
        private int unitBeatNo = 1;

        public InstrumentTracker(String name) {
            this.name = name;
        }
        public void setBarNo(int barNo) {
            this.barNo = barNo;
        }
        public void setBeatNo(int beatNo) {
            this.beatNo = beatNo;
        }
        public void setUnitBeatNo(int unitBeatNo) {
            this.unitBeatNo = unitBeatNo;
        }
        public int getPageNo() {
            return pageNo;
        }
        public int getBarNo() {
            return barNo;
        }
        public int getBeatNo() {
            return beatNo;
        }
        public int getUnitBeatNo() {
            return unitBeatNo;
        }
        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }
    }

    static class InstrumentCounter {
        private final String name;
        private int pageNo = 0;
        private int barNo = 0;
        private int beatNo= 0;
        private int unitBeatNo = 1;
        private long startTimeMillis = 0;
        private long endTimeMillis = 0;

        public InstrumentCounter(String name) {
            this.name = name;
        }

        public void setBarNo(int barNo) {
            this.barNo = barNo;
        }

        public void setBeatNo(int beatNo) {
            this.beatNo = beatNo;
        }

        public void setUnitBeatNo(int unitBeatNo) {
            this.unitBeatNo = unitBeatNo;
        }

        public int getPageNo() {
            return pageNo;
        }

        public int getBarNo() {
            return barNo;
        }

        public int getBeatNo() {
            return beatNo;
        }

        public int getUnitBeatNo() {
            return unitBeatNo;
        }

        public void incrementPageNo() {
            pageNo++;
        }

        public void incrementBarNo() {
            barNo++;
        }

        public void incrementBeatNo() {
            beatNo++;
        }

        public void incrementUnitBeatNo(int increment) {
            unitBeatNo += increment;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public long getStartTimeMillis() {
            return startTimeMillis;
        }

        public void setStartTimeMillis(long startTimeMillis) {
            this.startTimeMillis = startTimeMillis;
        }

        public long getEndTimeMillis() {
            return endTimeMillis;
        }

        public void setEndTimeMillis(long endTimeMillis) {
            this.endTimeMillis = endTimeMillis;
        }
    }

    static class ScoreElementsContainer {
        private final String scoreName;
        private final boolean isMaster;
        private final HashMap<Integer, HashMap<String, Set<ScoreElement>>> pageElementsMap = new HashMap<>();

        public ScoreElementsContainer(String scoreName, boolean isMaster) {
            this.scoreName = scoreName;
            this.isMaster = isMaster;
        }

        public void addScoreElement(ScoreElement scoreElement) {
            String scname = scoreElement.getScoreName();
            if(!scoreName.equals(scname)) {
                LOG.error("Unexpected score name: {} expected: {}", scname, scoreName);
                return;
            }
            int pageNo = scoreElement.getPageNo();
            String instr = scoreElement.getInstrumentName();
            if(isMaster) {
                String resource = scoreElement.getResource();
                ResourceType resourceType = getResourceType(resource);
                if (resourceType != FILE) {
                    instr = instr + SCRIPTS;
                }
            }
            HashMap<String, Set<ScoreElement>> pageElements = pageElementsMap.computeIfAbsent(pageNo, k -> new HashMap<>());
            Set<ScoreElement> scoreElements = pageElements.computeIfAbsent(instr, k -> new TreeSet<>());
            scoreElements.add(scoreElement);
        }

        public HashMap<Integer, HashMap<String, Set<ScoreElement>>> getPageElementsMap() {
            return pageElementsMap;
        }

        public String getScoreName() {
            return scoreName;
        }

        public int getPageNo() {
            return pageElementsMap.keySet().size();
        }

        public List<String> export() {
            List<String> lines = new ArrayList<>();
            int sectionPageNo = getPageNo();
            for(int i = 1; i <= sectionPageNo; i++) {
                HashMap<String, Set<ScoreElement>> pageElements = pageElementsMap.get(i);
                Set<String> instSet = pageElements.keySet();
                ArrayList<String> instList = new ArrayList<>(instSet);
                Collections.sort(instList);

                for(String instr : instList) {
                    Set<ScoreElement> elements = pageElements.get(instr);
                    for(ScoreElement element : elements) {
                        String line = element.toCsvString();
                        lines.add(line);
                    }
                }
            }
            return lines;
        }
    }
}
