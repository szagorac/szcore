package com.xenaksys.szcore.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil {
    static final Logger LOG = LoggerFactory.getLogger(ThreadUtil.class);

    public static void doSleep(Thread thread, long milliis){
        try {
            thread.sleep(milliis);
        } catch (InterruptedException e) {
            LOG.error("Scheduler interrupted", e);
        }
    }

}
