package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.StrategyType;

import java.util.ArrayList;
import java.util.List;

public class DynamicMovementStrategyConfig implements StrategyConfig{

    private final List<MovementConfig> movements = new ArrayList<>();
    private final List<String> parts = new ArrayList<>();
    private boolean isActive;
    private String scoreName;
    private boolean isStopOnMovementEnd;

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public void addMovements(List<MovementConfig> movements) {
        this.movements.addAll(movements);
    }

    public void addMovement(MovementConfig movement) {
        this.movements.add(movement);
    }

    public List<MovementConfig> getMovements() {
        return movements;
    }

    public void addParts(List<String> parts) {
        this.parts.addAll(parts);
    }

    public List<String> getParts() {
        return parts;
    }

    public boolean isStopOnMovementEnd() {
        return isStopOnMovementEnd;
    }

    public void setStopOnMovementEnd(boolean stopOnMovementEnd) {
        isStopOnMovementEnd = stopOnMovementEnd;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.DYNAMIC;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}