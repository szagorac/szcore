package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class MutablePageId extends PageId {

    public MutablePageId(int pageNo, Id instrumentId, Id scoreId) {
        super(pageNo, instrumentId, scoreId);
    }


    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setInstrumentId(Id instrumentId) {
        this.instrumentId = instrumentId;
    }

    public void setScoreId(Id scoreId) {
        this.scoreId = scoreId;
    }

    public void reset() {
        this.instrumentId = null;
        this.scoreId = null;
        this.pageNo = 0;
    }

}
