package com.xenaksys.szcore.event.gui;

public class PrecountInfo {

    private final boolean isPrecountOn;
    private final int beaterNo;
    private final int colourId;

    public PrecountInfo(boolean isPrecountOn, int beaterNo, int colourId) {
        this.isPrecountOn = isPrecountOn;
        this.beaterNo = beaterNo;
        this.colourId = colourId;
    }

    public boolean isPrecountOn() {
        return isPrecountOn;
    }

    public int getBeaterNo() {
        return beaterNo;
    }

    public int getColourId() {
        return colourId;
    }

    @Override
    public String toString() {
        return "PrecountInfo{" +
                "isPrecountOn=" + isPrecountOn +
                ", beaterNo=" + beaterNo +
                ", colourId=" + colourId +
                '}';
    }
}
