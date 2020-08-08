package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.ScriptPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.CONFIG_ID;
import static com.xenaksys.szcore.Consts.CONFIG_PRESETS;
import static com.xenaksys.szcore.Consts.CONFIG_SCORE_NAME;
import static com.xenaksys.szcore.Consts.CONFIG_SCRIPTS;
import static com.xenaksys.szcore.Consts.WEBSCORE_PRESET_FILE_SUFFIX;
import static com.xenaksys.szcore.Consts.YAML_FILE_EXTENSION;

public class WebscoreConfigLoader extends YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(WebscoreConfigLoader.class);

    public static WebscorePresetConfig loadWebScorePresets(String workingDir) throws Exception {
        String path = workingDir + Consts.SLASH + WEBSCORE_PRESET_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }

        return loadWebScorePresets(file);
    }

    public static WebscorePresetConfig loadWebScorePresets(File file) throws Exception {
        WebscorePresetConfig config = new WebscorePresetConfig();
        Map<String, Object> configMap = loadYaml(file);

        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadWebScorePresets: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        List<Map<String, Object>> presetsConfigs = getListOfMaps(CONFIG_PRESETS, configMap);
        for (Map<String, Object> presetConfig : presetsConfigs) {
            Integer id = getInteger(CONFIG_ID, presetConfig);
            if (id == null) {
                throw new RuntimeException("loadWebScorePresets: Invalid Preset ID");
            }
            ScriptPreset preset = new ScriptPreset(id);

            List<String> presetScripts = getStrList(CONFIG_SCRIPTS, presetConfig);
            preset.addScripts(presetScripts);

            config.addPreset(preset);
        }

        return config;
    }

}
