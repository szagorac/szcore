package com.xenaksys.szcore.time.waitstrategy;

import com.xenaksys.szcore.model.WaitStrategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class BockingWaitStrategy implements WaitStrategy {

    private long interval;

    public BockingWaitStrategy(long interval, TimeUnit timeUnit) {
        long nanos = TimeUnit.NANOSECONDS.convert(interval, timeUnit);
        this.interval = nanos;

    }

    public void doWait() {
        LockSupport.parkNanos(interval);
    }
}
