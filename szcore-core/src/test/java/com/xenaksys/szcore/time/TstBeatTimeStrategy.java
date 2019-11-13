package com.xenaksys.szcore.time;

import com.xenaksys.szcore.model.BeatTimeStrategy;


public class TstBeatTimeStrategy implements BeatTimeStrategy {

    @Override
    public long calculateNextBeatTime(long lastPublishedTime, long previousPublishedTime, long idealIntervalMillis, boolean isTempoChange) {
        return 0;
    }
}
