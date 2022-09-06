package com.xenaksys.szcore.event.gui;

public class StrategyEvent extends ClientInEvent {
    private final StrategyEventType strategyEventType;

    private String sectionName;
    private String movementName;
    private Integer orderIndex;

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

    @Override
    public ClientInEventType getClientEventType() {
        return ClientInEventType.STRATEGY;
    }

    @Override
    public String toString() {
        return "StrategyEvent{" +
                "strategyEventType=" + strategyEventType +
                '}';
    }
}
