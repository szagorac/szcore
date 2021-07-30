package com.xenaksys.szcore.web;

import java.util.List;
import java.util.Map;

public class WebScoreAction {
    private final WebScoreActionType type;
    private final Map<String, Object> params;
    private final List<String> targets;

    public WebScoreAction(WebScoreActionType actionType, List<String> targets, Map<String, Object> params) {
        this.type = actionType;
        this.targets = targets;
        this.params = params;
    }

    public WebScoreActionType getType() {
        return type;
    }

    public List<String> getTargets() {
        return targets;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "WebAudienceAction{" +
                ", actionType=" + type +
                ", targets=" + targets +
                ", params=" + params +
                '}';
    }
}
