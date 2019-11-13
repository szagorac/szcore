package com.xenaksys.szcore.time;


import com.xenaksys.szcore.model.MutableClock;

public class TstClock implements MutableClock {

    long elapsedTime = 0l;
    long systemTime = 0l;

    @Override
    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        this.elapsedTime = elapsedTimeMillis;
    }

    @Override
    public long getSystemTimeMillis() {
        return systemTime;
    }

    @Override
    public long getElapsedTimeMillis() {
        return elapsedTime;
    }

    public void setSystemTime(long systemTime){
        this.systemTime = systemTime;
    }
}
