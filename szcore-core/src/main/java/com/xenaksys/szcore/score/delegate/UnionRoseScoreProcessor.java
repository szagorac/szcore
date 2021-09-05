package com.xenaksys.szcore.score.delegate;

import com.xenaksys.szcore.algo.ScoreRandomisationStrategy;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.score.web.audience.delegate.UnionRoseWebAudienceProcessor;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;

import java.util.List;
import java.util.Properties;

public class UnionRoseScoreProcessor extends ScoreProcessorDelegate {

    public UnionRoseScoreProcessor(TransportFactory transportFactory,
                                   MutableClock clock,
                                   OscPublisher oscPublisher,
                                   WebPublisher webPublisher,
                                   Scheduler scheduler,
                                   EventFactory eventFactory,
                                   TaskFactory taskFactory,
                                   BasicScore szcore,
                                   ScoreProcessorImpl parent,
                                   Properties props
                                   ) {
        super(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, szcore, parent, props);
    }

    protected void createWebAudienceProcessor() {
        setWebAudienceProcessor(new UnionRoseWebAudienceProcessor(this, getEventFactory(), getClock()));
    }

    public void recalcRndStrategy(ScoreRandomisationStrategy strategy, Page page) {
        if(strategy == null || page == null) {
            return;
        }
        strategy.recalcStrategy(page);
        int pageQuantity = strategy.getNumberOfRequiredPages();
        UnionRoseWebAudienceProcessor audienceProcessor = (UnionRoseWebAudienceProcessor) getWebAudienceProcessor();
        List<Integer> pageIds = audienceProcessor.prepareNextTilesToPlay(pageQuantity);
        strategy.setPageSelection(pageIds);
    }
}
