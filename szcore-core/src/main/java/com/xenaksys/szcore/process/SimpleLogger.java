package com.xenaksys.szcore.process;

import com.xenaksys.szcore.model.SzcoreLogger;
import org.slf4j.LoggerFactory;

public class SimpleLogger implements SzcoreLogger {
    static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SimpleLogger.class);

    @Override
    public void info(String value) {
//        LOG.info(value);
    }

    @Override
    public void error(String value) {
        LOG.error(value);
    }

    @Override
    public void warn(String value) {
        LOG.warn(value);
    }
}
