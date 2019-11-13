package com.xenaksys.szcore.time;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MusicTask;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Transport;

public class TstScheduler implements Scheduler {

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void addTransport(Transport transport) {

    }

    @Override
    public void onTransportStopped(Id transportId) {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setElapsedTimeMillis(long elapsedTimeMillis) {

    }

    @Override
    public void setPrecountTimeMillis(long precountTimeMillis) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void add(MusicTask task) {

    }

    @Override
    public void processQueue() {

    }

    @Override
    public void resetScheduledTasks() {

    }

    @Override
    public void setPublishSleep(long millis) {

    }
}
