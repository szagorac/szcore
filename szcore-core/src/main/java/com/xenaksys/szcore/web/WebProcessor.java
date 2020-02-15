package com.xenaksys.szcore.web;

import com.google.gson.Gson;
import com.xenaksys.szcore.event.ElementSelectedEvent;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.WebEvent;
import com.xenaksys.szcore.event.WebEventType;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.ScoreService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.ZsResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_EVENT_ELEMENT_ID;
import static com.xenaksys.szcore.Consts.WEB_EVENT_IS_SELECTED;
import static com.xenaksys.szcore.Consts.WEB_EVENT_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_SENT_TIME_NAME;
import static com.xenaksys.szcore.Consts.WEB_EVENT_TIME_NAME;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_MESSAGE;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_SUBMITTED;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_TIME;
import static com.xenaksys.szcore.Consts.WEB_RESPONSE_TYPE;
import static com.xenaksys.szcore.event.EventType.WEB;

public class WebProcessor implements Processor {
    static final Logger LOG = LoggerFactory.getLogger(WebProcessor.class);

    private static final Gson GSON = new Gson();

    private final ScoreService scoreService;
    private final EventService eventService;
    private final EventFactory eventFactory;
    private final Clock clock;

    private Map<String, WebClientState> clientStates = new HashMap<>();

    public WebProcessor(ScoreService scoreService, EventService eventService, Clock clock, EventFactory eventFactory) {
        this.scoreService = scoreService;
        this.eventService = eventService;
        this.eventFactory = eventFactory;
        this.clock = clock;
    }

    @Override
    public void process(SzcoreEvent event) {
        if(WEB != event.getEventType()) {
            return;
        }

        WebEvent webEvent = (WebEvent)event;
        String sourceAddr = webEvent.getSourceAddr();
        long sendTime = webEvent.getClientEventSentTime();
        long receiveTime = webEvent.getCreationTime();
        long latency = receiveTime - sendTime;

        WebClientState clientState = clientStates.computeIfAbsent(sourceAddr, WebClientState::new);
        clientState.addLatency(latency);

        scoreService.onWebEvent(webEvent);
    }

    public ZsHttpResponse onHttpRequest(ZsHttpRequest zsRequest) {
        LOG.info("onHttpRequest: path: {} sourceAddr: {}", zsRequest.getRequestPath(), zsRequest.getSourceAddr());

        String out;
        try {
            Map<String, String> stringParams = zsRequest.getStringParams();
            String eventName = stringParams.get(WEB_EVENT_NAME);
            if(eventName != null) {
                out = processWebEvent(eventName, zsRequest);
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

    private String processWebEvent(String eventName, ZsHttpRequest zsRequest) throws Exception {
        if(eventName == null) {
            return createErrorWebString("InvalidEventName");
        }
        WebEventType type = null;
        try {
            type = WebEventType.valueOf(eventName.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid WebEventType: {}", eventName);
        }

        if(type == null) {
            return createErrorWebString("InvalidEventType:" + eventName);
        }

        String sourceAddr = zsRequest.getSourceAddr();
        String requestPath = zsRequest.getRequestPath();
        String eventId = zsRequest.getParam(WEB_EVENT_NAME);
        long creationTime = getClock().getSystemTimeMillis();
        long clientEventCreatedTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_TIME_NAME));
        long clientEventSentTime = Long.parseLong(zsRequest.getParam(WEB_EVENT_SENT_TIME_NAME));

        switch (type) {
            case ELEMENT_SELECTED:
                String elementId = zsRequest.getParam(WEB_EVENT_ELEMENT_ID);
                boolean isSelected = Boolean.parseBoolean(zsRequest.getParam(WEB_EVENT_IS_SELECTED));
                ElementSelectedEvent selectedEvent = eventFactory.createElementSelectedEvent(elementId, isSelected, eventId,
                        sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);

                eventService.receive(selectedEvent);
                return createOkWebString(WEB_RESPONSE_SUBMITTED);
            default:
                return EMPTY;
        }
    }

    public Clock getClock() {
        return clock;
    }
}
