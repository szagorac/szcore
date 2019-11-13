package com.xenaksys.szcore.time.clock;

import com.xenaksys.szcore.model.MutableClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutableNanoClock extends NanoClock implements MutableClock {
    static final Logger LOG = LoggerFactory.getLogger(MutableNanoClock.class);

    public void setElapsedTimeMillis(long elapsedTimeMillis) {

        long current;

        do {
            current = getElapsedTimeMillis();
//            LOG.debug("Setting Clock elapsedTimeMillis: " + elapsedTimeMillis);
        }
        while (!this.elapsedTimeMillis.compareAndSet(current, elapsedTimeMillis));
    }

}
