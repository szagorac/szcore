package com.xenaksys.szcore.score.web.audience.export;

import java.util.HashMap;
import java.util.Map;

public class WebAudienceScoreStateDeltaExport {
    private final HashMap<String, Object> delta;

    public WebAudienceScoreStateDeltaExport(HashMap<String, Object> delta) {
        this.delta = delta;
    }

    public Map<String, Object> getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "WebAudienceScoreStateDeltaExport{" +
                "delta=" + delta.toString() +
                '}';
    }
}
