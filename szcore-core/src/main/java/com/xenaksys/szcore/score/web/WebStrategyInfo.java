package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.score.web.strategy.WebStrategy;

import java.util.ArrayList;
import java.util.List;

public class WebStrategyInfo {
    private List<WebStrategy> strategies;

    public void addStrategy(WebStrategy strategy) {
        if(strategies == null) {
            strategies = new ArrayList<>();
        }
        strategies.add(strategy);
    }

    public List<WebStrategy> getStrategies() {
        return strategies;
    }
}
