package com.xenaksys.szcore.process;

import com.lmax.disruptor.RingBuffer;
import com.xenaksys.szcore.event.IncomingOscEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscDisruptorReceiver {
    static final Logger LOG = LoggerFactory.getLogger(OscDisruptorReceiver.class);

    private final RingBuffer<IncomingOscEvent> ringBuffer;
    private final IncomingOscEventTranslator translator = new IncomingOscEventTranslator();

    private volatile boolean isActive = false;

    public OscDisruptorReceiver(RingBuffer<IncomingOscEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void publish(IncomingOscEvent event) throws Exception {
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

