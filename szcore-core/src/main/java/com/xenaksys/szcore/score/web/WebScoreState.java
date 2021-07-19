package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.web.WebScoreAction;

import java.util.ArrayList;
import java.util.List;

public class WebScoreState {
    private WebScoreInfo scoreInfo;
    private WebPageInfo pageInfo;
    private WebPartInfo partInfo;
    private List<WebScoreAction> actions;
    private String part;
    private int beatNo;
    private long eventServerTime;

    public WebScoreInfo getScoreInfo() {
        return scoreInfo;
    }

    public void setScoreInfo(WebScoreInfo scoreInfo) {
        this.scoreInfo = scoreInfo;
    }

    public WebPageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(WebPageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public WebPartInfo getPartInfo() {
        return partInfo;
    }

    public void setPartInfo(WebPartInfo partInfo) {
        this.partInfo = partInfo;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public int getBeatNo() {
        return beatNo;
    }

    public void setBeatNo(int beatNo) {
        this.beatNo = beatNo;
    }

    public long getEventServerTime() {
        return eventServerTime;
    }

    public void setEventServerTime(long eventServerTime) {
        this.eventServerTime = eventServerTime;
    }

    public void addAction(WebScoreAction action) {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        actions.add(action);
    }

    public List<WebScoreAction> getActions() {
        return actions;
    }
}
