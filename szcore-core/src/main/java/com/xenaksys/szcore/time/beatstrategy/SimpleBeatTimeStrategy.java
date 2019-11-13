package com.xenaksys.szcore.time.beatstrategy;

import com.xenaksys.szcore.model.BeatTimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleBeatTimeStrategy implements BeatTimeStrategy {
    static final Logger LOG = LoggerFactory.getLogger(SimpleBeatTimeStrategy.class);

    @Override
    public long calculateNextBeatTime(long lastPublishedTime, long previousPublishedTime, long idealIntervalMillis, boolean isTempoChange) {
        long nextTickTime = 0;

        long currentInterval = lastPublishedTime - previousPublishedTime;
        long diff = currentInterval - idealIntervalMillis;

        if (isTempoChange) {
            return lastPublishedTime + idealIntervalMillis;
        }

        if (diff <= -1 * idealIntervalMillis / 2) {
            //first tick
            nextTickTime = lastPublishedTime + idealIntervalMillis;
        } else if (diff > idealIntervalMillis) {
            LOG.warn("Tick critically late, reset from now. Latency millis: " + diff);
            nextTickTime = lastPublishedTime + idealIntervalMillis;
        } else {
            nextTickTime = previousPublishedTime + 2 * idealIntervalMillis + diff / 2;
        }

        if (diff > 5) {
            LOG.warn("Current diff: " + diff + " previousPublishedTime: " + previousPublishedTime + " lastPublishedTime: "
                    + lastPublishedTime + " idealIntervalMillis: " + idealIntervalMillis);
        }

        return nextTickTime;
    }
}
