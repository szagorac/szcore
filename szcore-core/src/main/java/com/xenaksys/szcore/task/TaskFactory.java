package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.ModWindowEvent;
import com.xenaksys.szcore.event.PrecountBeatSetupEvent;
import com.xenaksys.szcore.event.PrepStaveChangeEvent;
import com.xenaksys.szcore.event.ScriptingEngineEvent;
import com.xenaksys.szcore.event.StaveActiveChangeEvent;
import com.xenaksys.szcore.event.StopEvent;
import com.xenaksys.szcore.event.TempoChangeEvent;
import com.xenaksys.szcore.event.TimeSigChangeEvent;
import com.xenaksys.szcore.event.TransitionEvent;
import com.xenaksys.szcore.event.TransportPositionEvent;
import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.score.ScoreScriptingEngine;
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

    public ModWindowTask createModWindowTask(ModWindowEvent event, long playTime, ScoreProcessor scoreProcessor) {
        return new ModWindowTask(playTime, event, scoreProcessor);
    }

    public StaveActiveChangeTask createActiveStaveChangeTask(StaveActiveChangeEvent event, long playTime, Stave stave, OscPublisher oscPublisher) {
        return new StaveActiveChangeTask(playTime, event, stave, oscPublisher);
    }

    public PrepStaveChangeTask createPrepStaveChangeTask(PrepStaveChangeEvent event, long playTime, ScoreProcessorImpl scoreProcessor) {
        return new PrepStaveChangeTask(playTime, event, scoreProcessor);
    }

    public PrecountBeatSetupTask createPrecountBeatSetupTask(PrecountBeatSetupEvent precountBeatSetupEvent, String destination, Transport transport, Scheduler scheduler,
                                                             OscPublisher oscPublisher, EventFactory eventFactory, TaskFactory taskFactory, WebScore webScore, Clock clock) {
        return new PrecountBeatSetupTask(precountBeatSetupEvent, destination, transport, scheduler, oscPublisher, eventFactory, taskFactory, webScore, clock);
    }

    public TransitionSetupTask createTransitionSetupTask(TransitionEvent transitionEvent, String destination, Scheduler scheduler,
                                                         OscPublisher oscPublisher, EventFactory eventFactory, Clock clock) {
        return new TransitionSetupTask(transitionEvent, destination, scheduler, oscPublisher, eventFactory, clock);
    }

    public WebScoreEventTask createWebScoreEventTask(long playTime, WebScoreEvent event, WebScore webScore) {
        return new WebScoreEventTask(playTime, event, webScore);
    }

    public ScriptingEngineEventTask createScriptingEngineEventTask(long playTime, ScriptingEngineEvent event, ScoreScriptingEngine scriptingEngine) {
        return new ScriptingEngineEventTask(playTime, event, scriptingEngine);
    }
}
