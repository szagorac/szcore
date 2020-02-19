package com.xenaksys.szcore.web;

import java.util.List;

public class WebAction {
    private final String id;
    private final WebActionType actionType;
    private final List<String> elementIds;

    public WebAction(String id, WebActionType actionType, List<String> elementIds) {
        this.id = id;
        this.actionType = actionType;
        this.elementIds = elementIds;
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
}
