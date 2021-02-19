package com.xenaksys.szcore.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScriptPreset {
    private final int id;
    private final List<String> scripts = new ArrayList<>();
    private final Map<String, Object> configs = new HashMap<>();

    public ScriptPreset(int id) {
        this.id = id;
    }

    public void addScripts(List<String> scripts) {
        if (scripts == null) {
            return;
        }
        this.scripts.addAll(scripts);
    }

    public void addScript(String script) {
        if (script == null) {
            return;
        }
        scripts.add(script);
    }

    public List<String> getScripts() {
        return scripts;
    }

    public void addConfig(String key, Object value) {
        if (key == null || value == null) {
            return;
        }
        configs.put(key, value);
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

    public Object getConfig(String key) {
        return configs.get(key);
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptPreset that = (ScriptPreset) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ScriptPreset{" +
                "id=" + id +
                ", scripts=" + scripts +
                '}';
    }
}
