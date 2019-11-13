package com.xenaksys.szcore.publish;


import com.xenaksys.szcore.net.osc.OSCMessage;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingOscPublishProcessor extends OscPublishProcessor{
    static final Logger LOG = LoggerFactory.getLogger(LoggingOscPublishProcessor.class);


    protected void sendMessage(OSCMessage msg, OSCPortOut port){
        LOG.info("Sending msg address: " + msg.getAddress() + " args: " + msg.getArguments());
    }
}
