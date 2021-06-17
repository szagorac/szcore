package com.xenaksys.szcore.web;

import com.google.gson.Gson;
import com.xenaksys.szcore.event.ElementSelectedEvent;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.IncomingWebEvent;
import com.xenaksys.szcore.event.IncomingWebEventType;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import com.xenaksys.szcore.event.UpdateWebStatusEvent;
import com.xenaksys.szcore.event.WebClientInfoUpdateEvent;
import com.xenaksys.szcore.event.WebPollEvent;
import com.xenaksys.szcore.event.WebRequestLogEvent;
import com.xenaksys.szcore.event.WebStartEvent;
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
import com.xenaksys.szcore.score.web.export.WebScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateExport;
import com.xenaksys.szcore.util.Histogram;
import com.xenaksys.szcore.util.NetUtil;
import com.xenaksys.szcore.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.xenaksys.szcore.Consts.COMMA;
import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.SPACE;
import static com.xenaksys.szcore.Consts.WEB_EVENT_ELEMENT_ID;
import static com.xenaksys.szcore.Consts.WEB_EVENT_IS_POLL_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_IS_SELECTED;
import static com.xenaksys.szcore.Consts.WEB_EVENT_LAST_STATE_UPDATE_TIME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_SENT_TIME_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_TIME_NAME;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_MESSAGE;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_STATE;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_SUBMITTED;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_TIME;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_TYPE;
import static com.xenaksys.szcore.event.EventType.WEB_IN;

public class WebProcessor implements Processor, WebScoreStateListener {
    static final Logger LOG = LoggerFactory.getLogger(WebProcessor.class);

    private static final Gson GSON = new Gson();

    private final ScoreService scoreService;
    private final EventService eventService;
    private final EventFactory eventFactory;
    private final EventReceiver eventReceiver;
    private final Clock clock;

    private final Map<String, WebClientInfo> clientInfos = new ConcurrentHashMap<>();

    private volatile String currentWebScoreState;
    private volatile String currentWebScoreStateDelta;
    private volatile long stateUpdateTime = 0;
    private volatile long stateDeltaUpdateTime = 0;

    private final Histogram serverHitHisto = new Histogram(10, 1000L);

    public WebProcessor(ScoreService scoreService, EventService eventService, Clock clock, EventFactory eventFactory, EventReceiver eventReceiver) {
        this.scoreService = scoreService;
        this.eventService = eventService;
        this.eventFactory = eventFactory;
        this.eventReceiver = eventReceiver;
        this.clock = clock;
    }

    @Override
    public void process(SzcoreEvent event) {
        if (WEB_IN != event.getEventType()) {
            return;
        }
        IncomingWebEvent webEvent = (IncomingWebEvent) event;
        IncomingWebEventType type = webEvent.getWebEventType();

        switch (type) {
            case ELEMENT_SELECTED:
            case WEB_START:
                processWebScoreEvent(webEvent);
                break;
            case POLL:
                processWebPoll((WebPollEvent) webEvent);
                break;
            case CONNECTIONS_UPDATE:
                processWebStatusUpdate((UpdateWebStatusEvent) webEvent);
                break;
            case REQUEST_LOG:
                processWebRequestLog((WebRequestLogEvent) webEvent);
                break;
            default:
                LOG.info("onIncomingWebEvent: unknown IncomingWebEventType: {}", type);
        }
    }

