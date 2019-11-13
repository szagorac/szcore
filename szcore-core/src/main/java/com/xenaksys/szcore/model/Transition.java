package com.xenaksys.szcore.model;

public interface Transition extends Script {

    public String getComponent();
    public long getDuration();
    public long getStartValue();
    public long getEndValue() ;
    public long getFrequency();

}
