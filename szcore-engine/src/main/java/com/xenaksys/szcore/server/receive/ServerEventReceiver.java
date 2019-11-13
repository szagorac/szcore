package com.xenaksys.szcore.server.receive;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.OscReceiver;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.receive.AbstractIncomingEventListener;
import com.xenaksys.szcore.receive.OscReceiveProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerEventReceiver extends AbstractIncomingEventListener {
    static final Logger LOG = LoggerFactory.getLogger(ServerEventReceiver.class);

    private final OscReceiver processor;
    private final OscReceiveProcessor eventReceiver;

    public ServerEventReceiver(OscReceiver processor, OscReceiveProcessor eventReceiver, OscListenerId id) {
        super(id);
        this.processor = processor;
        this.eventReceiver = eventReceiver;
    }

    public void init(){
//        eventReceiver.addListener(this, Consts.DEFAULT_OSC_OUT_PORT);
//        eventReceiver.addListener(this, Consts.DEFAULT_OSC_PORT);
//        eventReceiver.addListener(this, Consts.DEFAULT_OSC_ERR_PORT);
        eventReceiver.addListener(this, Consts.DEFAULT_OSC_SERVER_PORT);
    }

    @Override
    public void onEvent(SzcoreEvent event) {
        if(event == null){
            return;
        }
        processor.process(event);
    }
}
