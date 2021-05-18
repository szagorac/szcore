package com.xenaksys.szcore.time;

import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.TimeEventListener;
import com.xenaksys.szcore.model.WaitStrategy;
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

public class TestTimer {
    static final Logger LOG = LoggerFactory.getLogger(TestTimer.class);

    com.xenaksys.szcore.model.Timer timer;
    long interval;
    MutableClock clock;
    AtomicInteger counter = new AtomicInteger();
    TLongArrayList times = new TLongArrayList();
    Map<Integer, Integer> timeDiffs = new HashMap<>();

    @Before
    public void init(){
        interval = 1l;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
//        WaitStrategy waitStrategy = new NanoSpinWaitStrategy(interval, timeUnit);
        WaitStrategy waitStrategy = new BlockingWaitStrategy(interval, timeUnit);

        clock = new MutableNanoClock();

        timer = new BasicTimer(waitStrategy, clock);
        TimerListener listener = new TimerListener();
        timer.registerListener(listener);
    }

    @Test
    public void testSchedule(){
        times.clear();
        long startTime = System.nanoTime();
        timer.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.stop();
        long endTime = System.nanoTime();
        long runTimeMillis = (endTime - startTime)/1000000;
        long expectedCalls = runTimeMillis/interval;
        int received = counter.get();

        long diff = Math.abs(expectedCalls - received);
        LOG.info("received: " + received + " expected: " + expectedCalls + " diff:" + diff);

        Assert.assertTrue(diff > 0);

        long max = 0;

        for(int i= 1; i < times.size(); i++){
            long value = times.get(i);
            long prev = times.get(i - 1);
            int diffInt = (int)(value - prev);
            Integer count = timeDiffs.get(diffInt);
            if(count == null){
                count = 0;
            }
            count += 1;
            timeDiffs.put(diffInt, count);

            if(diffInt > max){
                max = diffInt;
            }
        }

        LOG.info("Max Interval: " + max);
        List<Integer> intervals = new ArrayList<>(timeDiffs.keySet());
        Collections.sort(intervals);
        for(int inerval : intervals){
            int count = timeDiffs.get(inerval);
            LOG.info("For interval: " + inerval + " had: " + count + " events");
        }
    }

    class TimerListener implements TimeEventListener {
        @Override
        public void onTimeEvent() {
//            LOG.info("time millis: " + clock.getElapsedTimeMillis());
            times.add(clock.getElapsedTimeMillis());
            counter.incrementAndGet();
        }

        @Override
        public void onStop() {
//            LOG.info("Received ON STOP");
        }

        @Override
        public void onStart() {
//            LOG.info("Received ON START");
        }
    }
}
