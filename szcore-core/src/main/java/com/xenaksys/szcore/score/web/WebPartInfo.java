package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.algo.IntRange;

import java.util.List;

public class WebPartInfo {
    private String name;
    private List<IntRange> pageRanges;
    private String imgDir;
    private String imgPageNameToken;
    private String imgContPageName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IntRange> getPageRanges() {
        return pageRanges;
    }

    public void setPageRanges(List<IntRange> pageRanges) {
        this.pageRanges = pageRanges;
    }

    public String getImgDir() {
        return imgDir;
    }

    public void setImgDir(String imgDir) {
        this.imgDir = imgDir;
    }

    public String getImgPageNameToken() {
        return imgPageNameToken;
    }

    public void setImgPageNameToken(String imgPageNameToken) {
        this.imgPageNameToken = imgPageNameToken;
    }

    public String getImgContPageName() {
        return imgContPageName;
    }

    public void setImgContPageName(String imgContPageName) {
        this.imgContPageName = imgContPageName;
    }
}
