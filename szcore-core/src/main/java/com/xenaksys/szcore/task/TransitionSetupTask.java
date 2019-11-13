package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.TransitionEvent;
import com.xenaksys.szcore.event.TransitionScriptEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Transition;

public class TransitionSetupTask extends EventMusicTask {
    private final Scheduler scheduler;
    private final OscPublisher oscPublisher;
    private final EventFactory eventFactory;
    private final String destination;
    private final Clock clock;

    public TransitionSetupTask(TransitionEvent transitionEvent, String destination, Scheduler scheduler,
                               OscPublisher oscPublisher, EventFactory eventFactory, Clock clock) {
        super(0, transitionEvent);
        this.scheduler = scheduler;
        this.oscPublisher = oscPublisher;
        this.eventFactory = eventFactory;
        this.destination = destination;
        this.clock = clock;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (event == null || !(event instanceof TransitionEvent)) {
            return;
        }

        TransitionEvent transitionEvent = (TransitionEvent) event;

        Transition transition = transitionEvent.getTransition();
        if(transition == null) {
            return;
        }

        String component = transition.getComponent();
        if(component == null) {
            return;
        }

        long duration = transition.getDuration();
        long freq = transition.getFrequency();
        long start = transition.getStartValue();
        long end = transition.getEndValue();
        long startElapsedTime = clock.getElapsedTimeMillis();
        long playTime = startElapsedTime;
        double currentValue = 1.0 * start;

        if(end >= start) {
            double increment = 0.0;
            int eventNo  = 1;
            if(duration == 0L) {
                increment = 1.0 * (end - start);
            } else {
                increment = 1.0 * (end - start) / (duration / freq);
                eventNo  = Math.round(duration/freq);
            }

            for(int i = 0; i <= eventNo; i++) {
                long v = Math.round(currentValue);
                addTansitionTask(playTime, component, (int)v);
                currentValue += increment;
                playTime += freq;
            }
        } else {
            double increment = 0.0;
            int eventNo  = 1;
            if(duration == 0L) {
                increment = 1.0 * (start - end);
            } else {
                increment = 1.0 * (start - end) / (duration / freq);
                eventNo  = Math.round(duration/freq);
            }

            for(int i = 0; i <= eventNo; i++) {
                long v = Math.round(currentValue);
                addTansitionTask(playTime, component, (int)v);
                currentValue -= increment;
                playTime += freq;
            }
        }

        long elapsedTime = clock.getElapsedTimeMillis();
        long endTime = elapsedTime + duration;
        addTansitionTask(endTime, component, end);
    }

    private void addTansitionTask(long playTime, String component, long alpha) {
        if (playTime == 0l) { // 0 is special value
            playTime = -1;
        }
        TransitionScriptEvent event = eventFactory.createTransitionScriptEvent(destination, clock.getSystemTimeMillis());
        event.addCommandArg(component, alpha);
        OscEventTask task = new OscEventTask(playTime, event, oscPublisher);
//        LOG.info("Create Transition Task playTime: " + playTime + " component: " + component + " alpha: " + alpha);
        scheduler.add(task);
    }

}
