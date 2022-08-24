package com.xenaksys.szcore.gui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AudienceVote {
    private IntegerProperty voteNo = new SimpleIntegerProperty(0);
    private IntegerProperty minVote = new SimpleIntegerProperty(0);
    private IntegerProperty maxVote = new SimpleIntegerProperty(0);
    private IntegerProperty avgVote = new SimpleIntegerProperty(0);
    private IntegerProperty voterNo = new SimpleIntegerProperty(0);

    public int getVoteNo() {
        return voteNo.get();
    }

    public IntegerProperty voteNoProperty() {
        return voteNo;
    }

    public void setVoteNo(int voteNo) {
        this.voteNo.set(voteNo);
    }

    public int getMinVote() {
        return minVote.get();
    }

    public IntegerProperty minVoteProperty() {
        return minVote;
    }

    public void setMinVote(int minVote) {
        this.minVote.set(minVote);
    }

    public int getMaxVote() {
        return maxVote.get();
    }

    public IntegerProperty maxVoteProperty() {
        return maxVote;
    }

    public void setMaxVote(int maxVote) {
        this.maxVote.set(maxVote);
    }

    public int getAvgVote() {
        return avgVote.get();
    }

    public IntegerProperty avgVoteProperty() {
        return avgVote;
    }

    public void setAvgVote(int avgVote) {
        this.avgVote.set(avgVote);
    }

    public int getVoterNo() {
        return voterNo.get();
    }

    public IntegerProperty voterNoProperty() {
        return voterNo;
    }

    public void setVoterNo(int voterNo) {
        this.voterNo.set(voterNo);
    }

    @Override
    public String toString() {
        return "AudienceVote{" +
                "voteNo=" + voteNo +
                ", minVote=" + minVote +
                ", maxVote=" + maxVote +
                ", avgVote=" + avgVote +
                ", voterNo=" + voterNo +
                '}';
    }
}
