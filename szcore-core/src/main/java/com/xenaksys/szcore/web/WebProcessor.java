package com.xenaksys.szcore.web;

import com.google.gson.Gson;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.event.gui.WebAudienceClientInfoUpdateEvent;
import com.xenaksys.szcore.event.gui.WebScoreClientInfoUpdateEvent;
import com.xenaksys.szcore.event.osc.ElementSelectedAudienceEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEventType;
import com.xenaksys.szcore.event.web.audience.UpdateWebAudienceConnectionsEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceRequestLogEvent;
import com.xenaksys.szcore.event.web.audience.WebPollAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebStartAudienceEvent;
import com.xenaksys.szcore.event.web.in.UpdateWebScoreConnectionsEvent;
import com.xenaksys.szcore.event.web.in.WebScoreConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEvent;
import com.xenaksys.szcore.event.web.in.WebScoreInEventType;
import com.xenaksys.szcore.event.web.in.WebScorePartReadyEvent;
import com.xenaksys.szcore.event.web.in.WebScorePartRegEvent;
import com.xenaksys.szcore.event.web.in.WebScoreRemoveConnectionEvent;
import com.xenaksys.szcore.event.web.in.WebScoreSelectInstrumentSlotEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEventType;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventReceiver;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.HistoBucketView;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.ZsResponseType;
import com.xenaksys.szcore.net.browser.BrowserOS;
import com.xenaksys.szcore.net.browser.BrowserType;
import com.xenaksys.szcore.net.browser.UAgentInfo;
import com.xenaksys.szcore.score.web.WebScoreInfo;
import com.xenaksys.szcore.score.web.WebScoreState;
import com.xenaksys.szcore.score.web.WebScoreTargetType;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.util.Histogram;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.ParseUtil;
import com.xenaksys.szcore.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.SPACE;
import static com.xenaksys.szcore.Consts.WEB_EVENT_ELEMENT_ID;
import static com.xenaksys.szcore.Consts.WEB_EVENT_IS_POLL_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_IS_SELECTED;
import static com.xenaksys.szcore.Consts.WEB_EVENT_LAST_STATE_UPDATE_TIME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_PART;
import static com.xenaksys.szcore.Consts.WEB_EVENT_SENT_TIME_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_SERVER_TIME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_SLOT_INSTRUMENT;
import static com.xenaksys.szcore.Consts.WEB_EVENT_SLOT_NO;
import static com.xenaksys.szcore.Consts.WEB_EVENT_TIME_NAME;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_MESSAGE;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_STATE;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_SUBMITTED;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_TIME;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_TYPE;
import static com.xenaksys.szcore.event.EventType.WEB_AUDIENCE_IN;
import static com.xenaksys.szcore.event.EventType.WEB_SCORE_IN;

public class WebProcessor implements Processor, WebAudienceStateListener {
    static final Logger LOG = LoggerFactory.getLogger(WebProcessor.class);

    private static final Gson GSON = new Gson();

    private final ScoreService scoreService;
    private final EventService eventService;
    private final EventFactory eventFactory;
    private final EventReceiver eventReceiver;
    private final Clock clock;

    private final Map<String, WebClientInfo> audienceClientInfos = new ConcurrentHashMap<>();
    private final Map<String, WebClientInfo> scoreClientInfos = new ConcurrentHashMap<>();

    private volatile String currentWebAudienceScoreState;
    private volatile String currentWebAudienceScoreStateDelta;
    private volatile long stateUpdateTime = 0;
    private volatile long stateDeltaUpdateTime = 0;

    private final Histogram serverHitHisto = new Histogram(Consts.HISTOGRAM_MAX_BUCKETS_NO, Consts.HISTOGRAM_BUCKET_PERIOD_MS);
    private List<String> bannedHosts = new CopyOnWriteArrayList<>();

    public WebProcessor(ScoreService scoreService, EventService eventService, Clock clock, EventFactory eventFactory, EventReceiver eventReceiver) {
        this.scoreService = scoreService;
        this.eventService = eventService;
        this.eventFactory = eventFactory;
        this.eventReceiver = eventReceiver;
        this.clock = clock;
    }

