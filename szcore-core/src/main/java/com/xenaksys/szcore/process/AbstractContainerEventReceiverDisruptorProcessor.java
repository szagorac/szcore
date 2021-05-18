package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.EventContainer;
import com.xenaksys.szcore.model.EventReceiver;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractContainerEventReceiverDisruptorProcessor implements EventReceiver {
    static final Logger LOG = LoggerFactory.getLogger(AbstractContainerEventReceiverDisruptorProcessor.class);


    private final Disruptor<EventContainer> disruptor;
    private final ProcessorEventHandler eventHandler;
    private final EventContainerDisruptorReceiver receiver;

    public AbstractContainerEventReceiverDisruptorProcessor(Disruptor<EventContainer> disruptor) {
        this.disruptor = disruptor;
        this.eventHandler = new ProcessorEventHandler();
        this.receiver = new EventContainerDisruptorReceiver(disruptor.getRingBuffer());
        this.disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void process(SzcoreEvent event) {
        try {
            receiver.publish(event);
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

    abstract protected void processInternal(EventContainer event);


    class ProcessorEventHandler implements EventHandler<EventContainer> {

        @Override
        public void onEvent(EventContainer event, long sequence, boolean endOfBatch) throws Exception {
            processInternal(event);
        }
    }

}
