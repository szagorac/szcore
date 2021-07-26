package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class PageDisplayEvent extends OscEvent {
    private final PageId pageId;
    private final String filename;
    private final StaveId staveId;
    private final PageId rndPageId;

    public PageDisplayEvent(PageId pageId, PageId rndPageId, String filename, StaveId staveId, String address, List<Object> arguments, BeatId eventBaseBeat, String destination, long time) {
        super(address, arguments, eventBaseBeat, destination, time);
        this.pageId = pageId;
        this.filename = filename;
        this.staveId = staveId;
        this.rndPageId = rndPageId;
    }

    public PageId getPageId() {
        return pageId;
    }

    public PageId getRndPageId() {
        return rndPageId;
    }

    public String getFilename() {
        return filename;
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.PAGE_DISPLAY;
    }
}
