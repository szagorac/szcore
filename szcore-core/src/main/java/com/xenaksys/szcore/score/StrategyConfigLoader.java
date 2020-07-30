package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.ScoreRandomisationStrategyConfig;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.CONFIG_ACTIVE_RANGES;
import static com.xenaksys.szcore.Consts.CONFIG_END;
import static com.xenaksys.szcore.Consts.CONFIG_INSTRUMENT_ID;
import static com.xenaksys.szcore.Consts.CONFIG_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_RND_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_SCORE_NAME;
import static com.xenaksys.szcore.Consts.CONFIG_START;
import static com.xenaksys.szcore.Consts.STRATEGY_CONFIG_FILE_SUFFIX;
import static com.xenaksys.szcore.Consts.YAML_FILE_EXTENSION;

public class StrategyConfigLoader {
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

        List<Map<String, Object>> instActiveRangesConfigs = getListOfMaps(CONFIG_ACTIVE_RANGES, rndStrategyConfig);
        for (Map<String, Object> instConfig : instActiveRangesConfigs) {
            String instId = getString(CONFIG_INSTRUMENT_ID, instConfig);
            InstrumentId instrumentId = new InstrumentId(instId);
            Instrument inst = score.getInstrument(instrumentId);
            if (inst == null) {
                throw new RuntimeException("loadStrategyConfig: Invalid instrument: " + instId);
            }

            Map<String, Object> rangeConfig = getMap(CONFIG_RANGE, instConfig);
            int start = getInteger(CONFIG_START, rangeConfig);
            int end = getInteger(CONFIG_END, rangeConfig);

            IntRange range = new IntRange(start, end);

            config.addPageRange((InstrumentId) inst.getId(), range);
        }

        return config;
    }

    public static Map<String, Object> loadYaml(File file) throws Exception {
        String doc = FileUtil.readYamlFromFile(file);
        Yaml yaml = new Yaml();
        return yaml.load(doc);
    }

    public static List<Map<String, Object>> getListOfMaps(String key, Map<String, Object> configMap) {
        List<Object> objList = getList(key, configMap);
        return convertToListOfMaps(objList);
    }

    public static List<Map<String, Object>> convertToListOfMaps(List<Object> objList) {
        List<Map<String, Object>> listOfMaps = new ArrayList<>();
        if (objList == null) {
            return listOfMaps;
        }

        for (Object obj : objList) {
            if (obj instanceof Map) {
                listOfMaps.add((Map<String, Object>) obj);
            } else {
                LOG.warn("Unexpected element in object list {}, expected map", obj);
            }
        }

        return listOfMaps;
    }

    public static List<Object> getList(String key, Map<String, Object> configMap) {
        Object val = configMap.get(key);
        if (val == null) {
            return null;
        }

        if (!(val instanceof List)) {
            LOG.warn("Unexpected type for map value, key {}, value {}", key, val);
            return null;
        }

        return (List<Object>) val;
    }

    public static String getString(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            LOG.warn("Could not find string value for key {}", key);
            return null;
        }

        if (!(val instanceof String)) {
            LOG.warn("Unexpected type for string value, key {}, value {}", key, val);
            return null;
        }

        return (String) val;
    }

    public static Map<String, Object> getMap(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            return null;
        }

        if (!(val instanceof Map)) {
            LOG.warn("Unexpected type for map value, key {}, value {}", key, val);
            return null;
        }

        return (Map<String, Object>) val;
    }

    public static Integer getInteger(String key, Map<String, Object> scoreMap) {
        Object val = scoreMap.get(key);
        if (val == null) {
            LOG.warn("Could not find integer value for key {}", key);
            return null;
        }

        if (!(val instanceof Integer)) {
            LOG.warn("Unexpected type for integer value, key {}, value {}", key, val);
            return null;
        }

        return (Integer) val;
    }
}
