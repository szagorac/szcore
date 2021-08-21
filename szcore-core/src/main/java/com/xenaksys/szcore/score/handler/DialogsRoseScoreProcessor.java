package com.xenaksys.szcore.score.handler;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.Bar;
import com.xenaksys.szcore.model.Id;
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
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.BasicPage;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.InscorePageMap;
import com.xenaksys.szcore.score.InstrumentBeatTracker;
import com.xenaksys.szcore.score.ScoreLoader;
import com.xenaksys.szcore.score.ScoreProcessorWrapper;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;

import java.io.File;
import java.util.Collection;

import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NAME;
import static com.xenaksys.szcore.Consts.CONTINUOUS_PAGE_NO;
import static com.xenaksys.szcore.Consts.UNDERSCORE;

public class DialogsRoseScoreProcessor extends GenericScoreProcessor {

    public DialogsRoseScoreProcessor(TransportFactory transportFactory,
                                     MutableClock clock,
                                     OscPublisher oscPublisher,
                                     WebPublisher webPublisher,
                                     Scheduler scheduler,
                                     EventFactory eventFactory,
                                     TaskFactory taskFactory,
                                     BasicScore szcore,
                                     ScoreProcessorWrapper parent
                                   ) {
        super(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, szcore, parent);
    }

    @Override
    public Score loadScore(File file) throws Exception {
        Scheduler scheduler = getScheduler();
        if (scheduler.isActive()) {
            LOG.warn("Scheduler is active, can not perform load score");
            throw new Exception("Scheduler is active, can not perform load score");
        }
        LOG.info("LOADING SCORE: " + file.getCanonicalPath());
        reset();
        BasicScore szcore = (BasicScore)getScore();
        if(szcore == null) {
            Score score = ScoreLoader.load(file);
            szcore = (BasicScore) score;
        }
        return szcore;
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

        Collection<Instrument> instruments = szcore.getInstruments();
        BeatId lastBeat = null;
        for (Instrument instrument : instruments) {
            Id instrumentId = instrument.getId();
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

        if (szcore.isRandomizeContinuousPageContent()) {
            szcore.initRandomisation();
        }

        if (!szcore.isUseContinuousPage()) {
            addStopEvent(lastBeat, transport.getId());
        }
        int precountMillis = 5 * 1000;
        int precountBeatNo = 4;
        szcore.setPrecount(precountMillis, precountBeatNo);

        setScoreLoaded(true);
    }
}
