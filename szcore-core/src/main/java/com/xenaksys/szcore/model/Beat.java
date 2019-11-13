package com.xenaksys.szcore.model;

import com.xenaksys.szcore.model.id.BeatId;

public interface Beat extends Identifiable, Comparable<Beat> {

    BeatId getBeatId();

    int getBeatNo();

    long getStartTimeMillis();

    long getDurationMillis();

    long getEndTimeMillis();

    int getBaseBeatUnitsNoAtStart();

    int getBaseBeatUnitsDuration();

    int getBaseBeatUnitsNoOnEnd();

    int getPositionXStartPxl();

    int getPositionXEndPxl();

    int getPositionYStartPxl();

    int getPositionYEndPxl();

    Id getBarId();

    Id getPageId();

    Id getInstrumentId();

    Id getScoreId();

    boolean isUpbeat();

}
