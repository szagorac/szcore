package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.publish.LoggingOscPublishProcessor;
import com.xenaksys.szcore.publish.WebPublisherProcessor;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.BasicScheduler;
import com.xenaksys.szcore.time.BasicTimer;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.time.beatstrategy.SimpleBeatTimeStrategy;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import com.xenaksys.szcore.time.waitstrategy.BlockingWaitStrategy;
import gnu.trove.map.TIntObjectMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestScoreProcessorRun {
    static final Logger LOG = LoggerFactory.getLogger(TestScoreProcessorRun.class);

    ScoreProcessor scoreProcessor;
    TransportFactory transportFactory;
    OscPublisher oscPublisher;

    boolean isSkip = true;

    @Before
    public void init() {

        WaitStrategy waitStrategy = new BlockingWaitStrategy(1, TimeUnit.MILLISECONDS);
        MutableClock clock = new MutableNanoClock();
        Timer timer = new BasicTimer(waitStrategy, clock);
        oscPublisher = new LoggingOscPublishProcessor();
        WebPublisher webPublisher = new WebPublisherProcessor();
        Scheduler scheduler = new BasicScheduler(clock, timer);
        BeatTimeStrategy beatTimeStrategy = new SimpleBeatTimeStrategy();
        transportFactory = new TransportFactory(clock, scheduler, beatTimeStrategy);

        EventFactory eventFactory = new EventFactory();
        TaskFactory taskFactory = new TaskFactory();

        scoreProcessor = new ScoreProcessorImpl(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory);
    }

    @Test
    public void testRunScoreSingleInstrument() {
        if (isSkip) {
            return;
        }
        String filePath = "testScoreSingleInstrument.csv";

        try {
            OSCPortOut port = new OSCPortOut();

            oscPublisher.addOscPort("Clarinet", port);

            scoreProcessor.loadAndPrepare(filePath);

            scoreProcessor.setPosition(0);

            Thread.sleep(1000);

            Transport defaultTransport = transportFactory.getTransport(Consts.DEFAULT_TRANSPORT_NAME);
            Id transportId = defaultTransport.getId();
            Score score = scoreProcessor.getScore();
            TIntObjectMap<List<SzcoreEvent>> scoreBaseBeatEvents = score.getScoreBaseBeatEvents(transportId);
            Assert.assertNotNull(scoreBaseBeatEvents);
            Assert.assertEquals(4, scoreBaseBeatEvents.size());
            int[] keys = scoreBaseBeatEvents.keys();
            Arrays.sort(keys);
            for (int key : keys) {
                List<SzcoreEvent> events = scoreBaseBeatEvents.get(key);
                if (events != null) {
                    for (SzcoreEvent event : events) {
                        LOG.info("Found event for base beat: " + key + " event: " + event);
                    }
                }
            }

            Thread.sleep(1000);

            scoreProcessor.play();

            Thread.sleep(1000000);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRunScoreMultiInstruments() {

        if (isSkip) {
            return;
        }
        String filePath = "testScoreMultiInstrument.csv";

        try {
            OSCPortOut port = new OSCPortOut();

            oscPublisher.addOscPort("Clarinet", port);
            oscPublisher.addOscPort("Cello", port);

            scoreProcessor.loadAndPrepare(filePath);

            scoreProcessor.setPosition(0);

            Thread.sleep(1000);

            Transport defaultTransport = transportFactory.getTransport(Consts.DEFAULT_TRANSPORT_NAME);
            Id transportId = defaultTransport.getId();
            Score score = scoreProcessor.getScore();
            TIntObjectMap<List<SzcoreEvent>> scoreBaseBeatEvents = score.getScoreBaseBeatEvents(transportId);
            Assert.assertNotNull(scoreBaseBeatEvents);
            Assert.assertEquals(4, scoreBaseBeatEvents.size());
            int[] keys = scoreBaseBeatEvents.keys();
            Arrays.sort(keys);
            for (int key : keys) {
                List<SzcoreEvent> events = scoreBaseBeatEvents.get(key);
                if (events != null) {
                    for (SzcoreEvent event : events) {
                        LOG.info("Found event for base beat: " + key + " event: " + event);
                    }
                }
            }

            Thread.sleep(1000);

            scoreProcessor.play();

            Thread.sleep(1000000);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
