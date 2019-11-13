package com.xenaksys.szcore.gui.event;

import com.xenaksys.szcore.gui.processor.ClientEventProcessor;
import com.xenaksys.szcore.model.EventService;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.receive.AbstractIncomingEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientIncomingEventReceiver extends AbstractIncomingEventListener {
    static final Logger LOG = LoggerFactory.getLogger(ClientIncomingEventReceiver.class);

    private final ClientEventProcessor processor;
    private final EventService eventService;

    public ClientIncomingEventReceiver(ClientEventProcessor processor, EventService eventService, OscListenerId id) {
        super(id);
        this.processor = processor;
        this.eventService = eventService;
    }

    public void init(){
        eventService.subscribe(this);
    }

    @Override
    public void onEvent(SzcoreEvent event) {
        if(event == null){
            return;
        }
        processor.process(event);
    }
}
