package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class BeatId implements Id, Comparable<BeatId> {

    protected int beatNo;
    protected Id instrumentId;
    protected Id pageId;
    protected Id scoreId;
    protected Id barId;
    protected int baseBeatNo;

    public BeatId(int beatNo, Id instrumentId, Id pageId, Id scoreId, Id barId, int baseBeatNo) {
        this.beatNo = beatNo;
        this.instrumentId = instrumentId;
        this.pageId = pageId;
        this.scoreId = scoreId;
        this.barId = barId;
        this.baseBeatNo = baseBeatNo;
    }

    public int getBeatNo() {
        return beatNo;
    }

    public Id getInstrumentId() {
        return instrumentId;
    }

    public Id getPageId() {
        return pageId;
    }

    public Id getScoreId() {
        return scoreId;
    }

    public Id getBarId() {
        return barId;
    }

    public int getBaseBeatNo() {
        return baseBeatNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeatId)) return false;

        BeatId beatId = (BeatId) o;

        if (beatNo != beatId.beatNo) return false;
        if (!instrumentId.equals(beatId.instrumentId)) return false;
        if (!pageId.equals(beatId.pageId)) return false;
        if (!scoreId.equals(beatId.scoreId)) return false;
        return barId.equals(beatId.barId);

    }

    @Override
    public int hashCode() {
        int result = beatNo;
        result = 31 * result + instrumentId.hashCode();
        result = 31 * result + pageId.hashCode();
        result = 31 * result + scoreId.hashCode();
        result = 31 * result + barId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BeatId{" +
                "beatNo=" + beatNo +
                ", instrumentId=" + instrumentId +
                ", pageId=" + pageId +
                ", scoreId=" + scoreId +
                ", barId=" + barId +
                ", baseBeatNo=" + baseBeatNo +
                '}';
    }

    @Override
    public int compareTo(BeatId o) {
        int result = this.getBeatNo() - o.getBeatNo();
        if (result == 0) {
            BarId barId = (BarId) this.getBarId();
            BarId oBarId = (BarId) o.getBarId();
            if (barId != null && oBarId != null) {
                result = barId.getBarNo() - oBarId.getBarNo();
            }
        }
        if (result == 0) {
            PageId pageId = (PageId) this.getPageId();
            PageId oPageId = (PageId) o.getPageId();
            if (pageId != null && oPageId != null) {
                result = pageId.getPageNo() - oPageId.getPageNo();
            }
        }
        return result;
    }
}
