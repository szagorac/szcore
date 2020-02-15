package com.xenaksys.szcore.web;

public class WebAction {
    private final String id;
    private final WebActionType actionType;
    private final String elementId;

    public WebAction(String id, WebActionType actionType, String elementId) {
        this.id = id;
        this.actionType = actionType;
        this.elementId = elementId;
    }

    public String getId() {
        return id;
    }

    public WebActionType getActionType() {
        return actionType;
    }

    public String getElementId() {
        return elementId;
    }
}