    @Override
    public void process(SzcoreEvent event) {
        if (WEB_AUDIENCE_IN == event.getEventType()) {
            IncomingWebAudienceEvent webEvent = (IncomingWebAudienceEvent) event;
            IncomingWebAudienceEventType type = webEvent.getWebEventType();

            switch (type) {
                case ELEMENT_SELECTED:
                case WEB_START:
                    processWebScoreEvent(webEvent);
                    break;
                case POLL:
                    processWebPoll((WebPollAudienceEvent) webEvent);
                    break;
                case CONNECTIONS_UPDATE:
                    processWebAudienceConnectionsUpdate((UpdateWebAudienceConnectionsEvent) webEvent);
                    break;
                case REQUEST_LOG:
                    processWebRequestLog((WebAudienceRequestLogEvent) webEvent);
                    break;
                default:
                    LOG.info("onIncomingWebEvent: unknown IncomingWebAudienceEventType: {}", type);
            }
        } else if (WEB_SCORE_IN == event.getEventType()) {
            WebScoreInEvent webEvent = (WebScoreInEvent) event;
            WebScoreInEventType eventType = webEvent.getWebScoreEventType();
            switch (eventType) {
                case CONNECTIONS_REMOVE:
                case CONNECTION:
                case SELECT_ISLOT:
                    scoreService.onIncomingWebScoreEvent(webEvent);
                    break;
                case CONNECTIONS_UPDATE:
                    processWebScoreConnectionsUpdate((UpdateWebScoreConnectionsEvent) webEvent);
                    break;
                case PART_REG:
                    processWebScoreRegisterPart((WebScorePartRegEvent) webEvent);
                    break;
                case PART_READY:
                    processWebScorePartReady((WebScorePartReadyEvent) webEvent);
                    break;
                default:
                    LOG.error("process()  WebScoreInEvent: unexpected event: {}", eventType);
            }
        }
    }

    private void processWebRequestLog(WebAudienceRequestLogEvent webEvent) {
        ZsWebRequest request = webEvent.getZsRequest();
        long reqTime = request.getTimeMs();

        serverHitHisto.hit(reqTime);
        boolean isClientHitLogged = false;

        String sourceAddr = request.getSourceAddr();
        WebClientInfo cInfo = getAudienceClientInfo(sourceAddr);
        if (cInfo != null) {
            cInfo.logHit(reqTime);
            isClientHitLogged = true;
        }

        String[] hostPort = NetUtil.getHostPort(sourceAddr);
        if (hostPort == null || hostPort.length != 2) {
            return;
        }
        String host = hostPort[0];
        List<WebClientInfo> hostClientInfos = getAudienceHostClientInfo(host);

        String userAgent = request.getUserAgent();
        for (WebClientInfo clientInfo : hostClientInfos) {
            //Assume the same client for the same IP addr
            if (cInfo == null && !isClientHitLogged) {
                clientInfo.logHit(reqTime);
                isClientHitLogged = true;
            }

            if (clientInfo.getConnectionType() == WebConnectionType.SSE) {
                if (userAgent != null) {
                    if (clientInfo.getUserAgent() == null) {
                        clientInfo.getWebConnection().setUserAgent(userAgent);
                    }
                    setUserAgentClientInfo(userAgent, clientInfo);
                }
            }
        }
    }

    private void processWebPoll(WebPollAudienceEvent pollEvent) {
        LOG.debug("processWebPoll: ");
        if (pollEvent.getSourceAddr() == null) {
            return;
        }
        WebClientInfo clientInfo = getOrCreateAudienceClientInfo(pollEvent.getSourceAddr());
        if (clientInfo != null && WebConnectionType.POLL != clientInfo.getConnectionType()) {
            clientInfo.setConnectionType(WebConnectionType.POLL);
        }
    }

    private void processWebAudienceConnectionsUpdate(UpdateWebAudienceConnectionsEvent connectionsEvent) {
        LOG.debug("processWebAudienceConnectionsUpdate: ");
        updateWebAudienceConnections(connectionsEvent.getClientConnections());
    }

    private void processWebScoreConnectionsUpdate(UpdateWebScoreConnectionsEvent connectionsEvent) {
        LOG.debug("processWebScoreConnectionsUpdate: ");
        updateWebScoreConnections(connectionsEvent.getClientConnections());
    }

    private void processWebScoreRegisterPart(WebScorePartRegEvent webEvent) {
        LOG.debug("processWebScoreRegisterPart: ");
        String addr = webEvent.getSourceAddr();
        WebClientInfo clientInfo = scoreClientInfos.get(addr);
        if (clientInfo == null) {
            LOG.error("processWebScoreRegisterPart: Can not find client info for source addr: {}", addr);
        } else {
            clientInfo.setInstrument(webEvent.getPart());
            updateGuiScoreClientInfo(clientInfo);
        }

        scoreService.onIncomingWebScoreEvent(webEvent);
    }

    private void processWebScorePartReady(WebScorePartReadyEvent webEvent) {
        LOG.debug("processWebScorePartReady: ");
        String addr = webEvent.getSourceAddr();
        WebClientInfo clientInfo = scoreClientInfos.get(addr);
        if (clientInfo == null) {
            LOG.error("processWebScorePartReady: Can not find client info for source addr: {}", addr);
        } else {
            clientInfo.setReady(true);
            updateGuiScoreClientInfo(clientInfo);
        }

        scoreService.onIncomingWebScoreEvent(webEvent);
    }

