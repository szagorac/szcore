package com.xenaksys.szcore.time;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.NoteDuration;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoImpl;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.TimeSignatureImpl;
import com.xenaksys.szcore.model.TransportListener;
import com.xenaksys.szcore.model.WaitStrategy;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.time.beatstrategy.SimpleBeatTimeStrategy;
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

public class TestMultiScheduledTransport {

    static final Logger LOG = LoggerFactory.getLogger(TestScheduler.class);

    MutableClock clock;
    BasicTransport transport;
    BasicTransport transport2;
    Scheduler scheduler;

    AtomicInteger playedCounter = new AtomicInteger();
    TLongArrayList playedTimes = new TLongArrayList();
    AtomicInteger bbPlayedCounter = new AtomicInteger();
    TLongArrayList bbPlayedTimes = new TLongArrayList();

    AtomicInteger playedCounter2 = new AtomicInteger();
    TLongArrayList playedTimes2 = new TLongArrayList();
    AtomicInteger bbPlayedCounter2 = new AtomicInteger();
    TLongArrayList bbPlayedTimes2 = new TLongArrayList();

    Map<Integer, Integer> timeDiffs = new HashMap<>();
    Map<Integer, Integer> bbTimeDiffs = new HashMap<>();

    int bpm1 = 180;
    int ticksPerBeatNo = 24;
    NoteDuration tempoBeat = NoteDuration.CROTCHET;
    Tempo tempo1 = new TempoImpl(bpm1, tempoBeat);
    NoteDuration baseBeatResolution = NoteDuration.EIGHTH;
    TimeSignature timeSignature = new TimeSignatureImpl(4, NoteDuration.CROTCHET);

    int bpm2 = 140;
    Tempo tempo2 = new TempoImpl(bpm2, tempoBeat);

    @Before
    public void init(){
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long interval = 1l;
        WaitStrategy waitStrategy = new BlockingWaitStrategy(interval, timeUnit);

        clock = new MutableNanoClock();
        com.xenaksys.szcore.model.Timer timer = new BasicTimer(waitStrategy, clock);
        scheduler = new BasicScheduler(clock, timer);

        BeatTimeStrategy beatTimeStrategy = new SimpleBeatTimeStrategy();

        IntId id = new IntId(1);
        transport = new BasicTransport(id, clock, scheduler, beatTimeStrategy);
        TstTransportListener listener = new TstTransportListener();
        transport.addListener(listener);

        transport.setTempo(tempo1);
        transport.setTimeSignature(timeSignature);
        transport.setBeatPublishResolution(baseBeatResolution);
        transport.setPublishClockTick(true);
        transport.setNumberOfTicksPerBeat(ticksPerBeatNo);

        scheduler.addTransport(transport);

        IntId id2 = new IntId(1);
        transport2 = new BasicTransport(id2, clock, scheduler, beatTimeStrategy);
        TstTransportListener2 listener2 = new TstTransportListener2();
        transport2.addListener(listener2);

        transport2.setTempo(tempo2);
        transport2.setTimeSignature(timeSignature);
        transport2.setBeatPublishResolution(baseBeatResolution);
        transport2.setPublishClockTick(true);
        transport2.setNumberOfTicksPerBeat(ticksPerBeatNo);

        scheduler.addTransport(transport2);
    }

