package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.ScriptPreset;
import gnu.trove.map.hash.TIntObjectHashMap;

public class ScriptingEngineConfig {
    private String scoreName;
    private final TIntObjectHashMap<ScriptPreset> presets = new TIntObjectHashMap<>();

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public ScriptPreset getPreset(int presetNo) {
        return presets.get(presetNo);
    }

    public void addPreset(ScriptPreset preset) {
        presets.put(preset.getId(), preset);
    }
}
