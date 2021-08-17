package com.xenaksys.szcore.score;


import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.SzcoreEvent;
import com.xenaksys.szcore.model.id.BeatId;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TransportContext {

    private final Id transportId;
    private final List<SzcoreEvent> initEvents = new ArrayList<>();
    private final List<SzcoreEvent> clockTickEvents = new ArrayList<>();
    private final LinkedBlockingQueue<SzcoreEvent> oneOffClockTickEvents = new LinkedBlockingQueue<>();
    private final List<SzcoreEvent> clockBaseBeatEvents = new ArrayList<>();
    private final TIntObjectMap<List<SzcoreEvent>> scoreBaseBeatEvents = new TIntObjectHashMap<>();
    private final TIntObjectMap<List<SzcoreEvent>> oneOffBaseBeatEvents = new TIntObjectHashMap<>();
    private final TIntObjectMap<List<BeatId>> beatNoToId = new TIntObjectHashMap<>();

    public TransportContext(Id transportId) {
        this.transportId = transportId;
    }

    public Id getTransportId() {
        return transportId;
    }

    public void addClockTickEvent(SzcoreEvent clockTickEvent) {
        clockTickEvents.add(clockTickEvent);
    }

    public void addOneOffClockTickEvent(SzcoreEvent oneOffEvent) {
        oneOffClockTickEvents.add(oneOffEvent);
    }

    public void addClockBaseBeatTickEvent(SzcoreEvent clockBaseBeatEvent) {
        clockBaseBeatEvents.add(clockBaseBeatEvent);
    }

    public void addScoreBaseBeatEvent(SzcoreEvent scoreBaseBeatEvent) {
        BeatId beatId = scoreBaseBeatEvent.getEventBaseBeat();
        int baseBeatNo = 0;
        if (beatId != null) {
            baseBeatNo = beatId.getBaseBeatNo();
        }

        List<SzcoreEvent> beatEvents = scoreBaseBeatEvents.get(baseBeatNo);
        if (beatEvents == null) {
            beatEvents = new ArrayList<>();
            scoreBaseBeatEvents.put(baseBeatNo, beatEvents);
        }
        beatEvents.add(scoreBaseBeatEvent);
    }

    public void addOneOffBaseBeatEvent(SzcoreEvent scoreBaseBeatEvent) {
        BeatId beatId = scoreBaseBeatEvent.getEventBaseBeat();
        int baseBeatNo = 0;
        if (beatId != null) {
            baseBeatNo = beatId.getBaseBeatNo();
        }

        List<SzcoreEvent> beatEvents = oneOffBaseBeatEvents.get(baseBeatNo);
        if (beatEvents == null) {
            beatEvents = new ArrayList<>();
            oneOffBaseBeatEvents.put(baseBeatNo, beatEvents);
        }
        beatEvents.add(scoreBaseBeatEvent);
    }

    public void addBeatId(BeatId beatId) {
        int baseBeatNo = 0;
        if (beatId != null) {
            baseBeatNo = beatId.getBaseBeatNo();
        }

        List<BeatId> beatIds = beatNoToId.get(baseBeatNo);
        if (beatIds == null) {
            beatIds = new ArrayList<>();
            beatNoToId.put(baseBeatNo, beatIds);
        }

        beatIds.add(beatId);

    }

    public List<BeatId> getBeatIds(int beatNo) {
        return beatNoToId.get(beatNo);
    }

    public List<SzcoreEvent> getInitEvents() {
        return initEvents;
    }


    public List<SzcoreEvent> getClockTickEvents() {
        return clockTickEvents;
    }

    public LinkedBlockingQueue<SzcoreEvent> getOneOffClockTickEvents() {
        return oneOffClockTickEvents;
    }

    public List<SzcoreEvent> getClockBaseBeatEvents() {
        return clockBaseBeatEvents;
    }


    public TIntObjectMap<List<SzcoreEvent>> getScoreBaseBeatEvents() {
        return scoreBaseBeatEvents;
    }

    public TIntObjectMap<List<SzcoreEvent>> getOneOffBaseBeatEvents() {
        return oneOffBaseBeatEvents;
    }

    public List<SzcoreEvent> getOneOffBaseBeatEvents(int baseBeatNo) {
        return oneOffBaseBeatEvents.get(baseBeatNo);
    }

    public void removeOneOffBaseBeatEvents(int baseBeatNo) {
//        LOG.info("########## REMOVING ############# baseBeatNo: " + baseBeatNo);

        oneOffBaseBeatEvents.remove(baseBeatNo);
    }

    public void resetOnStop() {
        oneOffBaseBeatEvents.clear();
        oneOffClockTickEvents.clear();
    }

    public void replaceOneOffBaseBeatEvents(int baseBeatNo, List<SzcoreEvent> events) {
        oneOffBaseBeatEvents.put(baseBeatNo, events);
    }
}
