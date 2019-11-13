package com.xenaksys.szcore.event;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.id.BeatId;

import java.util.List;

public class OscJavascriptEvent extends OscEvent {

    OscJavascriptEvent(List<Object> arguments, BeatId eventBaseBeat, String destination, long time) {
        super(Consts.OSC_ADDRESS_SCORE_JAVASCRIPT, arguments, eventBaseBeat, destination, time);
    }
}
