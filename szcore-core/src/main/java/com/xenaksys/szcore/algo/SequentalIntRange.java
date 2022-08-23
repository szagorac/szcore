package com.xenaksys.szcore.algo;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SequentalIntRange implements IntRange {
    private final int start;
    private final int end;
    private final int size;

    public SequentalIntRange(int start, int end) {
        this.start = start;
        this.end = end;
        this.size = end - start + 1;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public boolean isInRange(int value) {
        int max = Integer.MAX_VALUE;
        if (end != 0) {
            max = end;
        }
        return value >= start && value <= max;
    }

    @Override
    public int getRndValueFromRange() {
        if (start == end) {
            return start;
        }
        return ThreadLocalRandom.current().nextInt(start, end + 1);
    }

    // elementIndex: 0 based range element index
    @Override
    public int getElement(int elementIndex) {
        if (elementIndex < 0) {
            return start;
        } else if (elementIndex >= size) {
            return end;
        }
        return start + elementIndex;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int[] getFullRange() {
        int[] out = new int[size];
        for(int i = 0; i < size; i++) {
            out[i] = getElement(i);
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequentalIntRange range = (SequentalIntRange) o;
        return start == range.start &&
                end == range.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public int compareTo(IntRange o) {
        return Integer.compare(getStart(), o.getStart());
    }
}
