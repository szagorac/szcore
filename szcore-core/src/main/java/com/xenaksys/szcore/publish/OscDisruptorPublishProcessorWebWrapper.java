package com.xenaksys.szcore.publish;

import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.EventType;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.web.WebProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscDisruptorPublishProcessorWebWrapper extends OscDisruptorPublishProcessor {
    static final Logger LOG = LoggerFactory.getLogger(OscDisruptorPublishProcessorWebWrapper.class);

    private final WebProcessor webProcessor;

    public OscDisruptorPublishProcessorWebWrapper(Disruptor<OscEvent> disruptor, WebProcessor webProcessor) {
        super(disruptor);
        this.webProcessor = webProcessor;
    }

    @Override
    public void process(SzcoreEvent event) {
        EventType type = event.getEventType();
        if (type != EventType.OSC) {
            return;
        }
        OscEvent oscEvent = (OscEvent) event;
        sendToWebScore(oscEvent);
        String destination = oscEvent.getDestination();
        if (isDestination(destination)) {
            super.process(oscEvent);
        }
    }

    //HACK to intercept osc events and send to WebScore
    private void sendToWebScore(OscEvent event) {
        webProcessor.onInterceptedOscOutEvent(event);
    }
}
