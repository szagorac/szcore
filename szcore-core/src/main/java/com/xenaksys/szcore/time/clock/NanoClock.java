package com.xenaksys.szcore.time.clock;

import com.xenaksys.szcore.model.Clock;

import java.util.concurrent.atomic.AtomicLong;

public class NanoClock implements Clock {

    protected AtomicLong elapsedTimeMillis = new AtomicLong();

    @Override
    public long getSystemTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long getElapsedTimeMillis() {
        return elapsedTimeMillis.get();
    }

}
