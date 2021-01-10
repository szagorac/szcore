package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WebScoreLoader {
    static final Logger LOG = LoggerFactory.getLogger(WebScoreLoader.class);

    static final String ITEM_DELIMITER = "`";

    public static volatile String workingDir;

    private static String[] expextedHeaders = {
            "script",            //0
            "unitBeatNo",       //1
    };

    public static WebScore load(String path) throws Exception {
        if (path != null) {
            File file = FileUtil.getFileFromClassPath(path);
            return load(file);
        }
        return null;
    }

    public static WebScore load(File file) throws Exception {
        if (file == null) {
            return null;
        }

        workingDir = file.getParent();

        List<String> lines = FileUtil.loadFile(file);
        return loadLines(lines);
    }

    public static WebScore loadLines(List<String> lines) throws Exception {
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
            processScoreElement(scoreElement, null);
        }

        return null;
    }

    private static void processScoreElement(WebScoreElement scoreElement, BasicScore score) throws Exception {

        Id scoreId = score.getId();
        if (scoreId == null) {
            LOG.error("Invalid NULL Score ID");
            return;
        }

        String elementScoreName = score.getName();
        processFileScoreElement(scoreElement, score, null, scoreId);
    }


    private static void processFileScoreElement(WebScoreElement scoreElement, BasicScore score, String fileName, Id scoreId) throws Exception {

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
