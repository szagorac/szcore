package com.xenaksys.szcore.model;

public class PageInfo {
    private final int pageNo;
    private final int displayPageNo;
    private final String section;

    public PageInfo(int pageNo, int displayPageNo, String section) {
        this.pageNo = pageNo;
        this.displayPageNo = displayPageNo;
        this.section = section;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getDisplayPageNo() {
        return displayPageNo;
    }

    public String getSection() {
        return section;
    }
}
