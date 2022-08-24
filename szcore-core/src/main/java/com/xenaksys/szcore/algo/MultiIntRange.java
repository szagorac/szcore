package com.xenaksys.szcore.algo;

import gnu.trove.list.array.TIntArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class MultiIntRange implements IntRange {
    static final Logger LOG = LoggerFactory.getLogger(ScoreRandomisationStrategy.class);

    private TIntArrayList intList = new TIntArrayList();

    public MultiIntRange(List<IntRange> ranges) {
        init(ranges);
    }

    private void init(List<IntRange> ranges) {
        ranges.sort(null);
        for (IntRange pageRange : ranges) {
            int pageStart = pageRange.getStart();
            int pageEnd = pageRange.getEnd();
            for (int i = pageStart; i <= pageEnd; i++) {
                if (intList.contains(i)) {
                    LOG.warn("Found duplicate int in range: {}, ignoring ...", i);
                    continue;
                }
                intList.add(i);
            }
        }
        intList.sort();
    }

    public int getStart() {
        return intList.get(0);
    }

    public int getEnd() {
        return intList.get(intList.size() - 1);
    }

    public boolean isInRange(int value) {
        return intList.contains(value);
    }

    // elementIndex: 0 based range element index
    public int getElement(int elementIndex) {
        if (elementIndex < 0) {
            return getStart();
        } else if (elementIndex >= intList.size()) {
            return getEnd();
        }
        return intList.get(elementIndex);
    }

    public int getRndValueFromRange() {
        int rndIdx = ThreadLocalRandom.current().nextInt(0, intList.size());
        return intList.get(rndIdx);
    }

    @Override
    public int getSize() {
        return intList.size();
    }

    @Override
    public int[] getFullRange() {
        int[] out = new int[intList.size()];
        for(int i = 0; i < intList.size(); i++) {
            out[i] = intList.get(i);
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiIntRange that = (MultiIntRange) o;
        return intList.equals(that.intList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intList);
    }

    @Override
    public int compareTo(IntRange o) {
        return Integer.compare(getStart(), o.getStart());
    }
}
