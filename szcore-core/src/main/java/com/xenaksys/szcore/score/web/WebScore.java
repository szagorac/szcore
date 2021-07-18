package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.web.WebClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final BasicScore score;
    private final Clock clock;
    private final Map<String, List<WebClientInfo>> instrumentClients = new ConcurrentHashMap<>();
    private final Map<String, WebClientInfo> clients = new ConcurrentHashMap<>();

    private WebScoreInfo scoreInfo;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public WebScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.score = (BasicScore) scoreProcessor.getScore();
    }

    public void init() {
        this.scoreInfo = initScoreInfo();
        try {
            sendScoreInfo();
        } catch (Exception e) {
            LOG.error("Failed to send score info to all", e);
        }
    }

    private WebScoreInfo initScoreInfo() {
        WebScoreInfo info = new WebScoreInfo();
        info.setTitle(score.getName());

        Tempo tempo = null;
        List<String> instNames = new ArrayList<>();
        Collection<Instrument> instruments = score.getInstruments();
        for (Instrument inst : instruments) {
            instNames.add(inst.getName());
            Transport transport = score.getInstrumentTransport(inst.getId());
            if (tempo == null) {
                tempo = transport.getTempo();
            }
        }
        info.setInstruments(instNames);

        if (tempo != null) {
            info.setBpm(tempo.getBpm());
        }

        return info;
    }

    public void resetState() {
    }

    public void processConnectionEvent(WebScoreConnectionEvent event) {
        if (event == null) {
            return;
        }
        try {
            WebClientInfo clientInfo = event.getWebClientInfo();
            boolean isRegistered = clients.containsKey(clientInfo.getClientAddr());
            addOrUpdateClientInfo(clientInfo);
            if (!isRegistered) {
                sendScoreInfo(clientInfo);
            }
        } catch (Exception e) {
            LOG.error("processConnectionEvent: failed to process score connection", e);
        }
    }

    private void addOrUpdateClientInfo(WebClientInfo clientInfo) throws Exception {
        if (clientInfo == null) {
            return;
        }
        clients.put(clientInfo.getClientAddr(), clientInfo);
        addInstrumentClient(clientInfo);
    }

    private void addInstrumentClient(WebClientInfo clientInfo) throws Exception {
        String instrument = clientInfo.getInstrument();
        if (instrument == null) {
            return;
        }

        List<WebClientInfo> clientInfos = instrumentClients.computeIfAbsent(instrument, k -> new ArrayList<>());
        if (!clientInfos.contains(clientInfo)) {
            clientInfos.add(clientInfo);
            sendPartInfo(clientInfo);
        } else {
            LOG.debug("addInstrumentClient, client is already registered");
        }
    }

    public void sendScoreInfo() throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setScoreInfo(scoreInfo);
        scoreProcessor.sendWebScoreState(WebScoreTargetType.ALL.name(), WebScoreTargetType.ALL, scoreState);
    }

    public void sendScoreInfo(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setScoreInfo(scoreInfo);
        scoreProcessor.sendWebScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public void sendCurrentBeat(int beatNo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setBeatNo(beatNo);
        scoreProcessor.sendWebScoreState(WebScoreTargetType.ALL.name(), WebScoreTargetType.ALL, scoreState);
    }

    public void sendPartInfo(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setPart(clientInfo.getInstrument());
        scoreProcessor.sendWebScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public void processRemoveConnectionEvent(WebScoreRemoveConnectionEvent webEvent) {
        LOG.info("processRemoveConnectionEvent");
    }

    public void processPartRegistration(WebScorePartRegEvent event) {
        try {
            String clientId = event.getSourceAddr();
            WebClientInfo clientInfo = clients.get(clientId);
            if (clientInfo == null) {
                LOG.error("processPartRegistration: Unknown client: {}", clientId);
                return;
            }
            String instrument = event.getPart();
            if (instrument == null) {
                return;
            }
            if (instrument.equals(clientInfo.getInstrument())) {
                LOG.debug("processPartRegistration, instrument is already registered");
            } else {
                clientInfo.setInstrument(instrument);
            }
            addInstrumentClient(clientInfo);
        } catch (Exception e) {
            LOG.error("processPartRegistration: failed to process part registration", e);
        }
    }
}
