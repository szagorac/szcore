package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.DynamicMovementStrategyConfig;
import com.xenaksys.szcore.algo.config.MovementConfig;
import com.xenaksys.szcore.model.MovementInfo;
import com.xenaksys.szcore.model.MovementSectionInfo;
import com.xenaksys.szcore.score.BasicScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicMovementStrategy implements ScoreStrategy {
    static final Logger LOG = LoggerFactory.getLogger(DynamicMovementStrategy.class);

    private final BasicScore szcore;
    private final DynamicMovementStrategyConfig config;
    private final Map<String, MovementInfo> movementInfos = new ConcurrentHashMap<>();
    private final List<String> movementOrder = new ArrayList<>();
    private String[] instruments;
    private String[] dynamicParts;
    private String defaultPart;
    private boolean isReady = false;
    private String currentMovement;
    private String nextMovement;

    public DynamicMovementStrategy(BasicScore szcore, DynamicMovementStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        if (config == null) {
            return;
        }
        List<MovementConfig> movementsConfig = getMovements();
        if (movementsConfig == null) {
            return;
        }
        for (MovementConfig movementConfig : movementsConfig) {
            MovementInfo movementInfo = getOrCreateMovementInfo(movementConfig);
            movementInfos.put(movementInfo.getMovementId(), movementInfo);
            movementOrder.add(movementInfo.getMovementId());
        }
        if (movementsConfig.size() > 0) {
            this.currentMovement = movementsConfig.get(0).getName();
        }

        isReady = true;
    }

    private MovementInfo getOrCreateMovementInfo(final MovementConfig movementConfig) {
        if (movementInfos.containsKey(movementConfig.getName())) {
            return movementInfos.get(movementConfig.getName());
        }
        MovementInfo movementInfo = new MovementInfo(movementConfig.getName());
        Collection<MovementConfig.SectionConfig> sectionConfigs = movementConfig.getSections();
        for (MovementConfig.SectionConfig sectionConfig : sectionConfigs) {
            MovementSectionInfo sectionInfo = new MovementSectionInfo(sectionConfig.getName());
            sectionInfo.setPageRange(sectionConfig.getRange());
            sectionInfo.setActive(false);
            movementInfo.addSection(sectionInfo);
        }

        movementInfos.put(movementInfo.getMovementId(), movementInfo);
        return movementInfo;
    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public DynamicMovementStrategyConfig getConfig() {
        return config;
    }

    public List<MovementConfig> getMovements() {
        return config.getMovements();
    }

    public List<String> getMovementOrder() {
        return movementOrder;
    }

    public boolean isSectionOwned(String section) {
        return movementInfos.containsKey(section) && movementInfos.get(section) != null;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public boolean isActive() {
        return config.isActive();
    }

    private boolean isBuildComplete() {
        List<MovementConfig> movements = getMovements();
        if (movements == null) {
            return true;
        }

        for (MovementConfig movement : movements) {
            if (!movementOrder.contains(movement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.DYNAMIC;
    }

    public void setInstruments(String[] instruments) {
        this.instruments = instruments;
    }

    public String[] getInstruments() {
        return instruments;
    }

    public void setDynamicParts(String[] instruments) {
        this.dynamicParts = instruments;
    }

    public String[] getDynamicParts() {
        return dynamicParts;
    }

    public void setDefaultPart(String instrumentDefault) {
        this.defaultPart = instrumentDefault;
    }

    public String getDefaultPart() {
        return defaultPart;
    }

    public void addClientId(String clientId) {
        if (clientId == null) {
            return;
        }
        if (defaultPart == null) {
            return;
        }
        for (MovementInfo movementInfo : movementInfos.values()) {
            movementInfo.addClientIdDefaultPart(clientId, defaultPart);
        }
    }

    public void addClientInstrument(String movement, String section, String clientId, String instrumentId) {
        if (movement == null || section == null || clientId == null || instrumentId == null) {
            return;
        }
        MovementInfo movementInfo = getMovementInfo(movement);
        if (movementInfo == null) {
            return;
        }
        movementInfo.addClientPart(clientId, instrumentId, section);
    }

    public MovementInfo getMovementInfo(String movementId) {
        if (movementId == null) {
            return null;
        }
        return movementInfos.get(movementId);
    }

    public List<MovementInfo> getMovementInfos() {
        return new ArrayList<>(movementInfos.values());
    }

    public boolean isStopOnMovementEnd() {
        return config.isStopOnMovementEnd();
    }

    public String getCurrentMovement() {
        return currentMovement;
    }

    public void setCurrentMovement(String currentMovement) {
        this.currentMovement = currentMovement;
        setNextMovement();
    }

    public void setNextMovement() {
        if (currentMovement == null) {
            return;
        }
        int idx = movementOrder.indexOf(currentMovement);
        if (idx < 0 || idx >= movementOrder.size() - 1) {
            nextMovement = null;
            return;
        }
        idx++;
        nextMovement = movementOrder.get(idx);
    }

    public String getNextMovement() {
        return nextMovement;
    }

    public void onMovementStart(String movement) {
        if (currentMovement != null && !currentMovement.equals(movement)) {
            LOG.error("onSectionStart: Unexpected movement start: {}, expected: {}", movement, currentMovement);
        }
        MovementInfo movementInfo = movementInfos.get(movement);
        movementInfo.setActive(true);
        setNextMovement();
    }

    public void onMovementEnd() {
        onMovementEnd(currentMovement);
    }

    public void onMovementEnd(String movement) {
        if (currentMovement != null && !currentMovement.equals(movement)) {
            LOG.error("onSectionEnd: Unexpected movement end: {}, expected: {}", movement, currentMovement);
        }
        MovementInfo sectionInfo = movementInfos.get(movement);
        sectionInfo.setActive(false);
        setCurrentMovement(getNextMovement());
    }

    public void reset() {
        isReady = false;
        movementInfos.clear();
        movementOrder.clear();
    }
}