    @Test
    public void testTicks(){
        resetCounters();

        int beatsNo = 4;
        int allowedLatency = 10;

        int beatDuration = Consts.MILLIS_IN_MINUTE/ bpm1;
        int duration = beatDuration * beatsNo;
        int tickDuration = (int)Math.round(1.0*beatDuration/ticksPerBeatNo);
        double multiplier = 1.0*tempoBeat.getNumberOfInWhole()/baseBeatResolution.getNumberOfInWhole();
        int baseBeatDuration = (int) Math.round(1.0*beatDuration * multiplier);

        int beatDuration2 = Consts.MILLIS_IN_MINUTE/ bpm2;
        int duration2 = beatDuration2 * beatsNo;
        int tickDuration2 = (int)Math.round(1.0*beatDuration2/ticksPerBeatNo);
        int baseBeatDuration2 = (int) Math.round(1.0*beatDuration2 * multiplier);

        int maxDuration = Math.max(duration, duration2);

        LOG.info("Start load ...");
        transport.start();
        transport2.start();

        try {
            Thread.sleep(maxDuration + 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info("Stop load");
        transport.stop();
        transport2.stop();
        scheduler.stop();

        Assert.assertFalse(scheduler.isActive());

        LOG.info("Played Tick events for Transport 1: " + playedCounter.get() + " tickDuration: " + tickDuration);

        populateTimeDiffs(playedTimes, tickDuration, timeDiffs);
        int maxTickDiff = getMaxLatency(timeDiffs);
        Assert.assertTrue(maxTickDiff < allowedLatency);

        LOG.info("Played Beat Times for Transport 1 baseBeatDuration:" + baseBeatDuration +  " times: " + bbPlayedTimes);
        populateTimeDiffs(bbPlayedTimes, baseBeatDuration, bbTimeDiffs);
        int maxBeatDiff = getMaxLatency(bbTimeDiffs);
        Assert.assertTrue(maxBeatDiff < allowedLatency);

        timeDiffs.clear();
        LOG.info("Played Tick events for Transport 2: " + playedCounter2.get() + " tickDuration: " + tickDuration2);
        populateTimeDiffs(playedTimes2, tickDuration2, timeDiffs);
        maxTickDiff = getMaxLatency(timeDiffs);
        Assert.assertTrue(maxTickDiff < allowedLatency);

        bbTimeDiffs.clear();
        LOG.info("Played Beat Times for Transport 2 baseBeatDuration: " + baseBeatDuration2  + " times: " + bbPlayedTimes2);
        populateTimeDiffs(bbPlayedTimes2, baseBeatDuration2, bbTimeDiffs);
        maxBeatDiff = getMaxLatency(bbTimeDiffs);
        Assert.assertTrue(maxBeatDiff < allowedLatency);
    }

    void populateTimeDiffs(TLongArrayList playedTimes, int idealDuration, Map<Integer, Integer> timeDiffs){
        for(int i = 1; i < playedTimes.size(); i++){
            long previousTime = playedTimes.get(i-1);
            long playedTime = playedTimes.get(i);
            int diff = (int)(playedTime - previousTime);
            int offsetToIdeal = diff - idealDuration;
            Integer count = timeDiffs.get(offsetToIdeal);
            if(count == null){
                count = 0;
            }
            count += 1;
            timeDiffs.put(offsetToIdeal, count);
        }
    }

    int getMaxLatency(Map<Integer, Integer> timeDiffs){
        List<Integer> latencies = new ArrayList<>(timeDiffs.keySet());
        Collections.sort(latencies);
        int maxDiff = 0;
        for(int latency : latencies){
            int count = timeDiffs.get(latency);
            int diff = Math.abs(latency);
            if(diff > maxDiff){
                maxDiff = diff;
            }
            LOG.info("For latency: " + latency + " had: " + count + " events");
        }
        return maxDiff;
    }

    public void resetCounters(){
        playedCounter.set(0);
        playedTimes.clear();
        bbPlayedCounter.set(0);
        bbPlayedTimes.clear();
    }

    class TstTransportListener implements TransportListener {

        @Override
        public void onClockTick(int beatNo, int tickNo) {
            long time = clock.getElapsedTimeMillis();
//            LOG.info("Got Tick time: " + time);
            playedCounter.incrementAndGet();
            playedTimes.add(time);
        }

        @Override
        public void onBaseBeat(int beatNo) {
            bbPlayedCounter.incrementAndGet();
            bbPlayedTimes.add(clock.getElapsedTimeMillis());
        }

        @Override
        public void onTempoChange(Tempo tempo) {

        }

        @Override
        public void onPositionChange(int beatNo) {

        }
    }

    class TstTransportListener2 implements TransportListener {

        @Override
        public void onClockTick(int beatNo, int tickNo) {
            long time = clock.getElapsedTimeMillis();
//            LOG.info("Got Tick time: " + time);
            playedCounter2.incrementAndGet();
            playedTimes2.add(time);
        }

        @Override
        public void onBaseBeat(int beatNo) {
            bbPlayedCounter2.incrementAndGet();
            bbPlayedTimes2.add(clock.getElapsedTimeMillis());
        }

        @Override
        public void onTempoChange(Tempo tempo) {

        }

        @Override
        public void onPositionChange(int beatNo) {

        }
    }

}

