package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.NoteDuration;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoImpl;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.TimeSignatureImpl;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class WebScoreLoader {
    static final Logger LOG = LoggerFactory.getLogger(WebScoreLoader.class);

    static final String ITEM_DELIMITER = "`";

    public static volatile String workingDir;

    private static String[] expextedHeaders = {
            "script",            //0
            "unitBeatNo",       //1
    };

    static WebScore load(String path) throws Exception {
        if(path != null) {
            File file = FileUtil.getFileFromClassPath(path);
            return load(file);
        }
        return null;
    }

    static WebScore load(File file) throws Exception {
        if(file == null) {
           return null;
        }

        workingDir = file.getParent();

        List<String> lines = FileUtil.loadFile(file);
        return loadLines(lines);
    }

    static WebScore loadLines(List<String> lines) throws Exception {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        String headersLine = lines.remove(0);
        String[] headers = parseHeaders(headersLine);

        boolean isHeaderCorrect = Arrays.equals(expextedHeaders, headers);
        if (!isHeaderCorrect) {
            LOG.error("Unexpeted headers: " + headers);
            return null;
        }

        List<WebScoreElement> scoreElements = new ArrayList<>();

        for (String line : lines) {
            WebScoreElement scoreElement = parseLine(line);
            scoreElements.add(scoreElement);
        }

        WebScore score = createScoreFromElements(scoreElements);

        return score;
    }

    private static WebScore createScoreFromElements(List<WebScoreElement> scoreElements) throws Exception {
        if (scoreElements == null || scoreElements.isEmpty()) {
            return null;
        }

        WebScoreElement scoreElement0 = scoreElements.get(0);

        for (WebScoreElement scoreElement : scoreElements) {
            processScoreElement(scoreElement, score);
        }

        return null;
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

          processFileScoreElement(scoreElement, score, resource, scoreId);
    }


    private static void processFileScoreElement(ScoreElement scoreElement, BasicScore score, String fileName, Id scoreId) throws Exception {
        //instrument
        String instrumentName = scoreElement.getInstrumentName();
        boolean isAudioVideo = false;
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

        if (!score.containsPage(pageId)) {
            InscorePageMap inscorePageMap = loadPageInscoreMap(pageId, fileName);
            BasicPage page = new BasicPage(pageId, pageName, fileName, inscorePageMap);
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

    public static InscorePageMap loadPageInscoreMap(PageId pageId, String fileName) {
        String mapFilename = fileName + Consts.INSCORE_FILE_SUFFIX + Consts.TXT_FILE_EXTENSION;
        String path = workingDir + Consts.SLASH + mapFilename;
        InscorePageMap inscorePageMap = new InscorePageMap(pageId);
        try {
            File file = FileUtil.getFileFromPath(path);
            List<String> lines = FileUtil.loadFile(file);

            for(String line : lines) {
                InscoreMapElement inscoreMapElement = InscoreMapElement.parseLine(line);
                inscorePageMap.addElement(inscoreMapElement);
            }
        } catch (Exception e) {
            LOG.error("Failed to load map file {}", path, e);
        }

        return inscorePageMap;
    }

    private static WebScoreElement parseLine(String line) {
        WebScoreElement scoreElement = null;
        try {
            if (line == null) {
                return null;
            }
            String[] values = line.split(ITEM_DELIMITER);

            scoreElement = new WebScoreElement();

            scoreElement.setScript(values[0]);
            scoreElement.setUnitBeatNo(Integer.parseInt(values[1]));

        } catch (NumberFormatException e) {
            LOG.error("Failed to parse line: " + line);
        }

        return scoreElement;
    }

    private static String[] parseHeaders(String line) {
        if (line == null) {
            return null;
        }
        String[] headers = line.split(Consts.COMMA);
        return headers;
    }
}
