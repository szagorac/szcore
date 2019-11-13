package com.xenaksys.szcore.process;

import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.IncomingOscEvent;
import com.xenaksys.szcore.event.OscEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DisruptorFactory {
    static final Logger LOG = LoggerFactory.getLogger(DisruptorFactory.class);

    private static final AtomicInteger DEFAULT_POOL_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger OUT_POOL_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger IN_POOL_NUMBER = new AtomicInteger(1);

    public static Disruptor<OscEvent> createDefaultDisruptor() {
        int bufferSize = 512;
        OscEventFactory eventFactory = new OscEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_THREAD_FACTORY + "_" +  DEFAULT_POOL_NUMBER.getAndIncrement());
        LOG.info("Created thread factory: " + threadFactory.getNamePrefix());
        return new Disruptor<>(eventFactory, bufferSize, threadFactory);
    }

    public static Disruptor<OscEvent> createOutDisruptor() {
        int bufferSize = 512;
        OscEventFactory eventFactory = new OscEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_OUT_THREAD_FACTORY + "_" +  OUT_POOL_NUMBER.getAndIncrement());
        LOG.info("Created thread factory: " + threadFactory.getNamePrefix());
        return new Disruptor<>(eventFactory, bufferSize, threadFactory);
    }

    public static Disruptor<IncomingOscEvent> createInDisruptor() {
        int bufferSize = 512;
        IncomingOscEventFactory eventFactory = new IncomingOscEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_IN_THREAD_FACTORY + "_" +  IN_POOL_NUMBER.getAndIncrement());
        LOG.info("Created thread factory: " + threadFactory.getNamePrefix());
        return new Disruptor<>(eventFactory, bufferSize, threadFactory);
    }
}
