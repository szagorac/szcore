package com.xenaksys.szcore.time;

import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.TimeEventListener;
import com.xenaksys.szcore.model.Timer;
import com.xenaksys.szcore.model.WaitStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class BasicTimer implements Timer {
    static final Logger LOG = LoggerFactory.getLogger(BasicTimer.class);

    static final int WAIT_TIME_MILLIS = 5;
    static final int MAX_WAIT_MILLIS = 1000;

    private volatile boolean isRunning = false;
    private volatile boolean isActive = false;
    private volatile long elapsedTimeMillis = 0l;
    private volatile long precountTimeMillis = 0l;
    private volatile long precountTimeLeft = 0l;
    private AtomicLong startNano = new AtomicLong(0l);
    private AtomicLong precountStartNano = new AtomicLong(0l);

    private final WaitStrategy waitStrategy;
    private final MutableClock clock;
    private List<TimeEventListener> listeners = new CopyOnWriteArrayList<>();

    public BasicTimer(WaitStrategy waitStrategy, MutableClock clock) {
        this.waitStrategy = waitStrategy;
        this.clock = clock;
    }

    @Override
    public void start() {
        if (isActive) {
            LOG.info("Attempt to start Active Timer, ignoring.");
            return;
        }

        this.isActive = true;
        Thread timer = new Thread(new TimerThread(), "Szcore-Timer-Thread");
        timer.start();
    }

    @Override
    public void stop() {
        LOG.info("Stopping timer");
        this.isActive = false;
        long startTime = clock.getSystemTimeMillis();
        while (isRunning) {
            try {
                Thread.sleep(WAIT_TIME_MILLIS);
            } catch (InterruptedException e) {
                LOG.error("Wait Interrupted error: ", e);
            }
            long currentTime = clock.getSystemTimeMillis();
            long diff = currentTime - startTime;
            if (diff > MAX_WAIT_MILLIS) {
                LOG.error("Timer still running after wait millis: " + MAX_WAIT_MILLIS);
                break;
            }
        }
        LOG.info("Timer sent stop");
    }

    @Override
    public void reset() {
        stop();
        this.isRunning = false;
        this.isActive = false;
        this.elapsedTimeMillis = 0l;
        this.precountTimeMillis = 0l;
        this.precountTimeLeft = 0l;
        this.startNano = new AtomicLong(0l);
        this.precountStartNano = new AtomicLong(0l);
        clock.setElapsedTimeMillis(0l);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        if (isActive()) {
            LOG.error("Timer is active, can not change elapsed time");
            return;
        }
        this.elapsedTimeMillis = elapsedTimeMillis;
//LOG.info("Setting elapsedTimeMillis: " + elapsedTimeMillis);
        this.clock.setElapsedTimeMillis(elapsedTimeMillis);
    }

    @Override
    public void setPrecountTimeMillis(long precountTimeMillis) {
        this.precountTimeMillis = precountTimeMillis;
        clock.setElapsedTimeMillis(-precountTimeMillis);
    }

    @Override
    public void registerListener(TimeEventListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners() {
        for (TimeEventListener listener : listeners) {
            listener.onTimeEvent();
        }
    }

    public void notifyListenersOnStop() {
        for (TimeEventListener listener : listeners) {
            listener.onStop();
        }
    }

    public void notifyListenersOnStart() {
        for (TimeEventListener listener : listeners) {
            listener.onStart();
        }
    }

    @Override
    public void init() {
        notifyListeners();
    }

    private void processTick() {
        long currentNano = System.nanoTime();
        if (precountTimeLeft > 0l) {
            processPrecount(currentNano);
            return;
        }
        long diff = currentNano - startNano.get();
        double diffMillisD = diff / 1000000;
        int diffMillis = (int) (diffMillisD + 0.5);
//        LOG.debug("Playtime tick diffMillis: " + diffMillis);
        clock.setElapsedTimeMillis(diffMillis);

        notifyListeners();
    }

    private void processPrecount(long currentNano) {
        long diff = currentNano - precountStartNano.get();
        double diffMillisD = diff / 1000000;
        int diffMillis = (int) (diffMillisD + 0.5);
        precountTimeLeft = precountTimeMillis - diffMillis;
//        LOG.debug("Precount time left: " + precountTimeLeft + "  diffMillis: " + diffMillis);
        clock.setElapsedTimeMillis(-1L * precountTimeLeft);

        notifyListeners();
    }

    private void setStartTimes() {
        long now = System.nanoTime();
        long startTimeNanos = now;

        if (precountTimeMillis > 0l) {
            long precountOffsetNanos = TimeUnit.NANOSECONDS.convert(precountTimeMillis, TimeUnit.MILLISECONDS);
            startTimeNanos += precountOffsetNanos;
        }

        if (elapsedTimeMillis != 0l) {
            long elapsedNanos = TimeUnit.NANOSECONDS.convert(elapsedTimeMillis, TimeUnit.MILLISECONDS);
            startTimeNanos = startTimeNanos - elapsedNanos;
        }
//LOG.info("Setting startTimeNanos: " + startTimeNanos + " now: " + now + " diff: " + (now - startTimeNanos));
//LOG.info("Setting precountTimeMillis: " + precountTimeMillis );
        long current;

        precountTimeLeft = precountTimeMillis;
        do {
            current = precountStartNano.get();
        }
        while (!this.precountStartNano.compareAndSet(current, now));

        do {
            current = startNano.get();
        }
        while (!this.startNano.compareAndSet(current, startTimeNanos));

//LOG.info("Setting startNano: " + startNano.get());

    }

    class TimerThread implements Runnable {

        @Override
        public void run() {
            notifyListenersOnStart();
            setStartTimes();
            while (isActive) {
                isRunning = true;
                processTick();
                waitStrategy.doWait();
            }

            isRunning = false;
            notifyListenersOnStop();
            LOG.info("Stopped timer");
        }
    }

}
