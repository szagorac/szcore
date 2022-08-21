package com.xenaksys.szcore.algo.config;

import java.util.List;
import java.util.Map;

public class ExternalScoreConfig {
    private String id;
    private int preset;
    private List<String> scripts;
    private Map<String, String> targetValues;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPreset(int preset) {
        this.preset = preset;
    }

    public int getPreset() {
        return preset;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }

    public Map<String, String> getTargetValues() {
        return targetValues;
    }

    public void setTargetValues(Map<String, String> targetValues) {
        this.targetValues = targetValues;
    }

    @Override
    public String toString() {
        return "ExternalScoreConfig{" +
                "id='" + id + '\'' +
                ", preset=" + preset +
                '}';
    }
}
