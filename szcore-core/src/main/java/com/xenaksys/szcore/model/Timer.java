package com.xenaksys.szcore.model;

public interface Timer {
    void start();

    void stop();

    void reset();

    boolean isRunning();

    boolean isActive();

    void setElapsedTimeMillis(long elapsedTimeMillis);

    void setPrecountTimeMillis(long precountTimeMillis);

    void registerListener(TimeEventListener listener);

    void init();
}
