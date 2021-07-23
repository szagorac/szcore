package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.instrument.BasicInstrument;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.NoteDuration;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoImpl;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.TimeSignatureImpl;
import com.xenaksys.szcore.model.Transition;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;
import com.xenaksys.szcore.scripting.ScriptingEngineScript;
import com.xenaksys.szcore.util.FileUtil;
import com.xenaksys.szcore.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.score.ResourceType.FILE;
import static com.xenaksys.szcore.score.ResourceType.JAVASCRIPT;
import static com.xenaksys.szcore.score.ResourceType.MAXMSP;
import static com.xenaksys.szcore.score.ResourceType.SCRIPT_ENGINE;
import static com.xenaksys.szcore.score.ResourceType.TRANSITION;
import static com.xenaksys.szcore.score.ResourceType.WEB_AUDIENCE;


public class ScoreLoader {
    static final Logger LOG = LoggerFactory.getLogger(ScoreLoader.class);

    static final String RESOURCE_JAVASCRIPT = "javascript";
    static final String RESOURCE_WEB = "web";
    static final String RESOURCE_MAXMSP = "max";
    static final String RESOURCE_SCRIPT_ENGINE = "sce";
    static final String RESOURCE_TRANSITION = "transition";
    static final String BEAT = "beat";
    static final String IS_RESET_POINT = "reset";
    static final String ONLY = "only";
    static final String BOTH = "both";
    static final String EQUALS = "=";
    static final String NAME_VAL_DELIMITER = "=";
    static final String SCRIPT_DELIMITER = ":";
    static final String SCRIPT_COMMA_REPLACE_CHAR = "|";
    static final String COMMA = ",";
    static final String COMMA_TOKEN = "@C@";
    static final String CURLY_QUOTE = "�";
    static final String SINGLE_QUOTE = "'";
    static final String AV = "AV";

    public static volatile String workingDir;

    private static final String[] expextedHeaders = {
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
            "resource",             //22
            "unitBeatNo"            //23
    };

    static Score load(String path) throws Exception {
        if (path != null) {
            File file = FileUtil.getFileFromClassPath(path);
            return load(file);
        }
        return null;
    }

    static Score load(File file) throws Exception {
        if (file == null) {
            return null;
        }

        workingDir = file.getParent();

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
            LOG.error("Unexpeted headers: " + headers);
            return null;
        }

        List<ScoreElement> scoreElements = new ArrayList<>();

        for (String line : lines) {
            ScoreElement scoreElement = parseLine(line);
            scoreElements.add(scoreElement);
        }

