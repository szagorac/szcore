package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.IncomingOscEvent;
import com.xenaksys.szcore.model.OscReceiver;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractOscReceiverDisruptorProcessor implements OscReceiver {
    static final Logger LOG = LoggerFactory.getLogger(AbstractOscReceiverDisruptorProcessor.class);


    private final Disruptor<IncomingOscEvent> disruptor;
    private final ProcessorEventHandler eventHandler;
    private final OscDisruptorReceiver receiver;

    public AbstractOscReceiverDisruptorProcessor(Disruptor<IncomingOscEvent> disruptor) {
        this.disruptor = disruptor;
        this.eventHandler = new ProcessorEventHandler();
        this.receiver = new OscDisruptorReceiver(disruptor.getRingBuffer());
        this.disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void process(SzcoreEvent event) {
        if(event == null || !(event instanceof IncomingOscEvent)){
            return;
        }

        try {
            IncomingOscEvent oscEvent = (IncomingOscEvent)event;
            receiver.publish(oscEvent);
        } catch (Exception e) {
            LOG.error("Failed to publish event: " + event, e);
        }

    }

    public void start(){
        receiver.setActive(true);
    }

    public void stop(){
        receiver.setActive(false);
    }

    abstract protected void processInternal(IncomingOscEvent event);


    class ProcessorEventHandler implements EventHandler<IncomingOscEvent> {

        @Override
        public void onEvent(IncomingOscEvent event, long sequence, boolean endOfBatch) throws Exception {
            processInternal(event);
        }
    }

}
