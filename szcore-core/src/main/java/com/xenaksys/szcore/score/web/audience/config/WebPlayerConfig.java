package com.xenaksys.szcore.score.web.audience.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.Map;

import static com.xenaksys.szcore.Consts.*;

public class WebPlayerConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebPlayerConfig.class);

    private static final String[] DEFAULT_AUDIO_FILES = {
            "/audio/DialogsPitch1-1.mp3",
            "/audio/DialogsPitch1-2.mp3",
            "/audio/DialogsPitch1-3.mp3",
            "/audio/DialogsPitch2-1.mp3",
            "/audio/DialogsPitch2-2.mp3",
            "/audio/DialogsPitch2-3.mp3",
            "/audio/DialogsPitch3-1.mp3",
            "/audio/DialogsPitch3-2.mp3",
            "/audio/DialogsPitch3-3.mp3"
    };
    private static final int[][] DEFAULT_AUDIO_FILE_INDEX_MAP = {
            {},
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
    };

    private String[] audioFiles = DEFAULT_AUDIO_FILES;
    private int[][] audioFileIndexMap = DEFAULT_AUDIO_FILE_INDEX_MAP;

    private final PropertyChangeSupport pcs;

    public WebPlayerConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public String[] getAudioFiles() {
        return audioFiles;
    }

    public void setAudioFiles(String[] audioFiles) {
        this.audioFiles = audioFiles;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_PLAYER, WEB_CONFIG_AUDIO_FILES, audioFiles);
    }

    public int[][] getAudioFileIndexMap() {
        return audioFileIndexMap;
    }

    public void setAudioFileIndexMap(int[][] audioFileIndexMap) {
        this.audioFileIndexMap = audioFileIndexMap;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_PLAYER, WEB_CONFIG_AUDIO_FILE_INDEX_MAP, audioFileIndexMap);
    }

    public void update(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return;
        }
        if (config.containsKey(WEB_CONFIG_AUDIO_FILES)) {
            setAudioFiles((String[]) config.get(WEB_CONFIG_AUDIO_FILES));
        }
        if (config.containsKey(WEB_CONFIG_AUDIO_FILE_INDEX_MAP)) {
            setAudioFileIndexMap((int[][]) config.get(WEB_CONFIG_AUDIO_FILE_INDEX_MAP));
        }
    }
}
