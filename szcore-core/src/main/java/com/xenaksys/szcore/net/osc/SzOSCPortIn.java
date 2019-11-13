/*
 * Copyright (C) 2004-2014, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.xenaksys.szcore.net.osc;

import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.net.osc.utility.OSCByteArrayToJavaConverter;
import com.xenaksys.szcore.net.osc.utility.SzOSCPacketDispatcher;
import com.xenaksys.szcore.receive.SzcoreDistributorListener;
import com.xenaksys.szcore.receive.SzcoreIncomingEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SzOSCPortIn extends OSCPortIn {
    static final Logger LOG = LoggerFactory.getLogger(SzOSCPortIn.class);

    /**
     * Buffers were 1500 bytes in size, but were
     * increased to 1536, as this is a common MTU.
     */
    private static final int BUFFER_SIZE = 1536;

    /**
     * state for listening
     */
    private boolean listening;
    private final Map<String, SzcoreDistributorListener> addressListeners = new HashMap<>();

    /**
     * Create an OSCPort that listens using a specified socket.
     *
     * @param socket DatagramSocket to listen on.
     */
    public SzOSCPortIn(DatagramSocket socket, Clock clock) {
        this(socket, new SzOSCPacketDispatcher(clock), new OSCByteArrayToJavaConverter());
    }

    public SzOSCPortIn(DatagramSocket socket, SzOSCPacketDispatcher dispatcher) {
        this(socket, dispatcher, new OSCByteArrayToJavaConverter());
    }

    public SzOSCPortIn(DatagramSocket socket, SzOSCPacketDispatcher dispatcher, OSCByteArrayToJavaConverter converter) {
        super(socket, dispatcher, converter);
    }

    /**
     * Create an OSCPort that listens on the specified port.
     * Strings will be decoded using the systems default character set.
     *
     * @param port UDP port to listen on.
     * @throws SocketException if the port number is invalid,
     *                         or there is already a socket listening on it
     */
    public SzOSCPortIn(int port, Clock clock) throws SocketException {
        this(new DatagramSocket(port), clock);
    }

    public SzOSCPortIn(int port, SzOSCPacketDispatcher dispatcher) throws SocketException {
        this(new DatagramSocket(port), dispatcher);
    }

    public SzOSCPortIn(int port, SzOSCPacketDispatcher dispatcher, OSCByteArrayToJavaConverter converter) throws SocketException {
        this(new DatagramSocket(port), dispatcher, converter);
    }

    /**
     * Create an OSCPort that listens on the specified port,
     * and decodes strings with a specific character set.
     *
     * @param port    UDP port to listen on.
     * @param charset how to decode strings read from incoming packages.
     *                This includes message addresses and string parameters.
     * @throws SocketException if the port number is invalid,
     *                         or there is already a socket listening on it
     */
    public SzOSCPortIn(int port, Charset charset, Clock clock) throws SocketException {
        this(port, clock);

        this.converter.setCharset(charset);
    }

    /**
     * Run the loop that listens for OSC on a socket until
     * {@link #isListening()} becomes false.
     *
     * @see Runnable#run()
     */
    @Override
    public void run() {
        final byte[] buffer = new byte[BUFFER_SIZE];
        final DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
        final DatagramSocket socket = getSocket();
        while (listening) {
            try {
                try {
                    socket.receive(packet);
                } catch (SocketException ex) {
                    if (listening) {
                        throw ex;
                    } else {
                        // if we closed the socket while receiving data,
                        // the exception is expected/normal, so we hide it
                        continue;
                    }
                }
                final OSCPacket oscPacket = converter.convert(buffer,
                        packet.getLength(), packet.getAddress(), packet.getPort(), getPort());
                dispatcher.dispatchPacket(oscPacket);
            } catch (IOException ex) {
                ex.printStackTrace(); // XXX This may not be a good idea, as this could easily lead to a never ending series of exceptions thrown (due to the non-exited while loop), and because the user of the lib may want to handle this case himself
            }
        }
    }

    /**
     * Start listening for incoming OSCPackets
     */
    public void startListening() {
        if (listening) {
            return;
        }
        listening = true;
        final Thread thread = new Thread(this);
        // The JVM exits when the only threads running are all daemon threads.
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stop listening for incoming OSCPackets
     */
    public void stopListening() {
        listening = false;
    }

    /**
     * Am I listening for packets?
     *
     * @return true if this port is in listening mode
     */
    public boolean isListening() {
        return listening;
    }

    public void addListener(String addressSelector, OSCListener listener) {
        LOG.error("Unsupported method: addListener(String addressSelector, OSCListener listener)");
    }

    /**
     * Registers a listener that will be notified of incoming messages,
     * if their address matches the given selector.
     *
     * @param listener will be notified of incoming packets, if they match
     */
    public void addListener(SzcoreIncomingEventListener listener) {
        SzOSCPacketDispatcher szDispatcher = (SzOSCPacketDispatcher) dispatcher;
        szDispatcher.addListener(listener);
    }
}
