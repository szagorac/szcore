package com.xenaksys.szcore.model;

import java.util.List;
import java.util.Map;

public class ExtScoreInfo {

    private final String id;
    private int preset;
    private List<String> scripts;
    private Map<String, String> targetValues;

    public ExtScoreInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getPreset() {
        return preset;
    }

    public void setPreset(int preset) {
        this.preset = preset;
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
        return "ExtScoreInfo{" +
                "id='" + id + '\'' +
                ", preset=" + preset +
                '}';
    }
}