    private void processWebScoreEvent(IncomingWebAudienceEvent webEvent) {
        String sourceAddr = webEvent.getSourceAddr();
        long sendTime = webEvent.getClientEventSentTime();
        long receiveTime = webEvent.getCreationTime();
        long latency = receiveTime - sendTime;

        WebClientInfo clientInfo = getAudienceClientInfo(sourceAddr);
        if (clientInfo != null) {
            clientInfo.addLatency(latency);
        }

        scoreService.onIncomingWebAudienceEvent(webEvent);
    }

    public WebClientInfo getOrCreateAudienceClientInfo(String sourceAddr) {
        if (sourceAddr == null) {
            return null;
        }
        return audienceClientInfos.computeIfAbsent(sourceAddr, WebClientInfo::new);
    }

    public WebClientInfo getAudienceClientInfo(String sourceAddr) {
        if (sourceAddr == null) {
            return null;
        }
        return audienceClientInfos.get(sourceAddr);
    }

    public List<WebClientInfo> getAudienceHostClientInfo(String host) {
        if (host == null) {
            return null;
        }
        List<WebClientInfo> out = new ArrayList<>();
        for (WebClientInfo clientInfo : audienceClientInfos.values()) {
            if (host.equals(clientInfo.getHost())) {
                out.add(clientInfo);
            }
        }
        return out;
    }

    public WebClientInfo getOrCreateScoreClientInfo(String sourceAddr) {
        if (sourceAddr == null) {
            return null;
        }
        return scoreClientInfos.computeIfAbsent(sourceAddr, WebClientInfo::new);
    }

    public WebClientInfo getScoreClientInfo(String sourceAddr) {
        if (sourceAddr == null) {
            return null;
        }
        return scoreClientInfos.get(sourceAddr);
    }

    public List<WebClientInfo> getScoreHostClientInfo(String host) {
        if (host == null) {
            return null;
        }
        List<WebClientInfo> out = new ArrayList<>();
        for (WebClientInfo clientInfo : scoreClientInfos.values()) {
            if (host.equals(clientInfo.getHost())) {
                out.add(clientInfo);
            }
        }
        return out;
    }

    public void updateOrCreateAudienceClientInfo(WebConnection webConnection) {
        if (webConnection == null) {
            return;
        }
        if (webConnection.isScoreClient()) {
            updateOrCreateScoreClientInfo(webConnection);
            return;
        }
        String sourceAddr = webConnection.getClientAddr();
        WebClientInfo clientInfo = getOrCreateAudienceClientInfo(sourceAddr);
        if (clientInfo == null) {
            return;
        }

        WebConnection conn = clientInfo.getWebConnection();
        if (!webConnection.equals(conn)) {
            clientInfo.setWebConnection(webConnection);
        }

        WebConnectionType type = webConnection.getConnectionType();
        if (type != null && clientInfo.getConnectionType() != type) {
            clientInfo.setConnectionType(type);
        }

        String userAgent = webConnection.getUserAgent();
        if (userAgent == null) {
            return;
        }
        setUserAgentClientInfo(userAgent, clientInfo);

        if (bannedHosts.contains(webConnection.getHost())) {
            clientInfo.setBanned(true);
        }

        if (webConnection.isScoreClient()) {
            processScoreConnection(clientInfo);
        }
    }

    public WebClientInfo updateOrCreateScoreClientInfo(WebConnection webConnection) {
        if (webConnection == null) {
            return null;
        }
        if (!webConnection.isScoreClient()) {
            updateOrCreateAudienceClientInfo(webConnection);
            return null;
        }
        String sourceAddr = webConnection.getClientAddr();
        WebClientInfo clientInfo = getOrCreateScoreClientInfo(sourceAddr);
        if (clientInfo == null) {
            return null;
        }

        WebConnection conn = clientInfo.getWebConnection();
        if (!webConnection.equals(conn)) {
            clientInfo.setWebConnection(webConnection);
        }

        WebConnectionType type = webConnection.getConnectionType();
        if (type != null && clientInfo.getConnectionType() != type) {
            clientInfo.setConnectionType(type);
        }

        String userAgent = webConnection.getUserAgent();
        if (userAgent != null) {
            setUserAgentClientInfo(userAgent, clientInfo);
        }

        if (bannedHosts.contains(webConnection.getHost())) {
            clientInfo.setBanned(true);
        }

        processScoreConnection(clientInfo);
        return clientInfo;
    }

    private void processScoreConnection(WebClientInfo clientInfo) {
        WebScoreConnectionEvent connectionEvent = eventFactory.createWebScoreConnectionEvent(clientInfo, clock.getSystemTimeMillis());
        eventService.receive(connectionEvent);
    }

