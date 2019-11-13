package com.xenaksys.szcore.model;


public interface BeatTimeStrategy {

    long calculateNextBeatTime(long lastPublishedTime, long previousPublishedTime, long idealIntervalMillis, boolean isTempoChange);
}
