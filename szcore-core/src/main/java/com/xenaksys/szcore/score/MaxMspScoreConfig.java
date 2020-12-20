package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.ScriptEventPreset;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MaxMspScoreConfig {

    private String scoreName;
    private String audioFilePrefix = "UnionRose_";
    private int pageBufferNo = 2;
    private int pageBufferStartOffsetNo = 2;
    private AtomicInteger currentBufferNo = new AtomicInteger(1);
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

    public int getPageBufferNo() {
        return pageBufferNo;
    }

    public void setPageBufferNo(int pageBufferNo) {
        this.pageBufferNo = pageBufferNo;
    }

    public int getPageBufferStartOffsetNo() {
        return pageBufferStartOffsetNo;
    }

    public void setPageBufferStartOffsetNo(int pageBufferStartOffsetNo) {
        this.pageBufferStartOffsetNo = pageBufferStartOffsetNo;
    }

    public int getPageBufferEndNo() {
        return pageBufferStartOffsetNo + pageBufferNo;
    }

    public String getAudioFilePrefix() {
        return audioFilePrefix;
    }

    public void setAudioFilePrefix(String audioFilePrefix) {
        this.audioFilePrefix = audioFilePrefix;
    }

    public void incrementPageBufferNo() {
        if (currentBufferNo.get() < pageBufferNo) {
            currentBufferNo.incrementAndGet();
            return;
        }
        currentBufferNo.set(1);
    }

    public int getNextPageBufferNo() {
        int out = pageBufferStartOffsetNo;
        if (currentBufferNo.get() < pageBufferNo) {
            out += currentBufferNo.get();
        }
        return out;
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

    public OscScript createSetFileInNextBufferScript(BeatId beatId, int pageNo, int buffer) {
        int bufferNo = buffer;
        if (buffer == 0) {
            bufferNo = getNextPageBufferNo();
        }
        String target = Consts.OSC_ADDRESS_ZSCORE + Consts.MAXMSP_BUFFER_TARGET.replace(Consts.MAXMSP_BUFFER_NO_TOKEN, "" + bufferNo);

        //args 0 = cmd, other args ...
        List<Object> args = new ArrayList<>();
        args.add(Consts.MAXMSP_CMD_SET_FILE);
//        args.add(target);

        String fileName = audioFilePrefix;
        fileName += Consts.MAXMSP_BAR_PREFIX;
        fileName += "" + pageNo;
        fileName += Consts.WAV_FILE_EXTENSION;
        args.add(fileName);

        IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
        return new OscScript(id, beatId, target, args, false);
    }

    public OscScript createPlayNextBufferScript(BeatId beatId, int buffer) {
        int bufferNo = buffer;
        if (buffer == 0) {
            bufferNo = getNextPageBufferNo();
        }
        String target = Consts.OSC_ADDRESS_ZSCORE + Consts.MAXMSP_BUFFER_TARGET.replace(Consts.MAXMSP_BUFFER_NO_TOKEN, "" + bufferNo);

        //args 0 = cmd, other args ...
        List<Object> args = new ArrayList<>();
        args.add(Consts.MAXMSP_CMD_PLAY);
//        args.add(target);

        IntId id = new IntId(Consts.ID_SOURCE.incrementAndGet());
        return new OscScript(id, beatId, target, args, false);
    }
}
