package com.xenaksys.szcore.score.web.audience;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Script;
import com.xenaksys.szcore.model.ScriptType;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

public class WebAudienceScoreScript implements Script {
    private IntId id;
    private BeatId beatId;
    private String content;
    private boolean isResetPoint;
    private boolean isResetOnly;

    public WebAudienceScoreScript(IntId id, BeatId beatId, String content, boolean isResetPoint, boolean isResetOnly) {
        this.id = id;
        this.beatId = beatId;
        this.content = content;
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
        return ScriptType.WEB_AUDIENCE_SCORE;
    }

    @Override
    public Script copy(BeatId newBeatId) {
        IntId newId = new IntId(Consts.ID_SOURCE.incrementAndGet());
        return new WebAudienceScoreScript(newId, newBeatId, this.content, this.isResetPoint, this.isResetOnly);
    }

    public boolean isResetPoint() {
        return isResetPoint;
    }

    public boolean isResetOnly() {
        return isResetOnly;
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
        return "WebAudienceScoreScript{" +
                "id=" + id +
                ", beatId=" + beatId +
                ", script='" + content + '\'' +
                ", isResetPoint='" + isResetPoint + '\'' +
                ", isResetOnly='" + isResetOnly + '\'' +
                '}';
    }
}
