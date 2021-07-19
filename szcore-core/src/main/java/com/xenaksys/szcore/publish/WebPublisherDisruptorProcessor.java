package com.xenaksys.szcore.publish;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.web.out.OutgoingWebEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.WebPublisher;
import com.xenaksys.szcore.process.WebDisruptorPublisher;
import com.xenaksys.szcore.web.WebProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebPublisherDisruptorProcessor implements WebPublisher {
    static final Logger LOG = LoggerFactory.getLogger(WebPublisherDisruptorProcessor.class);


    private final Disruptor<OutgoingWebEvent> disruptor;
    private final ProcessorEventHandler eventHandler;
    private final WebDisruptorPublisher publisher;
    private final WebProcessor webProcessor;

    public WebPublisherDisruptorProcessor(Disruptor<OutgoingWebEvent> disruptor, WebProcessor webProcessor) {
        this.disruptor = disruptor;
        this.eventHandler = new ProcessorEventHandler();
        this.webProcessor = webProcessor;
        this.publisher = new WebDisruptorPublisher(disruptor.getRingBuffer());
        this.disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void process(SzcoreEvent event) {
        if (!(event instanceof OutgoingWebEvent)) {
            return;
        }

        try {
            OutgoingWebEvent webEvent = (OutgoingWebEvent) event;
            publisher.publish(webEvent);
        } catch (Exception e) {
            LOG.error("Failed to publish event: " + event, e);
        }
    }

    public void start() {
        publisher.setActive(true);
    }

    public void stop() {
        publisher.setActive(false);
    }

    protected void processInternal(OutgoingWebEvent event) {
        webProcessor.onOutgoingWebEvent(event);
    }

    class ProcessorEventHandler implements EventHandler<OutgoingWebEvent> {
        @Override
        public void onEvent(OutgoingWebEvent event, long sequence, boolean endOfBatch) throws Exception {
            try {
                processInternal(event);
            } catch (Exception e) {
                LOG.error("Failed to process OutgoingWebAudienceEvent {}", event);
            }
        }
    }

}
