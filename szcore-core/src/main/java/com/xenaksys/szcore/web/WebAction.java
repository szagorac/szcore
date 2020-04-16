package com.xenaksys.szcore.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebAction {
    private final String id;
    private final WebActionType actionType;
    private final List<String> elementIds;
    private final Map<String, Object> params;

    public WebAction(String id, WebActionType actionType, List<String> elementIds) {
        this(id, actionType, elementIds, new HashMap<>());
    }

    public WebAction(String id, WebActionType actionType, List<String> elementIds, Map<String, Object> params) {
        this.id = id;
        this.actionType = actionType;
        this.elementIds = elementIds;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public WebActionType getActionType() {
        return actionType;
    }

    public List<String> getElementId() {
        return elementIds;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
