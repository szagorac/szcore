package com.xenaksys.szcore.model;


import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.net.osc.OSCPortOut;
import gnu.trove.map.TIntObjectMap;

import java.util.Collection;
import java.util.List;

public interface Score extends Identifiable {

    List<SzcoreEvent> getInitEvents();

    List<SzcoreEvent> getClockTickEvents(Id transportId);

    List<SzcoreEvent> getClockBaseBeatEvents(Id transportId);

    Collection<Id> getTransportIds();

    TIntObjectMap<List<SzcoreEvent>> getScoreBaseBeatEvents(Id transportId);

    TIntObjectMap<List<SzcoreEvent>> getOneOffBaseBeatEvents(Id transportId);

    List<SzcoreEvent> getOneOffBaseBeatEvents(Id transportId, int baseBeatNo);

    void removeOneOffBeatEvents(Id transportId, int baseBeatNo);

    String getName();

    Collection<Instrument> getInstruments();

    Collection<Page> getPages();

    Collection<Bar> getBars();

    Collection<Beat> getBeats();

    Collection<BeatId> getInstrumentBeatIds(Id instrumentId);

    Collection<Stave> getStaves();

    Instrument getInstrument(Id instrumentId);

    List<Stave> getInstrumentStaves(Id instrumentId);

    OSCPortOut getInstrumentOscPort(Id instrumentId);

    Transport getInstrumentTransport(Id instrumentId);

    List<Id> getTransportInstrumentIds(Id transportId);

    Transport getTransport(Id transportId);

    Collection<Transport> getTransports();

    Bar getBar(Id id);

    Beat getBeat(Id id);

    Page getPage(Id id);

    long getBeatTime(BeatId beatId);

    BeatId getBeatForTime(long time, Id transportId);

    String getOscDestination(Id instrumentId);

    Stave getCurrentStave(Id instrumentId);

    Stave getNextStave(Id instrumentId);

    Page getNextPage(PageId pageId);

    List<BeatId> getBeatIds(Id transportId, int beatNo);

    List<BeatId> findBeatIds(Id transportId, int beatNo);

    boolean isPrecount();

    int getPrecountBeatNo();

    int getPrecountMillis();

    long getMaxBeatInterval();

    long getPrecountBeaterInterval();

    Beat getUpbeat(BeatId beatId);

    List<Script> getBeatScripts(BeatId beatId);
}
