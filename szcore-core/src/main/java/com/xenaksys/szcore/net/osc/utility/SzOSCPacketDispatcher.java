/*
 * Copyright (C) 2003-2014, C. Ramakrishnan / Auracle.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.xenaksys.szcore.net.osc.utility;

import com.xenaksys.szcore.event.osc.IncomingOscEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.net.osc.AddressSelector;
import com.xenaksys.szcore.net.osc.OSCBundle;
import com.xenaksys.szcore.net.osc.OSCListener;
import com.xenaksys.szcore.net.osc.OSCMessage;
import com.xenaksys.szcore.net.osc.OSCPacket;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SzOSCPacketDispatcher extends OSCPacketDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(SzOSCPacketDispatcher.class);

    protected final List<SzcoreIncomingEventListener> listeners = new CopyOnWriteArrayList<>();
    protected final Clock clock;

    public SzOSCPacketDispatcher(Clock clock) {
        this.clock = clock;
    }


    public void addListener(SzcoreIncomingEventListener listener) {
        listeners.add(listener);
    }

    public void addListener(AddressSelector addressSelector, OSCListener listener) {
        LOG.error("Unsupported method: addListener(AddressSelector addressSelector, OSCListener listener)");
    }

    public void dispatchPacket(OSCPacket packet) {
        dispatchPacket(packet, clock.getSystemTimeMillis());
    }

    public void dispatchPacket(OSCPacket packet, long timestamp) {
        if (packet instanceof OSCBundle) {
            dispatchBundle((OSCBundle) packet);
        } else {
            dispatchMessage((OSCMessage) packet, timestamp);
        }
    }

    protected void dispatchBundle(OSCBundle bundle) {
        final Date timestamp = bundle.getTimestamp();
        final List<OSCPacket> packets = bundle.getPackets();
        for (final OSCPacket packet : packets) {
            dispatchPacket(packet, timestamp);
        }
    }

    protected void dispatchMessage(OSCMessage message, long time) {
        //TODO optimise - do not create new object each time
        IncomingOscEvent event = new IncomingOscEvent(message, time);
        for (SzcoreIncomingEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
