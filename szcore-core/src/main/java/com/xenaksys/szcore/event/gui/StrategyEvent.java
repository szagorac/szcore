package com.xenaksys.szcore.event.gui;

public class StrategyEvent extends ClientInEvent {
    private final StrategyEventType strategyEventType;

    public StrategyEvent(StrategyEventType strategyEventType, long time) {
        super(time);
        this.strategyEventType = strategyEventType;
    }

    public StrategyEventType getStrategyEventType() {
        return strategyEventType;
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
