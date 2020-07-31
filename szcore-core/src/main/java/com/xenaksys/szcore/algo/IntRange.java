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
        int max = Integer.MAX_VALUE;
        if (end != 0) {
            max = end;
        }
        return value >= start && value <= max;
    }
}
