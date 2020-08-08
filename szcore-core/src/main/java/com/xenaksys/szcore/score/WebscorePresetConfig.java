package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.ScriptPreset;
import gnu.trove.map.hash.TIntObjectHashMap;

public class WebscorePresetConfig {

    private String scoreName;
    private final TIntObjectHashMap<ScriptPreset> presets = new TIntObjectHashMap<>();

    public void addPreset(ScriptPreset preset) {
        presets.put(preset.getId(), preset);
    }

    public ScriptPreset getPreset(int id) {
        return presets.get(id);
    }

    public TIntObjectHashMap<ScriptPreset> getPresets() {
        return presets;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }
}
