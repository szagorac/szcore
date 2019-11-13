package com.xenaksys.szcore.model;

public interface Scheduler {

    void init();

    void start();

    void addTransport(Transport transport);

    void onTransportStopped(Id transportId);

    void stop();

    boolean isActive();

    void setElapsedTimeMillis(long elapsedTimeMillis);

    void setPrecountTimeMillis(long precountTimeMillis);

    void reset();

    void add(MusicTask task);

    void processQueue();

    void resetScheduledTasks();

    void setPublishSleep(long millis);
}
