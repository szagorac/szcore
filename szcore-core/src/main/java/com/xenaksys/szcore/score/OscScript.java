package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

import java.util.Arrays;
import java.util.List;

public class OscScript implements Script {
    private IntId id;
    private BeatId beatId;
    private String content;
    private String target;
    private List<Object> args;

    public OscScript(IntId id, BeatId beatId, String target, List<Object> args) {
        this.id = id;
        this.beatId = beatId;
        this.content = target;
        this.target = target;
        this.args = args;
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
        return ScriptType.MAX;
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
                ", args=" + Arrays.toString(args.toArray()) +
                '}';
    }
}
