package com.xenaksys.szcore.event;


import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OscEvent implements SzcoreEvent {

    private final static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

    private String address;
    private List<Object> arguments = new ArrayList<>();
    private BeatId eventBaseBeat;
    private String destination;
    private long time;

    public OscEvent() {
    }

    public OscEvent(String address, List<Object> arguments, long time) {
        this(address, arguments, null, time);
    }

    public OscEvent(String address, List<Object> arguments, BeatId eventBaseBeat, long time) {
        this(address, arguments, eventBaseBeat, null, time);
    }

    public OscEvent(String address, List<Object> arguments, BeatId eventBaseBeat, String destination, long time) {
        this.address = address;
        this.arguments = arguments;
        this.eventBaseBeat = eventBaseBeat;
        this.destination = destination;
        this.time = time;
    }

    @Override
    public EventType getEventType() {
        return EventType.OSC;
    }

    @Override
    public BeatId getEventBaseBeat() {
        return eventBaseBeat;
    }

    public String getDestination() {
        return destination;
    }

    public String getAddress() {
        return address;
    }

    public OscEventType getOscEventType() {
        return OscEventType.GENERIC;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public long getCreationTime() {
        return time;
    };

    public void setAddress(String address) {
        this.address = address;
    }

    public void addArgument(Object arg){
        if(arguments == null){
            arguments = new ArrayList<>();
        }

        arguments.add(arg);
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    public void setEventBaseBeat(BeatId eventBaseBeat) {
        this.eventBaseBeat = eventBaseBeat;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OscEvent{" +
                "address='" + address + '\'' +
                ", arguments=" + arguments +
                ", eventBaseBeat=" + eventBaseBeat +
                ", destination='" + destination + '\'' +
                ", eventType='" + getEventType() + '\'' +
                ", oscEventType='" + getOscEventType() + '\'' +
                '}';
    }
}
