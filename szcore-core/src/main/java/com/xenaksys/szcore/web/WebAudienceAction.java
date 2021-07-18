package com.xenaksys.szcore.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WebAudienceAction {
    private final String id;
    private final WebAudienceActionType actionType;
    private final List<String> elementIds;
    private final Map<String, Object> params;

    public WebAudienceAction(String id, WebAudienceActionType actionType, List<String> elementIds) {
        this(id, actionType, elementIds, new HashMap<>());
    }

    public WebAudienceAction(String id, WebAudienceActionType actionType, List<String> elementIds, Map<String, Object> params) {
        this.id = id;
        this.actionType = actionType;
        this.elementIds = elementIds;
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public WebAudienceActionType getActionType() {
        return actionType;
    }

    public List<String> getElementIds() {
        return elementIds;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebAudienceAction webAudienceAction = (WebAudienceAction) o;
        return id.equals(webAudienceAction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "WebAudienceAction{" +
                "id='" + id + '\'' +
                ", actionType=" + actionType +
                ", elementIds=" + elementIds +
                ", params=" + params +
                '}';
    }
}
