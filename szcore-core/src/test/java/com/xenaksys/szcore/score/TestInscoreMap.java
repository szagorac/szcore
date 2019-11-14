package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestInscoreMap {
    static final Logger LOG = LoggerFactory.getLogger(ScoreLoader.class);

    @Test
    public void testParseMapElement(){
        String mapElement = "( [30, 68[ [100, 100[ ) ( [257/8, 259/8[ )";
        InscoreMapElement element = InscoreMapElement.parseLine(mapElement);
        String out = element.toInscoreString();
        Assert.assertEquals(mapElement, out);
    }

    @Test
    public void testParseMapFile(){
        try {
            String path = "testScore_Instrument1_page9_InScoreMap.txt";
            File file = FileUtil.getFileFromClassPath(path);
            String originalFile = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            ScoreLoader.workingDir = file.getParent();

            StrId instrumentId = new StrId("Instrument1");
            StrId scoreId = new StrId("testScore");
            PageId pageId = new PageId(9, instrumentId, scoreId);
            String fileName = "testScore_Instrument1_page9";

            InscorePageMap inscorePageMap = ScoreLoader.loadPageInscoreMap(pageId, fileName);

            String out = inscorePageMap.toInscoreString();
            Assert.assertEquals(originalFile, out);
        } catch (IOException e) {
            LOG.error("Failed to test map file", e);
        }
    }
}
