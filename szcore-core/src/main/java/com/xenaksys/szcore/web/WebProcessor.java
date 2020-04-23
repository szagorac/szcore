package com.xenaksys.szcore.web;

import com.google.gson.Gson;
import com.xenaksys.szcore.event.*;
import com.xenaksys.szcore.model.*;
import com.xenaksys.szcore.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.*;
import static com.xenaksys.szcore.event.EventType.WEB_IN;

public class WebProcessor implements Processor, WebScoreEventListener {
    static final Logger LOG = LoggerFactory.getLogger(WebProcessor.class);

    private static final Gson GSON = new Gson();

    private final ScoreService scoreService;
    private final EventService eventService;
    private final EventFactory eventFactory;
    private final Clock clock;

    private final Map<String, WebClientState> clientStates = new HashMap<>();

    private volatile String currentWebScoreState;
    private volatile long stateUpdateTime = 0;

    public WebProcessor(ScoreService scoreService, EventService eventService, Clock clock, EventFactory eventFactory) {
        this.scoreService = scoreService;
        this.eventService = eventService;
        this.eventFactory = eventFactory;
        this.clock = clock;
    }

    @Override
    public void process(SzcoreEvent event) {
        if (WEB_IN != event.getEventType()) {
            return;
        }

        IncomingWebEvent webEvent = (IncomingWebEvent) event;
        String sourceAddr = webEvent.getSourceAddr();
        long sendTime = webEvent.getClientEventSentTime();
        long receiveTime = webEvent.getCreationTime();
        long latency = receiveTime - sendTime;

        WebClientState clientState = clientStates.computeIfAbsent(sourceAddr, WebClientState::new);
        clientState.addLatency(latency);

        scoreService.onIncomingWebEvent(webEvent);
    }

    public String onWsRequest(String data) {
        return currentWebScoreState;
    }
    public ZsHttpResponse onHttpRequest(ZsHttpRequest zsRequest) {
//        LOG.debug("onHttpRequest: path: {} sourceAddr: {}", zsRequest.getRequestPath(), zsRequest.getSourceAddr());
        String out;
        try {
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
        return new ZsHttpResponse(ZsResponseType.JSON, out);
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

    private String processIncomingWebEvent(String eventName, ZsHttpRequest zsRequest) throws Exception {
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
        long creationTime = getClock().getSystemTimeMillis();
        long clientEventCreatedTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_TIME_NAME));
        long clientEventSentTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_SENT_TIME_NAME));

        switch (type) {
            case GET_SERVER_STATE:
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

    private boolean isSendStateUpdate(long lastClientStateUpdateTime) {
        if(stateUpdateTime == 0 || lastClientStateUpdateTime == 0) {
            return true;
        }
        return stateUpdateTime > lastClientStateUpdateTime;
    }

    @Override
    public void onWebScoreEvent(WebScoreState webScoreState) {
        if (webScoreState == null) {
            return;
        }
        String out = GSON.toJson(webScoreState);
        int stringLenBytes = Util.getStringLengthUtf8(out);
        long stringLenKb = stringLenBytes / 1024;
        LOG.info("onWebScoreEvent: WebState size: {}Kb json: {}", stringLenKb, out);
        if (stringLenKb > 64) {
            LOG.error("onWebScoreEvent: ### WARNING ### WebState size {}Kb larger than 64Kb", stringLenKb);
        }
        this.currentWebScoreState = out;
        this.stateUpdateTime = getClock().getSystemTimeMillis();
    }

    @Override
    public void onOutgoingWebEvent(OutgoingWebEvent webEvent) {
        if (webEvent == null) {
            return;
        }

        OutgoingWebEventType type = webEvent.getWebEventType();
        switch (type) {
            case PUSH_SERVER_STATE:
                scoreService.pushToWebClients(currentWebScoreState);
                break;
        }
    }

    public Clock getClock() {
        return clock;
    }

}
