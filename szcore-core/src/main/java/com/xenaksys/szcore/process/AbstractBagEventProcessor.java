package com.xenaksys.szcore.process;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.event.BagSzcoreEvent;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractBagEventProcessor  implements Processor {
    static final Logger LOG = LoggerFactory.getLogger(AbstractBagEventProcessor.class);


    private final Disruptor<BagSzcoreEvent> disruptor;
    private final ProcessorEventHandler eventHandler;
    private final BagEventDisruptorPublisher publisher;

    public AbstractBagEventProcessor(Disruptor<BagSzcoreEvent> disruptor) {
        this.disruptor = disruptor;
        this.eventHandler = new ProcessorEventHandler();
        this.publisher = new BagEventDisruptorPublisher(disruptor.getRingBuffer());
        this.disruptor.handleEventsWith(eventHandler);
    }

    @Override
    public void process(SzcoreEvent event) {
        if(event == null || !(event instanceof BagSzcoreEvent)){
            return;
        }

        try {
            BagSzcoreEvent bev = (BagSzcoreEvent)event;
            publisher.publish(bev);
        } catch (Exception e) {
            LOG.error("Failed to publish event: " + event, e);
        }

    }

    public void start(){
        publisher.setActive(true);
    }

    public void stop(){
        publisher.setActive(false);
    }

    abstract protected void processInternal(BagSzcoreEvent event);


    class ProcessorEventHandler implements EventHandler<BagSzcoreEvent> {

        @Override
        public void onEvent(BagSzcoreEvent event, long sequence, boolean endOfBatch) throws Exception {
            processInternal(event);
        }
    }

}
