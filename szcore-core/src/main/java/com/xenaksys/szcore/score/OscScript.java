package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OscScript implements Script {
    private final IntId id;
    private final BeatId beatId;
    private final String content;
    private final String target;
    private final List<Object> args;
    private final boolean isResetPoint;
    private final boolean isResetOnly;

    public OscScript(IntId id, BeatId beatId, String target, List<Object> args, boolean isResetPoint, boolean isResetOnly) {
        this.id = id;
        this.beatId = beatId;
        this.content = target;
        this.target = target;
        this.args = args;
        this.isResetPoint = isResetPoint;
        this.isResetOnly = isResetOnly;
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
        return ScriptType.OSC_PLAYER;
    }

    @Override
    public Script copy(BeatId newBeatId) {
        IntId newId = new IntId(Consts.ID_SOURCE.incrementAndGet());
        List<Object> newArgs = new ArrayList<>(this.args);
        return new OscScript(newId, newBeatId, this.target, newArgs, this.isResetPoint, this.isResetOnly);
    }

    public List<Object> getArgs() {
        return args;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public Id getId() {
        return id;
    }

    public boolean isResetPoint() {
        return isResetPoint;
    }

    public boolean isResetOnly() {
        return isResetOnly;
    }

    @Override
    public int compareTo(Script o) {
        return id.getValue() - ((IntId) o.getId()).getValue();
    }

    @Override
    public String toString() {
        return "MaxScript{" +
                "id=" + id +
                ", beatId=" + beatId +
                ", content='" + content + '\'' +
                ", target='" + target + '\'' +
                ", isReset='" + isResetPoint + '\'' +
                ", isResetOnly='" + isResetOnly + '\'' +
                ", args=" + Arrays.toString(args.toArray()) +
                '}';
    }
}
