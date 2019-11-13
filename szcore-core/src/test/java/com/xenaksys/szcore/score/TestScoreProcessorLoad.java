package com.xenaksys.szcore.score;


import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.publish.OscPublishProcessor;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.time.TstBeatTimeStrategy;
import com.xenaksys.szcore.time.TstClock;
import com.xenaksys.szcore.time.TstScheduler;
import gnu.trove.map.TIntObjectMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TestScoreProcessorLoad {
    static final Logger LOG = LoggerFactory.getLogger(TestScoreProcessorLoad.class);

    ScoreProcessor scoreProcessor;
    TransportFactory transportFactory;

    @Before
    public void init(){

        MutableClock clock = new TstClock();
        OscPublisher oscPublisher = new OscPublishProcessor();
        Scheduler scheduler = new TstScheduler();
        BeatTimeStrategy beatTimeStrategy = new TstBeatTimeStrategy();
        transportFactory = new TransportFactory(clock, scheduler, beatTimeStrategy);

        EventFactory eventFactory = new EventFactory();
        TaskFactory taskFactory = new TaskFactory();

        scoreProcessor = new ScoreProcessorImpl(transportFactory, clock, oscPublisher, scheduler, eventFactory, taskFactory);
    }

    @Ignore
    @Test
    public void testLoadScore(){
        String filePath = "testScoreSingleInstrument.csv";

        try {
            scoreProcessor.loadAndPrepare(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Score score = scoreProcessor.getScore();
        Assert.assertNotNull(score);

        Transport defaultTransport = transportFactory.getTransport(Consts.DEFAULT_TRANSPORT_NAME);

        Collection<Instrument> instruments = score.getInstruments();
        Assert.assertEquals(1, instruments.size());
        Instrument instrument = instruments.iterator().next();

        List<Stave> staves = score.getInstrumentStaves(instrument.getId());
        Assert.assertEquals(2, staves.size());

        List<Stave> instrumentStaves = score.getInstrumentStaves(instrument.getId());
        Assert.assertEquals(2, instrumentStaves.size());

        Collection<Page> pages = score.getPages();
        Assert.assertEquals(3, pages.size());

        Collection<Bar> bars = score.getBars();
        Assert.assertEquals(16, bars.size());

        Collection<Beat> beats = score.getBeats();
        Assert.assertEquals(59, beats.size());

        Id transportId = defaultTransport.getId();

//        List<SzcoreEvent> initEvents = score.getInitEvents();
//        Assert.assertNotNull(initEvents);
//        Assert.assertEquals(7, initEvents.size());
//        for(SzcoreEvent event : initEvents){
//            LOG.info("Found init event: " + event);
//        }

        List<SzcoreEvent> clockClockTickEvents = score.getClockTickEvents(transportId);
        Assert.assertNotNull(clockClockTickEvents);
        Assert.assertEquals(2, clockClockTickEvents.size());
        for(SzcoreEvent event : clockClockTickEvents){
            LOG.info("Found clockClockTickEvents event: " + event);
        }

        List<SzcoreEvent> clockBaseBeatEvents = score.getClockBaseBeatEvents(transportId);
        Assert.assertNotNull(clockBaseBeatEvents);
        Assert.assertEquals(2, clockBaseBeatEvents.size());
        for(SzcoreEvent event : clockBaseBeatEvents){
            LOG.info("Found clockBaseBeatEvents event: " + event);
        }

        TIntObjectMap<List<SzcoreEvent>> scoreBaseBeatEvents = score.getScoreBaseBeatEvents(transportId);
        Assert.assertNotNull(scoreBaseBeatEvents);
        Assert.assertEquals(10, scoreBaseBeatEvents.size());

        int[] keys = scoreBaseBeatEvents.keys();
        Arrays.sort(keys);
        for(int key : keys){
            List<SzcoreEvent> events = scoreBaseBeatEvents.get(key);
            if(events != null){
                for(SzcoreEvent event : events){
                    LOG.info("Found event for base beat: " + key + " event: " + event);
                }
            }
        }


    }

    @Ignore
    @Test
    public void testLoadMultiInstrumentScore(){
        String filePath = "testScoreMultiInstrument.csv";

        try {
            scoreProcessor.loadAndPrepare(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Score score = scoreProcessor.getScore();
        Assert.assertNotNull(score);

        Transport defaultTransport = transportFactory.getTransport(Consts.DEFAULT_TRANSPORT_NAME);

        Collection<Instrument> instruments = score.getInstruments();
        Assert.assertEquals(2, instruments.size());

        for(Instrument instrument : instruments) {
            List<Stave> staves = score.getInstrumentStaves(instrument.getId());
            Assert.assertEquals(2, staves.size());

            List<Stave> instrumentStaves = score.getInstrumentStaves(instrument.getId());
            Assert.assertEquals(2, instrumentStaves.size());
        }

        Collection<Page> pages = score.getPages();
        Assert.assertEquals(6, pages.size());

        Collection<Bar> bars = score.getBars();
        Assert.assertEquals(32, bars.size());

        Collection<Beat> beats = score.getBeats();
        Assert.assertEquals(118, beats.size());

        Id transportId = defaultTransport.getId();

//        List<SzcoreEvent> initEvents = score.getInitEvents();
//        Assert.assertNotNull(initEvents);
//        Assert.assertEquals(12, initEvents.size());
//        for(SzcoreEvent event : initEvents){
//            LOG.info("Found init event: " + event);
//        }

        List<SzcoreEvent> clockClockTickEvents = score.getClockTickEvents(transportId);
        Assert.assertNotNull(clockClockTickEvents);
        Assert.assertEquals(12, clockClockTickEvents.size());
        for(SzcoreEvent event : clockClockTickEvents){
            LOG.info("Found clockClockTickEvents event: " + event);
        }

        List<SzcoreEvent> clockBaseBeatEvents = score.getClockBaseBeatEvents(transportId);
        Assert.assertNotNull(clockBaseBeatEvents);
        Assert.assertEquals(8, clockBaseBeatEvents.size());
        for(SzcoreEvent event : clockBaseBeatEvents){
            LOG.info("Found clockBaseBeatEvents event: " + event);
        }

        TIntObjectMap<List<SzcoreEvent>> scoreBaseBeatEvents = score.getScoreBaseBeatEvents(transportId);
        Assert.assertNotNull(scoreBaseBeatEvents);
        Assert.assertEquals(12, scoreBaseBeatEvents.size());

        int[] keys = scoreBaseBeatEvents.keys();
        Arrays.sort(keys);
        for(int key : keys){
            List<SzcoreEvent> events = scoreBaseBeatEvents.get(key);
            if(events != null){
                for(SzcoreEvent event : events){
                    LOG.info("Found event for base beat: " + key + " event: " + event);
                }
            }
        }


    }
}
