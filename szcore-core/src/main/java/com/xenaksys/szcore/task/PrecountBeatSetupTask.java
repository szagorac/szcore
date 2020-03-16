package com.xenaksys.szcore.task;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.PrecountBeatOffEvent;
import com.xenaksys.szcore.event.PrecountBeatOnEvent;
import com.xenaksys.szcore.event.PrecountBeatSetupEvent;
import com.xenaksys.szcore.model.*;

public class PrecountBeatSetupTask extends EventMusicTask {
    private final Transport transport;
    private final Scheduler scheduler;
    private final OscPublisher oscPublisher;
    private final EventFactory eventFactory;
    private final String destination;
    private final Clock clock;

    public PrecountBeatSetupTask(PrecountBeatSetupEvent precountBeatSetupEvent, String destination, Transport transport, Scheduler scheduler,
                                 OscPublisher oscPublisher, EventFactory eventFactory, Clock clock) {
        super(0, precountBeatSetupEvent);
        this.transport = transport;
        this.scheduler = scheduler;
        this.oscPublisher = oscPublisher;
        this.eventFactory = eventFactory;
        this.destination = destination;
        this.clock = clock;
    }

    @Override
    public void play() {
        SzcoreEvent event = getEvent();
        if (!(event instanceof PrecountBeatSetupEvent)) {
            return;
        }

        PrecountBeatSetupEvent precountBeatSetupEvent = (PrecountBeatSetupEvent) event;
        boolean isPrecount = precountBeatSetupEvent.isPrecount();
        if (!isPrecount) {
            return;
        }

        long precountTimeMillis = precountBeatSetupEvent.getPrecountTimeMillis();
        long precountInitBeaterInterval = precountBeatSetupEvent.getInitBeaterInterval();
        int precountBeatNo = precountBeatSetupEvent.getPrecountBeatNo();
        long beatIntervalMillis = transport.getTempoBeatIntervalMillis();
        long startPositionMillis = transport.getStartPositionMillis();
        long halfBeatIntevalMillis = Math.round(beatIntervalMillis / 2.0);
        if((precountInitBeaterInterval*precountBeatNo + beatIntervalMillis*precountBeatNo + 1000) > precountTimeMillis){
            precountTimeMillis = precountInitBeaterInterval*precountBeatNo + beatIntervalMillis*precountBeatNo + 1000;
        }


        for (int i = 1; i <= precountBeatNo; i++) {
            //add beeater init events
            long initPlayTime = -1L * (precountTimeMillis - (i * precountInitBeaterInterval));
            addBeaterOnTask(initPlayTime, i, Consts.OSC_COLOUR_RED);

            //add precount events
            int multiplier = i - 1; //taking upbeat into account
            long onPlayTime = -1L * beatIntervalMillis * multiplier;
            int colourId = getColourId(i);
            addBeaterOnTask(adjustPlaytime(onPlayTime, startPositionMillis), i, colourId);
            long offPlayTime = onPlayTime + halfBeatIntevalMillis;
            addBeaterOffTask(adjustPlaytime(offPlayTime, startPositionMillis), i);

            if (i == precountBeatNo) {
                //set beaters off before count
                offPlayTime = onPlayTime - halfBeatIntevalMillis;
                addBeaterOffTask(adjustPlaytime(offPlayTime, startPositionMillis), i);
            }
        }

        addBeaterOnTask(adjustPlaytime(beatIntervalMillis, startPositionMillis), 1, Consts.OSC_COLOUR_GREEN);
//        addBeaterOffTask(2*beatIntervalMillis, 1);

    }

    private long adjustPlaytime(long playTime, long startPositionMillis){
        if(playTime >= 0){
            return playTime + startPositionMillis;
        }

        return playTime;
    }

    private int getColourId(int beatNo) {

        switch (beatNo) {
            case 1:
            case 2:
                return Consts.OSC_COLOUR_ORANGE;
            case 3:
            case 4:
            default:
                return Consts.OSC_COLOUR_RED;
        }

    }

    private void addBeaterOnTask(long playTime, int beaterNo, int colourId) {
        if (playTime == 0l) { // 0 is special value
            playTime = -1;
        }
        PrecountBeatOnEvent event = eventFactory.createPrecountBeatOnEvent(destination, clock.getSystemTimeMillis());
        event.addCommandArg(beaterNo, colourId);
        OscEventTask task = new OscEventTask(playTime, event, oscPublisher);
        //LOG.info("Create Beater ON Task playTime: " + playTime + " beaterNo: " + beaterNo + " colourId: " + colourId);
        scheduler.add(task);
    }

    private void addBeaterOffTask(long playTime, int beaterNo) {
        if (playTime == 0l) { // 0 is special value
            playTime = -1;
        }
        PrecountBeatOffEvent event = eventFactory.createPrecountBeatOffEvent(destination, clock.getSystemTimeMillis());
        event.addCommandArg(beaterNo);
        OscEventTask task = new OscEventTask(playTime, event, oscPublisher);
        //LOG.info("Create Beater OFF Task playTime: " + playTime + " beaterNo: " + beaterNo);
        scheduler.add(task);
    }
}
