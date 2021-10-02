package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.TranspositionStrategyConfig;
import com.xenaksys.szcore.score.BasicScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranspositionStrategy implements ScoreStrategy {
    static final Logger LOG = LoggerFactory.getLogger(TranspositionStrategy.class);

    private final BasicScore szcore;
    private final TranspositionStrategyConfig config;
    private boolean isReady = false;

    public TranspositionStrategy(BasicScore szcore, TranspositionStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        if(config == null) {
            return;
        }
    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public TranspositionStrategyConfig getConfig() {
        return config;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public boolean isActive() {
        return config.isActive();
    }

    @Override
    public StrategyType getType() {
        return StrategyType.TRANSPOSITION;
    }
}
