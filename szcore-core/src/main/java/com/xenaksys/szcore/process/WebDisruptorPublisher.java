package com.xenaksys.szcore.process;

import com.lmax.disruptor.RingBuffer;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDisruptorPublisher {
    static final Logger LOG = LoggerFactory.getLogger(WebDisruptorPublisher.class);

    private final RingBuffer<OutgoingWebEvent> ringBuffer;
    private final WebEventTranslator translator = new WebEventTranslator();

    private volatile boolean isActive = false;

    public WebDisruptorPublisher(RingBuffer<OutgoingWebEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void publish(OutgoingWebEvent event) throws Exception {
        if (isActive) {
            ringBuffer.publishEvent(translator, event);
        } else {
            LOG.warn("Disruptor is not active, ignoring message: " + event);
        }
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

