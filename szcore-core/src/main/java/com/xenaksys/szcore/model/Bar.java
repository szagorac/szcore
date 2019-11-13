package com.xenaksys.szcore.model;


import java.util.Collection;

public interface Bar extends Identifiable, Comparable<Bar> {

    int getBarNo();

    String getBarName();

    Id getPageId();

    Id getInstrumentId();

    Id getScoreId();

    Tempo getTempo();

    TimeSignature getTimeSignature();

    Collection<Beat> getBeats();

    boolean isUpbeatBar();

}
