package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.DateTickEvent;
import com.xenaksys.szcore.event.osc.ElementAlphaEvent;
import com.xenaksys.szcore.event.osc.ElementColorEvent;
import com.xenaksys.szcore.event.osc.ElementYPositionEvent;
import com.xenaksys.szcore.event.osc.InstrumentResetSlotsEvent;
import com.xenaksys.szcore.event.osc.InstrumentSlotsEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.osc.OscEventType;
import com.xenaksys.szcore.event.osc.OscStaveActivateEvent;
import com.xenaksys.szcore.event.osc.OscStaveTempoEvent;
import com.xenaksys.szcore.event.osc.OscStopEvent;
import com.xenaksys.szcore.event.osc.PageDisplayEvent;
import com.xenaksys.szcore.event.osc.PageMapDisplayEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOffEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOnEvent;
import com.xenaksys.szcore.event.osc.ResetStavesEvent;
import com.xenaksys.szcore.event.osc.StaveStartMarkEvent;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartReadyEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectInstrumentSlotEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.InscoreMapElement;
import com.xenaksys.szcore.score.OverlayElementType;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.ScoreProcessorImpl;
import com.xenaksys.szcore.util.WebUtil;
import com.xenaksys.szcore.web.WebClientInfo;
import com.xenaksys.szcore.web.WebScoreAction;
import com.xenaksys.szcore.web.WebScoreActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    private final Map<StaveId, OverlayProcessor> overlayProcessors = new ConcurrentHashMap<>();

    private WebScoreInfo scoreInfo;

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
                    break;
                case PAGE_MAP_DISPLAY:
                    processPageMapEvent((PageMapDisplayEvent) event);
                    break;
                case STAVE_TEMPO:
                    processTempoEvent((OscStaveTempoEvent) event);
                    break;
                case PRECOUNT_BEAT_ON:
                    processPrecountBeatOn((PrecountBeatOnEvent) event);
                    break;
                case PRECOUNT_BEAT_OFF:
                    processPrecountBeatOff((PrecountBeatOffEvent) event);
                    break;
                case STOP:
                    processStopEvent((OscStopEvent) event);
                    break;
                case STAVE_ACTIVATE:
                    processActivateStave((OscStaveActivateEvent) event);
                    break;
                case DATE_TICK:
                    processDateTick((DateTickEvent) event);
                    break;
                case STAVE_START_MARK:
                    processStaveStartMark((StaveStartMarkEvent) event);
                    break;
                case INSTRUMENT_SLOTS:
                    processInstrumentSlots((InstrumentSlotsEvent) event);
                    break;
                case INSTRUMENT_RESET_SLOTS:
                    processInstrumentResetSlots((InstrumentResetSlotsEvent) event);
                    break;
                case RESET_STAVES:
                    processResetStaves((ResetStavesEvent) event);
                    break;
                case ELEMENT_Y_POSITION:
                    processElementYPosition((ElementYPositionEvent) event);
                    break;
                case ELEMENT_ALPHA:
                    processElementAlpha((ElementAlphaEvent) event);
                    break;
                case ELEMENT_COLOR:
                    processElementColour((ElementColorEvent) event);
                    break;
                case STAVE_TICK_DY:
                case HELLO:
                case SERVER_HELLO:
                case SET_TITLE:
                case SET_PART:
                    break;
                default:
                    LOG.error("processInterceptedOscEvent: Unhandled event: {}", oscEventType);
            }
        } catch (Exception e) {
            LOG.error("publishToWebScoreHack: failed to publish web score event", e);
        }
    }

    private void processElementColour(ElementColorEvent event) {
        String destination = event.getDestination();
        OverlayType overlayType = event.getOverlayType();
        StaveId staveId = event.getStaveId();
        int r = event.getR();
        int g = event.getG();
        int b = event.getB();
        String colHex = WebUtil.rgbToHex(r,g,b);
        String webStaveId = WebUtil.getWebStaveId(staveId);
        sendOverlayColour(destination, overlayType, webStaveId, colHex);
    }

    private void processElementAlpha(ElementAlphaEvent event) {
        String destination = event.getDestination();
        OverlayType overlayType = event.getOverlayType();
        OverlayElementType overlayElementType = event.getOverlayElementType();
        StaveId staveId = event.getStaveId();
        boolean isEnabled = event.isEnabled();
        int alpha = event.getAlpha();
        Double opacity = WebUtil.convertToOpacity(alpha);
        String webStaveId = WebUtil.getWebStaveId(staveId);
        sendOverlayElementInfo(destination, overlayType, overlayElementType, isEnabled, webStaveId, opacity);
    }
    private void processElementYPosition(ElementYPositionEvent event) {
        String destination = event.getDestination();
        OverlayType overlayType = event.getOverlayType();
        StaveId staveId = event.getStaveId();
        long unscaledValue = event.getUnscaledValue();
        processElementYPosition(destination, staveId, overlayType,unscaledValue);
    }

    private void processElementYPosition(String destination, StaveId staveId, OverlayType overlayType, long unscaledValue) {
        OverlayProcessor overlayProcessor = overlayProcessors.computeIfAbsent(staveId, s -> new OverlayProcessor());
        Double y = overlayProcessor.calculateValue(staveId, overlayType, unscaledValue);
        if(y == null) {
            return;
        }
        String webStaveId = WebUtil.getWebStaveId(staveId);
        sendOverlayLinePosition(destination, overlayType, webStaveId, y);
    }

    private void processResetStaves(ResetStavesEvent event) {
        String destination = event.getDestination();
        sendResetStaves(destination);
    }

    private void processInstrumentSlots(InstrumentSlotsEvent event) {
        String destination = event.getDestination();
        String instrumentsCsv = event.getInstrumentsCsv();
        sendInstrumentSlots(destination, instrumentsCsv);
    }

    private void processInstrumentResetSlots(InstrumentResetSlotsEvent event) {
        String destination = event.getDestination();
        sendResetInstrumentSlots(destination);
    }

    private void processStaveStartMark(StaveStartMarkEvent event) {
        String destination = event.getDestination();
        StaveId staveId = event.getStaveId();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        int beatNo = event.getBeatNo();
        sendStaveStartMark(destination, webStaveId, beatNo);
    }

    private void processDateTick(DateTickEvent event) {
        String destination = event.getDestination();
        StaveId staveId = event.getStaveId();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        int beatNo = event.getBeatNo();
        sendBeat(destination, webStaveId, beatNo);
    }

    private void processActivateStave(OscStaveActivateEvent event) {
        String destination = event.getDestination();
        StaveId staveId = event.getStaveId();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        boolean isActive = event.isActive();
        boolean isPlayStave = event.isPlayStave();
        sendActivateStave(destination, webStaveId, isActive, isPlayStave);
    }

    private void processStopEvent(OscStopEvent event) {
        sendStop();
    }

    private void processPrecountBeatOff(PrecountBeatOffEvent event) {
        int beaterNo = event.getBeaterNo();
        sendPrecountBeatOff(beaterNo);
    }

    private void processPrecountBeatOn(PrecountBeatOnEvent event) {
        int beaterNo = event.getBeaterNo();
        int colourId = event.getColourId();
        sendPrecountBeatOn(beaterNo, colourId);
    }

    private void processTempoEvent(OscStaveTempoEvent event) {
        String destination = event.getDestination();
        int bpm = event.getTempo();
        sendTempo(destination, bpm);
    }

    private void processPageMapEvent(PageMapDisplayEvent event) {
        String destination = event.getDestination();
        List<InscoreMapElement> mapElements = event.getMapElements();
        if(mapElements == null) {
            LOG.error("processPageMapEvent: invalid web timespace map");
            return;
        }
        String webPageId = getWebPageId(event.getPageId());
        String webStaveId = WebUtil.getWebStaveId(event.getStaveId());
        sendTimeSpaceMapInfo(destination, webPageId, webStaveId, mapElements);
    }

    private void processPageDisplayEvent(PageDisplayEvent event) {
        String destination = event.getDestination();
        String fileName = event.getFilename();
        StaveId staveId = event.getStaveId();
        String webPageId = getWebPageId(event.getPageId());
        String webStaveId = WebUtil.getWebStaveId(staveId);
        PageId rndPageId = event.getRndPageId();
        String webRndPageId = null;
        if(rndPageId != null) {
            webRndPageId = getWebPageId(rndPageId);
        }
        sendPageInfo(destination, webPageId, webRndPageId, fileName, webStaveId);
    }

    public String getWebPageId(PageId pageId) {
        return Consts.WEB_SCORE_PAGE_PREFIX + pageId.getPageNo();
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

    private void addOrUpdateClientInfo(WebClientInfo clientInfo)  {
        if (clientInfo == null) {
            return;
        }
        clients.put(clientInfo.getClientAddr(), clientInfo);
        addInstrumentClient(clientInfo);
    }

    private void addInstrumentClient(WebClientInfo clientInfo) {
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

    public void sendScoreInfo() {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setScoreInfo(scoreInfo);
        scoreProcessor.sendWebScoreState(WebScoreTargetType.ALL.name(), WebScoreTargetType.ALL, scoreState);
    }

    public void sendScoreInfo(WebClientInfo clientInfo) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setScoreInfo(scoreInfo);
        scoreProcessor.sendWebScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public void sendPartInfo(WebClientInfo clientInfo) {
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

    public void sendPageInfo(String destination, String pageId, String webRndPageId, String filename, String staveId) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebPageInfo webPageInfo = new WebPageInfo();
        webPageInfo.setFilename(filename);
        webPageInfo.setStaveId(staveId);
        webPageInfo.setPageId(pageId);
        webPageInfo.setRndPageId(webRndPageId);
        scoreState.setPageInfo(webPageInfo);
        sendToDestination(destination, scoreState);
    }


    private void sendTimeSpaceMapInfo(String destination, String webPageId, String webStaveId, List<InscoreMapElement> mapElements) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebTimeSpaceMapInfo mapInfo = new WebTimeSpaceMapInfo();
        mapInfo.setPageId(webPageId);
        mapInfo.setStaveId(webStaveId);
        mapInfo.setMap(mapElements);
        scoreState.setTimeSpaceMapInfo(mapInfo);
        sendToDestination(destination, scoreState);
    }

    private void sendTempo(String destination, int bpm)  {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setBpm(bpm);
        sendToDestination(destination, scoreState);
    }

    private void sendPrecountBeatOn(int beaterNo, int colourId) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_LIGHT_NO, beaterNo);
        params.put(Consts.WEB_PARAM_COLOUR_ID, colourId);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.SEMAPHORE_ON, null, params);
        scoreState.addAction(action);
        sendToDestination(Consts.ALL_DESTINATIONS, scoreState);
    }

    private void sendPrecountBeatOff(int beaterNo) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_LIGHT_NO, beaterNo);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.SEMAPHORE_OFF, null, params);
        scoreState.addAction(action);
        sendToDestination(Consts.ALL_DESTINATIONS, scoreState);
    }

    private void sendStop() {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.STOP, null, null);
        scoreState.addAction(action);
        sendToDestination(Consts.ALL_DESTINATIONS, scoreState);
    }
    private void sendActivateStave(String destination, String webStaveId, boolean isActive, boolean isPlayStave) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_IS_ACTIVE, isActive);
        params.put(Consts.WEB_PARAM_IS_PLAY, isPlayStave);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.ACTIVATE, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendBeat(String destination, String webStaveId, int beatNo) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = Collections.singletonMap(Consts.WEB_PARAM_BEAT_NO, beatNo);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.BEAT, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendStaveStartMark(String destination, String webStaveId, int beatNo) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = Collections.singletonMap(Consts.WEB_PARAM_BEAT_NO, beatNo);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.START_MARK, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendInstrumentSlots(String destination, String instrumentsCsv) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        Map<String, Object> params = Collections.singletonMap(Consts.WEB_PARAM_CSV_INSTRUMENTS, instrumentsCsv);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.INSTRUMENT_SLOTS, null, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendResetInstrumentSlots(String destination) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.RESET_INSTRUMENT_SLOTS, null, null);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendResetStaves(String destination) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.RESET_STAVES, null, null);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendOverlayLinePosition(String destination, OverlayType overlayType, String webStaveId, Double lineY) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_OVERLAY_TYPE, overlayType.name());
        params.put(Consts.WEB_PARAM_OVERLAY_LINE_Y, lineY);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.OVERLAY_LINE, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendOverlayElementInfo(String destination, OverlayType overlayType, OverlayElementType overlayElementType, boolean isEnabled, String webStaveId, Double opacity) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(4);
        params.put(Consts.WEB_PARAM_OVERLAY_TYPE, overlayType.name());
        params.put(Consts.WEB_PARAM_OVERLAY_ELEMENT, overlayElementType.name());
        params.put(Consts.WEB_PARAM_IS_ENABLED, isEnabled);
        params.put(Consts.WEB_PARAM_OPACITY, opacity);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.OVERLAY_ELEMENT, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendOverlayColour(String destination, OverlayType overlayType, String webStaveId, String colHex) {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(4);
        params.put(Consts.WEB_PARAM_OVERLAY_TYPE, overlayType.name());
        params.put(Consts.WEB_PARAM_COLOUR, colHex);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.OVERLAY_COLOUR, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendToDestination(String destination, WebScoreState scoreState) {
        if (Consts.ALL_DESTINATIONS.equals(destination)) {
            scoreProcessor.sendWebScoreState(Consts.ALL_DESTINATIONS, WebScoreTargetType.ALL, scoreState);
            return;
        }
        if (Consts.DEFAULT_OSC_PORT_NAME.equals(destination)) {
            LOG.warn("sendToDestination: Unexpected destination: {}, sending to all", destination);
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

    public void processPartReady(WebScorePartReadyEvent event) {
        try {
            String clientId = event.getSourceAddr();
            WebClientInfo clientInfo = clients.get(clientId);
            if (clientInfo == null) {
                LOG.error("processPartReady: Unknown client: {}", clientId);
                return;
            }
            clientInfo.setReady(true);
            String instrument = event.getPart();
            if (instrument == null) {
                return;
            }
            WebClientInfo instrumentClient = getInstrumentClient(instrument, clientId);
            if(instrumentClient == null) {
                LOG.error("processPartReady: Can not find instrument client");
                addInstrumentClient(clientInfo);
                instrumentClient = clientInfo;
            }
            instrumentClient.setReady(true);
        } catch (Exception e) {
            LOG.error("processPartRegistration: failed to process part registration", e);
        }
    }

    public void processSelectInstrumentSlot(WebScoreSelectInstrumentSlotEvent webEvent) {
        try {
            int slotNo = webEvent.getSlotNo();
            String slotInstrument = webEvent.getSlotInstrument();
            String sourceInst = webEvent.getPart();
            scoreProcessor.processSelectInstrumentSlot(slotNo, slotInstrument, sourceInst);
        } catch (Exception e) {
            LOG.error("processSelectInstrumentSlot: failed to process select intrument slot", e);
        }
    }

    public WebClientInfo getInstrumentClient(String instrument, String addr) {
        if(instrument == null || addr == null) {
            return null;
        }
        List<WebClientInfo> clients = instrumentClients.get(instrument);
        for (WebClientInfo clientInfo : clients) {
            if(addr.equals(clientInfo.getClientAddr())) {
                return clientInfo;
            }
        }
        return null;
    }

    public List<WebClientInfo> getInstrumentClients(String instrument) {
        if(instrument == null) {
            return null;
        }
        return instrumentClients.get(instrument);
    }

    public boolean isDestination(String destination) {
        return clients.containsKey(destination) || instrumentClients.containsKey(destination) || Consts.ALL_DESTINATIONS.equals(destination);
    }

    public boolean isReady() {
        for(String id : instrumentClients.keySet()) {
            List<WebClientInfo> clientInfos = instrumentClients.get(id);
            for(WebClientInfo clientInfo : clientInfos) {
                if (!clientInfo.isReady()) {
                    return false;
                }
            }
        }
        return true;
    }
}
