package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import com.xenaksys.szcore.event.WebScoreConnectionEvent;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final BasicScore score;
    private final Clock clock;
    private final Map<String, WebClientInfo> instrumentClient = new ConcurrentHashMap<>();
    private final Set<WebClientInfo> clients = new HashSet<>();

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
            String target = clientInfo.getClientAddr();
            WebScoreTargetType targetType = WebScoreTargetType.HOST;

            clients.add(clientInfo);
            String instrument = clientInfo.getInstrument();
            if (instrument != null) {
                instrumentClient.put(instrument, clientInfo);
                target = instrument;
                targetType = WebScoreTargetType.INSTRUMENT;
            }
            WebScoreState scoreState = new WebScoreState();
            scoreState.setScoreInfo(scoreInfo);
            OutgoingWebEvent outEvent = eventFactory.createWebScoreOutEvent(null, null, OutgoingWebEventType.PUSH_SCORE_STATE, clock.getSystemTimeMillis());
            outEvent.addData(Consts.WEB_DATA_SCORE_STATE, scoreState);
            outEvent.addData(Consts.WEB_DATA_TARGET, target);
            outEvent.addData(Consts.WEB_DATA_TARGET_TYPE, targetType);
            scoreProcessor.onOutgoingWebEvent(outEvent);
        } catch (Exception e) {
            LOG.error("processConnectionEvent: failed to process score connection", e);
        }
    }
}
