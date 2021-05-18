package com.xenaksys.szcore.event;

import java.util.ArrayList;
import java.util.List;

public class ElementColorEvent extends OscEvent {

    public ElementColorEvent(String address, List<Object> arguments, String destination, long time) {
        super(address, arguments, null, destination, time);
    }

    public void setColor(int r, int g, int b, int alpha) {
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        while ( args.size() > 1) {
            args.remove(1);
        }
        args.add(1, r);
        args.add(2, g);
        args.add(3, b);
        args.add(4, alpha);
    }

    public void setColor(int r, int g, int b) {
        ArrayList<Object> args = (ArrayList<Object>) getArguments();
        while ( args.size() > 1) {
            args.remove(1);
        }
        args.add(1, r);
        args.add(2, g);
        args.add(3, b);
    }

    public OscEventType getOscEventType() {
        return OscEventType.ELEMENT_COLOR;
    }
}
