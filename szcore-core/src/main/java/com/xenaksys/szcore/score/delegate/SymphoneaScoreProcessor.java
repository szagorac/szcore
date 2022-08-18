package com.xenaksys.szcore.score.delegate;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.DynamicMovementStrategy;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategy;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.VoteAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceVoteEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEventType;
import com.xenaksys.szcore.model.*;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StrId;
import com.xenaksys.szcore.score.*;
import com.xenaksys.szcore.score.delegate.web.symphonea.SymphoneaWebAudienceProcessor;
import com.xenaksys.szcore.score.web.WebScore;
import com.xenaksys.szcore.score.web.overlay.SymphoneaWebOverlayFactory;
import com.xenaksys.szcore.task.TaskFactory;
import com.xenaksys.szcore.time.TransportFactory;
import com.xenaksys.szcore.web.WebClientInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.xenaksys.szcore.Consts.*;

public class SymphoneaScoreProcessor extends ScoreProcessorDelegate {

    private final static String PART1 = "Part1";
    private final static String PART2 = "Part2";
    private final static String PART3 = "Part3";
    private final static String INSTRUMENT_DEFAULT = PART1;
    private final static String[] INSTRUMENTS = {PART1, PART2, PART3};
    private final static String[] DYNAMIC_INSTRUMENTS = INSTRUMENTS;

    public SymphoneaScoreProcessor(TransportFactory transportFactory,
                                   MutableClock clock,
                                   OscPublisher oscPublisher,
                                   WebPublisher webPublisher,
                                   Scheduler scheduler,
                                   EventFactory eventFactory,
                                   TaskFactory taskFactory,
                                   BasicScore szcore,
                                   ScoreProcessorDelegator parent,
                                   EventReceiver eventReceiver,
                                   List<OutgoingWebEventType> latencyCompensatorEventTypeFilter,
                                   Properties props
                                   ) {
        super(transportFactory, clock, oscPublisher, webPublisher, scheduler, eventFactory, taskFactory, szcore, parent, eventReceiver, latencyCompensatorEventTypeFilter, props);
    }

    protected void createWebAudienceProcessor() {
        setWebAudienceProcessor(new SymphoneaWebAudienceProcessor(this, getEventFactory(), getClock()));
    }

    public void initWebScore() {
        setWebScore(new WebScore(this, getEventFactory(), getClock(), getProps(), new SymphoneaWebOverlayFactory()));
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
        DynamicMovementStrategy dynamicMovementStrategy = szcore.getDynamicScoreStrategy();
        if(dynamicMovementStrategy != null && dynamicMovementStrategy.isActive()) {
            dynamicMovementStrategy.setInstruments(INSTRUMENTS);
            dynamicMovementStrategy.setDynamicParts(DYNAMIC_INSTRUMENTS);
            dynamicMovementStrategy.setDefaultPart(INSTRUMENT_DEFAULT);
        }

        int precountMillis = 5 * 1000;
        int precountBeatNo = 4;
        szcore.setPrecount(precountMillis, precountBeatNo);

        Collection<Instrument> maxClients = szcore.getOscPlayers();
        getOscDestinationEventListener().reloadDestinations(maxClients);

//        addNoScoreInstrument(INSTRUMENT_ABSTAIN);

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

    protected boolean isSendInstrumentSlotsEvent(String name) {
        if(name == null) {
            return false;
        }
//        return !INSTRUMENT_PRESENTER.equals(name);
        return false;
    }

    @Override
    public void processSelectSection(String section, WebClientInfo clientInfo) {
        super.processSelectSection(section, clientInfo);
    }

    protected void processInstrumentReplace(String sourceInst, String slotInstrument, Instrument currentInst, Instrument replaceInst, WebClientInfo clientInfo) {
        ScoreBuilderStrategy builderStrategy = getBasicScore().getScoreBuilderStrategy();
        if(builderStrategy == null || clientInfo == null) {
            return;
        }

        try {
            getWebScore().replaceInstrumentClient(slotInstrument, clientInfo);
            builderStrategy.addClientInstrument(builderStrategy.getCurrentSection(), clientInfo.getClientId(), slotInstrument);
            getWebScore().sendPartInfo(clientInfo);
        } catch (Exception e) {
            LOG.error("processInstrumentReplace: cailed to set instrument client", e);
        }
    }

    protected void processVote(VoteAudienceEvent webEvent) {
        LOG.debug("processVote symphonea: ");
        SymphoneaWebAudienceProcessor audienceProcessor = (SymphoneaWebAudienceProcessor) getWebAudienceProcessor();
        if (audienceProcessor == null) {
            return;
        }
        WebAudienceVoteEvent voteEvent = getEventFactory().createWebAudienceVoteEvent(webEvent.getValue(), webEvent.getUsersNo(), getClock().getSystemTimeMillis());
        audienceProcessor.processWebAudienceEvent(voteEvent);
        updateClients(getBasicScore().getScoreBuilderStrategy());
    }

    @Override
    public void onSectionStart(String section) {
        SymphoneaWebAudienceProcessor audienceProcessor = (SymphoneaWebAudienceProcessor) getWebAudienceProcessor();
        if (audienceProcessor != null) {
            audienceProcessor.onSectionStart(section);
        }
        super.onSectionStart(section);
    }

    public void onSectionStop(String section) {
        SymphoneaWebAudienceProcessor audienceProcessor = (SymphoneaWebAudienceProcessor) getWebAudienceProcessor();
        if (audienceProcessor != null) {
            audienceProcessor.onSectionStop(section);
        }
        super.onSectionStop(section);
    }

    @Override
    public void publishAudienceViewState(boolean isNotesEnabled, boolean isAudioEnabled, boolean isThumbsEnabled, boolean isMeterEnabled, boolean isVoteEnabled) {
        SymphoneaWebAudienceProcessor audienceProcessor = (SymphoneaWebAudienceProcessor) getWebAudienceProcessor();
        if (audienceProcessor != null) {
            audienceProcessor.setAudienceViewState(isNotesEnabled, isAudioEnabled, isThumbsEnabled, isMeterEnabled, isVoteEnabled);
        }
    }

    @Override
    public void sendAudienceConfig(String configName, int presetNo, Map<String, Object> overrides) {
        SymphoneaWebAudienceProcessor audienceProcessor = (SymphoneaWebAudienceProcessor) getWebAudienceProcessor();
        if (audienceProcessor != null) {
            audienceProcessor.sendAudienceConfig(configName, presetNo, overrides);
        }
    }
}