    private void processWebRequestLog(WebRequestLogEvent webEvent) {
        ZsWebRequest request = webEvent.getZsRequest();
        long reqTime = request.getTimeMs();

        serverHitHisto.hit(reqTime);
        boolean isClientHitLogged = false;

        String sourceAddr = request.getSourceAddr();
        WebClientInfo cInfo = getClientInfo(sourceAddr);
        if (cInfo != null) {
            cInfo.logHit(reqTime);
            isClientHitLogged = true;
        }

        String[] hostPort = NetUtil.getHostPort(sourceAddr);
        if (hostPort == null || hostPort.length != 2) {
            return;
        }
        String host = hostPort[0];
        List<WebClientInfo> hostClientInfos = getHostClientInfo(host);

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
                    setUserAgentCLientInfo(userAgent, clientInfo);
                }
            }
        }
    }

    private void processWebPoll(WebPollEvent pollEvent) {
        LOG.debug("processWebPoll: ");
        if (pollEvent.getSourceAddr() == null) {
            return;
        }
        WebClientInfo clientInfo = getOrCreateClientInfo(pollEvent.getSourceAddr());
        if (clientInfo != null && WebConnectionType.POLL != clientInfo.getConnectionType()) {
            clientInfo.setConnectionType(WebConnectionType.POLL);
        }
    }

    private void processWebStatusUpdate(UpdateWebStatusEvent connectionsEvent) {
        LOG.debug("processWebConnectionUpdate: ");
        updateWebStatus(connectionsEvent.getClientConnections());
    }

    private void processWebScoreEvent(IncomingWebEvent webEvent) {
        String sourceAddr = webEvent.getSourceAddr();
        long sendTime = webEvent.getClientEventSentTime();
        long receiveTime = webEvent.getCreationTime();
        long latency = receiveTime - sendTime;

        WebClientInfo clientInfo = getClientInfo(sourceAddr);
        if(clientInfo != null) {
            clientInfo.addLatency(latency);
        }

        scoreService.onIncomingWebEvent(webEvent);
    }

    public WebClientInfo getOrCreateClientInfo(String sourceAddr) {
        if (sourceAddr == null) {
            return null;
        }
        return clientInfos.computeIfAbsent(sourceAddr, WebClientInfo::new);
    }

    public WebClientInfo getClientInfo(String sourceAddr) {
        if (sourceAddr == null) {
            return null;
        }
        return clientInfos.get(sourceAddr);
    }

    public List<WebClientInfo> getHostClientInfo(String host) {
        if (host == null) {
            return null;
        }
        List<WebClientInfo> out = new ArrayList<>();
        for (WebClientInfo clientInfo : clientInfos.values()) {
            if (host.equals(clientInfo.getHost())) {
                out.add(clientInfo);
            }
        }
        return out;
    }

    public void updateOrCreateClientInfo(WebConnection webConnection) {
        if (webConnection == null) {
            return;
        }

        String sourceAddr = webConnection.getClientAddr();
        WebClientInfo clientInfo = getOrCreateClientInfo(sourceAddr);
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
        setUserAgentCLientInfo(userAgent, clientInfo);
    }

    private void setUserAgentCLientInfo(String userAgent, WebClientInfo clientInfo) {
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

    private String createStateDeltaWebString(String state) {
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
        IncomingWebEventType type = null;
        try {
            type = IncomingWebEventType.valueOf(eventName.toUpperCase());
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
                if(isPoll) {
                    WebPollEvent pollEvent = eventFactory.createWebPollEvent(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                    eventService.receive(pollEvent);
                }
                if (currentWebScoreState != null) {
                    if(isSendStateUpdate(lastClientStateUpdateTime)) {
                        return createStateWebString(currentWebScoreState);
                    } else {
                        return createOkWebString(EMPTY);
                    }
                } else {
                    return createErrorWebString("Score state not available");
                }
            case ELEMENT_SELECTED:
                String elementId = zsRequest.getParam(WEB_EVENT_ELEMENT_ID);
                boolean isSelected = Boolean.parseBoolean(zsRequest.getParam(WEB_EVENT_IS_SELECTED));
                ElementSelectedEvent selectedEvent = eventFactory.createElementSelectedEvent(elementId, isSelected, eventId,
                        sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);

                eventService.receive(selectedEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            case WEB_START:
                WebStartEvent webStartEvent = eventFactory.createWebStartEvent(eventId,
                        sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
                eventService.receive(webStartEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            default:
                return EMPTY;
        }
    }

    private void logRequest(ZsWebRequest zsRequest) {
        WebRequestLogEvent selectedEvent = eventFactory.createWebRequestLogEvent(zsRequest, clock.getSystemTimeMillis());
        eventService.receive(selectedEvent);
    }

    private boolean isSendStateUpdate(long lastClientStateUpdateTime) {
        if (stateUpdateTime == 0 || lastClientStateUpdateTime == 0) {
            return true;
        }
        return stateUpdateTime > lastClientStateUpdateTime;
    }

    @Override
    public void onWebScoreStateChange(WebScoreStateExport webScoreStateExport) {
        if (webScoreStateExport == null) {
            return;
        }
        String out = GSON.toJson(webScoreStateExport);
        int stringLenBytes = Util.getStringLengthUtf8(out);
        long stringLenKb = stringLenBytes / 1024;
        LOG.debug("onWebScoreEvent: WebState size: {}Kb json: {}", stringLenKb, out);
        if (stringLenKb > 64) {
            LOG.error("onWebScoreEvent: ### WARNING ### WebState size {}Kb larger than 64Kb", stringLenKb);
        }
        this.currentWebScoreState = out;
        this.stateUpdateTime = getClock().getSystemTimeMillis();
    }

    @Override
    public void onWebScoreStateDeltaChange(WebScoreStateDeltaExport webScoreStateDeltaExport) {
        if (webScoreStateDeltaExport == null) {
            return;
        }
        String out = GSON.toJson(webScoreStateDeltaExport);
        int stringLenBytes = Util.getStringLengthUtf8(out);
        long stringLenKb = stringLenBytes / 1024;
        LOG.debug("onWebScoreStateDeltaChange: WebState size: {}Kb json: {}", stringLenKb, out);
        if (stringLenKb > 64) {
            LOG.error("onWebScoreStateDeltaChange: ### WARNING ### WebState size {}Kb larger than 64Kb", stringLenKb);
        }
        this.currentWebScoreStateDelta = out;
        this.stateDeltaUpdateTime = getClock().getSystemTimeMillis();
    }

    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) {
        if (webEvent == null) {
            return;
        }
        OutgoingWebEventType type = webEvent.getWebEventType();
        switch (type) {
            case PUSH_SERVER_STATE:
                pushWebScoreState();
                break;
            case PUSH_SERVER_STATE_DELTA:
                pushWebScoreStateDelta();
                break;
        }
    }

    private void pushWebScoreState() {
        if (currentWebScoreState == null) {
            return;
        }
        String out = createStateWebString(currentWebScoreState);
        scoreService.pushToWebClients(out);
    }

    private void pushWebScoreStateDelta() {
        if (currentWebScoreStateDelta == null) {
            return;
        }
        String delta = createStateDeltaWebString(currentWebScoreStateDelta);
        scoreService.pushToWebClients(delta);
        //reset delta - is that smart?
        currentWebScoreStateDelta = null;
    }

    public Clock getClock() {
        return clock;
    }

    public void onWebConnection(WebConnection webConnection) {
        updateOrCreateClientInfo(webConnection);
    }

    public void onUpdateWebConnections(Set<WebConnection> connections) {
        UpdateWebStatusEvent selectedEvent = eventFactory.createUpdateWebConnectionsEvent(connections, getClock().getSystemTimeMillis());
        eventService.receive(selectedEvent);
    }

    public void updateWebStatus(Set<WebConnection> currentConnections) {
        if (currentConnections == null) {
            return;
        }

        //Remove addresses not in current connections
        List<String> toRemove = new ArrayList<>();
        for (String sourceId : clientInfos.keySet()) {
            WebClientInfo clientInfo = clientInfos.get(sourceId);
            WebConnection clientConnection = clientInfo.getConnection();
            if (WebConnectionType.POLL == clientConnection.getConnectionType()) {
                continue;
            }
            if (!currentConnections.contains(clientConnection)) {
                LOG.debug("updateWsConnections: removing connection: {}", clientConnection);
                toRemove.add(clientConnection.getClientAddr());
            }
        }
        for(String clientId : toRemove) {
            clientInfos.remove(clientId);
        }

        //Update or Create addresses in current connections
        for (WebConnection connection : currentConnections) {
            String clientId = connection.getClientAddr();
            WebConnectionType type = connection.getConnectionType();
            LOG.debug("updateWsConnections: update/create addr: {} type: {}", clientId, type);
            updateOrCreateClientInfo(connection);
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

        updateGuiClientInfos(histoBucketViews, totalHits);
    }

    private void updateGuiClientInfos(List<HistoBucketView> histoBucketViews, int totalHits) {
        Collection<WebClientInfo> wcis = clientInfos.values();
        ArrayList<WebClientInfo> out = new ArrayList<>(wcis);
        WebClientInfoUpdateEvent clientInfoUpdateEvent = eventFactory.createWebClientInfoUpdateEvent(out, histoBucketViews, totalHits, clock.getSystemTimeMillis());
        eventReceiver.notifyListeners(clientInfoUpdateEvent);
    }
}
