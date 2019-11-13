package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.id.BarId;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;

public class BasicBeat implements Beat {

    private final BeatId id;
    private final long startTimeMillis;
    private final long durationMillis;
    private final long endTimeMillis;
    private final int baseBeatUnitsNoAtStart;
    private final int baseBeatUnitsDuration;
    private final int baseBeatUnitsNoOnEnd;
    private final int positionXStartPxl;
    private final int positionXEndPxl;
    private final int positionYStartPxl;
    private final int positionYEndPxl;
    private final boolean isUpbeat;


    public BasicBeat(BeatId id, long startTimeMillis, long durationMillis, long endTimeMillis, int baseBeatUnitsNoAtStart,
                     int baseBeatUnitsDuration, int baseBeatUnitsNoOnEnd, int positionXStartPxl, int positionXEndPxl,
                     int positionYStartPxl, int positionYEndPxl, boolean isUpbeat) {
        this.id = id;
        this.startTimeMillis = startTimeMillis;
        this.durationMillis = durationMillis;
        this.endTimeMillis = endTimeMillis;
        this.baseBeatUnitsNoAtStart = baseBeatUnitsNoAtStart;
        this.baseBeatUnitsDuration = baseBeatUnitsDuration;
        this.baseBeatUnitsNoOnEnd = baseBeatUnitsNoOnEnd;
        this.positionXStartPxl = positionXStartPxl;
        this.positionXEndPxl = positionXEndPxl;
        this.positionYStartPxl = positionYStartPxl;
        this.positionYEndPxl = positionYEndPxl;
        this.isUpbeat = isUpbeat;
    }

    @Override
    public BeatId getBeatId() {
        return id;
    }

    @Override
    public int getBeatNo() {
        return id.getBeatNo();
    }

    @Override
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    @Override
    public long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    @Override
    public int getBaseBeatUnitsNoAtStart() {
        return baseBeatUnitsNoAtStart;
    }

    @Override
    public int getBaseBeatUnitsDuration() {
        return baseBeatUnitsDuration;
    }

    @Override
    public int getBaseBeatUnitsNoOnEnd() {
        return baseBeatUnitsNoOnEnd;
    }

    @Override
    public int getPositionXStartPxl() {
        return positionXStartPxl;
    }

    @Override
    public int getPositionXEndPxl() {
        return positionXEndPxl;
    }

    @Override
    public int getPositionYStartPxl() {
        return positionYStartPxl;
    }

    @Override
    public int getPositionYEndPxl() {
        return positionYEndPxl;
    }

    @Override
    public Id getBarId() {
        return id.getBarId();
    }

    @Override
    public Id getPageId() {
        return id.getPageId();
    }

    @Override
    public Id getInstrumentId() {
        return id.getInstrumentId();
    }

    @Override
    public Id getScoreId() {
        return id.getScoreId();
    }

    @Override
    public boolean isUpbeat() {
        return isUpbeat;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public int compareTo(Beat o) {
        int result = this.getBeatNo() - o.getBeatNo();
        if (result == 0) {
            BarId barId = (BarId) this.getBarId();
            BarId oBarId = (BarId) o.getBarId();
            result = barId.getBarNo() - oBarId.getBarNo();
        }
        if (result == 0) {
            PageId pageId = (PageId) this.getPageId();
            PageId oPageId = (PageId) o.getPageId();
            result = pageId.getPageNo() - oPageId.getPageNo();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicBeat)) return false;

        BasicBeat basicBeat = (BasicBeat) o;

        return id.equals(basicBeat.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BasicBeat{" +
                "id=" + id +
                ", startTimeMillis=" + startTimeMillis +
                ", durationMillis=" + durationMillis +
                ", endTimeMillis=" + endTimeMillis +
                ", baseBeatUnitsNoAtStart=" + baseBeatUnitsNoAtStart +
                ", baseBeatUnitsDuration=" + baseBeatUnitsDuration +
                ", baseBeatUnitsNoOnEnd=" + baseBeatUnitsNoOnEnd +
                ", positionXStartPxl=" + positionXStartPxl +
                ", positionXEndPxl=" + positionXEndPxl +
                ", positionYStartPxl=" + positionYStartPxl +
                ", positionYEndPxl=" + positionYEndPxl +
                ", isUpbeat=" + isUpbeat +
                '}';
    }
}
