package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.model.Id;

public class ScoreInfoEvent extends ClientEvent {

    private final Id scoreId;
    private final boolean isStop;
    private final PrecountInfo precountInfo;

    public ScoreInfoEvent(Id scoreId, boolean isStop, PrecountInfo precountInfo, long creationTime) {
        super(creationTime);
        this.scoreId = scoreId;
        this.isStop = isStop;
        this.precountInfo = precountInfo;
    }

    public boolean isStop() {
        return isStop;
    }

    public Id getScoreId() {
        return scoreId;
    }

    public PrecountInfo getPrecountInfo() {
        return precountInfo;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.SCORE_INFO;
    }

    @Override
    public String toString() {
        return "ScoreInfoEvent{" +
                "scoreId=" + scoreId +
                ", isRunning=" + isStop +
                ", precountInfo=" + precountInfo +
                '}';
    }
}
