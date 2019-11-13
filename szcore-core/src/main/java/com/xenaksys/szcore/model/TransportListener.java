package com.xenaksys.szcore.model;


public interface TransportListener {

    void onClockTick(int beatNo, int tickNo);

    void onBaseBeat(int beatNo);

    void onTempoChange(Tempo tempo);

    void onPositionChange(int beatNo);

}
