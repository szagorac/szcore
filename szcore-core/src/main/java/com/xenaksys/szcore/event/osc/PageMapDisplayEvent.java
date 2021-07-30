package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.InscoreMapElement;

import java.util.List;

public class PageMapDisplayEvent extends OscEvent {
    private final List<InscoreMapElement> mapElements;
    private final PageId pageId;
    private final StaveId staveId;

    public PageMapDisplayEvent(PageId pageId, StaveId staveId, String address, List<Object> arguments, List<InscoreMapElement> mapElements, BeatId eventBaseBeat, String destination, long time) {
        super(address, arguments, eventBaseBeat, destination, time);
        this.mapElements = mapElements;
        this.pageId = pageId;
        this.staveId = staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.PAGE_MAP_DISPLAY;
    }

    public List<InscoreMapElement> getMapElements() {
        return mapElements;
    }

    public PageId getPageId() {
        return pageId;
    }

    public StaveId getStaveId() {
        return staveId;
    }
}
