package com.xenaksys.szcore.model.id;

import com.xenaksys.szcore.model.Id;

public class StaveId implements Id {

    private final Id instrumentId;
    private final int staveNo;

    public StaveId(Id instrumentId, int staveNo) {
        this.instrumentId = instrumentId;
        this.staveNo = staveNo;
    }

    public Id getInstrumentId() {
        return instrumentId;
    }

    public int getStaveNo() {
        return staveNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StaveId)) return false;

        StaveId staveId = (StaveId) o;

        if (staveNo != staveId.staveNo) return false;
        return instrumentId.equals(staveId.instrumentId);

    }

    @Override
    public int hashCode() {
        int result = instrumentId.hashCode();
        result = 31 * result + staveNo;
        return result;
    }

    @Override
    public String toString() {
        return "StaveId{" +
                "instrumentId=" + instrumentId +
                ", staveNo=" + staveNo +
                '}';
    }
}
