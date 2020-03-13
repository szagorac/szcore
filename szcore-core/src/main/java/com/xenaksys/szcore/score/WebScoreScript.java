package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

public class WebScoreScript implements Script {
    private IntId id;
    private BeatId beatId;
    private String content;

    public WebScoreScript(IntId id, BeatId beatId, String content) {
        this.id = id;
        this.beatId = beatId;
        this.content = content;
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
        return ScriptType.WEB_SCORE;
    }


    @Override
    public Id getId() {
        return id;
    }

    @Override
    public int compareTo(Script o) {
        return id.getValue() - ((IntId)o.getId()).getValue();
    }

    @Override
    public String toString() {
        return "BasicScript{" +
                "id=" + id +
                ", beatId=" + beatId +
                ", script='" + content + '\'' +
                '}';
    }
}
