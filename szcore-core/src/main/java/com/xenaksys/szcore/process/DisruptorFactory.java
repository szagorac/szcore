package com.xenaksys.szcore.process;

import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventContainer;
import com.xenaksys.szcore.event.osc.IncomingOscEvent;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.event.web.OutgoingWebEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DisruptorFactory {
    static final Logger LOG = LoggerFactory.getLogger(DisruptorFactory.class);

    private static final AtomicInteger DEFAULT_POOL_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger OSC_OUT_POOL_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger WEB_OUT_POOL_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger IN_POOL_NUMBER = new AtomicInteger(1);
    private static final AtomicInteger WEB_IN_POOL_NUMBER = new AtomicInteger(1);

    public static Disruptor<OscEvent> createDefaultDisruptor() {
        int bufferSize = 512;
        OscEventFactory eventFactory = new OscEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_THREAD_FACTORY + "_" +  DEFAULT_POOL_NUMBER.getAndIncrement());
        LOG.info("Created thread factory: " + threadFactory.getNamePrefix());
        return new Disruptor<>(eventFactory, bufferSize, threadFactory);
    }

    public static Disruptor<OscEvent> createOscOutDisruptor() {
        int bufferSize = 512;
        OscEventFactory eventFactory = new OscEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_OSC_OUT_THREAD_FACTORY + "_" +  OSC_OUT_POOL_NUMBER.getAndIncrement());
        LOG.info("Created thread factory: " + threadFactory.getNamePrefix());
        return new Disruptor<>(eventFactory, bufferSize, threadFactory);
    }

    public static Disruptor<OutgoingWebEvent> createWebOutDisruptor() {
        int bufferSize = 512;
        WebEventFactory eventFactory = new WebEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_WEB_OUT_THREAD_FACTORY + "_" +  WEB_OUT_POOL_NUMBER.getAndIncrement());
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

    public static Disruptor<EventContainer> createContainerInDisruptor() {
        int bufferSize = 2048;
        IncomingEventFactory eventFactory = new IncomingEventFactory();
        SzcoreThreadFactory threadFactory = new SzcoreThreadFactory(Consts.DISRUPTOR_CONTAINER_IN_THREAD_FACTORY + "_" +  WEB_IN_POOL_NUMBER.getAndIncrement());
        LOG.info("Created thread factory: " + threadFactory.getNamePrefix());
        return new Disruptor<>(eventFactory, bufferSize, threadFactory);
    }
}
