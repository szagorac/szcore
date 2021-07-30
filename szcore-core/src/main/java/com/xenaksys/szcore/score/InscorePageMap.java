package com.xenaksys.szcore.score;

import com.google.gson.Gson;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.PageId;

import java.util.LinkedList;
import java.util.List;

public class InscorePageMap {
    private static final Gson GSON = new Gson();

    private final PageId pageId;
    private String inscoreStr;
    private String webStr;

    private List<InscoreMapElement> mapElements = new LinkedList<>();

    public InscorePageMap(PageId pageId) {
        this.pageId = pageId;
    }

    public void addElement(InscoreMapElement element) {
        mapElements.add(element);
    }

    public List<InscoreMapElement> getMapElements() {
        return mapElements;
    }

    public void setMapElements(List<InscoreMapElement> mapElements) {
        this.mapElements = mapElements;
    }

    public void createInscoreStr() {
        inscoreStr = toInscoreString();
    }

    public String getInscoreStr() {
        return inscoreStr;
    }

    public void createWebStr() {
        webStr = toWebString();
    }

    public String getWebStr() {
        return webStr;
    }

    public String toInscoreString() {
        StringBuilder sb  = new StringBuilder();
        String delim = Consts.EMPTY;
        for(InscoreMapElement mapElement : mapElements) {
            sb.append(delim);
            sb.append(mapElement.toInscoreString());
            delim = Consts.SYSTEM_NEW_LINE;
        }
        return sb.toString();
    }

    public String toWebString() {
        return GSON.toJson(mapElements);
    }

}
