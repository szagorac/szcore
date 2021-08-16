package com.xenaksys.szcore.score.handler;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnionRoseScoreProcessor extends GenericScoreProcessor {
    static final Logger LOG = LoggerFactory.getLogger(UnionRoseScoreProcessor.class);

    public UnionRoseScoreProcessor(TransportFactory transportFactory,
                                   MutableClock clock,
                                   OscPublisher oscPublisher,
                                   WebPublisher webPublisher,
                                   Scheduler scheduler,
                                   EventFactory eventFactory,
                                   TaskFactory taskFactory,
                                   BasicScore szcore) {
        super(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, szcore);
    }
}
