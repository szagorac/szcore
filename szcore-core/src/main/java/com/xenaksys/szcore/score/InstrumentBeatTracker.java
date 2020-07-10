package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Beat;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class InstrumentBeatTracker {
    static final Logger LOG = LoggerFactory.getLogger(InstrumentBeatTracker.class);

    private Beat current = null;
    private int startBaseBeatNo = 0;
    private int beatTickNo = 0;
    private AtomicInteger tick = new AtomicInteger(0);

    private final Transport transport;
    private final Id instrumentId;

    public InstrumentBeatTracker(Transport transport, Id instrumentId) {
        this.transport = transport;
        this.instrumentId = instrumentId;
    }

    public void setCurrentBeat(Beat beat) {
//LOG.info("Tracker setting current beat: " + beat);
        if(beat == null || !instrumentId.equals(beat.getInstrumentId())){
            return;
        }

        if(current == null || (beat.getBeatNo() != current.getBeatNo())){
            this.current = beat;
            tick.set(-1);
        }

        int tickNo = transport.getNumberOfTicksPerBeat();
        int baseBeatDuration  = current.getBaseBeatUnitsDuration();
        beatTickNo = tickNo * baseBeatDuration/2;
        startBaseBeatNo = beat.getBaseBeatUnitsNoAtStart();
    }

    public Transport getTransport() {
        return transport;
    }

    public Id getInstrument() {
        return instrumentId;
    }

    public Beat getCurrent() {
        return current;
    }

    public int getBeatTickNo() {
        return beatTickNo;
    }

    public int getTick() {
        return tick.get();
    }

    public void setBeatTickNo(int beatTickNo) {
        this.beatTickNo = beatTickNo;
    }

    public void setTick(int tick) {
        this.tick.set(tick);
    }

    public int incrementTick(int beatNo){
        if(beatNo < startBaseBeatNo){
//LOG.info(" Not incrementing tick, beatNo: " + beatNo + " startBaseBeatNo: " + startBaseBeatNo);
            return tick.get();
        }
        return tick.incrementAndGet();
    }

    public int getStartBaseBeatNo() {
        return startBaseBeatNo;
    }

    public Id getInstrumentId() {
        return instrumentId;
    }

    public int getPercentCompleted(){
        if (beatTickNo == 0){
            return 0;
        }
        int currentTick = tick.get();
        if(currentTick < 0){
            currentTick = 0;
        }
//LOG.info("getPercentCompleted currentTick: {} beatTickNo: {} inst: {}", currentTick,  beatTickNo, instrumentId);
        return (currentTick * 100)/beatTickNo;
    }
}
