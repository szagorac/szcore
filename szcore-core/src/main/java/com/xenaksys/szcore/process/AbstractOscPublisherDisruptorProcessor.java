package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractOscPublisherDisruptorProcessor implements OscPublisher {
    static final Logger LOG = LoggerFactory.getLogger(AbstractOscPublisherDisruptorProcessor.class);


    private final Disruptor<OscEvent> disruptor;
    private final ProcessorEventHandler eventHandler;
    private final OscDisruptorPublisher publisher;
    private OSCPortOut broadcastPort;

    public AbstractOscPublisherDisruptorProcessor(Disruptor<OscEvent> disruptor) {
        this.disruptor = disruptor;
        this.eventHandler = new ProcessorEventHandler();
        this.publisher = new OscDisruptorPublisher(disruptor.getRingBuffer());
        this.disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void process(SzcoreEvent event) {
        if(event == null || !(event instanceof OscEvent)){
            return;
        }

        try {
            OscEvent oscEvent = (OscEvent)event;
            publisher.publish(oscEvent);
        } catch (Exception e) {
            LOG.error("Failed to publish event: " + event, e);
        }

    }
    @Override
    public void setOscBroadcastPort(OSCPortOut port) {
        if(port != null) {
            this.broadcastPort = port;
        }
    }

    @Override
    public OSCPortOut getBroadcastPort() {
        return broadcastPort;
    }

    public void start(){
        publisher.setActive(true);
    }

    public void stop(){
        publisher.setActive(false);
    }

    abstract protected void processInternal(OscEvent event);


    class ProcessorEventHandler implements EventHandler<OscEvent> {

        @Override
        public void onEvent(OscEvent event, long sequence, boolean endOfBatch) throws Exception {
            processInternal(event);
        }
    }

}
