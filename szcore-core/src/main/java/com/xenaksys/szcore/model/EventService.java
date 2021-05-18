package com.xenaksys.szcore.model;

import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;

public interface EventService {

    void subscribe(SzcoreIncomingEventListener listener);

    void publish(SzcoreEvent event);

    void receive(SzcoreEvent event);

    void stop();

    EventFactory getEventFactory();

    Clock getClock();

}
