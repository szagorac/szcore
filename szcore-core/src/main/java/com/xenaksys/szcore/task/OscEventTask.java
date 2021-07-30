package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.SzcoreEvent;

public class OscEventTask extends EventMusicTask {
    OscPublisher oscPublisher;

    public OscEventTask(long playTime, OscEvent event, OscPublisher oscPublisher) {
        super(playTime, event);
        this.oscPublisher = oscPublisher;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof OscEvent)) {
            return;
        }

        OscEvent oscEvent = (OscEvent) event;
        oscPublisher.process(oscEvent);
    }
}
