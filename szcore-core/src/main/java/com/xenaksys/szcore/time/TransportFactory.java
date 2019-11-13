package com.xenaksys.szcore.time;


import com.xenaksys.szcore.model.BeatTimeStrategy;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Scheduler;
import com.xenaksys.szcore.model.Transport;
import com.xenaksys.szcore.model.id.StrId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransportFactory {

    private final Clock clock;
    private final Scheduler scheduler;
    private final BeatTimeStrategy beatTimeStrategy;

    private Map<Id, Transport> transports = new ConcurrentHashMap<>();

    public TransportFactory(Clock clock, Scheduler scheduler, BeatTimeStrategy beatTimeStrategy) {
        this.clock = clock;
        this.scheduler = scheduler;
        this.beatTimeStrategy = beatTimeStrategy;
    }

    public Transport getTransport(String name) {
        StrId id = new StrId(name);
        return getTransport(id);
    }

    public Transport getTransport(Id transportId) {
        Transport transport = transports.get(transportId);
        if (transport == null) {
            transport = new BasicTransport(transportId, clock, scheduler, beatTimeStrategy);
            transports.put(transportId, transport);
        }

        return transport;
    }
}
