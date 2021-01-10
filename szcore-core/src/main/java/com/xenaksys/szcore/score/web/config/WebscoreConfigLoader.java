package com.xenaksys.szcore.score.web.config;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.YamlLoader;
import com.xenaksys.szcore.score.web.WebscorePageRangeAssignmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.CONFIG_ASSIGNMENT_TYPE;
import static com.xenaksys.szcore.Consts.CONFIG_END;
import static com.xenaksys.szcore.Consts.CONFIG_ID;
import static com.xenaksys.szcore.Consts.CONFIG_PAGE_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_PAGE_RANGE_MAPPING;
import static com.xenaksys.szcore.Consts.CONFIG_PRESETS;
import static com.xenaksys.szcore.Consts.CONFIG_SCORE_NAME;
import static com.xenaksys.szcore.Consts.CONFIG_SCRIPTS;
import static com.xenaksys.szcore.Consts.CONFIG_START;
import static com.xenaksys.szcore.Consts.CONFIG_TILE_COLS;
import static com.xenaksys.szcore.Consts.CONFIG_TILE_ROW;
import static com.xenaksys.szcore.Consts.WEBSCORE_PRESET_FILE_SUFFIX;
import static com.xenaksys.szcore.Consts.YAML_FILE_EXTENSION;

public class WebscoreConfigLoader extends YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(WebscoreConfigLoader.class);

    public static WebscoreConfig load(String workingDir) throws Exception {
        String path = workingDir + Consts.SLASH + WEBSCORE_PRESET_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }

        return load(file);
    }

    public static WebscoreConfig load(File file) throws Exception {
        WebscoreConfig config = new WebscoreConfig();
        Map<String, Object> configMap = loadYaml(file);

        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadWebScore: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        List<Map<String, Object>> presetsConfigs = getListOfMaps(CONFIG_PRESETS, configMap);
        for (Map<String, Object> presetConfig : presetsConfigs) {
            Integer id = getInteger(CONFIG_ID, presetConfig);
            if (id == null) {
                throw new RuntimeException("loadWebScore: Invalid Preset ID");
            }
            ScriptPreset preset = new ScriptPreset(id);

            List<String> presetScripts = getStrList(CONFIG_SCRIPTS, presetConfig);
            preset.addScripts(presetScripts);

            config.addPreset(preset);
        }

        List<Map<String, Object>> pageRangeMappings = getListOfMaps(CONFIG_PAGE_RANGE_MAPPING, configMap);
        for (Map<String, Object> pageRangeMapping : pageRangeMappings) {
            WebscorePageRangeConfig pageRangeConfig = new WebscorePageRangeConfig();

            Integer tileRow = getInteger(CONFIG_TILE_ROW, pageRangeMapping);
            if (tileRow == null) {
                throw new RuntimeException("loadWebScore: Invalid Tile Row");
            }
            pageRangeConfig.setTileRow(tileRow);

            Map<String, Object> tileColsConfig = getMap(CONFIG_TILE_COLS, pageRangeMapping);
            int start = getInteger(CONFIG_START, tileColsConfig);
            int end = getInteger(CONFIG_END, tileColsConfig);
            IntRange tileCols = new IntRange(start, end);
            pageRangeConfig.setTileCols(tileCols);

            Map<String, Object> pageRange = getMap(CONFIG_PAGE_RANGE, pageRangeMapping);
            int startTileCol = getInteger(CONFIG_START, pageRange);
            int endTileCol = getInteger(CONFIG_END, pageRange);
            IntRange pRange = new IntRange(startTileCol, endTileCol);
            pageRangeConfig.setPageRange(pRange);

            String assignmentTypeStr = getString(CONFIG_ASSIGNMENT_TYPE, pageRangeMapping);
            WebscorePageRangeAssignmentType assignmentType = WebscorePageRangeAssignmentType.SEQ;
            if (assignmentTypeStr != null) {
                assignmentType = WebscorePageRangeAssignmentType.valueOf(assignmentTypeStr);
            }
            pageRangeConfig.setAssignmentType(assignmentType);

            config.addPageRangeConfig(pageRangeConfig);
        }

        config.populateTileConfigs();

        return config;
    }

}
