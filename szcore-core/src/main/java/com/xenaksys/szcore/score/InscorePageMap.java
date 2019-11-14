package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.PageId;

import java.util.LinkedList;
import java.util.List;

public class InscorePageMap {
    private final PageId pageId;

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
}
