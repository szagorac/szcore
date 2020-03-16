package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.*;
import com.xenaksys.szcore.model.*;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.score.WebScore;

public class TaskFactory {


    //TODO create pool of Tasks to avoid garbage creation
    public TempoChangeTask createTempoChangeTask(TempoChangeEvent event, long playTime, Transport transport, OscPublisher oscPublisher, TempoModifier currentModifier, boolean isSchedulerRunning) {
        return new TempoChangeTask(playTime, event, transport, oscPublisher, currentModifier, isSchedulerRunning);
    }

    public TimeSigChangeTask createTimeSigChangeTask(TimeSigChangeEvent event, long playTime, Transport transport) {
        return new TimeSigChangeTask(playTime, event, transport);
    }

    public TransportPositionTask createTransportPositionTask(TransportPositionEvent event, long playTime, Transport transport) {
        return new TransportPositionTask(playTime, event, transport);
    }

    public StopPlayTask createStopPlayTask(StopEvent event, long playTime, ScoreProcessor scoreProcessor) {
        return new StopPlayTask(playTime, event, scoreProcessor);
    }

    public StaveActiveChangeTask createActiveStaveChangeTask(StaveActiveChangeEvent event, long playTime, Stave stave, OscPublisher oscPublisher) {
        return new StaveActiveChangeTask(playTime, event, stave, oscPublisher);
    }

    public PrepStaveChangeTask createPrepStaveChangeTask(PrepStaveChangeEvent event, long playTime, ScoreProcessorImpl scoreProcessor) {
        return new PrepStaveChangeTask(playTime, event, scoreProcessor);
    }

    public PrecountBeatSetupTask createPrecountBeatSetupTask(PrecountBeatSetupEvent precountBeatSetupEvent, String destination, Transport transport, Scheduler scheduler,
                                                             OscPublisher oscPublisher, EventFactory eventFactory, Clock clock) {
        return new PrecountBeatSetupTask(precountBeatSetupEvent, destination, transport, scheduler, oscPublisher, eventFactory, clock);
    }

    public TransitionSetupTask createTransitionSetupTask(TransitionEvent transitionEvent, String destination, Scheduler scheduler,
                                                             OscPublisher oscPublisher, EventFactory eventFactory, Clock clock) {
        return new TransitionSetupTask(transitionEvent, destination, scheduler, oscPublisher, eventFactory, clock);
    }

    public WebScoreEventTask createWebScoreEventTask(long playTime, WebScoreEvent event, WebScore webScore) {
        return new WebScoreEventTask(playTime, event, webScore);
    }


}
