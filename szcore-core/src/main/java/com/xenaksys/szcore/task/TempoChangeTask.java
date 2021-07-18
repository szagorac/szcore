package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.osc.OscStaveTempoEvent;
import com.xenaksys.szcore.event.osc.TempoChangeEvent;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.TempoImpl;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.Transport;

import java.util.List;

public class TempoChangeTask extends EventMusicTask {
    private final Transport transport;
    private final OscPublisher oscPublisher;
    private final TempoModifier currentModifier;
    private final boolean isSchedulerRunning;

    public TempoChangeTask(long playTime, TempoChangeEvent event, Transport transport, OscPublisher oscPublisher, TempoModifier currentModifier, boolean isSchedulerRunning) {
        super(playTime, event);
        //LOG.debug("Creating TempoChangeTask: ");
        this.transport = transport;
        this.oscPublisher = oscPublisher;
        this.currentModifier = currentModifier;
        this.isSchedulerRunning = isSchedulerRunning;
//LOG.info("Setting currentModifier: " + currentModifier);
    }

    @Override
    public void play() {
        //LOG.debug("Playing TempoChangeTask: ");
        SzcoreEvent event = getEvent();
        if (!(event instanceof TempoChangeEvent)) {
            return;
        }

        TempoChangeEvent tempoChangeEvent = (TempoChangeEvent) event;
        Tempo tempo = tempoChangeEvent.getTempo();
        if (tempo == null) {
            return;
        }

        TempoModifier modifier = tempo.getTempoModifier();
        if(currentModifier != null){
            if(!currentModifier.equals(modifier)){
                tempo = new TempoImpl(tempo, currentModifier);
                LOG.info("Setting new modified tempo: " + tempo);
            }
        }

        List<OscStaveTempoEvent> oscEvents = tempoChangeEvent.getOscEvents();

        if(isSchedulerRunning){
            sendOscTempoEvents(oscEvents, tempo);
        }

        transport.setTempo(tempo);
    }

    private void sendOscTempoEvents(List<OscStaveTempoEvent> oscEvents, Tempo tempo) {
        if(oscEvents != null && oscPublisher != null){
            for(OscStaveTempoEvent oscEvent : oscEvents){
                //LOG.debug("Sending OscStaveTempoEvent: " + oscEvent);
                oscEvent.setTempo(tempo.getBpm());
                oscPublisher.process(oscEvent);
            }
        }
    }
}
