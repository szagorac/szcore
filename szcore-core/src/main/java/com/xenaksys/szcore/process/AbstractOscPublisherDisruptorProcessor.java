package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.model.OscPublisher;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractOscPublisherDisruptorProcessor implements OscPublisher {
    static final Logger LOG = LoggerFactory.getLogger(AbstractOscPublisherDisruptorProcessor.class);


    private final Disruptor<OscEvent> disruptor;
    private final ProcessorEventHandler eventHandler;
    private final OscDisruptorPublisher publisher;
    private List<OSCPortOut> broadcastPorts = new ArrayList<>();

    public AbstractOscPublisherDisruptorProcessor(Disruptor<OscEvent> disruptor) {
        this.disruptor = disruptor;
        this.eventHandler = new ProcessorEventHandler();
        this.publisher = new OscDisruptorPublisher(disruptor.getRingBuffer());
        this.disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void process(SzcoreEvent event) {
        if (!(event instanceof OscEvent)) {
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
    public void addOscBroadcastPort(OSCPortOut port) {
        if(port != null) {
            this.broadcastPorts.add(port);
        }
    }

    @Override
    public List<OSCPortOut> getBroadcastPorts() {
        return broadcastPorts;
    }

    @Override
    public void resetBroadcastPorts() {
        broadcastPorts.clear();
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
