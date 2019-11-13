package com.xenaksys.szcore.gui.processor;

import com.xenaksys.szcore.gui.view.LoggerController;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Processor;
import com.xenaksys.szcore.model.SzcoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class GuiLoggerProcessor implements Processor {
    static final Logger LOG = LoggerFactory.getLogger(GuiLoggerProcessor.class);

    private final static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

    private final LoggerController controller;
    private StringBuilder sb = new StringBuilder();
    private final Clock clock;

    public GuiLoggerProcessor(LoggerController controller, Clock clock) {
        this.controller = controller;
        this.clock = clock;
    }

    @Override
    public void process(SzcoreEvent event) {
        if(event == null){
            return;
        }

//        sb.setLength(0);
//        long now = clock.getSystemTimeMillis();
//        Date dnow = new Date(now);
//
//        sb.append(formatter.format(dnow));
//        sb.append(Consts.COLUMN);
//        sb.append(Consts.SPACE);
//
//        sb.append(event.toString());
//        controller.writeLine(sb.toString());
    }
}
