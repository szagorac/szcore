package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebPlayerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_FILES;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_FILE_INDEX_MAP;

public class WebPlayerConfigExport {
    static final Logger LOG = LoggerFactory.getLogger(WebPlayerConfigExport.class);

    private String[] audioFiles;
    private int[][] audioFileIndexMap;

    public String[] getAudioFiles() {
        return audioFiles;
    }

    public int[][] getAudioFileIndexMap() {
        return audioFileIndexMap;
    }

    public void populate(WebPlayerConfig from) {
        if (from == null) {
            return;
        }
        this.audioFiles = from.getAudioFiles();
        this.audioFileIndexMap = from.getAudioFileIndexMap();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_AUDIO_FILES, getAudioFiles());
        config.put(WEB_CONFIG_AUDIO_FILE_INDEX_MAP, getAudioFileIndexMap());
        return config;
    }
}
