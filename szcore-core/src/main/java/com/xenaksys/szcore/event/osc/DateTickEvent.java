package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class DateTickEvent extends OscJavascriptEvent {
    private final StaveId staveId;
    private final int staveNo;
    private final String baseJsCommand;

    private int beatNo;

    public DateTickEvent(List<Object> arguments, String destination, StaveId staveId, int beatNo, long time) {
        super(arguments, null, destination, time);
        this.staveId = staveId;
        this.staveNo = staveId.getStaveNo();
        this.beatNo = beatNo;
        this.baseJsCommand = Consts.OSC_JS_SET_DATE.replace(Consts.STAVE_NO, Integer.toString(staveNo));
    }

    public void addCommandArg() {
        String jsCommand = baseJsCommand.replace(Consts.BEAT_NO, Integer.toString(beatNo));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
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

    public StaveId getStaveId() {
        return staveId;
    }

    public OscEventType getOscEventType() {
        return OscEventType.DATE_TICK;
    }
}
