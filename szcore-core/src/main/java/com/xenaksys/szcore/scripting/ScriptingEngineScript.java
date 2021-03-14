package com.xenaksys.szcore.scripting;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

public class ScriptingEngineScript implements Script {
    private final IntId id;
    private final BeatId beatId;
    private final String content;
    private final boolean isReset;

    public ScriptingEngineScript(IntId id, BeatId beatId, String content, boolean isReset) {
        this.id = id;
        this.beatId = beatId;
        this.content = content;
        this.isReset = isReset;
    }

    @Override
    public BeatId getBeatId() {
        return beatId;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public ScriptType getType() {
        return ScriptType.SCRIPT_ENGINE;
    }

    @Override
    public Script copy(BeatId newBeatId) {
        IntId newId = new IntId(Consts.ID_SOURCE.incrementAndGet());
        return new ScriptingEngineScript(newId, newBeatId, this.content, this.isReset);
    }

    public boolean isReset() {
        return isReset;
    }

    @Override
    public Id getId() {
        return id;
    }

    @Override
    public int compareTo(Script o) {
        return id.getValue() - ((IntId) o.getId()).getValue();
    }

    @Override
    public String toString() {
        return "ScriptingEngineScript{" +
                "id=" + id +
                ", beatId=" + beatId +
                ", script='" + content + '\'' +
                ", isReset='" + isReset + '\'' +
                '}';
    }
}
