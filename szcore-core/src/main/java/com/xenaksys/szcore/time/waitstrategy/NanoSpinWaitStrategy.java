package com.xenaksys.szcore.time.waitstrategy;

import com.xenaksys.szcore.model.WaitStrategy;

import java.util.concurrent.TimeUnit;

public class NanoSpinWaitStrategy implements WaitStrategy {

    private long interval;

    public NanoSpinWaitStrategy(long interval, TimeUnit timeUnit) {
        long nanos = TimeUnit.NANOSECONDS.convert(interval, timeUnit);
        this.interval = nanos;
    }

    public void doWait() {
        long now = System.nanoTime();
        long end = now + interval;

        while (now <= end) {
            Thread.yield();
            now = System.nanoTime();
        }

    }
}
