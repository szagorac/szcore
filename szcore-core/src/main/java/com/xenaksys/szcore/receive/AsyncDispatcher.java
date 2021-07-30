package com.xenaksys.szcore.receive;

import com.xenaksys.szcore.event.osc.IncomingOscEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.net.osc.OSCMessage;
import com.xenaksys.szcore.net.osc.utility.SzOSCPacketDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;


public class AsyncDispatcher extends SzOSCPacketDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(OscReceiveProcessor.class);

    private final ArrayBlockingQueue<IncomingOscEvent> inQueue = new ArrayBlockingQueue<>(1000);

    private volatile boolean isActive = false;

    public AsyncDispatcher(Clock clock) {
        super(clock);
        isActive = true;
        Consumer consumer = new Consumer();
        Thread worker = new Thread(consumer);
        worker.setDaemon(true);
        worker.start();
    }

    protected void dispatchMessage(OSCMessage message, long currentTime) {
        if (!isActive) {
            return;
        }

        //TODO optimise - avoid new constructor
        IncomingOscEvent event = new IncomingOscEvent(message, currentTime);

        try {
            inQueue.put(event);
        } catch (InterruptedException e) {
            LOG.error("Interrupted osc dispatcher", e);
        }

    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void stop() {
        setActive(false);
    }

    class Consumer implements Runnable {
        public void run() {
            try {
                while (isActive) {
                    IncomingOscEvent event = inQueue.take();
                    for (SzcoreIncomingEventListener listener : listeners) {
                        listener.onEvent(event);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Exiting Dispatcher. ");
        }
    }
}
