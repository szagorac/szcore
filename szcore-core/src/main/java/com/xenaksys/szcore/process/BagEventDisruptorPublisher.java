package com.xenaksys.szcore.process;

import com.lmax.disruptor.RingBuffer;
import com.xenaksys.szcore.event.BagZscoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BagEventDisruptorPublisher {
    static final Logger LOG = LoggerFactory.getLogger(BagEventDisruptorPublisher.class);

    private final RingBuffer<BagZscoreEvent> ringBuffer;
    private final BagEventTranslator translator = new BagEventTranslator();

    private volatile boolean isActive = false;

    public BagEventDisruptorPublisher(RingBuffer<BagZscoreEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void publish(BagZscoreEvent event) throws Exception {
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

