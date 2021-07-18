package com.xenaksys.szcore.event.osc;

import java.util.ArrayList;
import java.util.List;

public class ElementAlphaEvent extends OscEvent {

    public ElementAlphaEvent(String address, List<Object> arguments, String destination,long time) {
        super(address, arguments, null, destination, time);
    }

    public void setAlpha(int yPosition) {
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        if (args.size() == 2) {
            args.remove(1);
        }
        args.add(1, yPosition);
    }

    public OscEventType getOscEventType() {
        return OscEventType.ELEMENT_ALPHA;
    }
}
