package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.TimeSigChangeEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.TimeSignature;
import com.xenaksys.szcore.model.Transport;

public class TimeSigChangeTask extends EventMusicTask {
    private final Transport transport;

    public TimeSigChangeTask(long playTime, TimeSigChangeEvent event, Transport transport) {
        super(playTime, event);
        this.transport = transport;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof TimeSigChangeEvent)) {
            return;
        }

        TimeSigChangeEvent timeSigChangeEvent = (TimeSigChangeEvent) event;
        TimeSignature timeSignature = timeSigChangeEvent.getTimeSignature();
        if (timeSignature == null) {
            return;
        }

        transport.setTimeSignature(timeSignature);
    }
}
