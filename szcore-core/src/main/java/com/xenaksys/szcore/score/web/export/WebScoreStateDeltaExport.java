package com.xenaksys.szcore.score.web.export;

import java.util.HashMap;
import java.util.Map;

public class WebScoreStateDeltaExport {
    private final HashMap<String, Object> delta;

    public WebScoreStateDeltaExport(HashMap<String, Object> delta) {
        this.delta = delta;
    }

    public Map<String, Object> getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "WebScoreStateDeltaExport{" +
                "delta=" + delta.toString() +
                '}';
    }
}
