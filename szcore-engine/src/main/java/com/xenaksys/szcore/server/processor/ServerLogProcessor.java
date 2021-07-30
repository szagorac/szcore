package com.xenaksys.szcore.server.processor;

import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.SzcoreLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLogProcessor implements Processor {
    static final Logger LOG = LoggerFactory.getLogger(ServerLogProcessor.class);

    private final SzcoreLogger logger;

    public ServerLogProcessor(SzcoreLogger logger) {
        this.logger = logger;
    }

    @Override
    public void process(SzcoreEvent event) {

        if(event instanceof OscEvent){
            OscEvent oscEvent = (OscEvent)event;
            logger.info("Received message address: " + oscEvent.getAddress() + " args: " + oscEvent.getArguments());
        } else {
            logger.info("Received message type: " + event.getEventType() + " baseBeat: " + event.getEventBaseBeat());
        }

    }
}
