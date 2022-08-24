package com.xenaksys.szcore.time.waitstrategy;

import com.xenaksys.szcore.model.WaitStrategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class BlockingWaitStrategy implements WaitStrategy {

    private final long interval;

    public BlockingWaitStrategy(long interval, TimeUnit timeUnit) {
        this.interval = TimeUnit.NANOSECONDS.convert(interval, timeUnit);
    }

    public void doWait() {
        LockSupport.parkNanos(interval);
    }
}
