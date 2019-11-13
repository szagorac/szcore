package com.xenaksys.szcore.model;

import com.xenaksys.szcore.net.osc.OSCPortOut;

import java.util.Collection;
import java.util.Map;

public interface OscPublisher extends Processor {

    void addOscPort(String destination, OSCPortOut port);

    OSCPortOut getOutPort(String destination);

    void setPublishPorts(Map<String, OSCPortOut> oscPublishPorts);

    boolean isDestination(String destination, int port);

    void removeDestination(String destination);

    Collection<String> getDestinations();

    void start();
}
