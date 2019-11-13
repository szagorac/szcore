package com.xenaksys.szcore.model;

public class TimeSignatureImpl implements TimeSignature {

    private final int numberOfBeats;
    private final NoteDuration noteDuration;

    public TimeSignatureImpl(int numberOfBeats, NoteDuration noteDuration) {
        this.numberOfBeats = numberOfBeats;
        this.noteDuration = noteDuration;
    }

    @Override
    public int getNumberOfBeats() {
        return numberOfBeats;
    }

    @Override
    public NoteDuration getBeatDuration() {
        return noteDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSignatureImpl)) return false;

        TimeSignatureImpl that = (TimeSignatureImpl) o;

        if (numberOfBeats != that.numberOfBeats) return false;
        return noteDuration == that.noteDuration;

    }

    @Override
    public int hashCode() {
        int result = numberOfBeats;
        result = 31 * result + noteDuration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TimeSignatureImpl{" +
                "numberOfBeats=" + numberOfBeats +
                ", noteDuration=" + noteDuration +
                '}';
    }
}
