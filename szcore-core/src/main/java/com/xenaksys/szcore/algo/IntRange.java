package com.xenaksys.szcore.algo;

public interface IntRange extends Comparable<IntRange> {
    int getStart();

    int getEnd();

    boolean isInRange(int value);

    int getRndValueFromRange();

    // elementIndex: 0 based range element index
    int getElement(int elementIndex);

    int getSize();

    int[] getFullRange();
}
