package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.*;
import com.xenaksys.szcore.model.id.*;
import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.xenaksys.szcore.score.ResourceType.*;


public class ScoreLoader {
    static final Logger LOG = LoggerFactory.getLogger(ScoreLoader.class);

    static final String RECOURCE_JAVASCRIPT = "javascript";
    static final String RECOURCE_TRANSITION = "transition";
    static final String SCRIPT_DELIMITER = ":";
    static final String SCRIPT_COMMA_REPLACE_CHAR = "|";
    static final String COMMA = ",";
    static final String CURLY_QUOTE = "�";
    static final String SINGLE_QUOTE = "'";
    static final String AV = "AV";



    private static String[] expextedHeaders = {
            "scoreName",            //0
            "instrumentName",       //1
            "pageName",             //2
            "pageNo",               //3
            "barName",              //4
            "barNo",                //5
            "timeSigNum",           //6
            "timeSigDenom",         //7
            "tempoBpm",             //8
            "tempoBeatValue",       //9
            "beatNo",               //10
            "startTimeMillis",      //11
            "durationTimeMillis",   //12
            "endTimeMillis",        //13
            "startBaseBeatUnits",   //14
            "durationBeatUnits",    //15
            "endBaseBeatUnits",     //16
            "xStartPxl",            //17
            "xEndPxl",              //18
            "yStartPxl",            //19
            "yEndPxl",              //20
            "isUpbeat",             //21
            "fileName",             //22
            "unitBeatNo"            //23
    };

    static Score load(String path) throws Exception {
        List<String> lines = FileUtil.loadLinesFromFile(path);
        return loadLines(lines);
    }

    static Score load(File file) throws Exception {
        List<String> lines = FileUtil.loadFile(file);
        return loadLines(lines);
    }

    static Score loadLines(List<String> lines) throws Exception {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        String headersLine = lines.remove(0);
        String[] headers = parseHeaders(headersLine);

        boolean isHeaderCorrect = Arrays.equals(expextedHeaders, headers);
        if (!isHeaderCorrect) {
            LOG.error("Unexpected headers: " + headers);
            return null;
        }

        List<ScoreElement> scoreElements = new ArrayList<>();

        for (String line : lines) {
            ScoreElement scoreElement = parseLine(line);
            scoreElements.add(scoreElement);
        }

        BasicScore score = createScoreFromElements(scoreElements);

        return score;
    }

    private static BasicScore createScoreFromElements(List<ScoreElement> scoreElements) throws Exception {
        if (scoreElements == null || scoreElements.isEmpty()) {
            return null;
        }

        ScoreElement scoreElement0 = scoreElements.get(0);
        String scoreName = scoreElement0.getScoreName();
        if (scoreName == null) {
            LOG.error("Invalid Score name");
            return null;
        }

        StrId scoreId = new StrId(scoreName);
        BasicScore score = new BasicScore(scoreId);

        for (ScoreElement scoreElement : scoreElements) {
            processScoreElement(scoreElement, score);
        }

        return score;
    }

    private static void processScoreElement(ScoreElement scoreElement, BasicScore score) throws Exception {

        Id scoreId = score.getId();
        if (scoreId == null) {
            LOG.error("Invalid NULL Score ID");
            return;
        }

        String elementScoreName = score.getName();
        String scoreName = scoreElement.getScoreName();

        if (scoreName != null && !scoreName.equals(elementScoreName)) {
            LOG.error("Unexpected score name: " + elementScoreName);
            return;
        }

        String resource = scoreElement.getResource();
        ResourceType resouceType = getResourceType(resource);
        switch(resouceType) {
            case JAVASCRIPT:
                processJavascriptScoreElement(scoreElement, score, resource, scoreId);
                break;
            case TRANSITION:
                processTransitionScoreElement(scoreElement, score, resource, scoreId);
                break;
            case FILE:
            default:
                processFileScoreElement(scoreElement, score, resource, scoreId);
        }

    }

    private static ResourceType getResourceType(String resource) {
        if(resource == null) {
            return FILE;

        }
        if(resource.startsWith(RECOURCE_JAVASCRIPT)){
            return JAVASCRIPT;
        }
        if(resource.startsWith(RECOURCE_TRANSITION)){
            return TRANSITION;
        }
        return FILE;
    }

