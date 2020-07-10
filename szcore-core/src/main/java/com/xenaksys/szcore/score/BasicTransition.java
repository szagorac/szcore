package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.Transition;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

public class BasicTransition implements Transition {
    private final IntId id;
    private final BeatId beatId;
    private final String component;
    private final long duration;
    private final long startValue;
    private final long endValue;
    private final long frequency;


    public BasicTransition(IntId id, BeatId beatId, String component, long duration, long startValue, long endValue, long frequency) {
        this.id = id;
        this.beatId = beatId;
        this.component = component;
        this.duration = duration;
        this.startValue = startValue;
        this.endValue = endValue;
        this.frequency = frequency;
    }

    @Override
    public BeatId getBeatId() {
        return beatId;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public Script copy(BeatId newBeatId) {
        IntId newId = new IntId(Consts.ID_SOURCE.incrementAndGet());
        return new BasicTransition(newId, newBeatId, this.component, this.duration, this.startValue, this.endValue, this.frequency);
    }

    @Override
    public String getComponent() {
        return component;
    }

    @Override
    public Id getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartValue() {
        return startValue;
    }

    public long getEndValue() {
        return endValue;
    }

    public long getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(Script o) {
        return id.getValue() - ((IntId)o.getId()).getValue();
    }

    @Override
    public String toString() {
        return "BasicTransition{" +
                "id=" + id +
                ", beatId=" + beatId +
                ", component='" + component + '\'' +
                ", duration=" + duration +
                ", startValue=" + startValue +
                ", endValue=" + endValue +
                ", frequency=" + frequency +
                '}';
    }
}
