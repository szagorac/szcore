package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.IntId;

public class TstScoreUtil {

    public static BeatId createBeatId(int beatNo, int instNo, int pageNo, int barNo, int baseBeatNo) {
        Id instrumentId = new IntId(beatNo);
        Id pageId = new IntId(instNo);
        Id scoreId = new IntId(pageNo);
        Id barId = new IntId(barNo);
        return new BeatId(beatNo, instrumentId, pageId, scoreId, barId, baseBeatNo);
    }
}
