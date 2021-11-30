package com.xenaksys.szcore.score.web.audience.config;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.YamlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.*;

public abstract class AudienceWebscoreConfigLoader extends YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(AudienceWebscoreConfigLoader.class);

    public void load(String workingDir, AudienceWebscoreConfig config) throws Exception {
        String path = workingDir + Consts.SLASH + AUDIENCE_WEBSCORE_CONFIG_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }
        load(file, config);
    }

    public void load(File file, AudienceWebscoreConfig config) throws Exception {
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

            if (presetConfig.containsKey(CONFIG_WEB_CONFIG)) {
                List<Map<String, Object>> webConfigs = getListOfMaps(CONFIG_WEB_CONFIG, presetConfig);
                for (Map<String, Object> webConfig : webConfigs) {
                    for (String key : webConfig.keySet()) {
                        preset.addConfig(key, webConfig.get(key));
                    }
                }
            }

            config.addPreset(preset);
        }
        loadDelegate(config, configMap);
    }

    public abstract void loadDelegate(AudienceWebscoreConfig config, Map<String, Object> configMap);
}
