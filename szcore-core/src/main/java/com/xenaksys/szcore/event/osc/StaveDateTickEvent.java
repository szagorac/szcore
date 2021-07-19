package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.StaveId;

import java.util.List;

public class StaveDateTickEvent extends OscEvent {
    private final StaveId staveId;
    private final int beatNo;

    public StaveDateTickEvent(String address, List<Object> arguments, String destination, StaveId staveId, long time) {
        this(address, arguments, destination, staveId, 0, time);
    }

    public StaveDateTickEvent(String address, List<Object> arguments, String destination, StaveId staveId, int beatNo, long time) {
        super(address, arguments, null, destination, time);
        this.staveId = staveId;
        this.beatNo = beatNo;
        setArgs();
    }

    public StaveId getStaveId() {
        return staveId;
    }

    public int getBeatNo() {
        return beatNo;
    }

    public void setArgs(){
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        if(beatNo != 0) {
            String beat = beatNo + Consts.EIGHTH;
            args.add(1, beat);
        }
    }

    public OscEventType getOscEventType() {
        return OscEventType.STAVE_DATE_TICK;
    }
}
