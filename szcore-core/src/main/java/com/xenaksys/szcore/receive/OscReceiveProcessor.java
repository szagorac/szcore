package com.xenaksys.szcore.receive;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.osc.IncomingOscEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.OscListenerId;
import com.xenaksys.szcore.net.osc.OSCPortIn;
import com.xenaksys.szcore.net.osc.SzOSCPortIn;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class OscReceiveProcessor extends AbstractIncomingEventListener implements Processor {
    static final Logger LOG = LoggerFactory.getLogger(OscReceiveProcessor.class);

    private TIntObjectHashMap<SzOSCPortIn> inPorts = new TIntObjectHashMap<>();
    private TIntObjectHashMap<List<SzcoreIncomingEventListener>> listeners = new TIntObjectHashMap<>();
    private List<AsyncDispatcher> dispatchers = new ArrayList<>();

    private final Clock clock;

    public OscReceiveProcessor(OscListenerId id, Clock clock) {
        super(id);
        this.clock = clock;
    }

    private void listenOnPort(int port) {
        try {
            if (inPorts.containsKey(port)) {
                LOG.warn("Already listening on port: " + port);
                return;
            }
            AsyncDispatcher dispatcher = new AsyncDispatcher(clock);
            dispatchers.add(dispatcher);
            SzOSCPortIn in = new SzOSCPortIn(port, dispatcher);
            inPorts.put(port, in);
        } catch (SocketException e) {
            LOG.error("Failed to add in port: " + port, e);
        }
    }

    public void addListener(SzcoreIncomingEventListener listener, int port) {
        if (port == 0) {
            LOG.warn("Invalid port");
            return;
//            int[] ports = inPorts.keys();
//            for(int p : ports){
//                addPortListener(p, address, listener);
//            }
        } else {
            addPortListener(port, listener);
        }
    }

    private void addPortListener(int port, SzcoreIncomingEventListener listener) {
        SzOSCPortIn portIn = inPorts.get(port);
        if (port != Consts.DEFAULT_ALL_PORTS && portIn == null) {
            listenOnPort(port);
            portIn = inPorts.get(port);
            portIn.addListener(this);
            portIn.startListening();
        }

        if (port != Consts.DEFAULT_ALL_PORTS && portIn == null) {
            LOG.error("Failed to add port: " + port);
            return;
        }

        List<SzcoreIncomingEventListener> pl = listeners.get(port);
        if (pl == null) {
            pl = new ArrayList<>();
            listeners.put(port, pl);
        }
        if (!pl.contains(listener)) {
            pl.add(listener);
        }
    }

    @Override
    public void process(SzcoreEvent event) {
        if (event == null) {
            return;
        }

        IncomingOscEvent incoming = (IncomingOscEvent) event;
        notifyListeners(incoming.getLocalPort(), incoming);
        notifyListeners(Consts.DEFAULT_ALL_PORTS, incoming);
    }

    private void notifyListeners(int port, IncomingOscEvent event) {
        List<SzcoreIncomingEventListener> toNotify = listeners.get(port);
        if (toNotify == null) {
            return;
        }
        for (SzcoreIncomingEventListener listener : toNotify) {
            listener.onEvent(event);
        }
    }

    public void stop() {
        for (Object obj : inPorts.values()) {
            OSCPortIn port = (OSCPortIn) obj;
            if (port.isListening()) {
                port.stopListening();
            }
        }

        for (AsyncDispatcher dispatcher : dispatchers) {
            dispatcher.stop();
        }
    }

    public void start() {
        for (Object obj : inPorts.values()) {
            OSCPortIn port = (OSCPortIn) obj;
            if (!port.isListening()) {
                port.startListening();
            }
        }
    }

    @Override
    public void onEvent(SzcoreEvent event) {
        process(event);
    }

}
