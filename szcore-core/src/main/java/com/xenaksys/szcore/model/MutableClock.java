package com.xenaksys.szcore.model;

public interface MutableClock extends Clock {
    void setElapsedTimeMillis(long elapsedTimeMillis);
}