    private static void processFileScoreElement(ScoreElement scoreElement, BasicScore score, String fileName, Id scoreId) throws Exception {
        //instrument
        String instrumentName = scoreElement.getInstrumentName();
        boolean isAudioVideo = false;
        if(instrumentName.startsWith(AV)) {
            isAudioVideo = true;
        }
        StrId instrumentId = new StrId(instrumentName);
        Instrument intrument = new BasicInstrument(instrumentId, instrumentName, isAudioVideo);
        Collection<Instrument> instruments = score.getInstruments();
        if (!instruments.contains(intrument)) {
            score.addInstrument(intrument);
        }

        //page
        String pageName = scoreElement.getPageName();
        int pageNo = scoreElement.getPageNo();
        if (fileName.contains(Consts.SLASH)) {
            fileName = fileName.substring(fileName.lastIndexOf(Consts.SLASH) + 1);
        }
        PageId pageId = new PageId(pageNo, instrumentId, scoreId);
        BasicPage page = new BasicPage(pageId, pageName, fileName);
        if (!score.containsPage(page)) {
            score.addPage(page);
        }

        //bar
        String barName = scoreElement.getBarName();
        int barNo = scoreElement.getBarNo();
        BarId barId = new BarId(barNo, instrumentId, pageId, scoreId);
        int bpm = scoreElement.getTempoBpm();
        int tempoBeatValue = scoreElement.getTempoBeatValue();
        NoteDuration noteDuration = NoteDuration.get(tempoBeatValue);
        if (noteDuration == null) {
            throw new Exception("Invalid note duration NULL for tempoBeatValue: " + tempoBeatValue);
        }
        Tempo tempo = new TempoImpl(bpm, noteDuration);
        int numberOfBeats = scoreElement.getTimeSigNum();
        int timeSigBeatValue = scoreElement.getTimeSigDenom();
        NoteDuration timeSigNoteDuration = NoteDuration.get(timeSigBeatValue);
        if (timeSigNoteDuration == null) {
            throw new Exception("Invalid note duration NULL for timeSigBeatValue: " + timeSigBeatValue);
        }
        TimeSignature timeSignature = new TimeSignatureImpl(numberOfBeats, timeSigNoteDuration);
        BasicBar bar = new BasicBar(barId, barName, tempo, timeSignature);
        if (!score.containsBar(bar)) {
            score.addBar(bar);
        }

        //beat
        int beatNo = scoreElement.getBeatNo();
        long startTimeMillis = scoreElement.getStartTimeMillis();
        long durationMillis = scoreElement.getDurationTimeMillis();
        long endTimeMillis = scoreElement.getEndTimeMillis();
        int baseBeatUnitsNoAtStart = scoreElement.getStartBaseBeatUnits();
        int baseBeatUnitsDuration = scoreElement.getDurationBeatUnits();
        int baseBeatUnitsNoOnEnd = scoreElement.getEndBaseBeatUnits();
        int positionXStartPxl = scoreElement.getxStartPxl();
        int positionXEndPxl = scoreElement.getxEndPxl();
        int positionYStartPxl = scoreElement.getyStartPxl();
        int positionYEndPxl = scoreElement.getyEndPxl();
        int isUpbeatInt = scoreElement.getIsUpbeat();
        boolean isUpbeat = !(isUpbeatInt == 0);

        BeatId id = new BeatId(beatNo, instrumentId, pageId, scoreId, barId, baseBeatUnitsNoAtStart);
        BasicBeat beat = new BasicBeat(id, startTimeMillis, durationMillis, endTimeMillis, baseBeatUnitsNoAtStart,
                baseBeatUnitsDuration, baseBeatUnitsNoOnEnd, positionXStartPxl, positionXEndPxl,
                positionYStartPxl, positionYEndPxl, isUpbeat);

        if (!score.containsBeat(beat)) {
            score.addBeat(beat);
        }
    }

    private static void processJavascriptScoreElement(ScoreElement scoreElement, BasicScore score, String resource, Id scoreId) throws Exception {

        String instrumentName = scoreElement.getInstrumentName();
        StrId instrumentId = new StrId(instrumentName);

        int pageNo = scoreElement.getPageNo();
        PageId pageId = new PageId(pageNo, instrumentId, scoreId);

        int barNo = scoreElement.getBarNo();
        BarId barId = new BarId(barNo, instrumentId, pageId, scoreId);

        int beatNo = scoreElement.getBeatNo();
        int baseBeatUnitsNoAtStart = scoreElement.getStartBaseBeatUnits();

        BeatId beatId = new BeatId(beatNo, instrumentId, pageId, scoreId, barId, baseBeatUnitsNoAtStart);

        IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());

        String script = resource;
        if(script.startsWith(RECOURCE_JAVASCRIPT)){
            script = script.substring(RECOURCE_JAVASCRIPT.length());
        }

        if(script.startsWith(SCRIPT_DELIMITER)){
            script = script.substring(SCRIPT_DELIMITER.length());
        }
        
        if(script.contains(SCRIPT_COMMA_REPLACE_CHAR)){
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if(script.contains(CURLY_QUOTE)){
            script = script.replace(CURLY_QUOTE, SINGLE_QUOTE);
        }

        Script scriptObj = new BasicScript(id, beatId, script);
        LOG.info("Created script: {}", scriptObj);
        score.addScript(scriptObj);
    }

