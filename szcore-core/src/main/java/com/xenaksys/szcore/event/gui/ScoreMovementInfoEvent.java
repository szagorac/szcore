package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.MovementInfo;

import java.util.List;

public class ScoreMovementInfoEvent extends ClientEvent {

    private final List<MovementInfo> movementInfos;
    private final Id scoreId;
    private final String currentMovement;
    private final String nextMovement;
    private final String currentSection;
    private final String nextSection;

    public ScoreMovementInfoEvent(Id scoreId, List<MovementInfo> movementInfos, String currentMovement, String nextMovement, String currentSection, String nextSection, long creationTime) {
        super(creationTime);
        this.scoreId = scoreId;
        this.currentMovement = currentMovement;
        this.nextMovement = nextMovement;
        this.movementInfos = movementInfos;
        this.currentSection = currentSection;
        this.nextSection = nextSection;
    }

    public Id getScoreId() {
        return scoreId;
    }

    public List<MovementInfo> getMovementInfos() {
        return movementInfos;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public String getNextSection() {
        return nextSection;
    }

    public String getCurrentMovement() {
        return currentMovement;
    }

    public String getNextMovement() {
        return nextMovement;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.MOVEMENT_INFO;
    }

    @Override
    public String toString() {
        return "ScoreMovementInfoEvent{" +
                "currentMovement='" + currentMovement + '\'' +
                ", nextMovement='" + nextMovement + '\'' +
                ", currentSection='" + currentSection + '\'' +
                ", nextSection='" + nextSection + '\'' +
                ", movementInfos=" + movementInfos +
                '}';
    }
}
