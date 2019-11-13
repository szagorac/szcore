package com.xenaksys.szcore.model;


public interface Tempo {

    int getBpm();

    int getScoreBpm();

    NoteDuration getBeatDuration();

    TempoModifier getTempoModifier();

}
