package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.model.Id;

public class ScoreInfoEvent extends ClientEvent {

    private final Id scoreId;
    private final boolean isRunning;
    private final boolean isPrecount;
    private final int beaterNo;
    private final int colourId;

    public ScoreInfoEvent(Id scoreId, boolean isRunning, boolean isPrecountOn, int beaterNo, int colourId, long creationTime) {
        super(creationTime);
        this.scoreId = scoreId;
        this.isRunning = isRunning;
        this.isPrecount = isPrecountOn;
        this.beaterNo = beaterNo;
        this.colourId = colourId;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Id getScoreId() {
        return scoreId;
    }

    public boolean isPrecount() {
        return isPrecount;
    }

    public int getBeaterNo() {
        return beaterNo;
    }

    public int getColourId() {
        return colourId;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.SCORE_INFO;
    }

    @Override
    public String toString() {
        return "ScoreInfoEvent{" +
                "scoreId=" + scoreId +
                ", isRunning=" + isRunning +
                '}';
    }
}
