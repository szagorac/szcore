package com.xenaksys.szcore.time.clock;

import com.xenaksys.szcore.model.Clock;

public class SimpleClock implements Clock {

    @Override
    public long getSystemTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long getElapsedTimeMillis() {
        return 0;
    }

}
