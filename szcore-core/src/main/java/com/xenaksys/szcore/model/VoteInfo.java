package com.xenaksys.szcore.model;

public class VoteInfo {
    private int current;
    private int min;
    private int max;
    private int avg;
    private int voterNo;


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public int getVoterNo() {
        return voterNo;
    }

    public void setVoterNo(int voterNo) {
        this.voterNo = voterNo;
    }

    public void populate(int current, int min, int max, int avg, int voterNo) {
        this.current = current;
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.voterNo = voterNo;
    }

    public void reset() {
        this.current = 0;
        this.min = 0;
        this.max = 0;
        this.avg = 0;
        this.voterNo = 0;
    }

    @Override
    public String toString() {
        return "VoteInfo{" +
                "current=" + current +
                ", min=" + min +
                ", max=" + max +
                ", avg=" + avg +
                ", voterNo=" + voterNo +
                '}';
    }
}

