package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.OscStaveActivateEvent;
import com.xenaksys.szcore.event.StaveActiveChangeEvent;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.BasicStave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaveActiveChangeTask extends EventMusicTask {
    static final Logger LOG = LoggerFactory.getLogger(StaveActiveChangeTask.class);

    private final Stave stave;
    OscPublisher oscPublisher;

    public StaveActiveChangeTask(long playTime, StaveActiveChangeEvent event, Stave stave, OscPublisher oscPublisher) {
        super(playTime, event);
        this.stave = stave;
        this.oscPublisher = oscPublisher;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (event == null || !(event instanceof StaveActiveChangeEvent)) {
            return;
        }

        StaveActiveChangeEvent staveActiveChangeEvent = (StaveActiveChangeEvent) event;
        StaveId staveId = staveActiveChangeEvent.getStaveId();
        if (staveId == null) {
            return;
        }

        boolean isActive = staveActiveChangeEvent.isActive();

        if (stave == null) {
            return;
        }

        BasicStave bs = (BasicStave) stave;
        bs.setActive(isActive);

        OscStaveActivateEvent oscStaveActivateEvent = staveActiveChangeEvent.getOscStaveActivateEvent();
        if (oscStaveActivateEvent == null) {
            return;
        }

//        LOG.info("Executing StaveActiveChangeTask, event: " + event);

        oscPublisher.process(oscStaveActivateEvent);
    }
}
