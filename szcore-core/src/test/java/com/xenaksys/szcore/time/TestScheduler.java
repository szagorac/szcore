package com.xenaksys.szcore.time;

import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.MusicTask;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.task.LogMusicTask;
import com.xenaksys.szcore.time.clock.MutableNanoClock;
import com.xenaksys.szcore.time.waitstrategy.BlockingWaitStrategy;
import gnu.trove.list.array.TLongArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestScheduler{
    static final Logger LOG = LoggerFactory.getLogger(TestTransport.class);

    MutableClock clock;
    Scheduler scheduler;
    AtomicInteger playedCounter = new AtomicInteger();
    AtomicInteger expectedCounter = new AtomicInteger();
    TLongArrayList playedTimes = new TLongArrayList();
    TLongArrayList expectedTimes = new TLongArrayList();
    Map<Long, Long> taskIdPlayTime = new HashMap<>();
    Map<Integer, Integer> timeDiffs = new HashMap<>();

    @Before
    public void init(){
        long interval = 1l;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        WaitStrategy waitStrategy = new BlockingWaitStrategy(interval, timeUnit);
//        WaitStrategy waitStrategy = new NanoSpinWaitStrategy(interval, timeUnit);

        clock = new MutableNanoClock();
        com.xenaksys.szcore.model.Timer timer = new BasicTimer(waitStrategy, clock);

        scheduler = new BasicScheduler(clock, timer);
    }

    @Test
    public void testPreSchedule(){
        resetCounters();

        int duration = 1000;
        int freqMillis = 10;

        for(int i = freqMillis; i < duration; i +=freqMillis) {
            MusicTask task = new TestMusicTask(i, clock);
            expectedCounter.incrementAndGet();
            expectedTimes.add(i);
            scheduler.add(task);
        }

        LOG.info("Created events: " + expectedCounter.get());

        LOG.info("Start load ...");
        scheduler.start();

        try {
            Thread.sleep(duration + freqMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("Stop load");
        scheduler.stop();

        Assert.assertEquals(expectedTimes.size(), playedTimes.size());
        Assert.assertEquals(expectedCounter.get(), playedCounter.get());
        Assert.assertFalse(scheduler.isActive());

        LOG.info("Played events: " + playedCounter.get());

        for(int i = 0; i < expectedTimes.size(); i++){
            long expectedTime = expectedTimes.get(i);
            long playedTime = playedTimes.get(i);
            int diff = (int)(playedTime - expectedTime);
            Integer count = timeDiffs.get(diff);
            if(count == null){
                count = 0;
            }
            count += 1;
            timeDiffs.put(diff, count);
        }


        List<Integer> latencies = new ArrayList<>(timeDiffs.keySet());
        Collections.sort(latencies);
        for(int latency : latencies){
            int count = timeDiffs.get(latency);
            LOG.info("For latency: " + latency + " had: " + count + " events");
        }

    }


    @Test
    public void testDynamicSchedule() {
        scheduler.reset();
        resetCounters();

        int duration = 1000;
        int freqMillis = 10;

        for (int i = freqMillis; i < duration; i += freqMillis) {
            MusicTask task = new TestMusicTask(i, clock);
            expectedCounter.incrementAndGet();
            expectedTimes.add(i);
            scheduler.add(task);
        }

        LOG.info("Created events: " + expectedCounter.get());

        LOG.info("Start load ...");
        scheduler.start();

        int sleep = 500;
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long[] insertTimes = {625, 699, 735, 845, 853};


        for(long insertTime : insertTimes){
            long playTime = insertTime;
            MusicTask task = new TestMusicTask(playTime, clock);
            int count = expectedCounter.incrementAndGet();
            expectedTimes.add(playTime);
            scheduler.add(task);
            LOG.info("Added dynamic event: " + count + " at load time: " + playTime);
        }

        try {
            Thread.sleep(sleep + freqMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("Stop load");
        scheduler.stop();


        Assert.assertEquals(expectedTimes.size(), playedTimes.size());
        Assert.assertEquals(expectedCounter.get(), playedCounter.get());

        LOG.info("Played events: " + playedCounter.get());

        for(long taskId : insertTimes){
            Long playTime = taskIdPlayTime.get(taskId);
            Assert.assertNotNull(playTime);
            long diff = playTime - taskId;
            LOG.info("Played event id: " + taskId + " at: " + playTime + " diff: " + diff);
        }

    }

    @Test
    public void testElapsedTimeChange() {
        scheduler.reset();
        resetCounters();

        int duration = 1000;
        int elapsedTime = 500;
        int freqMillis = 100;

        for (int i = freqMillis; i <= duration; i += freqMillis) {
            MusicTask task = new TestMusicTask(i, clock);

            if(i >= elapsedTime) {
                expectedCounter.incrementAndGet();
                expectedTimes.add(i);
            }

            scheduler.add(task);
        }

        LOG.info("Created events: " + expectedCounter.get());
        scheduler.setElapsedTimeMillis(elapsedTime);

        LOG.info("Start load ...");
        scheduler.start();

        try {
            Thread.sleep(duration - elapsedTime + 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("Stop load");
        scheduler.stop();

        Assert.assertEquals(expectedTimes.size(), playedTimes.size());
        Assert.assertEquals(expectedCounter.get(), playedCounter.get());
        Assert.assertFalse(scheduler.isActive());

        LOG.info("Played events: " + playedCounter.get());

    }

    @Test
    public void testElapsedTimeChangeWhileRunning() {
        scheduler.reset();
        resetCounters();

        int duration = 1000;
        int elapsedTime = 300;
        int freqMillis = 100;

        for (int i = freqMillis; i <= duration; i += freqMillis) {
            MusicTask task = new TestMusicTask(i, clock);

            if(i >= elapsedTime) {
                expectedCounter.incrementAndGet();
                expectedTimes.add(i);
            }

            scheduler.add(task);
        }


        LOG.info("Start load ...");
        scheduler.start();
        try {
            Thread.sleep(duration - elapsedTime + 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        resetCounters();
        scheduler.setElapsedTimeMillis(elapsedTime);

        try {
            Thread.sleep(duration - elapsedTime + 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduler.stop();


        Assert.assertEquals(expectedTimes.size(), playedTimes.size());
        Assert.assertEquals(expectedCounter.get(), playedCounter.get());
        Assert.assertFalse(scheduler.isActive());

        LOG.info("Played events: " + playedCounter.get());

    }

    public void resetCounters(){
        playedCounter.set(0);
        playedTimes.clear();
        taskIdPlayTime.clear();
    }

    class TestMusicTask extends LogMusicTask{
        private final long id;

        public TestMusicTask(long playTime, Clock clock) {
            super(playTime, clock);
            id = playTime;
        }


        public void play() {
            playedCounter.incrementAndGet();
            long playTime = clock.getElapsedTimeMillis();
            playedTimes.add(playTime);
            taskIdPlayTime.put(id, playTime);
        }

        public long getId(){
            return id;
        }

        @Override
        public String toString() {
            return "TestMusicTask playTime: " + getPlayTime();
        }
    }

}