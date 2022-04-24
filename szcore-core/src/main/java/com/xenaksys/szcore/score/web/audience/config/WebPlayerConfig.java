package com.xenaksys.szcore.score.web.audience.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_FILES;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_FILE_INDEX_MAP;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_PLAYER;

public class WebPlayerConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebPlayerConfig.class);

    static ArrayList<String> DEFAULT_AUDIO_FILES = new ArrayList<>(Arrays.asList(
            "/audio/DialogsPitch1-1.mp3",
            "/audio/DialogsPitch1-2.mp3",
            "/audio/DialogsPitch1-3.mp3",
            "/audio/DialogsPitch2-1.mp3",
            "/audio/DialogsPitch2-2.mp3",
            "/audio/DialogsPitch2-3.mp3",
            "/audio/DialogsPitch3-1.mp3",
            "/audio/DialogsPitch3-2.mp3",
            "/audio/DialogsPitch3-3.mp3"
    ));

    static ArrayList<ArrayList<Integer>> DEFAULT_AUDIO_FILE_INDEX_MAP = new ArrayList<>();
    static {
        DEFAULT_AUDIO_FILE_INDEX_MAP.add( new ArrayList<>());
        DEFAULT_AUDIO_FILE_INDEX_MAP.add( new ArrayList<>(Arrays.asList(0, 1, 2)));
        DEFAULT_AUDIO_FILE_INDEX_MAP.add( new ArrayList<>(Arrays.asList(3, 4, 5)));
        DEFAULT_AUDIO_FILE_INDEX_MAP.add( new ArrayList<>(Arrays.asList(6, 7, 8)));
    }

    private ArrayList<String> audioFiles = DEFAULT_AUDIO_FILES;
    private ArrayList<ArrayList<Integer>> audioFilesIndexMap = DEFAULT_AUDIO_FILE_INDEX_MAP;

    private final PropertyChangeSupport pcs;

    public WebPlayerConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public ArrayList<String> getAudioFiles() {
        return audioFiles;
    }

    public void setAudioFiles(ArrayList<String> audioFiles) {
        this.audioFiles = audioFiles;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_PLAYER, WEB_CONFIG_AUDIO_FILES, audioFiles);
    }

    public ArrayList<ArrayList<Integer>> getAudioFilesIndexMap() {
        return audioFilesIndexMap;
    }

    public void setAudioFilesIndexMap(ArrayList<ArrayList<Integer>> audioFilesIndexMap) {
        this.audioFilesIndexMap = audioFilesIndexMap;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_PLAYER, WEB_CONFIG_AUDIO_FILE_INDEX_MAP, audioFilesIndexMap);
    }

    public void update(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            return;
        }
        if (config.containsKey(WEB_CONFIG_AUDIO_FILES)) {
            setAudioFiles((ArrayList<String>) config.get(WEB_CONFIG_AUDIO_FILES));
        }
        if (config.containsKey(WEB_CONFIG_AUDIO_FILE_INDEX_MAP)) {
            setAudioFilesIndexMap((ArrayList<ArrayList<Integer>>) config.get(WEB_CONFIG_AUDIO_FILE_INDEX_MAP));
        }
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_AUDIO_FILES, getAudioFiles());
        config.put(WEB_CONFIG_AUDIO_FILE_INDEX_MAP, getAudioFilesIndexMap());
        return config;
    }

}
