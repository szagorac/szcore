package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import com.xenaksys.szcore.event.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.WebScorePartRegEvent;
import com.xenaksys.szcore.event.WebScoreRemoveConnectionEvent;
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

    public void sendScoreInfo(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = new WebScoreState();
        scoreState.setScoreInfo(scoreInfo);
        sendScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public void sendPartInfo(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = new WebScoreState();
        scoreState.setPart(clientInfo.getInstrument());
        sendScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public void sendScoreState(String target, WebScoreTargetType targetType, WebScoreState scoreState) throws Exception {
        OutgoingWebEvent outEvent = eventFactory.createWebScoreOutEvent(null, null, OutgoingWebEventType.PUSH_SCORE_STATE, clock.getSystemTimeMillis());
        outEvent.addData(Consts.WEB_DATA_SCORE_STATE, scoreState);
        outEvent.addData(Consts.WEB_DATA_TARGET, target);
        outEvent.addData(Consts.WEB_DATA_TARGET_TYPE, targetType);
        scoreProcessor.onOutgoingWebEvent(outEvent);
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