    private void closeConnections(List<String> connectionIds) {
        if (connectionIds.isEmpty()) {
            return;
        }
        scoreService.closeScoreConnections(connectionIds);
        WebScoreRemoveConnectionEvent clientInfoUpdateEvent = eventFactory.createRemoveWebScoreConnectionsEvent(connectionIds, clock.getSystemTimeMillis());
        eventService.receive(clientInfoUpdateEvent);
    }

    private void setUserAgentClientInfo(String userAgent, WebClientInfo clientInfo) {
        if (userAgent == null || clientInfo == null) {
            return;
        }

        UAgentInfo agentInfo = new UAgentInfo(userAgent);
        LOG.debug("updateOrCreateClientInfo: userAgent: {}", userAgent);
        if (!userAgent.equals(clientInfo.getUserAgent())) {
            clientInfo.setUserAgentInfo(agentInfo);
        }

        BrowserType bt = agentInfo.getBrowserType();
        if (bt != clientInfo.getBrowserType()) {
            clientInfo.setBrowserType(bt);
        }

        BrowserOS os = agentInfo.getOs();
        if (os != clientInfo.getOs()) {
            clientInfo.setOs(os);
        }

        boolean isMobile = agentInfo.isMobile();
        if (isMobile != clientInfo.isMobile()) {
            clientInfo.setMobile(isMobile);
        }
    }

    public ZsWebResponse onWebRequest(ZsWebRequest zsRequest) {
        LOG.debug("onHttpRequest: path: {} sourceAddr: {}", zsRequest.getRequestPath(), zsRequest.getSourceAddr());
        String out;
        try {
            logRequest(zsRequest);
            if (zsRequest.isStatic()) {
                return null;
            }
            Map<String, String> stringParams = zsRequest.getStringParams();
            String eventName = stringParams.get(WEB_EVENT_NAME);
            if (eventName != null) {
                out = processIncomingWebEvent(eventName, zsRequest);
            } else {
                out = createErrorWebString("UnknownEvent");
            }
        } catch (Exception e) {
            LOG.error("Failed to process zsRequest: {}", zsRequest.getRequestPath());
            out = createErrorWebString("FailedToProcessRequest");
        }
        return new ZsWebResponse(ZsResponseType.JSON, out);
    }

    private String createErrorWebString(String message) {
        WebDataContainer dataContainer = new WebDataContainer();
        dataContainer.addParam(WEB_RESPONSE_TYPE, WebResponseType.ERROR.name());
        dataContainer.addParam(WEB_RESPONSE_MESSAGE, message);
        dataContainer.addParam(WEB_RESPONSE_TIME, "" + getClock().getSystemTimeMillis());
        return GSON.toJson(dataContainer);
    }

    private String createOkWebString(String message) {
        WebDataContainer dataContainer = new WebDataContainer();
        dataContainer.addParam(WEB_RESPONSE_TYPE, WebResponseType.OK.name());
        dataContainer.addParam(WEB_RESPONSE_MESSAGE, message);
        dataContainer.addParam(WEB_RESPONSE_TIME, "" + getClock().getSystemTimeMillis());
        return GSON.toJson(dataContainer);
    }

    private String createStateWebString(String state) {
        WebDataContainer dataContainer = new WebDataContainer();
        dataContainer.addParam(WEB_RESPONSE_TYPE, WebResponseType.STATE.name());
        dataContainer.addParam(WEB_RESPONSE_STATE, state);
        dataContainer.addParam(WEB_RESPONSE_TIME, "" + getClock().getSystemTimeMillis());
        return GSON.toJson(dataContainer);
    }

    private String createWebScoreContainerString(String data) {
        WebDataContainer dataContainer = new WebDataContainer();
        dataContainer.addParam(WEB_RESPONSE_TYPE, WebResponseType.STATE.name());
        dataContainer.addParam(WEB_RESPONSE_STATE, data);
        dataContainer.addParam(WEB_RESPONSE_TIME, "" + getClock().getSystemTimeMillis());
        return GSON.toJson(dataContainer);
    }

    private String createAudienceStateDeltaWebString(String state) {
        WebDataContainer dataContainer = new WebDataContainer();
        dataContainer.addParam(WEB_RESPONSE_TYPE, WebResponseType.STATE_DELTA.name());
        dataContainer.addParam(WEB_RESPONSE_STATE, state);
        dataContainer.addParam(WEB_RESPONSE_TIME, "" + getClock().getSystemTimeMillis());
        return GSON.toJson(dataContainer);
    }

    private String processIncomingWebEvent(String eventName, ZsWebRequest zsRequest) throws Exception {
        if (eventName == null) {
            return createErrorWebString("InvalidEventName");
        }
        if (zsRequest.isScoreRequest()) {
            return processIncomingWebScoreEvent(eventName, zsRequest);
        } else {
            return processIncomingWebAudienceEvent(eventName, zsRequest);
        }
    }

