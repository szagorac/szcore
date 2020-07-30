package com.xenaksys.szcore.algo;

public class IntRange {
    private final int start;
    private final int end;

    public IntRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isInRange(int value) {
        return value >= start && value <= end;
    }
}
