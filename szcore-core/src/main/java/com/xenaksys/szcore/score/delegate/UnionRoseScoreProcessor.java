package com.xenaksys.szcore.score.delegate;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.ScoreProcessorHandler;
import com.xenaksys.szcore.score.web.audience.delegate.UnionRoseWebAudienceProcessor;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;

public class UnionRoseScoreProcessor extends GenericScoreProcessor {

    public UnionRoseScoreProcessor(TransportFactory transportFactory,
                                   MutableClock clock,
                                   OscPublisher oscPublisher,
                                   WebPublisher webPublisher,
                                   Scheduler scheduler,
                                   EventFactory eventFactory,
                                   TaskFactory taskFactory,
                                   BasicScore szcore,
                                   ScoreProcessorHandler parent
                                   ) {
        super(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, szcore, parent);
    }

    protected void createWebAudienceProcessor() {
        setWebAudienceProcessor(new UnionRoseWebAudienceProcessor(this, getEventFactory(), getClock()));
    }
}
