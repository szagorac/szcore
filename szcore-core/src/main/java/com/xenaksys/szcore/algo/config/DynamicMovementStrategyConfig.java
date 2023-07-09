package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.StrategyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicMovementStrategyConfig implements StrategyConfig{

    private final List<MovementConfig> movements = new ArrayList<>();
    private final List<String> parts = new ArrayList<>();
    private final List<String> scoreParts = new ArrayList<>();
    private final Map<String, ExternalScoreConfig> maxConfig = new HashMap<>();
    private final Map<String, ExternalScoreConfig> webConfig = new HashMap<>();
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

    public void addScoreParts(List<String> scoreParts) {
        this.scoreParts.addAll(scoreParts);
    }

    public List<String> getScoreParts() {
        return scoreParts;
    }

    public boolean isStopOnMovementEnd() {
        return isStopOnMovementEnd;
    }

    public void setStopOnMovementEnd(boolean stopOnMovementEnd) {
        isStopOnMovementEnd = stopOnMovementEnd;
    }


    public void addMaxConfigs(List<ExternalScoreConfig> maxConfigs) {
        if(maxConfigs == null) {
            return;
        }
        for(ExternalScoreConfig config : maxConfigs) {
            maxConfig.put(config.getId(), config);
        }
    }

    public Map<String, ExternalScoreConfig> getMaxConfig() {
        return maxConfig;
    }

    public ExternalScoreConfig getMaxConfig(String id) {
        return maxConfig.get(id);
    }

    public void addWebConfigs(List<ExternalScoreConfig> webConfigs) {
        if(webConfigs == null) {
            return;
        }
        for(ExternalScoreConfig config : webConfigs) {
            webConfig.put(config.getId(), config);
        }
    }

    public Map<String, ExternalScoreConfig> getWebConfig() {
        return webConfig;
    }

    public ExternalScoreConfig getWebConfig(String id) {
        return webConfig.get(id);
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