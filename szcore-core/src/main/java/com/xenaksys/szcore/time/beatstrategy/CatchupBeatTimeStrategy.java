package com.xenaksys.szcore.time.beatstrategy;

import com.xenaksys.szcore.model.BeatTimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CatchupBeatTimeStrategy implements BeatTimeStrategy {
    static final Logger LOG = LoggerFactory.getLogger(CatchupBeatTimeStrategy.class);

    @Override
    public long calculateNextBeatTime(long lastPublishedTime, long previousPublishedTime, long idealIntervalMillis, boolean isTempoChange) {
        long nextTickTime = 0;

        long currentInterval = lastPublishedTime - previousPublishedTime;
        long diff = currentInterval - idealIntervalMillis;
        if (diff <= -1 * idealIntervalMillis / 2) {
            //first tick
            nextTickTime = lastPublishedTime + idealIntervalMillis;
        } else if (diff < 2l) {
            nextTickTime = previousPublishedTime + 2 * idealIntervalMillis;
        } else if (diff < idealIntervalMillis) {
//            LOG.warn("Tick very late, will try to catch up. Latency millis: " + diff);
            nextTickTime = previousPublishedTime + 2 * idealIntervalMillis + diff / 2;
        } else {
            LOG.error("Tick critically late, reset from now. Latency millis: " + diff);
            nextTickTime = lastPublishedTime + idealIntervalMillis;
        }

        return nextTickTime;
    }
}
