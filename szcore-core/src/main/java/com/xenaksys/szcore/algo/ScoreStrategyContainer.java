package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.algo.config.ScoreRandomisationStrategyConfig;
import com.xenaksys.szcore.algo.config.StrategyConfig;
import com.xenaksys.szcore.score.BasicScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;

public class ScoreStrategyContainer {
    static final Logger LOG = LoggerFactory.getLogger(ScoreStrategyContainer.class);

    private EnumMap<StrategyType, StrategyConfig> strategyConfigs = new EnumMap<>(StrategyType.class);
    private EnumMap<StrategyType, ScoreStrategy> strategies = new EnumMap<>(StrategyType.class);

    public void init(BasicScore szcore) {
        for(StrategyConfig config : strategyConfigs.values()) {
            initStrategy(config, szcore);
        }
    }

    private void initStrategy(StrategyConfig config, BasicScore szcore) {
        StrategyType type = config.getType();
        switch (type) {
            case RND:
                initRandomisation((ScoreRandomisationStrategyConfig)config, szcore);
                break;
            case BUILDER:
                initBuilderStrategy((ScoreBuilderStrategyConfig)config, szcore);
                break;
            default:
                LOG.error("initStrategy: unknown strategy type: {}", type);
        }
    }

    public void initRandomisation(ScoreRandomisationStrategyConfig config, BasicScore szcore) {
        ScoreRandomisationStrategy randomisationStrategy = new ScoreRandomisationStrategy(szcore, config);
        randomisationStrategy.init();
        addStrategy(randomisationStrategy);
    }

    public void initBuilderStrategy(ScoreBuilderStrategyConfig config, BasicScore szcore) {
        ScoreBuilderStrategy scoreBuilderStrategy = new ScoreBuilderStrategy(szcore, config);
        addStrategy(scoreBuilderStrategy);
    }

    public void setRandomisationStrategy(List<Integer> strategy) {
        ScoreRandomisationStrategy randomisationStrategy = getRandomisationStrategy();
        if(randomisationStrategy == null) {
            LOG.error("setRandomisationStrategy: can not dind randomisation strategy");
            return;
        }
        randomisationStrategy.setAssignmentStrategy(strategy);
    }

    public ScoreRandomisationStrategy getRandomisationStrategy() {
        if(!strategies.containsKey(StrategyType.RND)) {
            return null;
        }
        return (ScoreRandomisationStrategy)strategies.get(StrategyType.RND);
    }

    public ScoreBuilderStrategy getScoreBuilderStrategy() {
        if(!strategies.containsKey(StrategyType.BUILDER)) {
            return null;
        }
        return (ScoreBuilderStrategy)strategies.get(StrategyType.BUILDER);
    }

    public void addStrategyConfig(StrategyConfig strategyConfig) {
        strategyConfigs.put(strategyConfig.getType(), strategyConfig);
    }

    public void addStrategy(ScoreStrategy strategy) {
        strategies.put(strategy.getType(), strategy);
    }
}
