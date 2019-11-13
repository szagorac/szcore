package com.xenaksys.szcore.model;


import com.xenaksys.szcore.Consts;

public class TempoImpl implements Tempo {

    private final int scoreBpm;
    private final int modifiedBpm;
    private final NoteDuration noteDuration;
    private final TempoModifier tempoModifier;

    public TempoImpl(int scoreBpm, NoteDuration noteDuration) {
        this(scoreBpm, noteDuration, new TempoModifier(Consts.ONE_D));
    }

    public TempoImpl(Tempo tempo, TempoModifier modifier) {
        this(tempo.getScoreBpm(), tempo.getBeatDuration(), modifier);
    }

    public TempoImpl(int scoreBpm, NoteDuration noteDuration, TempoModifier modifier) {
        this.scoreBpm = scoreBpm;
        this.noteDuration = noteDuration;
        this.tempoModifier = modifier;
        this.modifiedBpm = calculateBpm();
    }

    private int calculateBpm(){
        if(tempoModifier == null || tempoModifier.getMultiplier() == Consts.ONE_D) {
            return scoreBpm;
        }

        return (int)Math.round(scoreBpm * tempoModifier.getMultiplier());
    }

    public TempoModifier getTempoModifier() {
        return tempoModifier;
    }

    @Override
    public int getBpm() {
        return modifiedBpm;
    }

    @Override
    public int getScoreBpm() {
        return scoreBpm;
    }

    @Override
    public NoteDuration getBeatDuration() {
        return noteDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TempoImpl)) return false;

        TempoImpl tempo = (TempoImpl) o;

        if (scoreBpm != tempo.scoreBpm) return false;
        return noteDuration == tempo.noteDuration;

    }

    @Override
    public int hashCode() {
        int result = scoreBpm;
        result = 31 * result + noteDuration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TempoImpl{" +
                "scoreBpm=" + scoreBpm +
                ", modifiedBpm=" + modifiedBpm +
                ", noteDuration=" + noteDuration +
                ", tempoModifier=" + tempoModifier +
                '}';
    }
}
