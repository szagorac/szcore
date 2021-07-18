package com.xenaksys.szcore.publish;

import com.lmax.disruptor.dsl.Disruptor;
import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.osc.OscEvent;
import com.xenaksys.szcore.net.osc.OSCMessage;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import com.xenaksys.szcore.process.AbstractOscPublisherDisruptorProcessor;
import com.xenaksys.szcore.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xenaksys.szcore.Consts.ALLOWED_DESTINATIONS;

public class OscDisruptorPublishProcessor extends AbstractOscPublisherDisruptorProcessor {
    static final Logger LOG = LoggerFactory.getLogger(OscDisruptorPublishProcessor.class);

    private Map<String, OSCPortOut> oscPublishPorts = new ConcurrentHashMap<>();
    private List<String> toRemove = new ArrayList<>();

    public OscDisruptorPublishProcessor(Disruptor<OscEvent> disruptor) {
        super(disruptor);
    }

    public void addOscPort(String destination, OSCPortOut port) {
        if(port == null || destination == null){
            return;
        }

        InetAddress inetAddress = port.getAddress();
        if (inetAddress == null) {
            return;
        }

        String hostAddr = inetAddress.getHostAddress();
        if (hostAddr == null) {
            return;
        }

        int portNo = port.getPort();
        String clientId = NetUtil.createClientId(hostAddr, portNo);

        toRemove.clear();
        for (String outKey : oscPublishPorts.keySet()) {
            OSCPortOut publishPort = oscPublishPorts.get(outKey);
            InetAddress publishInetAddress = publishPort.getAddress();
            String publishHostAddr = publishInetAddress.getHostAddress();
            int publishPortNo = publishPort.getPort();

            if (hostAddr.equals(publishHostAddr) && portNo == publishPortNo) {
                LOG.warn("addOscPort: Removing existing out port mapping: {}, adding mapping for destination: {}", outKey, destination);
                toRemove.add(outKey);
            }
        }

        for(String key : toRemove){
            oscPublishPorts.remove(key);
        }

        oscPublishPorts.put(destination, port);
    }

    public void setPublishPorts(Map<String, OSCPortOut> oscPublishPorts) {
        this.oscPublishPorts = oscPublishPorts;
    }

    public OSCPortOut getOutPort(String destination) {
        return oscPublishPorts.get(destination);
    }

    public boolean isDestination(String destination) {
        if (ALLOWED_DESTINATIONS.contains(destination)) {
            return true;
        }
        return oscPublishPorts.containsKey(destination);
    }

    public void removeDestination(String destination) {
        oscPublishPorts.remove(destination);
    }

    public Collection<String> getDestinations() {
        return oscPublishPorts.keySet();
    }


    protected void processInternal(OscEvent oscEvent) {

        String address = oscEvent.getAddress();
        Collection<Object> args = oscEvent.getArguments();

        String destination = oscEvent.getDestination();
        if (destination == null) {
            destination = Consts.DEFAULT_OSC_PORT_NAME;
        }

//        LOG.info("Sending event {} to destination: {}",oscEvent, destination);

        long creationTime = oscEvent.getCreationTime();
//
//long diff = System.currentTimeMillis() - creationTime;
//LOG.debug("Sending message time diff: " + diff + " creationTime: " + creationTime);

        if (Consts.ALL_DESTINATIONS.equals(destination)) {
            for (OSCPortOut port : oscPublishPorts.values()) {
                send(port, address, args);
            }
        } else if (Consts.BROADCAST.equals(destination)) {
            List<OSCPortOut> broadcastPorts = getBroadcastPorts();
            if(broadcastPorts != null && !broadcastPorts.isEmpty()) {
                for(OSCPortOut broadcastPort : broadcastPorts) {
                    send(broadcastPort, address, args);
                }
            }
        } else {
            OSCPortOut port = oscPublishPorts.get(destination);
            if (port == null) {
                LOG.error("Failed to find OSC port for destination: " + destination);
                return;
            }
            send(port, address, args);
        }
    }

    protected void send(OSCPortOut port, String address, Collection<Object> args) {
        if (port == null || address == null || args == null) {
            return;
        }

        OSCMessage msg = new OSCMessage(address, args);
        sendMessage(msg, port);
    }

    protected void sendMessage(OSCMessage msg, OSCPortOut port) {
        try {
//            LOG.info("Sending msg address: " + msg.getAddress() + " args: " + msg.getArguments() + " address: " + port.getAddress().getHostAddress() + " port: " + port.getPort());
            port.send(msg);
        } catch (Exception e) {
            LOG.error("Failed to send message", e);
        }
    }
}
