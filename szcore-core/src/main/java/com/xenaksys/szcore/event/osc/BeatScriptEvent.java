package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class BeatScriptEvent extends OscJavascriptEvent {

    public BeatScriptEvent(List<Object> arguments, BeatId beatId, String destination, long time) {
        super(arguments, beatId, destination, time);
    }

    public void addCommandArg(String script) {
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, script);

    }

    @Override
    public String toString() {
        return "BeatScriptEvent{" + super.toString() + "}";
    }

    public OscEventType getOscEventType() {
        return OscEventType.BEAT_SCRIPT;
    }
}
