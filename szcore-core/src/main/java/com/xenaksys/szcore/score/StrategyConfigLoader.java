package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.RndPageRangeConfig;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategyConfig;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.id.InstrumentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.CONFIG_ALL;
import static com.xenaksys.szcore.Consts.CONFIG_END;
import static com.xenaksys.szcore.Consts.CONFIG_INSTRUMENTS;
import static com.xenaksys.szcore.Consts.CONFIG_IS_RND_ACTIVE;
import static com.xenaksys.szcore.Consts.CONFIG_PAGE_RANGES;
import static com.xenaksys.szcore.Consts.CONFIG_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_RND_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_SCORE_NAME;
import static com.xenaksys.szcore.Consts.CONFIG_SELECTION_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_START;
import static com.xenaksys.szcore.Consts.NAME_FULL_SCORE;
import static com.xenaksys.szcore.Consts.STRATEGY_CONFIG_FILE_SUFFIX;
import static com.xenaksys.szcore.Consts.YAML_FILE_EXTENSION;

public class StrategyConfigLoader extends YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(StrategyConfigLoader.class);

    public static ScoreRandomisationStrategyConfig loadStrategyConfig(String workingDir, Score score) throws Exception {
        String path = workingDir + Consts.SLASH + STRATEGY_CONFIG_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }

        return loadStrategyConfig(file, score);
    }

    public static ScoreRandomisationStrategyConfig loadStrategyConfig(File file, Score score) throws Exception {
        ScoreRandomisationStrategyConfig config = new ScoreRandomisationStrategyConfig();
        Map<String, Object> configMap = loadYaml(file);

        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadStrategyConfig: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        Object yamlRndStrategyObj = configMap.get(CONFIG_RND_STRATEGY);
        if (yamlRndStrategyObj == null) {
            throw new RuntimeException("loadStrategyConfig: invalid random strategy config");
        }
        Map<String, Object> rndStrategyConfig = (Map<String, Object>) yamlRndStrategyObj;

        List<InstrumentId> participatingScoreInsts = new ArrayList<>();
        Collection<Instrument> scoreInstruments = score.getInstruments();
        for (Instrument instrument : scoreInstruments) {
            if (!instrument.isAv() && !instrument.getName().equals(NAME_FULL_SCORE)) {
                participatingScoreInsts.add((InstrumentId) instrument.getId());
            }
        }

        List<Map<String, Object>> instActiveRangesConfigs = getListOfMaps(CONFIG_PAGE_RANGES, rndStrategyConfig);
        for (Map<String, Object> instConfig : instActiveRangesConfigs) {
            List<InstrumentId> participatingInsts = new ArrayList<>();
            List<String> configInstruments = getStrList(CONFIG_INSTRUMENTS, instConfig);
            for (String inst : configInstruments) {
                if (CONFIG_ALL.equals(inst)) {
                    participatingInsts.addAll(participatingScoreInsts);
                    break;
                }
                for (InstrumentId instrumentId : participatingScoreInsts) {
                    if (inst.equals(instrumentId.getName())) {
                        participatingInsts.add(instrumentId);
                    }
                }
            }

            Map<String, Object> activeRangeConfig = getMap(CONFIG_RANGE, instConfig);
            int start = getInteger(CONFIG_START, activeRangeConfig);
            int end = getInteger(CONFIG_END, activeRangeConfig);
            IntRange activeRange = new IntRange(start, end);

            Boolean isActive = getBoolean(CONFIG_IS_RND_ACTIVE, instConfig);
            IntRange selRange = activeRange;
            if (isActive) {
                Map<String, Object> selectionRangeConfig = getMap(CONFIG_SELECTION_RANGE, instConfig);
                int selStart = getInteger(CONFIG_START, selectionRangeConfig);
                int selEnd = getInteger(CONFIG_END, selectionRangeConfig);
                selRange = new IntRange(selStart, selEnd);
            }

            RndPageRangeConfig pageRangeConfig = new RndPageRangeConfig(isActive, participatingInsts, activeRange, selRange);

            config.addPageRangeConfig(pageRangeConfig);
        }

        return config;
    }
}
