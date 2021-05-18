package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

public class BasicScript implements Script {
    private IntId id;
    private BeatId beatId;
    private String script;

    public BasicScript(IntId id, BeatId beatId, String script) {
        this.id = id;
        this.beatId = beatId;
        this.script = script;
    }

    @Override
    public BeatId getBeatId() {
        return beatId;
    }

    @Override
    public String getContent() {
        return script;
    }

    @Override
    public ScriptType getType() {
        return ScriptType.JAVASCRIPT;
    }

    @Override
    public Script copy(BeatId newBeatId) {
        IntId newId = new IntId(Consts.ID_SOURCE.incrementAndGet());
        return new BasicScript(newId, newBeatId, this.script);
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
        return "BasicScript{" +
                "id=" + id +
                ", beatId=" + beatId +
                ", script='" + script + '\'' +
                '}';
    }
}
