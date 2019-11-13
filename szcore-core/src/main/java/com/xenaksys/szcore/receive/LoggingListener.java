package com.xenaksys.szcore.receive;

import com.xenaksys.szcore.event.OscEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.OscListenerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingListener extends AbstractIncomingEventListener {
    static final Logger LOG = LoggerFactory.getLogger(LoggingListener.class);

    public LoggingListener(OscListenerId id) {
        super(id);
    }

    @Override
    public void onEvent(SzcoreEvent event) {

        if (event instanceof OscEvent) {
            OscEvent oscEvent = (OscEvent) event;
            LOG.info("Received message address: " + oscEvent.getAddress() + " args: " + oscEvent.getArguments());
        } else {
            LOG.info("Received message type: " + event.getEventType() + " baseBeat: " + event.getEventBaseBeat());
        }

    }
}