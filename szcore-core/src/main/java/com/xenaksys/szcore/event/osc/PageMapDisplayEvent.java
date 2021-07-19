package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class PageMapDisplayEvent extends OscEvent {

    public PageMapDisplayEvent(String address, List<Object> arguments, BeatId eventBaseBeat, String destination, long time) {
        super(address, arguments, eventBaseBeat, destination, time);
    }

    public OscEventType getOscEventType() {
        return OscEventType.PAGE_MAP_DISPLAY;
    }
}
