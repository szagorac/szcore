package com.xenaksys.szcore.score.delegate;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategy;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.EventReceiver;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.TempoModifier;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.BasicPage;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.InscorePageMap;
import com.xenaksys.szcore.score.InstrumentBeatTracker;
import com.xenaksys.szcore.score.ScoreLoader;
import com.xenaksys.szcore.score.ScoreProcessorDelegator;
import com.xenaksys.szcore.score.web.audience.delegate.DialogsWebAudienceProcessor;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.web.WebClientInfo;

import java.util.Collection;
import java.util.Properties;

import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NAME;
import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NO;
import static com.xenaksys.szcore.Consts.UNDERSCORE;

public class DialogsScoreProcessor extends ScoreProcessorDelegate {

    private final static String INSTRUMENT_DEFAULT = "Abstain";
    private final static String INSTRUMENT_PRESENTER = "Present";
    private final static String INSTRUMENT_AGREE = "Concur";
    private final static String INSTRUMENT_DISAGREE = "Dissent";
    private final static String INSTRUMENT_ABSTAIN = INSTRUMENT_DEFAULT;
    private final static String INSTRUMENT_OWNER = INSTRUMENT_PRESENTER;
    private final static String[] INSTRUMENTS = {INSTRUMENT_PRESENTER, INSTRUMENT_AGREE, INSTRUMENT_DISAGREE, INSTRUMENT_ABSTAIN};
    private final static String INSTRUMENT_ABSTAIN_PAGE_FILE_NAME = "Abstain_pagex";

    private Page abstainPage;

    public DialogsScoreProcessor(TransportFactory transportFactory,
                                 MutableClock clock,
                                 OscPublisher oscPublisher,
                                 WebPublisher webPublisher,
                                 Scheduler scheduler,
                                 EventFactory eventFactory,
                                 TaskFactory taskFactory,
                                 BasicScore szcore,
                                 ScoreProcessorDelegator parent,
                                 EventReceiver eventReceiver,
                                 Properties props
                                   ) {
        super(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, szcore, parent, eventReceiver, props);
    }

    protected void createWebAudienceProcessor() {
        setWebAudienceProcessor(new DialogsWebAudienceProcessor(this, getEventFactory(), getClock()));
    }

    @Override
    public void prepare(Score score) {
        BasicScore szcore = (BasicScore) score;
        setSzcore(szcore);

        Transport transport = getTransportFactory().getTransport(Consts.DEFAULT_TRANSPORT_NAME);
        transport.addListener(new ScoreTransportListener(transport.getId()));
        getScheduler().addTransport(transport);

        getTransportTempoModifiers().put(transport.getId(), new TempoModifier(Consts.ONE_D));

        PageId bpid = new PageId(0, new StrId(Consts.BLANK_PAGE_NAME), score.getId());
        Page blankPage = new BasicPage(bpid, Consts.BLANK_PAGE_NAME, Consts.BLANK_PAGE_FILE);
        szcore.setBlankPage(blankPage);

        Instrument presentInstrument = szcore.getInstrument(INSTRUMENT_PRESENTER);
        copyInstrument(presentInstrument, INSTRUMENT_ABSTAIN, INSTRUMENT_ABSTAIN_PAGE_FILE_NAME, false);

        Collection<Instrument> instruments = szcore.getInstruments();
        BeatId lastBeat = null;
        for (Instrument instrument : instruments) {
            InstrumentId instrumentId = (InstrumentId)instrument.getId();
            getInstrumentBeatTrackers().put(instrumentId, new InstrumentBeatTracker(transport, instrumentId));
            lastBeat = prepareInstrument(instrument, transport);
            Page lastPage = szcore.getLastInstrumentPage(instrumentId);

            String contPageName = instrument.getName() + UNDERSCORE + CONTINUOUS_PAGE_NAME;
            PageId contPageId = new PageId(CONTINUOUS_PAGE_NO, instrumentId, lastPage.getScoreId());
            InscorePageMap contInscorePageMap = ScoreLoader.loadPageInscoreMap(contPageId, contPageName);
            BasicPage contPage = new BasicPage(contPageId, contPageName, contPageName, lastPage.getInscorePageMap(), true);
            Collection<Bar> lpBars = lastPage.getBars();
            for (Bar bar : lpBars) {
                contPage.addBar(bar);
            }
            szcore.setContinuousPage(instrumentId, contPage);

            BasicPage endPage = new BasicPage(contPageId, contPageName, contPageName, contInscorePageMap, true);
            szcore.setEndPage(instrumentId, endPage);

            if (szcore.isUseContinuousPage()) {
                prepareContinuousPages(instrumentId, lastPage);
            }
        }

        szcore.initScoreStrategies();
        ScoreBuilderStrategy scoreBuilderStrategy = szcore.getScoreBuilderStrategy();
        scoreBuilderStrategy.setInstruments(INSTRUMENTS);
        scoreBuilderStrategy.setDefaultInstrument(INSTRUMENT_DEFAULT);

        if (!szcore.isUseContinuousPage()) {
            addStopEvent(lastBeat, transport.getId());
        }

        int precountMillis = 5 * 1000;
        int precountBeatNo = 4;
        szcore.setPrecount(precountMillis, precountBeatNo);

        setScoreLoaded(true);

        getWebScore().init();
    }

    public void recalcRndStrategy(ScoreRandomisationStrategy strategy, Page page) {
        if (strategy == null || page == null) {
            return;
        }
        strategy.recalcStrategy(page);
    }

    public void processRndStrategyOnModClose(ScoreRandomisationStrategy strategy) {
        //TODO
    }

    @Override
    public void processSelectSection(String section, WebClientInfo clientInfo) {
        super.processSelectSection(section, clientInfo);
        ScoreBuilderStrategy scoreBuilderStrategy = getBasicScore().getScoreBuilderStrategy();
        if(scoreBuilderStrategy == null) {
            return;
        }
        if(scoreBuilderStrategy.isSectionOwned(section)) {
            String owner = scoreBuilderStrategy.getSectionOwner(section);
            if(owner != null) {
                scoreBuilderStrategy.addClientInstrument(section, owner, INSTRUMENT_OWNER);
            }
        }
    }
}
