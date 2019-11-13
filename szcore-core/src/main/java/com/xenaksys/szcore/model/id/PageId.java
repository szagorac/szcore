package com.xenaksys.szcore.model.id;


import com.xenaksys.szcore.model.Id;

public class PageId implements Id {

    protected int pageNo;
    protected Id instrumentId;
    protected Id scoreId;

    public PageId(int pageNo, Id instrumentId, Id scoreId) {
        this.pageNo = pageNo;
        this.instrumentId = instrumentId;
        this.scoreId = scoreId;
    }

    public int getPageNo() {
        return pageNo;
    }

    public Id getInstrumentId() {
        return instrumentId;
    }

    public Id getScoreId() {
        return scoreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageId)) return false;

        PageId pageId = (PageId) o;

        if (pageNo != pageId.pageNo) return false;
        if (!instrumentId.equals(pageId.instrumentId)) return false;
        return scoreId.equals(pageId.scoreId);

    }

    @Override
    public int hashCode() {
        int result = pageNo;
        result = 31 * result + instrumentId.hashCode();
        result = 31 * result + scoreId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PageId{" +
                "pageNo=" + pageNo +
                ", instrumentId=" + instrumentId +
                ", scoreId=" + scoreId +
                '}';
    }
}
