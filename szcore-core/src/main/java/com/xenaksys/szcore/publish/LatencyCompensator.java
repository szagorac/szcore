package com.xenaksys.szcore.publish;


import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.out.DelayedOutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.event.web.out.OutgoingWebEventType;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LatencyCompensator {
    static final Logger LOG = LoggerFactory.getLogger(LatencyCompensator.class);
    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final List<OutgoingWebEventType> eventTypeFilter;
    private final Clock clock;

    private long webPublishDelayMs = 0L;
    private boolean isActive = true;

    public LatencyCompensator(ScoreProcessor scoreProcessor, EventFactory eventFactory, List<OutgoingWebEventType> eventTypeFilter, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.eventTypeFilter = eventTypeFilter;
        this.clock = clock;
    }

    public void process(OutgoingWebEvent webEvent) {
        if(webEvent == null) {
            return;
        }

        OutgoingWebEventType eventType = webEvent.getOutWebEventType();
        boolean isFilterOut = eventTypeFilter != null && eventTypeFilter.contains(eventType);

        boolean isSendNow =  webEvent.isSendNow() ||
                isFilterOut ||
                !scoreProcessor.isSchedulerRunning() ||
                !isActive ||
                webPublishDelayMs <= 0L;

        if(isSendNow) {
            scoreProcessor.publishWebEvent(webEvent);
        } else {
            DelayedOutgoingWebEvent delayedOutgoingWebEvent = eventFactory.createDelayedWebScoreOutEvent(webEvent, clock.getSystemTimeMillis());
            scoreProcessor.scheduleEvent(delayedOutgoingWebEvent, webPublishDelayMs);
        }
    }

    public void process(DelayedOutgoingWebEvent event) {
        long delayedTime = clock.getSystemTimeMillis();
        long ogTime = event.getOutgoingWebEvent().getCreationTime();
        long outDiff = delayedTime - ogTime;
        if(Math.abs(webPublishDelayMs - outDiff) > 5L) {
            LOG.info("processDelayedWebOutEvent ms diff: {}", delayedTime - ogTime);
        }
        scoreProcessor.publishWebEvent(event.getOutgoingWebEvent());
    }


    public long getWebPublishDelayMs() {
        return webPublishDelayMs;
    }

    public void setWebPublishDelayMs(long webPublishDelayMs) {
        this.webPublishDelayMs = webPublishDelayMs;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
