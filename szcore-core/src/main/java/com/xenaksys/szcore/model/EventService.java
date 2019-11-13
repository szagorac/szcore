package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;

import java.net.InetAddress;

public interface EventService {

    void subscribe(SzcoreIncomingEventListener listener);

    void publish(SzcoreEvent event);

    void stop();

    InetAddress getAddress();

    EventFactory getEventFactory();

    Clock getClock();

}