        return createScoreFromElements(scoreElements);
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
        switch (resouceType) {
            case JAVASCRIPT:
                processJavascriptScoreElement(scoreElement, score, resource, scoreId);
                break;
            case TRANSITION:
                processTransitionScoreElement(scoreElement, score, resource, scoreId);
                break;
            case WEB_AUDIENCE:
                processWebAudienceElement(scoreElement, score, resource, scoreId);
                break;
            case MAXMSP:
                processMaxMspScoreElement(scoreElement, score, resource, scoreId);
                break;
            case SCRIPT_ENGINE:
                processScriptEngineScoreElement(scoreElement, score, resource, scoreId);
                break;
            case FILE:
            default:
                processFileScoreElement(scoreElement, score, resource, scoreId);
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

    private static void processFileScoreElement(ScoreElement scoreElement, BasicScore score, String fileName, Id scoreId) throws Exception {
        //instrument
        String instrumentName = scoreElement.getInstrumentName();
        boolean isAudioVideo = false;
        if (instrumentName.startsWith(AV)) {
            isAudioVideo = true;
        }
        InstrumentId instrumentId = new InstrumentId(instrumentName);
        Instrument instrument = new BasicInstrument(instrumentId, instrumentName, isAudioVideo);
        Collection<Instrument> instruments = score.getInstruments();
        if (!instruments.contains(instrument)) {
            score.addInstrument(instrument);
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

            for (String line : lines) {
                InscoreMapElement inscoreMapElement = InscoreMapElement.parseLine(line);
                inscorePageMap.addElement(inscoreMapElement);
            }
            inscorePageMap.createInscoreStr();
            inscorePageMap.createWebStr();
        } catch (Exception e) {
            LOG.error("Failed to load map file {}", path, e);
        }

        return inscorePageMap;
    }

    private static void processMaxMspScoreElement(ScoreElement scoreElement, BasicScore score, String resource, Id scoreId) throws Exception {
        String instrumentName = scoreElement.getInstrumentName();
        InstrumentId instrumentId = new InstrumentId(instrumentName);

        boolean isAudioVideo = false;
        if (instrumentName.startsWith(AV)) {
            isAudioVideo = true;
        }
        Instrument instrument = new BasicInstrument(instrumentId, instrumentName, isAudioVideo);
        Collection<Instrument> oscPlayers = score.getOscPlayers();
        if (!oscPlayers.contains(instrument)) {
            score.addOscPlayer(instrument);
        }

        int pageNo = scoreElement.getPageNo();
        PageId pageId = new PageId(pageNo, instrumentId, scoreId);

        int barNo = scoreElement.getBarNo();
        BarId barId = new BarId(barNo, instrumentId, pageId, scoreId);

        int beatNo = scoreElement.getBeatNo();
        int baseBeatUnitsNoAtStart = scoreElement.getStartBaseBeatUnits();

        BeatId beatId = new BeatId(beatNo, instrumentId, pageId, scoreId, barId, baseBeatUnitsNoAtStart);

        IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());

        String script = resource;
        String target = Consts.OSC_ADDRESS_ZSCORE;
        List<Object> args = new ArrayList<>();
        boolean isReset = false;
        boolean isResetOnly = false;

        if (script.startsWith(RESOURCE_MAXMSP)) {
            script = script.substring(RESOURCE_MAXMSP.length());
        }

        if (script.startsWith(SCRIPT_DELIMITER)) {
            script = script.substring(SCRIPT_DELIMITER.length());
        }

        if (script.startsWith(BEAT)) {
            script = script.substring(BEAT.length());
            if (script.startsWith(NAME_VAL_DELIMITER)) {
                script = script.substring(NAME_VAL_DELIMITER.length());
            }

            int end = script.indexOf(SCRIPT_DELIMITER);
            String beatNoStr = script.substring(0, end);
            script = script.substring(end);
            int scriptBarOffsetBeatNo = Integer.parseInt(beatNoStr);
            if (scriptBarOffsetBeatNo != beatNo) {
                int offsetMod = (scriptBarOffsetBeatNo < 0) ? 0 : -1;
                scriptBarOffsetBeatNo = beatNo + scriptBarOffsetBeatNo + offsetMod;

                BeatId instrumentBeatId = score.getInstrumentBeat(instrumentId, scriptBarOffsetBeatNo);
                if (instrumentBeatId == null) {
                    LOG.warn("processWebScoreElement: Could not find instrument beat: {}", scriptBarOffsetBeatNo);
                } else {
                    beatId = instrumentBeatId;
                }
            }


            if (script.startsWith(SCRIPT_DELIMITER)) {
                script = script.substring(SCRIPT_DELIMITER.length());
            }
        }

        if (script.startsWith(IS_RESET_POINT)) {
            script = script.substring(IS_RESET_POINT.length());
            if (script.startsWith(NAME_VAL_DELIMITER)) {
                script = script.substring(NAME_VAL_DELIMITER.length());
            }

            int end = script.indexOf(SCRIPT_DELIMITER);
            String resetPointType = script.substring(0, end);
            script = script.substring(end);

            isReset = true;
            switch (resetPointType) {
                case ONLY:
                    isResetOnly = true;
                    break;
                case BOTH:
                    isResetOnly = false;
                    break;
            }

            if (script.startsWith(SCRIPT_DELIMITER)) {
                script = script.substring(SCRIPT_DELIMITER.length());
            }

        }

        if (script.contains(SCRIPT_COMMA_REPLACE_CHAR)) {
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if (script.contains(CURLY_QUOTE)) {
            script = script.replace(CURLY_QUOTE, SINGLE_QUOTE);
        }

        String cmd = EMPTY;
        //args 0 = cmd, 1 = target, other args ...
        String[] sargs = script.split(COMMA);
        if (sargs.length > 0) {
            cmd = sargs[0];
            args.add(cmd);
        }
        if (sargs.length > 1) {
            target += sargs[1];
        }

        if (sargs.length > 2) {
            for (int i = 2; i < sargs.length; i++) {
                String arg = ParseUtil.parseToken(sargs[i], COMMA_TOKEN, COMMA);
                args.add(ParseUtil.convertToType(arg));
            }
        }

        Script scriptObj = new OscScript(id, beatId, target, args, isReset, isResetOnly);
        LOG.info("processMaxMspScoreElement: Created script: {}", scriptObj);
        score.addScript(scriptObj);
    }

    private static void processWebAudienceElement(ScoreElement scoreElement, BasicScore score, String resource, Id scoreId) throws Exception {
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
        boolean isResetPoint = false;
        boolean isResetOnly = false;

        if (script.startsWith(RESOURCE_WEB)) {
            script = script.substring(RESOURCE_WEB.length());
        }

        if (script.startsWith(SCRIPT_DELIMITER)) {
            script = script.substring(SCRIPT_DELIMITER.length());
        }

        if (script.startsWith(BEAT)) {
            script = script.substring(BEAT.length());
            if (script.startsWith(NAME_VAL_DELIMITER)) {
                script = script.substring(NAME_VAL_DELIMITER.length());
            }

            int end = script.indexOf(SCRIPT_DELIMITER);
            String beatNoStr = script.substring(0, end);
            script = script.substring(end);
            int scriptBarOffsetBeatNo = Integer.parseInt(beatNoStr);
            if (scriptBarOffsetBeatNo != beatNo) {
                int offsetMod = (scriptBarOffsetBeatNo < 0) ? 0 : -1;
                scriptBarOffsetBeatNo = beatNo + scriptBarOffsetBeatNo + offsetMod;

                BeatId instrumentBeatId = score.getInstrumentBeat(instrumentId, scriptBarOffsetBeatNo);
                if (instrumentBeatId == null) {
                    LOG.warn("processWebScoreElement: Could not find instrument beat: {}", scriptBarOffsetBeatNo);
                } else {
                    beatId = instrumentBeatId;
                }
            }

            if (script.startsWith(SCRIPT_DELIMITER)) {
                script = script.substring(SCRIPT_DELIMITER.length());
            }
        }

        if (script.startsWith(IS_RESET_POINT)) {
            script = script.substring(IS_RESET_POINT.length());
            if (script.startsWith(NAME_VAL_DELIMITER)) {
                script = script.substring(NAME_VAL_DELIMITER.length());
            }

            int end = script.indexOf(SCRIPT_DELIMITER);
            String resetPointType = script.substring(0, end);
            script = script.substring(end);

            isResetPoint = true;
            switch (resetPointType) {
                case ONLY:
                    isResetOnly = true;
                    break;
                case BOTH:
                    isResetOnly = false;
                    break;
            }

            if (script.startsWith(SCRIPT_DELIMITER)) {
                script = script.substring(SCRIPT_DELIMITER.length());
            }

        }

        if (script.contains(SCRIPT_COMMA_REPLACE_CHAR)) {
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if (script.contains(CURLY_QUOTE)) {
            script = script.replace(CURLY_QUOTE, SINGLE_QUOTE);
        }

        script = ParseUtil.parseToken(script, COMMA_TOKEN, COMMA);

        Script scriptObj = new WebAudienceScoreScript(id, beatId, script, isResetPoint, isResetOnly);
        LOG.info("Created script: {}", scriptObj);
        score.addScript(scriptObj);
    }

    private static void processScriptEngineScoreElement(ScoreElement scoreElement, BasicScore score, String resource, Id scoreId) throws Exception {
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
        boolean isResetPoint = false;
        boolean isResetOnly = false;

        if (script.startsWith(RESOURCE_SCRIPT_ENGINE)) {
            script = script.substring(RESOURCE_SCRIPT_ENGINE.length());
        }

        if (script.startsWith(SCRIPT_DELIMITER)) {
            script = script.substring(SCRIPT_DELIMITER.length());
        }

        if (script.startsWith(BEAT)) {
            script = script.substring(BEAT.length());
            if (script.startsWith(NAME_VAL_DELIMITER)) {
                script = script.substring(NAME_VAL_DELIMITER.length());
            }

            int end = script.indexOf(SCRIPT_DELIMITER);
            String beatNoStr = script.substring(0, end);
            script = script.substring(end);
            int scriptBarOffsetBeatNo = Integer.parseInt(beatNoStr);
            if (scriptBarOffsetBeatNo != beatNo) {
                int offsetMod = (scriptBarOffsetBeatNo < 0) ? 0 : -1;
                scriptBarOffsetBeatNo = beatNo + scriptBarOffsetBeatNo + offsetMod;

                BeatId instrumentBeatId = score.getInstrumentBeat(instrumentId, scriptBarOffsetBeatNo);
                if (instrumentBeatId == null) {
                    LOG.warn("processWebScoreElement: Could not find instrument beat: {}", scriptBarOffsetBeatNo);
                } else {
                    beatId = instrumentBeatId;
                }
            }

            if (script.startsWith(SCRIPT_DELIMITER)) {
                script = script.substring(SCRIPT_DELIMITER.length());
            }
        }

        if (script.startsWith(IS_RESET_POINT)) {
            script = script.substring(IS_RESET_POINT.length());
            if (script.startsWith(NAME_VAL_DELIMITER)) {
                script = script.substring(NAME_VAL_DELIMITER.length());
            }

            int end = script.indexOf(SCRIPT_DELIMITER);
            String resetPointType = script.substring(0, end);
            script = script.substring(end);

            isResetPoint = true;
            switch (resetPointType) {
                case ONLY:
                    isResetOnly = true;
                    break;
                case BOTH:
                    isResetOnly = false;
                    break;
            }

            if (script.startsWith(SCRIPT_DELIMITER)) {
                script = script.substring(SCRIPT_DELIMITER.length());
            }
        }

        if (script.contains(SCRIPT_COMMA_REPLACE_CHAR)) {
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if (script.contains(CURLY_QUOTE)) {
            script = script.replace(CURLY_QUOTE, SINGLE_QUOTE);
        }

        Script scriptObj = new ScriptingEngineScript(id, beatId, script, isResetPoint, isResetOnly);
        LOG.info("Created ScriptingEngineScript script: {}", scriptObj);
        score.addScript(scriptObj);
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
        if (script.startsWith(RESOURCE_JAVASCRIPT)) {
            script = script.substring(RESOURCE_JAVASCRIPT.length());
        }

        if (script.startsWith(SCRIPT_DELIMITER)) {
            script = script.substring(SCRIPT_DELIMITER.length());
        }

        if (script.contains(SCRIPT_COMMA_REPLACE_CHAR)) {
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if (script.contains(CURLY_QUOTE)) {
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
        if (script.startsWith(RESOURCE_TRANSITION)) {
            script = script.substring(RESOURCE_TRANSITION.length());
        }

        if (script.startsWith(SCRIPT_DELIMITER)) {
            script = script.substring(SCRIPT_DELIMITER.length());
        }

        if (script.contains(SCRIPT_COMMA_REPLACE_CHAR)) {
            script = script.replace(SCRIPT_COMMA_REPLACE_CHAR, COMMA);
        }

        if (script.contains(CURLY_QUOTE)) {
            script = script.replace(CURLY_QUOTE, SINGLE_QUOTE);
        }

        if (script.isEmpty()) {
            return;
        }

        long duration = 0L;
        long frequency = 0L;
        long startValue = 0L;
        long endValue = 0L;
        String component = null;

        try {
            String[] params = script.split(COMMA);
            for (int i = 0; i < params.length; i++) {
                String val = params[i];
                if (val == null || val.isEmpty()) {
                    continue;
                }

                switch (i) {
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
            LOG.error("Failed to process Transition configuration {}", script, e);
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
        return line.split(Consts.COMMA);
    }
}
