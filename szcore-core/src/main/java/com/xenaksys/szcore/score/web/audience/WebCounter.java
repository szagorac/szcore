package com.xenaksys.szcore.score.web.audience;

import gnu.trove.map.hash.TLongIntHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;

public class WebCounter {
    static final Logger LOG = LoggerFactory.getLogger(WebCounter.class);

    private final String id;
    private final boolean isRegisterCounterTime;
    private final long counterTimeStepMs;
    private boolean isVisible = false;
    private String colour = WEB_TEXT_BACKGROUND_COLOUR;
    private AtomicInteger counter;
    private volatile int maxCount;
    private long lastCounterTime = 0L;
    private TLongIntHashMap counterTimeline = new TLongIntHashMap();

    public WebCounter(String id, int startVal, int maxCount, long counterTimeStepMs) {
        this.id = id;
        this.counter = new AtomicInteger(startVal);
        this.maxCount = maxCount;
        this.isRegisterCounterTime = counterTimeStepMs > 0L;
        this.counterTimeStepMs = counterTimeStepMs;
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getCounterValue() {
        return counter.get();
    }

    public void setCounterValue(int value) {
        counter.set(value);
    }

    public int increment() {
        return counter.incrementAndGet();
    }

    public int decrement() {
        return counter.decrementAndGet();
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void copyTo(WebCounter other) {
        other.setVisible(isVisible());
        other.setColour(getColour());
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
}
