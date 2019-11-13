package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class MutableBeatId extends BeatId {
    public MutableBeatId(int beatNo, Id instrumentId, Id pageId, Id scoreId, Id barId, int baseBeatNo) {
        super(beatNo, instrumentId, pageId, scoreId, barId, baseBeatNo);
    }

    public void setBeatNo(int beatNo) {
        this.beatNo = beatNo;
    }

    public void setInstrumentId(Id instrumentId) {
        this.instrumentId = instrumentId;
    }

    public void setPageId(Id pageId) {
        this.pageId = pageId;
    }

    public void setScoreId(Id scoreId) {
        this.scoreId = scoreId;
    }

    public void setBarId(Id barId) {
        this.barId = barId;
    }

    public void setBaseBeatNo(int baseBeatNo) {
        this.baseBeatNo = baseBeatNo;
    }

    public void reset() {
        this.beatNo = 0;
        this.instrumentId = null;
        this.pageId = null;
        this.scoreId = null;
        this.barId = null;
        this.baseBeatNo = 0;
    }

}
