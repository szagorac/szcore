package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.music.ModWindowEvent;
import com.xenaksys.szcore.event.music.PrecountBeatSetupEvent;
import com.xenaksys.szcore.event.music.PrepStaveChangeEvent;
import com.xenaksys.szcore.event.music.StopEvent;
import com.xenaksys.szcore.event.music.TimeSigChangeEvent;
import com.xenaksys.szcore.event.music.TransitionEvent;
import com.xenaksys.szcore.event.music.TransportPositionEvent;
import com.xenaksys.szcore.event.osc.StaveActiveChangeEvent;
import com.xenaksys.szcore.event.osc.TempoChangeEvent;
import com.xenaksys.szcore.event.script.ScriptingEngineEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreProcessor;
import com.xenaksys.szcore.scripting.ScoreScriptingEngine;

public class TaskFactory {


    //TODO create pool of Tasks to avoid garbage creation
    public TempoChangeTask createTempoChangeTask(TempoChangeEvent event, long playTime, Transport transport, OscPublisher oscPublisher, ScoreProcessor scoreProcessor, TempoModifier currentModifier, boolean isSchedulerRunning) {
        return new TempoChangeTask(playTime, event, transport, oscPublisher, currentModifier, scoreProcessor, isSchedulerRunning);
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

    public ScheduledEventTask createScheduledEventTask(long playTime, SzcoreEvent event, ScoreProcessor scoreProcessor) {
        return new ScheduledEventTask(playTime, event, scoreProcessor);
    }

    public PrepStaveChangeTask createPrepStaveChangeTask(PrepStaveChangeEvent event, long playTime, ScoreProcessor scoreProcessor) {
        return new PrepStaveChangeTask(playTime, event, scoreProcessor);
    }

    public PrecountBeatSetupTask createPrecountBeatSetupTask(PrecountBeatSetupEvent precountBeatSetupEvent, String destination, Transport transport, ScoreProcessor processor,
                                                             OscPublisher oscPublisher, EventFactory eventFactory, TaskFactory taskFactory, WebAudienceScoreProcessor webAudienceScoreProcessor, Clock clock) {
        return new PrecountBeatSetupTask(precountBeatSetupEvent, destination, transport, processor, oscPublisher, eventFactory, taskFactory, webAudienceScoreProcessor, clock);
    }

    public TransitionSetupTask createTransitionSetupTask(TransitionEvent transitionEvent, String destination, ScoreProcessor processor,
                                                         OscPublisher oscPublisher, EventFactory eventFactory, Clock clock) {
        return new TransitionSetupTask(transitionEvent, destination, processor, oscPublisher, eventFactory, clock);
    }

    public WebAudienceEventTask createWebAudienceEventTask(long playTime, WebAudienceEvent event, WebAudienceScoreProcessor webAudienceScoreProcessor) {
        return new WebAudienceEventTask(playTime, event, webAudienceScoreProcessor);
    }

    public ScriptingEngineEventTask createScriptingEngineEventTask(long playTime, ScriptingEngineEvent event, ScoreScriptingEngine scriptingEngine) {
        return new ScriptingEngineEventTask(playTime, event, scriptingEngine);
    }
}
