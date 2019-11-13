package com.xenaksys.szcore.process;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SzcoreThreadFactory implements ThreadFactory {
    static final Logger LOG = LoggerFactory.getLogger(SzcoreThreadFactory.class);

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public SzcoreThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + Consts.DEFAULT_THREAD_SUFFIX;
    }

    public Thread newThread(Runnable r) {
        int threadNo = threadNumber.getAndIncrement();
        if(threadNo > 1){
            LOG.warn("Creating multiple threads in factory: " + namePrefix);
        }

        String threadName = namePrefix + threadNo;
        LOG.info("Creating Thread: " + threadName);
        Thread t = new Thread(group, r, threadName, 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    public String getNamePrefix() {
        return namePrefix;
    }
}
