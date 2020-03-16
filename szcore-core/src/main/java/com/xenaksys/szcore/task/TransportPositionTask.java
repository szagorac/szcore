package com.xenaksys.szcore.task;

import com.xenaksys.szcore.event.TransportPositionEvent;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.Transport;

public class TransportPositionTask extends EventMusicTask {
    private final Transport transport;

    public TransportPositionTask(long playTime, TransportPositionEvent event, Transport transport) {
        super(playTime, event);
        this.transport = transport;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof TransportPositionEvent)) {
            return;
        }

        TransportPositionEvent transportPositionEvent = (TransportPositionEvent) event;
        int transportBeatNo = transportPositionEvent.getTransportBeatNo();
        int startBeatNo = transportPositionEvent.getStartBaseBeatNo();
        int tickNo = transportPositionEvent.getTickNo();
        long positionMillis = transportPositionEvent.getPositionMillis();

        if(transport.isRunning()){
            LOG.error("Transport is running, can not change change beat");
            return;
        }
LOG.error("Setting Transport position beatNo: " + transportBeatNo + " positionMillis: " + positionMillis);
        transport.setBeatNo(transportBeatNo);
        transport.setTickNo(tickNo);
        transport.init(positionMillis);
        transport.notifyListenersOnPositionChange(startBeatNo);
    }
}
