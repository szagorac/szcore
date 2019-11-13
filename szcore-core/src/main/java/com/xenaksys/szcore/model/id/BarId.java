package com.xenaksys.szcore.model.id;

import com.xenaksys.szcore.model.Id;


public class BarId implements Id {

    private final int barNo;
    private final Id instrumentId;
    private final Id pageId;
    private final Id scoreId;

    public BarId(int barNo, Id instrumentId, Id pageId, Id scoreId) {
        this.barNo = barNo;
        this.instrumentId = instrumentId;
        this.pageId = pageId;
        this.scoreId = scoreId;
    }

    public int getBarNo() {
        return barNo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BarId)) return false;

        BarId barId = (BarId) o;

        if (barNo != barId.barNo) return false;
        if (!instrumentId.equals(barId.instrumentId)) return false;
        if (!pageId.equals(barId.pageId)) return false;
        return scoreId.equals(barId.scoreId);

    }

    @Override
    public int hashCode() {
        int result = barNo;
        result = 31 * result + instrumentId.hashCode();
        result = 31 * result + pageId.hashCode();
        result = 31 * result + scoreId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BarId{" +
                "barNo=" + barNo +
                ", instrumentId=" + instrumentId +
                ", pageId=" + pageId +
                ", scoreId=" + scoreId +
                '}';
    }
}
