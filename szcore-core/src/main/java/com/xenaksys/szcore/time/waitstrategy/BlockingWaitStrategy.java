package com.xenaksys.szcore.time.waitstrategy;

import com.xenaksys.szcore.model.WaitStrategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class BlockingWaitStrategy implements WaitStrategy {

    private long interval;

    public BlockingWaitStrategy(long interval, TimeUnit timeUnit) {
        long nanos = TimeUnit.NANOSECONDS.convert(interval, timeUnit);
        this.interval = nanos;

    }

    public void doWait() {
        LockSupport.parkNanos(interval);
    }
}