    private String processIncomingWebScoreEvent(String eventName, ZsWebRequest zsRequest) throws Exception {
        WebScoreInEventType type = null;
        try {
            type = WebScoreInEventType.valueOf(eventName.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid WebEventType: {}", eventName);
        }

        if (type == null) {
            return createErrorWebString("InvalidEventType:" + eventName);
        }

        String sourceAddr = zsRequest.getSourceAddr();
        String requestPath = zsRequest.getRequestPath();
        String eventId = zsRequest.getParam(WEB_EVENT_NAME);

        long creationTime = getClock().getSystemTimeMillis();
        long clientEventCreatedTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_TIME_NAME));
        long clientEventSentTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_SENT_TIME_NAME));

        switch (type) {
            case PART_REG:
                String part = zsRequest.getParam(WEB_EVENT_PART);
                WebScorePartRegEvent partRegEvent = eventFactory.createWebScorePartRegEvent(eventId,
                        sourceAddr, part, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                eventService.receive(partRegEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            case PART_READY:
                part = zsRequest.getParam(WEB_EVENT_PART);
                WebScorePartReadyEvent partReadyEvent = eventFactory.createWebScorePartReadyEvent(eventId,
                        sourceAddr, part, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                eventService.receive(partReadyEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            case PING:
                String serverTimeStr = zsRequest.getParam(WEB_EVENT_SERVER_TIME);
                onClientPing(serverTimeStr, sourceAddr, creationTime);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            case SELECT_ISLOT:
                part = zsRequest.getParam(WEB_EVENT_PART);
                String slotNoStr = zsRequest.getParam(WEB_EVENT_SLOT_NO);
                String slotInstrument = zsRequest.getParam(WEB_EVENT_SLOT_INSTRUMENT);
                int slotNo = Integer.parseInt(slotNoStr);
                WebScoreSelectInstrumentSlotEvent slotEvent = eventFactory.createWebScoreSelectInstrumentSlotEvent(eventId,
                        sourceAddr, part, slotNo, slotInstrument, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                eventService.receive(slotEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            default:
                return createErrorWebString("Invalid event type: " + type);
        }
    }

    private void onClientPing(String serverTimeStr, String sourceAddr, long eventTime) {
        if (!ParseUtil.isNumeric(serverTimeStr)) {
            LOG.error("onClientPing: Invalid server time");
            return;
        }

        long serverTime = Long.parseLong(serverTimeStr);
        if (serverTime == 0L) {
            return;
        }
        WebClientInfo clientInfo = scoreClientInfos.get(sourceAddr);
        if (clientInfo == null) {
            LOG.error("onClientPing: Ping from Invalid participant: " + sourceAddr);
            return;
        }
        scoreService.onWebScorePing(clientInfo, serverTime, eventTime);
    }

    private String processIncomingWebAudienceEvent(String eventName, ZsWebRequest zsRequest) throws Exception {

        IncomingWebAudienceEventType type = null;
        try {
            type = IncomingWebAudienceEventType.valueOf(eventName.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid WebEventType: {}", eventName);
        }

        if (type == null) {
            return createErrorWebString("InvalidEventType:" + eventName);
        }

        String sourceAddr = zsRequest.getSourceAddr();
        String requestPath = zsRequest.getRequestPath();
        String eventId = zsRequest.getParam(WEB_EVENT_NAME);
        long lastClientStateUpdateTime = 0;
        if(zsRequest.containsParam(WEB_EVENT_LAST_STATE_UPDATE_TIME)) {
            String lastStateUpdateStr = zsRequest.getParam(WEB_EVENT_LAST_STATE_UPDATE_TIME);
            try {
                lastClientStateUpdateTime = Long.parseLong(lastStateUpdateStr);
            } catch (NumberFormatException e) {
                LOG.error("Failed to parse lastStateUpdateTime", e);
            }
        }
        boolean isPoll = false;
        if(zsRequest.containsParam(WEB_EVENT_IS_POLL_NAME)) {
            String isPollStr = zsRequest.getParam(WEB_EVENT_IS_POLL_NAME);
            if(isPollStr != null) {
                try {
                    isPoll = Boolean.parseBoolean(isPollStr);
                } catch (Exception e) {
                    LOG.error("Failed to process isPool HTTP parameter", e);
                }
            }
        }
        long creationTime = getClock().getSystemTimeMillis();
        long clientEventCreatedTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_TIME_NAME));
        long clientEventSentTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_SENT_TIME_NAME));

        switch (type) {
            case GET_SERVER_STATE:
                if (isPoll) {
                    WebPollAudienceEvent pollEvent = eventFactory.createWebAudiencePollEvent(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                    eventService.receive(pollEvent);
                }
                if (currentWebAudienceScoreState != null) {
                    if (isSendStateUpdate(lastClientStateUpdateTime)) {
                        return createStateWebString(currentWebAudienceScoreState);
                    } else {
                        return createOkWebString(EMPTY);
                    }
                } else {
                    return createErrorWebString("Score state not available");
                }
            case ELEMENT_SELECTED:
                String elementId = zsRequest.getParam(WEB_EVENT_ELEMENT_ID);
                boolean isSelected = Boolean.parseBoolean(zsRequest.getParam(WEB_EVENT_IS_SELECTED));
                ElementSelectedAudienceEvent selectedEvent = eventFactory.createElementSelectedEvent(elementId, isSelected, eventId,
                        sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);

                eventService.receive(selectedEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            case WEB_START:
                WebStartAudienceEvent webStartEvent = eventFactory.createWebAudienceStartEvent(eventId,
                        sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                eventService.receive(webStartEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            default:
                return EMPTY;
        }
    }

    private void logRequest(ZsWebRequest zsRequest) {
        WebAudienceRequestLogEvent selectedEvent = eventFactory.createWebAudienceRequestLogEvent(zsRequest, clock.getSystemTimeMillis());
        eventService.receive(selectedEvent);
    }

    private boolean isSendStateUpdate(long lastClientStateUpdateTime) {
        if (stateUpdateTime == 0 || lastClientStateUpdateTime == 0) {
            return true;
        }
        return stateUpdateTime > lastClientStateUpdateTime;
    }

    @Override
    public void onWebAudienceScoreStateChange(WebAudienceScoreStateExport webAudienceScoreStateExport) {
        if (webAudienceScoreStateExport == null) {
            return;
        }
        String out = GSON.toJson(webAudienceScoreStateExport);
        int stringLenBytes = Util.getStringLengthUtf8(out);
        long stringLenKb = stringLenBytes / 1024;
        LOG.debug("onWebScoreEvent: WebState size: {}Kb json: {}", stringLenKb, out);
        if (stringLenKb > 64) {
            LOG.error("onWebScoreEvent: ### WARNING ### WebState size {}Kb larger than 64Kb", stringLenKb);
        }
        this.currentWebAudienceScoreState = out;
        this.stateUpdateTime = getClock().getSystemTimeMillis();
    }

    @Override
    public void onWebAudienceScoreStateDeltaChange(WebAudienceScoreStateDeltaExport webAudienceScoreStateDeltaExport) {
        if (webAudienceScoreStateDeltaExport == null) {
            return;
        }
        String out = GSON.toJson(webAudienceScoreStateDeltaExport);
        int stringLenBytes = Util.getStringLengthUtf8(out);
        long stringLenKb = stringLenBytes / 1024;
        LOG.debug("onWebScoreStateDeltaChange: WebState size: {}Kb json: {}", stringLenKb, out);
        if (stringLenKb > 64) {
            LOG.error("onWebScoreStateDeltaChange: ### WARNING ### WebState size {}Kb larger than 64Kb", stringLenKb);
        }
        this.currentWebAudienceScoreStateDelta = out;
        this.stateDeltaUpdateTime = getClock().getSystemTimeMillis();
    }

    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) {
        if (webEvent == null) {
            return;
        }

        EventType eventType = webEvent.getEventType();
        switch (eventType) {
            case WEB_AUDIENCE_OUT:
                onOutAudienceEvent(webEvent);
                break;
            case WEB_SCORE_OUT:
                onOutScoreEvent(webEvent);
                break;
        }
    }

    private void onOutScoreEvent(OutgoingWebEvent webEvent) {
        OutgoingWebEventType type = webEvent.getOutWebEventType();
        switch (type) {
            case PUSH_SCORE_INFO:
                pushScoreInfo(webEvent);
                break;
            case PUSH_SCORE_STATE:
                pushScoreState(webEvent);
                break;
        }
    }

    private void pushScoreInfo(OutgoingWebEvent webEvent) {
        Map<String, Object> dataMap = webEvent.getDataMap();
        if (dataMap.isEmpty()) {
            return;
        }
        Object data = dataMap.get(Consts.WEB_DATA_SCORE_INFO);
        if (!(data instanceof WebScoreInfo)) {
            return;
        }

        WebScoreState dataWrapper = new WebScoreState();
        dataWrapper.setScoreInfo((WebScoreInfo) data);

        String out = GSON.toJson(dataWrapper);
        sendToScoreWeb(dataMap, out);
    }

    private void pushScoreState(OutgoingWebEvent webEvent) {
        Map<String, Object> dataMap = webEvent.getDataMap();
        if (dataMap.isEmpty()) {
            return;
        }
        Object data = dataMap.get(Consts.WEB_DATA_SCORE_STATE);
        if (!(data instanceof WebScoreState)) {
            return;
        }

        WebScoreState scoreState = (WebScoreState) data;
        String out = GSON.toJson(scoreState);
        sendToScoreWeb(dataMap, out);
    }

    private void sendToScoreWeb(Map<String, Object> dataMap, String out) {
        String target = Consts.WEB_DATA_TARGET_ALL;
        WebScoreTargetType targetType = WebScoreTargetType.ALL;

        Object data = dataMap.get(Consts.WEB_DATA_TARGET);
        if (data instanceof String) {
            target = (String) data;
        }
        data = dataMap.get(Consts.WEB_DATA_TARGET_TYPE);
        if (data instanceof WebScoreTargetType) {
            targetType = (WebScoreTargetType) data;
        }
        if (Consts.DEFAULT_OSC_PORT_NAME.equals(target)) {
            LOG.warn("sendToScoreWeb: unexpected target: {}, sending to all", target);
            target = Consts.WEB_DATA_TARGET_ALL;
            targetType = WebScoreTargetType.ALL;
        }
        String content = createWebScoreContainerString(out);
        if(WebScoreTargetType.INSTRUMENT == targetType) {
            List<WebClientInfo> instrumentClients = scoreService.getWebScoreInstrumentClients(target);
            if(instrumentClients != null && !instrumentClients.isEmpty()) {
                for(WebClientInfo clientInfo : instrumentClients) {
                    String addr = clientInfo.getClientAddr();
                    scoreService.pushToScoreWeb(addr, WebScoreTargetType.HOST, content);
                }
            }
        } else {
            scoreService.pushToScoreWeb(target, targetType, content);
        }
    }

    private void onOutAudienceEvent(OutgoingWebEvent webEvent) {
        OutgoingWebEventType type = webEvent.getOutWebEventType();
        if (type == null) {
            return;
        }
        switch (type) {
            case PUSH_SERVER_STATE:
                pushWebAudienceScoreState();
                break;
            case PUSH_SERVER_STATE_DELTA:
                pushWebAudienceScoreStateDelta();
                break;
        }
    }

    private void pushWebAudienceScoreState() {
        if (currentWebAudienceScoreState == null) {
            return;
        }
        String out = createStateWebString(currentWebAudienceScoreState);
        scoreService.pushToWebAudience(out);
    }

    private void pushWebAudienceScoreStateDelta() {
        if (currentWebAudienceScoreStateDelta == null) {
            return;
        }
        String delta = createAudienceStateDeltaWebString(currentWebAudienceScoreStateDelta);
        scoreService.pushToWebAudience(delta);
        //reset delta - is that smart?
        currentWebAudienceScoreStateDelta = null;
    }

    public Clock getClock() {
        return clock;
    }

    public void onWebConnection(WebConnection webConnection) {
        if (webConnection.isScoreClient()) {
            WebClientInfo clientInfo = updateOrCreateScoreClientInfo(webConnection);
            updateGuiScoreClientInfo(clientInfo);
        } else {
            updateOrCreateAudienceClientInfo(webConnection);
        }
    }

    public void onUpdateWebAudienceConnections(Set<WebConnection> connections) {
        UpdateWebAudienceConnectionsEvent selectedEvent = eventFactory.createUpdateWebAudienceConnectionsEvent(connections, getClock().getSystemTimeMillis());
        eventService.receive(selectedEvent);
    }

    public void onUpdateWebScoreConnections(Set<WebConnection> connections) {
        UpdateWebScoreConnectionsEvent selectedEvent = eventFactory.createUpdateWebScoreConnectionsEvent(connections, getClock().getSystemTimeMillis());
        eventService.receive(selectedEvent);
    }

    public void updateWebAudienceConnections(Set<WebConnection> currentConnections) {
        if (currentConnections == null) {
            return;
        }
        //Remove addresses not in current connections
        List<String> toRemove = new ArrayList<>();
        for (String sourceId : audienceClientInfos.keySet()) {
            WebClientInfo clientInfo = audienceClientInfos.get(sourceId);
            WebConnection clientConnection = clientInfo.getConnection();
            if (WebConnectionType.POLL == clientConnection.getConnectionType()) {
                continue;
            }
            if (!currentConnections.contains(clientConnection)) {
                LOG.debug("updateWebAudienceConnections: removing connection: {}", clientConnection);
                toRemove.add(clientConnection.getClientAddr());
            }

            int totalHitCount = clientInfo.getTotalHitCount(clock.getSystemTimeMillis());
            if (totalHitCount > Consts.MAX_ALLOWED_HIT_COUNT_10S) {
                LOG.info("updateWebAudienceConnections: client {} exceeded max allowed number of requests {}", clientInfo.getClientAddr(), Consts.MAX_ALLOWED_HIT_COUNT_10S);
                banClient(clientInfo);
            }
        }
        for(String clientId : toRemove) {
            audienceClientInfos.remove(clientId);
        }

        //Update or Create addresses in current connections
        for (WebConnection connection : currentConnections) {
            String clientId = connection.getClientAddr();
            WebConnectionType type = connection.getConnectionType();
            LOG.debug("updateWsConnections: update/create addr: {} type: {}", clientId, type);
            updateOrCreateAudienceClientInfo(connection);
        }

        List<HistoBucketView> histoBucketViews = serverHitHisto.getBucketViews(clock.getSystemTimeMillis());
        String histoLog = EMPTY;
        int totalHits = 0;
        for (HistoBucketView bucketView : histoBucketViews) {
            totalHits += bucketView.getCount();
            histoLog = histoLog + bucketView.getDateLabel() + SPACE + bucketView.getCount() + COMMA;
        }
        if (totalHits > 0) {
            LOG.info("updateWebStatus: web histogram: {}", histoLog);
        }

        updateGuiAudienceClientInfos(histoBucketViews, totalHits);
    }

    public void updateWebScoreConnections(Set<WebConnection> currentConnections) {
        if (currentConnections == null) {
            return;
        }

        //Remove addresses not in current connections
        List<String> toRemove = new ArrayList<>();
        for (String sourceId : scoreClientInfos.keySet()) {
            WebClientInfo clientInfo = scoreClientInfos.get(sourceId);
            WebConnection clientConnection = clientInfo.getConnection();
            if (WebConnectionType.POLL == clientConnection.getConnectionType()) {
                continue;
            }
            if (!currentConnections.contains(clientConnection)) {
                LOG.debug("updateWebScoreConnections: removing connection: {}", clientConnection);
                toRemove.add(clientConnection.getClientAddr());
            }
        }

        //Update or Create addresses in current connections
        for (WebConnection connection : currentConnections) {
            if (!connection.isScoreClient()) {
                LOG.error("updateWebScoreConnections: Unexpected client connection: {}", connection);
                continue;
            }
            if (!connection.isOpen()) {
                toRemove.add(connection.getClientAddr());
            }
            String clientId = connection.getClientAddr();
            WebConnectionType type = connection.getConnectionType();
            LOG.debug("updateWebScoreConnections: update/create addr: {} type: {}", clientId, type);
            updateOrCreateScoreClientInfo(connection);
        }

        for (String clientId : toRemove) {
            scoreClientInfos.remove(clientId);
        }
        closeConnections(toRemove);
        updateGuiScoreClientInfos();
    }

    public void banClient(String clientId) {
        if (scoreClientInfos.containsKey(clientId)) {
            banClient(scoreClientInfos.get(clientId));
        }
        if (audienceClientInfos.containsKey(clientId)) {
            banClient(audienceClientInfos.get(clientId));
        }
    }

    private void banClient(WebClientInfo clientInfo) {
        bannedHosts.add(clientInfo.getHost());
        clientInfo.setBanned(true);
        scoreService.banWebClient(clientInfo);
        updateGuiScoreClientInfo(clientInfo);
    }

    private void updateGuiAudienceClientInfos(List<HistoBucketView> histoBucketViews, int totalHits) {
        Collection<WebClientInfo> wcis = audienceClientInfos.values();
        ArrayList<WebClientInfo> out = new ArrayList<>(wcis);
        WebAudienceClientInfoUpdateEvent clientInfoUpdateEvent = eventFactory.createWebAudienceClientInfoUpdateEvent(out, histoBucketViews, totalHits, clock.getSystemTimeMillis());
        eventReceiver.notifyListeners(clientInfoUpdateEvent);
    }

    private void updateGuiScoreClientInfos() {
        Collection<WebClientInfo> wcis = scoreClientInfos.values();
        ArrayList<WebClientInfo> out = new ArrayList<>(wcis);
        WebScoreClientInfoUpdateEvent clientInfoUpdateEvent = eventFactory.createWebScoreClientInfoUpdateEvent(out, true, clock.getSystemTimeMillis());
        eventReceiver.notifyListeners(clientInfoUpdateEvent);
    }

    private void updateGuiScoreClientInfo(WebClientInfo clientInfo) {
        if (clientInfo == null) {
            return;
        }
        ArrayList<WebClientInfo> out = new ArrayList<>(1);
        out.add(clientInfo);
        WebScoreClientInfoUpdateEvent clientInfoUpdateEvent = eventFactory.createWebScoreClientInfoUpdateEvent(out, false, clock.getSystemTimeMillis());
        eventReceiver.notifyListeners(clientInfoUpdateEvent);
    }

    public void onInterceptedOscOutEvent(OscEvent event) {
        scoreService.onInterceptedOscOutEvent(event);
    }
}
