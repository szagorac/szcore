package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.ScoreBuilderStrategy;
import com.xenaksys.szcore.algo.SectionAssignmentType;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.algo.StrategyType;
import com.xenaksys.szcore.algo.TranspositionStrategy;
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
import com.xenaksys.szcore.event.osc.OverlayTextEvent;
import com.xenaksys.szcore.event.osc.PageDisplayEvent;
import com.xenaksys.szcore.event.osc.PageMapDisplayEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOffEvent;
import com.xenaksys.szcore.event.osc.PrecountBeatOnEvent;
import com.xenaksys.szcore.event.osc.ResetScoreEvent;
import com.xenaksys.szcore.event.osc.ResetStavesEvent;
import com.xenaksys.szcore.event.osc.StaveStartMarkEvent;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartReadyEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectInstrumentSlotEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectSectionEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.model.Tempo;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.InscoreMapElement;
import com.xenaksys.szcore.score.OverlayElementType;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.web.overlay.WebOverlayFactory;
import com.xenaksys.szcore.score.web.overlay.WebOverlayProcessor;
import com.xenaksys.szcore.score.web.strategy.WebBuilderStrategy;
import com.xenaksys.szcore.score.web.strategy.WebStrategy;
import com.xenaksys.szcore.util.ParseUtil;
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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final BasicScore score;
    private final Clock clock;
    private final Map<String, List<WebClientInfo>> instrumentClients = new ConcurrentHashMap<>();
    private final Map<String, WebClientInfo> allClients = new ConcurrentHashMap<>();
    private final Map<String, WebClientInfo> playerClients = new ConcurrentHashMap<>();
    private final Map<StaveId, WebOverlayProcessor> overlayProcessors = new ConcurrentHashMap<>();
    private final Properties props;

    private WebScoreInfo scoreInfo;
    private WebStrategyInfo webStrategyInfo;
    private WebOverlayFactory webOverlayFactory;

    public WebScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock, Properties props, WebOverlayFactory webOverlayFactory) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.score = (BasicScore) scoreProcessor.getScore();
        this.props = props;
        this.webOverlayFactory = webOverlayFactory;
    }

    public void init() {
        this.scoreInfo = initScoreInfo();
        initWebStrategyInfo();
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

        String scoreConfigName = ParseUtil.removeAllWhitespaces(info.getTitle()).toLowerCase(Locale.ROOT);
        String configName = Consts.WEBSCORE_DIR_CONFIG_PREFIX + scoreConfigName;
        String scoreNameDir = props.getProperty(configName);
        if(scoreNameDir == null) {
            scoreNameDir = info.getTitle().replaceAll("\\s+", "");
        }
        String scoreDir = Consts.WEB_SCORE_ROOT_DIR + scoreNameDir + Consts.SLASH;
        info.setScoreDir(scoreDir);

        String partPageConfigName = Consts.WEBSCORE_PART_HTML_PREFIX + scoreConfigName;
        String partPageName = props.getProperty(partPageConfigName);
        if(partPageName == null) {
            partPageName = "zsPart.html";
        }
        info.setPartPageName(partPageName);

        return info;
    }

    private void initWebStrategyInfo() {
        webStrategyInfo = new WebStrategyInfo();
        ScoreBuilderStrategy builderStrategy = score.getScoreBuilderStrategy();
        if(builderStrategy != null) {
            for(String clientId : playerClients.keySet()) {
                builderStrategy.addClientId(clientId);
            }
            createOrUpdateWebBuilderStrategy(builderStrategy);
        }
    }

    public void resetState() {
    }

    public void resetInstrumentClients() {
        this.instrumentClients.clear();
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
                case RESET_SCORE:
                    processResetScore((ResetScoreEvent)event);
                    break;
                case OVERLAY_TEXT:
                    processOverlayText((OverlayTextEvent)event);
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

    private void processResetScore(ResetScoreEvent event) {
        instrumentClients.clear();
    }

    private void processOverlayText(OverlayTextEvent event) throws Exception {
        String destination = event.getDestination();
        OverlayType overlayType = event.getOverlayType();
        String l1 = event.getL1();
        String l2 = event.getL2();
        String l3 = event.getL3();
        StaveId staveId = event.getStaveId();
        boolean isVisible = event.isVisible();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        sendOverlayText(destination, l1, l2, l3, overlayType, isVisible, webStaveId);
    }

    private void processElementColour(ElementColorEvent event) throws Exception {
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

    private void processElementAlpha(ElementAlphaEvent event) throws Exception {
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
    private void processElementYPosition(ElementYPositionEvent event) throws Exception {
        String destination = event.getDestination();
        OverlayType overlayType = event.getOverlayType();
        StaveId staveId = event.getStaveId();
        long unscaledValue = event.getUnscaledValue();
        processElementYPosition(destination, staveId, overlayType,unscaledValue);
    }

    private void processElementYPosition(String destination, StaveId staveId, OverlayType overlayType, long unscaledValue) throws Exception {
        WebOverlayProcessor webOverlayProcessor = overlayProcessors.computeIfAbsent(staveId, s -> createWebProcessor());
        Double y = webOverlayProcessor.calculateValue(staveId, overlayType, unscaledValue);
        if(y == null) {
            return;
        }
        String webStaveId = WebUtil.getWebStaveId(staveId);
        sendOverlayLinePosition(destination, overlayType, webStaveId, y);
    }

    private WebOverlayProcessor createWebProcessor() {
        return webOverlayFactory.createOverlayProcessor();
    }

    private void processResetStaves(ResetStavesEvent event) throws Exception {
        String destination = event.getDestination();
        sendResetStaves(destination);
    }

    private void processInstrumentSlots(InstrumentSlotsEvent event) throws Exception {
        String destination = event.getDestination();
        String instrumentsCsv = event.getInstrumentsCsv();
        sendInstrumentSlots(destination, instrumentsCsv);
    }

    private void processInstrumentResetSlots(InstrumentResetSlotsEvent event) throws Exception {
        String destination = event.getDestination();
        sendResetInstrumentSlots(destination);
    }

    private void processStaveStartMark(StaveStartMarkEvent event) throws Exception {
        String destination = event.getDestination();
        StaveId staveId = event.getStaveId();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        int beatNo = event.getBeatNo();
        sendStaveStartMark(destination, webStaveId, beatNo);
    }

    private void processDateTick(DateTickEvent event) throws Exception {
        String destination = event.getDestination();
        StaveId staveId = event.getStaveId();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        int beatNo = event.getBeatNo();
        sendBeat(destination, webStaveId, beatNo);
    }

    private void processActivateStave(OscStaveActivateEvent event) throws Exception {
        String destination = event.getDestination();
        StaveId staveId = event.getStaveId();
        String webStaveId = WebUtil.getWebStaveId(staveId);
        boolean isActive = event.isActive();
        boolean isPlayStave = event.isPlayStave();
        sendActivateStave(destination, webStaveId, isActive, isPlayStave);
    }

    private void processStopEvent(OscStopEvent event) throws Exception {
        sendStop();
    }

    private void processPrecountBeatOff(PrecountBeatOffEvent event) throws Exception {
        int beaterNo = event.getBeaterNo();
        sendPrecountBeatOff(beaterNo);
    }

    private void processPrecountBeatOn(PrecountBeatOnEvent event) throws Exception {
        int beaterNo = event.getBeaterNo();
        int colourId = event.getColourId();
        sendPrecountBeatOn(beaterNo, colourId);
    }

    private void processTempoEvent(OscStaveTempoEvent event) throws Exception {
        String destination = event.getDestination();
        int bpm = event.getTempo();
        sendTempo(destination, bpm);
    }

    private void processPageMapEvent(PageMapDisplayEvent event) throws Exception {
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

    private void processPageDisplayEvent(PageDisplayEvent event) throws Exception {
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
        WebTranspositionInfo transpositionInfo = null;
        TranspositionStrategy transpositionStrategy = score.getTranspositionStrategy();
        if(transpositionStrategy != null) {
            PageId pageId = event.getPageId();
            transpositionInfo = transpositionStrategy.getWebTranspositionInfo(pageId, staveId);
        }

        sendPageInfo(destination, webPageId, webRndPageId, fileName, webStaveId, transpositionInfo);
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
            boolean isRegistered = allClients.containsKey(clientInfo.getClientAddr());
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
        allClients.put(clientInfo.getClientAddr(), clientInfo);
        addClientIdClient(clientInfo);
        addInstrumentClient(clientInfo);
    }

    private void addClientIdClient(WebClientInfo clientInfo) {
        String clientId = clientInfo.getClientId();
        if(clientId == null) {
            return;
        }
        addOrUpdateClient(clientInfo, clientId);
        ScoreBuilderStrategy builderStrategy = score.getScoreBuilderStrategy();
        if(builderStrategy != null) {
            builderStrategy.addClientId(clientId);
        }
    }

    private void addOrUpdateClient(WebClientInfo clientInfo, String clientId) {
        if(clientId == null) {
            return;
        }
        if(playerClients.containsKey(clientId)) {
            WebClientInfo currentClientInfo = playerClients.get(clientId);
            String addr = clientInfo.getClientAddr();
            if(addr != null && addr.equals(currentClientInfo.getClientAddr())) {
                return;
            }
        }
        playerClients.put(clientId, clientInfo);
    }

    public void addInstrumentClient(WebClientInfo clientInfo) throws Exception {
        String instrument = clientInfo.getInstrument();
        if (instrument == null) {
            ScoreBuilderStrategy builderStrategy = score.getScoreBuilderStrategy();
            if(builderStrategy == null) {
                return;
            }
            instrument = builderStrategy.getDefaultInstrument();
        }
        if (instrument == null) {
            return;
        }
        addInstrumentClient(instrument, clientInfo);
    }

    public void addInstrumentClient(String instrument, WebClientInfo clientInfo) throws Exception {
        if (instrument == null || clientInfo == null) {
            return;
        }
        if(!instrument.equals(clientInfo.getInstrument())) {
            clientInfo.setInstrument(instrument);
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
        sendScoreInfo(WebScoreTargetType.ALL.name(), WebScoreTargetType.ALL);
    }

    public void sendScoreInfo(WebClientInfo clientInfo) throws Exception {
        sendScoreInfo(clientInfo.getClientAddr(), WebScoreTargetType.HOST);
    }

    public void sendScoreInfo(String addr, WebScoreTargetType targetType) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setScoreInfo(scoreInfo);
        scoreState.setStrategyInfo(webStrategyInfo);
        scoreProcessor.sendWebScoreState(addr, targetType, scoreState);
    }

    public void sendPartInfo(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = createPartInfoUpdate(clientInfo);
        scoreProcessor.sendWebScoreState(clientInfo.getClientAddr(), WebScoreTargetType.HOST, scoreState);
    }

    public WebScoreState createPartInfoUpdate(WebClientInfo clientInfo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        String instrumentName = clientInfo.getInstrument();
        Instrument instrument = score.getInstrument(instrumentName);
        if (instrument == null) {
            LOG.warn("createPartInfoUpdate: unknown instrument: {}", instrumentName);
            return null;
        }
        int firstPageNo = 1;
        int lastPageNo = 1;
        Page lastPage = score.getLastInstrumentPage(instrument.getId());
        if (lastPage != null) {
            lastPageNo = lastPage.getPageNo();
        }

        String section = null;
        ScoreBuilderStrategy scoreBuilderStrategy = score.getScoreBuilderStrategy();
        if(scoreBuilderStrategy != null) {
            section = scoreBuilderStrategy.getCurrentSection();
            SectionInfo sectionInfo = scoreBuilderStrategy.getSectionInfo(section);
            if(sectionInfo != null) {
                IntRange pageRange = sectionInfo.getPageRange();
                if(pageRange != null) {
                    firstPageNo = pageRange.getStart();
                    lastPageNo = pageRange.getEnd();
                }
            }
        }

        boolean isNoScoreInstrument = scoreProcessor.isNoScoreInstrument(instrumentName);
        SequentalIntRange pageRange = new SequentalIntRange(firstPageNo, lastPageNo);
        ArrayList<IntRange> pageRanges = new ArrayList<>();
        pageRanges.add(pageRange);
        String imgDir = scoreInfo.getScoreDir() + Consts.RSRC_DIR;

        String imgContPageName = instrumentName
                + Consts.UNDERSCORE
                + Consts.CONTINUOUS_PAGE_NAME
                + Consts.PNG_FILE_EXTENSION;

        String imgPageNameToken;
        if(isNoScoreInstrument) {
            imgPageNameToken = imgContPageName;
        } else {
            String scoreNameToken = scoreInfo.getTitle().replaceAll("\\s+", "_");
            imgPageNameToken = scoreNameToken
                    + Consts.UNDERSCORE
                    + instrumentName
                    + Consts.UNDERSCORE
                    + Consts.DEFAULT_PAGE_PREFIX
                    + Consts.WEB_SCORE_PAGE_NO_TOKEN
                    + Consts.PNG_FILE_EXTENSION;
        }

        WebPartInfo partInfo = new WebPartInfo();
        partInfo.setName(instrumentName);
        partInfo.setPageRanges(pageRanges);
        partInfo.setImgDir(imgDir);
        partInfo.setImgPageNameToken(imgPageNameToken);
        partInfo.setImgContPageName(imgContPageName);
        partInfo.setContPageNo(Consts.CONTINUOUS_PAGE_NO);
        partInfo.setCurrentSection(section);
        scoreState.setPartInfo(partInfo);
        return scoreState;
    }

    public void sendPageInfo(String destination, String pageId, String webRndPageId, String filename, String staveId, WebTranspositionInfo transpositionInfo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebPageInfo webPageInfo = new WebPageInfo();
        webPageInfo.setFilename(filename);
        webPageInfo.setStaveId(staveId);
        webPageInfo.setPageId(pageId);
        webPageInfo.setRndPageId(webRndPageId);
        if(transpositionInfo != null) {
            webPageInfo.setTranspositionInfo(transpositionInfo);
        }
        scoreState.setPageInfo(webPageInfo);
        sendToDestination(destination, scoreState);
    }

    public void sendStrategyInfo() throws Exception {
        sendStrategyInfo(WebScoreTargetType.ALL.name(), WebScoreTargetType.ALL);
    }

    public void sendStrategyInfo(String addr, WebScoreTargetType targetType) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setStrategyInfo(webStrategyInfo);
        scoreProcessor.sendWebScoreState(addr, targetType, scoreState);
    }

    private void sendTimeSpaceMapInfo(String destination, String webPageId, String webStaveId, List<InscoreMapElement> mapElements) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebTimeSpaceMapInfo mapInfo = new WebTimeSpaceMapInfo();
        mapInfo.setPageId(webPageId);
        mapInfo.setStaveId(webStaveId);
        mapInfo.setMap(mapElements);
        scoreState.setTimeSpaceMapInfo(mapInfo);
        sendToDestination(destination, scoreState);
    }

    private void sendTempo(String destination, int bpm) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        scoreState.setBpm(bpm);
        sendToDestination(destination, scoreState);
    }

    private void sendPrecountBeatOn(int beaterNo, int colourId) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_LIGHT_NO, beaterNo);
        params.put(Consts.WEB_PARAM_COLOUR_ID, colourId);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.SEMAPHORE_ON, null, params);
        scoreState.addAction(action);
        sendToDestination(Consts.ALL_DESTINATIONS, scoreState);
    }

    private void sendPrecountBeatOff(int beaterNo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_LIGHT_NO, beaterNo);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.SEMAPHORE_OFF, null, params);
        scoreState.addAction(action);
        sendToDestination(Consts.ALL_DESTINATIONS, scoreState);
    }

    private void sendStop() throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.STOP, null, null);
        scoreState.addAction(action);
        sendToDestination(Consts.ALL_DESTINATIONS, scoreState);
    }
    private void sendActivateStave(String destination, String webStaveId, boolean isActive, boolean isPlayStave) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_IS_ACTIVE, isActive);
        params.put(Consts.WEB_PARAM_IS_PLAY, isPlayStave);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.ACTIVATE, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendBeat(String destination, String webStaveId, int beatNo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = Collections.singletonMap(Consts.WEB_PARAM_BEAT_NO, beatNo);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.BEAT, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendStaveStartMark(String destination, String webStaveId, int beatNo) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = Collections.singletonMap(Consts.WEB_PARAM_BEAT_NO, beatNo);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.START_MARK, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendInstrumentSlots(String destination, String instrumentsCsv) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        Map<String, Object> params = Collections.singletonMap(Consts.WEB_PARAM_CSV_INSTRUMENTS, instrumentsCsv);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.INSTRUMENT_SLOTS, null, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendResetInstrumentSlots(String destination) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.RESET_INSTRUMENT_SLOTS, null, null);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendResetStaves(String destination) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.RESET_STAVES, null, null);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendOverlayLinePosition(String destination, OverlayType overlayType, String webStaveId, Double lineY) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(2);
        params.put(Consts.WEB_PARAM_OVERLAY_TYPE, overlayType.name());
        params.put(Consts.WEB_PARAM_OVERLAY_LINE_Y, lineY);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.OVERLAY_LINE, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendOverlayElementInfo(String destination, OverlayType overlayType, OverlayElementType overlayElementType, boolean isEnabled, String webStaveId, Double opacity) throws Exception {
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

    private void sendOverlayText(String destination, String l1, String l2, String l3, OverlayType overlayType, boolean isVisible, String webStaveId) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(4);
        params.put(Consts.WEB_PARAM_OVERLAY_TYPE, overlayType.name());
        params.put(Consts.WEB_PARAM_OVERLAY_ELEMENT, OverlayElementType.PITCH_TEXT.name());
        params.put(Consts.WEB_PARAM_IS_ENABLED, isVisible);
        params.put(Consts.WEB_PARAM_TEXT_L1, l1);
        params.put(Consts.WEB_PARAM_TEXT_L2, l2);
        params.put(Consts.WEB_PARAM_TEXT_L3, l3);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.OVERLAY_TEXT, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendOverlayColour(String destination, OverlayType overlayType, String webStaveId, String colHex) throws Exception {
        WebScoreState scoreState = scoreProcessor.getOrCreateWebScoreState();
        List<String> targets = Collections.singletonList(webStaveId);
        Map<String, Object> params = new HashMap<>(4);
        params.put(Consts.WEB_PARAM_OVERLAY_TYPE, overlayType.name());
        params.put(Consts.WEB_PARAM_COLOUR, colHex);
        WebScoreAction action = scoreProcessor.getOrCreateWebScoreAction(WebScoreActionType.OVERLAY_COLOUR, targets, params);
        scoreState.addAction(action);
        sendToDestination(destination, scoreState);
    }

    private void sendToDestination(String destination, WebScoreState scoreState) throws Exception {
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

        if (playerClients.containsKey(destination)) {
            WebClientInfo client = playerClients.get(destination);
            scoreProcessor.sendWebScoreState(client.getClientAddr(), WebScoreTargetType.HOST, scoreState);
        }

        if (allClients.containsKey(destination)) {
            WebClientInfo client = allClients.get(destination);
            scoreProcessor.sendWebScoreState(client.getClientAddr(), WebScoreTargetType.HOST, scoreState);
        }
    }

    public void processRemoveConnectionEvent(WebScoreRemoveConnectionEvent webEvent) {
        LOG.info("processRemoveConnectionEvent");
        if(webEvent == null || webEvent.getConnectionIds() == null || webEvent.getConnectionIds().isEmpty()) {
            return;
        }
        List<String> toRemove = webEvent.getConnectionIds();
        for(String connId : toRemove) {
            WebClientInfo removed = allClients.remove(connId);
            if(removed != null) {
                playerClients.remove(connId);
                for(String instrument : instrumentClients.keySet()) {
                    List<WebClientInfo> instClients = instrumentClients.get(instrument);
                    instClients.remove(removed);
                }
            }
        }
    }

    public void processInEvent(WebScoreInEvent event) {
        try {
            String sourceAddr = event.getSourceAddr();
            if(sourceAddr == null) {
                return;
            }
            WebClientInfo clientInfo = allClients.get(sourceAddr);
            if (clientInfo == null) {
                LOG.debug("processInEvent: Unknown client: {}", sourceAddr);
                return;
            }
            String eventClientId = event.getClientId();
            if (eventClientId == null) {
                LOG.debug("processInEvent: invalid client ID");
                return;
            }
            if(!eventClientId.equals(clientInfo.getClientId())) {
                clientInfo.setClientId(eventClientId);
            }
            addOrUpdateClient(clientInfo, eventClientId);
        } catch (Exception e) {
            LOG.error("processInEvent: failed to process web in event", e);
        }
    }

    public void processPartRegistration(WebScorePartRegEvent event) {
        try {
            String clientId = event.getSourceAddr();
            WebClientInfo clientInfo = allClients.get(clientId);
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
            WebClientInfo clientInfo = allClients.get(clientId);
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
            WebClientInfo clientInfo = getClientInfo(webEvent);
            scoreProcessor.processSelectInstrumentSlot(slotNo, slotInstrument, sourceInst, clientInfo);
        } catch (Exception e) {
            LOG.error("processSelectInstrumentSlot: failed to process select intrument slot", e);
        }
    }

    public void processSelectSection(WebScoreSelectSectionEvent event) {
        try {
            WebClientInfo clientInfo = getClientInfo(event);
            if (clientInfo == null) {
                LOG.error("processSelectSection: Unknown client source: {} id: {}", event.getSourceAddr(), event.getClientId());
                return;
            }
            String section = event.getSection();
            scoreProcessor.processSelectSection(section, clientInfo);
            ScoreBuilderStrategy scoreBuilderStrategy = score.getScoreBuilderStrategy();
            createOrUpdateWebBuilderStrategy(scoreBuilderStrategy);
            sendStrategyInfo();
        } catch (Exception e) {
            LOG.error("processSelectInstrumentSlot: failed to process select intrument slot", e);
        }
    }

    public void createOrUpdateWebBuilderStrategy(ScoreBuilderStrategy scoreBuilderStrategy) {
        if(scoreBuilderStrategy == null) {
            return;
        }
        WebBuilderStrategy webBuilderStrategy = getOrCreateWebBuilderStrategy();
        webBuilderStrategy.setName(StrategyType.BUILDER.name());
        webBuilderStrategy.setReady(scoreBuilderStrategy.isReady());
        SectionAssignmentType assignmentType = scoreBuilderStrategy.getConfig().getAssignmentType();
        if (assignmentType != null) {
            webBuilderStrategy.setAssignmentType(assignmentType.name());
        }
        List<String> sections = scoreBuilderStrategy.getSections();
        webBuilderStrategy.clearSectionOwners();
        if(sections != null) {
            webBuilderStrategy.setSections(sections);
            for(String section : sections) {
                String owner = scoreBuilderStrategy.getSectionOwner(section);
                if(owner != null) {
                    webBuilderStrategy.addSectionOwner(section, owner);
                }
            }
        }
    }

    private WebBuilderStrategy getWebBuilderStrategy() {
        List<WebStrategy> webStrategies = webStrategyInfo.getStrategies();
        if(webStrategies == null) {
            return null;
        }
        for (WebStrategy strategy : webStrategies) {
            if (strategy instanceof WebBuilderStrategy) {
                return (WebBuilderStrategy) strategy;
            }
        }
        return null;
    }

    private WebBuilderStrategy getOrCreateWebBuilderStrategy() {
        WebBuilderStrategy webBuilderStrategy = getWebBuilderStrategy();
        if(webBuilderStrategy != null) {
            return webBuilderStrategy;
        }
        webBuilderStrategy = new WebBuilderStrategy();
        webStrategyInfo.addStrategy(webBuilderStrategy);
        return webBuilderStrategy;
    }

    private void setBuilderWebStrategyReady(boolean isReady) {
        WebBuilderStrategy webBuilderStrategy = getOrCreateWebBuilderStrategy();
        webBuilderStrategy.setReady(isReady);
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
        return allClients.containsKey(destination) ||
                playerClients.containsKey(destination) ||
                instrumentClients.containsKey(destination) ||
                Consts.ALL_DESTINATIONS.equals(destination);
    }

    public WebClientInfo getClientInfo(WebScoreInEvent webEvent) {
        String clientId = webEvent.getClientId();
        if(clientId != null) {
            WebClientInfo clientInfo = playerClients.get(clientId);
            if(clientInfo != null) {
                return clientInfo;
            }
        }

        clientId = webEvent.getSourceAddr();
        return allClients.get(clientId);
    }

    public List<WebClientInfo> getScoreClients() {
        List<WebClientInfo> scoreClients = new ArrayList<>();
        for(WebClientInfo player : playerClients.values()) {
            if(player.isScoreClient()) {
                scoreClients.add(player);
            }
        }
        return scoreClients;
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

    public void setSectionInstrumentClients(Map<String, List<String>> instrumentClients){
        try {
            if(instrumentClients == null) {
                return;
            }
            resetInstrumentClients();
            for(String instrument : instrumentClients.keySet()) {
                List<String> clients = instrumentClients.get(instrument);
                for(String client : clients) {
                    WebClientInfo clientInfo = playerClients.get(client);
                    if(clientInfo == null) {
                        clientInfo = allClients.get(client);
                    }
                    if(clientInfo == null) {
                        continue;
                    }
                    if(!instrument.equals(clientInfo.getInstrument())) {
                        clientInfo.setInstrument(instrument);
                    }
                    addOrUpdateClientInfo(clientInfo);
                }
            }
        } catch (Exception e) {
            LOG.error("setSectionInstrumentClients: failed to add section instrument clients", e);
        }
    }
}
