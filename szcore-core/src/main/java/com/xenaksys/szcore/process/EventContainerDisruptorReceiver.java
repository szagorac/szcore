package com.xenaksys.szcore.process;

import com.lmax.disruptor.RingBuffer;
import com.xenaksys.szcore.event.EventContainer;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventContainerDisruptorReceiver {
    static final Logger LOG = LoggerFactory.getLogger(EventContainerDisruptorReceiver.class);

    private final RingBuffer<EventContainer> ringBuffer;
    private final EventContainerTranslator translator = new EventContainerTranslator();

    private volatile boolean isActive = false;

    public EventContainerDisruptorReceiver(RingBuffer<EventContainer> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void publish(SzcoreEvent event) throws Exception {
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

