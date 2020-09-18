package com.xenaksys.szcore.algo;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntRange range = (IntRange) o;
        return start == range.start &&
                end == range.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
