package com.xenaksys.szcore.model;

public class TempoModifier {

    double multiplier;

    public TempoModifier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TempoModifier)) return false;

        TempoModifier that = (TempoModifier) o;

        return Double.compare(that.multiplier, multiplier) == 0;

    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(multiplier);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return "TempoModifier{" +
                "multiplier=" + multiplier +
                '}';
    }
}