    private static void processTransitionScoreElement(ScoreElement scoreElement, BasicScore score, String resource, Id scoreId) throws Exception {

        String instrumentName = scoreElement.getInstrumentName();
        StrId instrumentId = new StrId(instrumentName);

        int pageNo = scoreElement.getPageNo();
        PageId pageId = new PageId(pageNo, instrumentId, scoreId);

        int barNo = scoreElement.getBarNo();
        BarId barId = new BarId(barNo, instrumentId, pageId, scoreId);

        int beatNo = scoreElement.getBeatNo();
        int baseBeatUnitsNoAtStart = scoreElement.getStartBaseBeatUnits();

        BeatId beatId = new BeatId(beatNo, instrumentId, pageId, scoreId, barId, baseBeatUnitsNoAtStart);

        IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());

        String script = resource;
        if(script.startsWith(RECOURCE_TRANSITION)){
            script = script.substring(RECOURCE_TRANSITION.length());
        }

        if(script.startsWith(SCRIPT_DELIMITER)){
            script = script.substring(SCRIPT_DELIMITER.length());
        }

        if(script.contains(SCRIPT_COMMA_REPLACE_CHAR)){
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if(script.contains(CURLY_QUOTE)){
            script = script.replace(CURLY_QUOTE, SINGLE_QUOTE);
        }

        if(script.isEmpty()) {
            return;
        }
        
        long duration = 0L;
        long frequency = 0L;
        long startValue = 0L;
        long endValue = 0L;
        String component = null;

        try {
            String [] params = script.split(COMMA);
            for(int i = 0; i < params.length; i++) {
                String val = params[i];
                if(val == null || val.isEmpty()) {
                    continue;
                }

                switch (i){
                    case 0:
                        duration = Long.parseLong(val);
                        break;
                    case 1:
                        frequency = Long.parseLong(val);
                        break;
                    case 2:
                        startValue = Long.parseLong(val);
                        break;
                    case 3:
                        endValue = Long.parseLong(val);
                        break;
                    case 4:
                        component = val;
                        break;
                    default:
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to process Transition configuration {}",script, e);
            return;
        }

        Transition transitionObj = new BasicTransition(id, beatId, component, duration, startValue, endValue, frequency);
        LOG.info("Created transitionObj: {}", transitionObj);
        score.addScript(transitionObj);
    }


    private static ScoreElement parseLine(String line) {
        ScoreElement scoreElement = null;
        try {
            if (line == null) {
                return null;
            }
            String[] values = line.split(Consts.COMMA);

            scoreElement = new ScoreElement();

            scoreElement.setScoreName(values[0]);
            scoreElement.setInstrumentName(values[1]);
            scoreElement.setPageName(values[2]);
            scoreElement.setPageNo(Integer.parseInt(values[3]));
            scoreElement.setBarName(values[4]);
            scoreElement.setBarNo(Integer.parseInt(values[5]));
            scoreElement.setTimeSigNum(parseTimeSigNum(values[6]));
            scoreElement.setTimeSigDenom(Integer.parseInt(values[7]));
            scoreElement.setTempoBpm(Integer.parseInt(values[8]));
            scoreElement.setTempoBeatValue(Integer.parseInt(values[9]));
            scoreElement.setBeatNo(Integer.parseInt(values[10]));
            scoreElement.setStartTimeMillis(Long.parseLong(values[11]));
            scoreElement.setDurationTimeMillis(Long.parseLong(values[12]));
            scoreElement.setEndTimeMillis(Long.parseLong(values[13]));
            scoreElement.setStartBaseBeatUnits(Integer.parseInt(values[14]));
            scoreElement.setDurationBeatUnits(Integer.parseInt(values[15]));
            scoreElement.setEndBaseBeatUnits(Integer.parseInt(values[16]));
            scoreElement.setxStartPxl(Integer.parseInt(values[17]));
            scoreElement.setxEndPxl(Integer.parseInt(values[18]));
            scoreElement.setyStartPxl(Integer.parseInt(values[19]));
            scoreElement.setyEndPxl(Integer.parseInt(values[20]));
            scoreElement.setIsUpbeat(Integer.parseInt(values[21]));
            scoreElement.setResource(values[22]);
            scoreElement.setUnitBeatNo(Integer.parseInt(values[23]));

        } catch (NumberFormatException e) {
            LOG.error("Failed to parse line: " + line);
        }

        return scoreElement;
    }

    private static int parseTimeSigNum(String value) {
        if (value.contains(Consts.PLUS)) {
            String[] values = value.split(Consts.PLUS_REGEX);
            int out = 0;
            for (String val : values) {
                out += Integer.parseInt(val);
            }
            return out;
        }
        return Integer.parseInt(value);
    }

    private static String[] parseHeaders(String line) {
        if (line == null) {
            return null;
        }
        String[] headers = line.split(Consts.COMMA);
        return headers;
    }
}
