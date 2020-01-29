package com.xenaksys.szcore.model;

import com.xenaksys.szcore.net.osc.OSCPortOut;

import java.util.Collection;
import java.util.Map;

public interface OscPublisher extends Processor {

    void addOscPort(String destination, OSCPortOut port);

    void setOscBroadcastPort(OSCPortOut port);

    OSCPortOut getOutPort(String destination);

    OSCPortOut getBroadcastPort();

    void setPublishPorts(Map<String, OSCPortOut> oscPublishPorts);

    boolean isDestination(String destination, int port);

    void removeDestination(String destination);

    Collection<String> getDestinations();

    void start();
}
