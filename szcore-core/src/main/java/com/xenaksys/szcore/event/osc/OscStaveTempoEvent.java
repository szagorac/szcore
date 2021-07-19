package com.xenaksys.szcore.event.osc;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OscStaveTempoEvent extends OscJavascriptEvent {
    static final Logger LOG = LoggerFactory.getLogger(OscStaveTempoEvent.class);

    private int tempo;

    public OscStaveTempoEvent(List<Object> arguments, String destination, int tempo, long time) {
        super(arguments, null, destination, time);
        this.tempo = tempo;
        addCommandArg();
    }

    public void addCommandArg() {
        String jsCommand = Consts.OSC_JS_SET_TEMPO.replace(Consts.TEMPO, Integer.toString(tempo));
        List<Object> args = getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
//LOG.info("jsCommand: " + jsCommand);
        args.add(1, jsCommand);
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
        addCommandArg();
    }

    public OscEventType getOscEventType() {
        return OscEventType.STAVE_TEMPO;
    }
}
