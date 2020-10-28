package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.ScriptEventPreset;
import com.xenaksys.szcore.model.id.BeatId;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;

public class MaxScoreConfig {

    private String scoreName;
    private final TIntObjectHashMap<ScriptEventPreset> presets = new TIntObjectHashMap<>();

    public void addPreset(ScriptEventPreset preset) {
        presets.put(preset.getId(), preset);
    }

    public ScriptEventPreset getPresetScripts(int id) {
        return presets.get(id);
    }

    public TIntObjectHashMap<ScriptEventPreset> getPresets() {
        return presets;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public ScriptEventPreset getBeatResetScripts(BeatId beatId) {
        int beatNo = beatId.getBaseBeatNo();
        if (presets.containsKey(beatNo)) {
            return presets.get(beatNo);
        }

        int[] keys = presets.keys();
        Arrays.sort(keys);
        int outIndex = Arrays.binarySearch(keys, beatNo);
        int idx = outIndex;
        if (outIndex < 0) {
            idx += 1;
            idx *= (-1);
            idx -= 1;
        }

        int outId = keys[idx];
        return presets.get(outId);
    }
}
