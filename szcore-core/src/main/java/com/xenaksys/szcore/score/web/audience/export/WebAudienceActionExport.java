package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.web.WebAudienceAction;
import com.xenaksys.szcore.web.WebAudienceActionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebAudienceActionExport {
    private String id;
    private WebAudienceActionType actionType;
    private List<String> elementIds;
    private Map<String, Object> params;

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

    public void populate(WebAudienceAction from) {
        if (from == null) {
            return;
        }
        this.id = from.getId();
        this.actionType = from.getActionType();
        this.elementIds = new ArrayList<>(from.getElementIds());
        if (from.getParams() != null) {
            this.params = new HashMap<>(from.getParams());
        }
    }

    @Override
    public String toString() {
        return "WebAudienceActionExport{" +
                "id='" + id + '\'' +
                ", actionType=" + actionType +
                ", elementIds=" + elementIds +
                ", params=" + params +
                '}';
    }
}
