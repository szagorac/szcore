package com.xenaksys.szcore.score.web.audience;

import gnu.trove.map.hash.TLongIntHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xenaksys.szcore.Consts.WEB_OBJ_COUNTER;
import static com.xenaksys.szcore.Consts.WEB_OBJ_VOTE;

public class WebCounter {
    static final Logger LOG = LoggerFactory.getLogger(WebCounter.class);

    private final String id;
    private final boolean isRegisterCounterTime;
    private final long counterTimeStepMs;
    private final AtomicInteger counter;
    private final AtomicInteger minCount;
    private final AtomicInteger maxCount;
    private final PropertyChangeSupport pcs;
    private volatile int voterNo;
    private long lastCounterTime = 0L;
    private TLongIntHashMap counterTimeline = new TLongIntHashMap();

    public WebCounter(PropertyChangeSupport pcs, String id, int startVal, int voterNo, long counterTimeStepMs) {
        this.id = id;
        this.counter = new AtomicInteger(startVal);
        this.minCount = new AtomicInteger(startVal);
        this.maxCount = new AtomicInteger(startVal);
        this.voterNo = voterNo;
        this.isRegisterCounterTime = counterTimeStepMs > 0L;
        this.counterTimeStepMs = counterTimeStepMs;
        this.pcs = pcs;
    }

    public String getId() {
        return id;
    }

    public int getCounterValue() {
        return counter.get();
    }

    public void setCounterValue(int value) {
        counter.set(value);
        setMinMax();
    }

    public int increment() {
        int count = counter.incrementAndGet();
        setMinMax();
        pcs.firePropertyChange(WEB_OBJ_COUNTER, WEB_OBJ_VOTE, this);
        return count;
    }

    public int decrement() {
        int count = counter.decrementAndGet();
        setMinMax();
        pcs.firePropertyChange(WEB_OBJ_COUNTER, WEB_OBJ_VOTE, this);
        return count;
    }

    public void setVoterNo(int voterNo) {
        this.voterNo = voterNo;
    }

    public int getVoterNo() {
        return voterNo;
    }

    public int getMin() {
        return minCount.get();
    }

    public int getMax() {
        return maxCount.get();
    }

    public int getAvg() {
        return (minCount.get() + maxCount.get())/2;
    }

    public void setMinMax() {
        int count = counter.get();
        if(count < minCount.get()) {
            minCount.set(count);
        }
        if(count > maxCount.get()) {
            maxCount.set(count);
        }
    }

    public void copyTo(WebCounter other) {
        other.setCounterValue(getCounterValue());
    }

    public void processTime(long time) {
        if(!isRegisterCounterTime) {
            return;
        }
        if(lastCounterTime > 0L && (time - lastCounterTime) < counterTimeStepMs) {
            return;
        }
        counterTimeline.put(time, getCounterValue());
        lastCounterTime = time;
    }

    public TLongIntHashMap getCounterTimeline() {
        return counterTimeline;
    }

    public void resetCounterTimeline(){
        lastCounterTime = 0;
        counterTimeline  = new TLongIntHashMap();
    }

    public void resetCounters() {
        counter.set(0);
        minCount.set(0);
        maxCount.set(0);
    }
}
