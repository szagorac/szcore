package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.osc.OscEventType;
import com.xenaksys.szcore.event.osc.PageDisplayEvent;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
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

    private final ScoreProcessorImpl scoreProcessor;
    private final EventFactory eventFactory;
    private final BasicScore score;
    private final Clock clock;
    private final Map<String, List<WebClientInfo>> instrumentClients = new ConcurrentHashMap<>();
    private final Map<String, WebClientInfo> clients = new ConcurrentHashMap<>();

    private WebScoreInfo scoreInfo;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public WebScore(ScoreProcessorImpl scoreProcessor, EventFactory eventFactory, Clock clock) {
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
        String scoreNameDir = info.getTitle().replaceAll("\\s+", "");
        String scoreDir = Consts.WEB_SCORE_ROOT_DIR + scoreNameDir + Consts.SLASH;
        info.setScoreDir(scoreDir);

        return info;
    }

    public void resetState() {
    }

    public void processInterceptedOscEvent(OscEvent event) {
        OscEventType oscEventType = event.getOscEventType();
        try {
            switch (oscEventType) {
                case PAGE_DISPLAY:
                    processPageDisplayEvent((PageDisplayEvent) event);
                case PAGE_MAP_DISPLAY:
                case ELEMENT_COLOR:
                    LOG.info("Received ELEMENT_COLOR event: {}", event);
                    break;
                case ELEMENT_ALPHA:
                    LOG.info("Received ELEMENT_ALPHA event: {}", event);
                    break;
                case HELLO:
                case SERVER_HELLO:
                case SET_TITLE:
                case SET_PART:
                case RESET_STAVES:
                case DATE_TICK:
                case STAVE_START_MARK:
                case STAVE_TICK_DY:
                case INSTRUMENT_RESET_SLOTS:
                case ELEMENT_Y_POSITION:
                default:
                    LOG.error("processInterceptedOscEvent: Unhandled event: {}", oscEventType);
            }
        } catch (Exception e) {
            LOG.error("publishToWebScoreHack: failed to publish web score event", e);
        }
    }

    private void processPageDisplayEvent(PageDisplayEvent event) throws Exception {
        String destination = event.getDestination();
        String fileName = event.getFilename();
        StaveId staveId = event.getStaveId();
        int staveNo = staveId.getStaveNo();
        PageId pageId = event.getPageId();
        String webPageId = "" + pageId.getPageNo();
        String webStaveId = null;
        switch (staveNo) {
            case 1:
                webStaveId = Consts.WEB_SCORE_STAVE_TOP;
                break;
            case 2:
                webStaveId = Consts.WEB_SCORE_STAVE_BOTTOM;
                break;
            default:
                LOG.error("processPageDisplayEvent: Unexpected stave number: " + staveNo);
        }
        sendPageInfo(destination, webPageId, fileName, webStaveId);
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

    public void sendPartInfo(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        String instrumentName = clientInfo.getInstrument();
        Instrument instrument = score.getInstrument(instrumentName);
        if (instrument == null) {
            LOG.error("sendPartInfo: unknown instrument: {}", instrumentName);
            return;
        }
        int firstPageNo = 1;
        int lastPageNo = 1;
        Page lastPage = score.getLastInstrumentPage(instrument.getId());
        if (lastPage != null) {
            lastPageNo = lastPage.getPageNo();
        }

        SequentalIntRange pageRange = new SequentalIntRange(firstPageNo, lastPageNo);
        ArrayList<IntRange> pageRanges = new ArrayList<>();
        pageRanges.add(pageRange);
        String imgDir = scoreInfo.getScoreDir() + Consts.RSRC_DIR;
        // ligetiTest6_Cello_page19.png
        String scoreNameToken = scoreInfo.getTitle().replaceAll("\\s+", "_");
        String imgPageNameToken = scoreNameToken
                + Consts.UNDERSCORE
                + instrumentName
                + Consts.UNDERSCORE
                + Consts.DEFAULT_PAGE_PREFIX
                + Consts.WEB_SCORE_PAGE_NO_TOKEN
                + Consts.PNG_FILE_EXTENSION;

        String imgContPageName = instrumentName
                + Consts.UNDERSCORE
                + Consts.CONTINUOUS_PAGE_NAME
                + Consts.PNG_FILE_EXTENSION;

        WebPartInfo partInfo = new WebPartInfo();
        partInfo.setName(instrumentName);
        partInfo.setPageRanges(pageRanges);
        partInfo.setImgDir(imgDir);
        partInfo.setImgPageNameToken(imgPageNameToken);
        partInfo.setImgContPageName(imgContPageName);
        partInfo.setContPageNo(Consts.CONTINUOUS_PAGE_NO);
        scoreState.setPartInfo(partInfo);
        scoreProcessor.sendWebScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public void sendPageInfo(String destination, String pageId, String filename, String staveId) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebPageInfo webPageInfo = new WebPageInfo();
        webPageInfo.setFilename(filename);
        webPageInfo.setStaveId(staveId);
        webPageInfo.setId(pageId);
        scoreState.setPageInfo(webPageInfo);
        sendToDestination(destination, scoreState);
    }

    private void sendToDestination(String destination, WebScoreState scoreState) throws Exception {
        if (Consts.ALL_DESTINATIONS.equals(destination)) {
            scoreProcessor.sendWebScoreState(Consts.ALL_DESTINATIONS, WebScoreTargetType.ALL, scoreState);
            return;
        }

        if (instrumentClients.containsKey(destination)) {
            List<WebClientInfo> clients = instrumentClients.get(destination);
            for (WebClientInfo clientInfo : clients) {
                scoreProcessor.sendWebScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
            }
            return;
        }

        if (clients.containsKey(destination)) {
            WebClientInfo client = clients.get(destination);
            scoreProcessor.sendWebScoreState(client.getClientAddr(), WebScoreTargetType.HOST, scoreState);
        }
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

    public boolean isDestination(String destination) {
        return clients.containsKey(destination) || instrumentClients.containsKey(destination) || Consts.ALL_DESTINATIONS.equals(destination);
    }
}
