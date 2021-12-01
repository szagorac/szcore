package com.xenaksys.szcore.score.web.audience.export;

import java.util.HashMap;
import java.util.Map;

public class WebAudienceScoreStateExport {
    private final HashMap<String, Object> state = new HashMap<>();

    public void addState(String key, Object value) {
        state.put(key, value);
    }

    public Map<String, Object> getState() {
        return state;
    }

    @Override
    public String toString() {
        return "WebAudienceScoreStateExport{" +
                "state=" + state +
                '}';
    }
}
