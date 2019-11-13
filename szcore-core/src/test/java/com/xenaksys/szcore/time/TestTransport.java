package com.xenaksys.szcore.time;

import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TransportListener;
import com.xenaksys.szcore.model.id.IntId;
import com.xenaksys.szcore.time.beatstrategy.CatchupBeatTimeStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TestTransport {

    static final Logger LOG = LoggerFactory.getLogger(TestScheduler.class);

    MutableClock clock;
    BasicTransport transport;
    boolean isTickPublished;
    boolean isBeatPublished;

    @Before
    public void init(){
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        clock = new TstClock();
        IntId id = new IntId(1);
        transport = new BasicTransport(id, clock, new TstScheduler(), new CatchupBeatTimeStrategy());
        TstTransportListener listener = new TstTransportListener();
        transport.addListener(listener);
    }

    @Test
    public void testIntervals(){


        long beatInterval = transport.getBaseBeatIntervalMillis();
        long expectedBeatInterval = Math.round((60000.0/120)/2);
        Assert.assertEquals(expectedBeatInterval, beatInterval);

        long tickInterval = transport.getTickIntervalMillis();
        long expectedTickInterval = Math.round((60000.0/120)/24);
        Assert.assertEquals(expectedTickInterval, tickInterval);

        clock.setElapsedTimeMillis(0);

        transport.start();
        transport.onSystemTick();
        Assert.assertTrue(isBeatPublished);
        Assert.assertTrue(isTickPublished);

        isBeatPublished = false;
        isTickPublished = false;

        clock.setElapsedTimeMillis(10);
        transport.onSystemTick();
        Assert.assertFalse(isBeatPublished);
        Assert.assertFalse(isTickPublished);

        clock.setElapsedTimeMillis(21);
        transport.onSystemTick();
        Assert.assertFalse(isBeatPublished);
        Assert.assertTrue(isTickPublished);

        isTickPublished = false;

        clock.setElapsedTimeMillis(250);
        transport.onSystemTick();
        Assert.assertTrue(isBeatPublished);
        Assert.assertTrue(isTickPublished);

        transport.stop();

    }

    class TstTransportListener implements TransportListener {

        @Override
        public void onClockTick(int beatNo, int tickNo) {
            isTickPublished = true;
        }

        @Override
        public void onBaseBeat(int beatNo) {
            isBeatPublished = true;
        }

        @Override
        public void onTempoChange(Tempo tempo) {

        }

        @Override
        public void onPositionChange(int beatNo) {

        }

    }

}

