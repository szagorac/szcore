package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DateTickEvent extends OscJavascriptEvent {
    static final Logger LOG = LoggerFactory.getLogger(DateTickEvent.class);

    private final int staveNo;
    private int beatNo;
    private String baseJsCommand;

    public DateTickEvent(List<Object> arguments, String destination, int staveNo, int beatNo, long time) {
        super(arguments, null, destination, time);
        this.staveNo = staveNo;
        this.beatNo = beatNo;
        this.baseJsCommand = Consts.OSC_JS_SET_DATE.replace(Consts.STAVE_NO, Integer.toString(staveNo));
    }

    public void addCommandArg() {
        String jsCommand = baseJsCommand.replace(Consts.BEAT_NO, Integer.toString(beatNo));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
//LOG.info("jsCommand: " + jsCommand);
        args.add(1, jsCommand);
    }

    public int getStaveNo() {
        return staveNo;
    }

    public int getBeatNo() {
        return beatNo;
    }

    public void setBeatNo(int beatNo) {
        this.beatNo = beatNo;
    }

    public String getBaseJsCommand() {
        return baseJsCommand;
    }

    public OscEventType getOscEventType() {
        return OscEventType.DATE_TICK;
    }
}
