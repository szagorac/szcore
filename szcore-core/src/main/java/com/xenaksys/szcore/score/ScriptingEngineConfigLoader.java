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
import static com.xenaksys.szcore.Consts.SCRIPTING_ENGINE_PRESET_FILE_SUFFIX;
import static com.xenaksys.szcore.Consts.YAML_FILE_EXTENSION;

public class ScriptingEngineConfigLoader extends YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(ScriptingEngineConfigLoader.class);

    public static ScriptingEngineConfig load(String workingDir) throws Exception {
        String path = workingDir + Consts.SLASH + SCRIPTING_ENGINE_PRESET_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }

        return load(file);
    }

    public static ScriptingEngineConfig load(File file) throws Exception {
        ScriptingEngineConfig config = new ScriptingEngineConfig();
        Map<String, Object> configMap = loadYaml(file);

        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("load ScriptingEngineConfig: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        List<Map<String, Object>> presetsConfigs = getListOfMaps(CONFIG_PRESETS, configMap);
        for (Map<String, Object> presetConfig : presetsConfigs) {
            Integer id = getInteger(CONFIG_ID, presetConfig);
            if (id == null) {
                throw new RuntimeException("load ScriptingEngineConfig: Invalid Preset ID");
            }
            ScriptPreset preset = new ScriptPreset(id);

            List<String> presetScripts = getStrList(CONFIG_SCRIPTS, presetConfig);
            preset.addScripts(presetScripts);

            config.addPreset(preset);
        }

        return config;
    }

}
