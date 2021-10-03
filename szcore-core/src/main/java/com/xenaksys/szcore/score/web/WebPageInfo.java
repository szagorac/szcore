package com.xenaksys.szcore.score.web;

public class WebPageInfo {
    private String filename;
    private String staveId;
    private String pageId;
    private String rndPageId;
    private WebTranspositionInfo transpositionInfo;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStaveId() {
        return staveId;
    }

    public void setStaveId(String staveId) {
        this.staveId = staveId;
    }

    public String getRndPageId() {
        return rndPageId;
    }

    public void setRndPageId(String rndPageId) {
        this.rndPageId = rndPageId;
    }

    public void setTranspositionInfo(WebTranspositionInfo transpositionInfo) {
        this.transpositionInfo = transpositionInfo;
    }

    public WebTranspositionInfo getTranspositionInfo() {
        return transpositionInfo;
    }
}
