package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.score.InscoreMapElement;

import java.util.List;

public class WebTimeSpaceMapInfo {
    private String pageId;
    private String staveId;
    private List<InscoreMapElement> map;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public List<InscoreMapElement> getMap() {
        return map;
    }

    public void setMap(List<InscoreMapElement> mapElements) {
        this.map = mapElements;
    }

    public String getStaveId() {
        return staveId;
    }

    public void setStaveId(String staveId) {
        this.staveId = staveId;
    }
}
