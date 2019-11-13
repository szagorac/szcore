package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Score;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class TestScoreBuilder {

    ScoreLoader builder;

    @Before
    public void init(){
        builder = new ScoreLoader();
    }

    @Test
    public void testLoadScore(){

        String filePath = "testScore.csv";
        Score score = null;
        try {
            score = builder.load(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(score);

        Collection<Page> pages = score.getPages();
        Assert.assertEquals(1, pages.size());

        Collection<Instrument> instruments = score.getInstruments();
        Assert.assertEquals(1, instruments.size());

        Collection<Bar> bars = score.getBars();
        Assert.assertEquals(5, bars.size());

        Collection<Beat> beats = score.getBeats();
        Assert.assertEquals(21, beats.size());

    }

    @Test
    public void testMultiLoadScore(){

        String filePath = "testScoreMulti.csv";
        Score score = null;
        try {
            score = builder.load(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(score);


        Collection<Instrument> instruments = score.getInstruments();
        Assert.assertEquals(4, instruments.size());

        Collection<Page> pages = score.getPages();
        Assert.assertEquals(16, pages.size());

        Collection<Bar> bars = score.getBars();
        Assert.assertEquals(84, bars.size());

        Collection<Beat> beats = score.getBeats();
        Assert.assertEquals(304, beats.size());



    }
}
