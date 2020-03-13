package com.xenaksys.szcore.model;

public interface Transition extends Script {

    String getComponent();
    long getDuration();
    long getStartValue();
    long getEndValue() ;
    long getFrequency();

    @Override
    default ScriptType getType() {
        return ScriptType.TRANSITION;
    }
}
