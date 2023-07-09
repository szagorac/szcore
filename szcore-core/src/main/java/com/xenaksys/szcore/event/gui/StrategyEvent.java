package com.xenaksys.szcore.event.gui;

public class StrategyEvent extends ClientInEvent {
    private final StrategyEventType strategyEventType;

    private String sectionName;
    private String nextSectionName;
    private String movementName;

    private Integer orderIndex;
    private Boolean isOverrideNextSection;
    private Boolean isOverrideCurrentSection;

    public StrategyEvent(StrategyEventType strategyEventType, long time) {
        super(time);
        this.strategyEventType = strategyEventType;
    }

    public StrategyEventType getStrategyEventType() {
        return strategyEventType;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getNextSectionName() {
        return nextSectionName;
    }

    public void setNextSectionName(String nextSectionName) {
        this.nextSectionName = nextSectionName;
    }

    public String getMovementName() {
        return movementName;
    }

    public void setMovementName(String movementName) {
        this.movementName = movementName;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getOverrideNextSection() {
        return isOverrideNextSection;
    }

    public void setOverrideNextSection(Boolean overrideSection) {
        isOverrideNextSection = overrideSection;
    }

    public Boolean getOverrideCurrentSection() {
        return isOverrideCurrentSection;
    }

    public void setOverrideCurrentSection(Boolean overrideSection) {
        isOverrideCurrentSection = overrideSection;
    }

    @Override
    public ClientInEventType getClientEventType() {
        return ClientInEventType.STRATEGY;
    }

    @Override
    public String toString() {
        return "StrategyEvent{" +
                "strategyEventType=" + strategyEventType +
                ", sectionName='" + sectionName + '\'' +
                ", nextSectionName='" + nextSectionName + '\'' +
                ", movementName='" + movementName + '\'' +
                ", orderIndex=" + orderIndex +
                ", isOverrideNextSection=" + isOverrideNextSection +
                ", isOverrideCurrentSection=" + isOverrideCurrentSection +
                '}';
    }
}
