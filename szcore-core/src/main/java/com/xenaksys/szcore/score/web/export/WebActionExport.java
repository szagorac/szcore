package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.web.WebAction;
import com.xenaksys.szcore.web.WebActionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebActionExport {
    private String id;
    private WebActionType actionType;
    private List<String> elementIds;
    private Map<String, Object> params;

    public String getId() {
        return id;
    }

    public WebActionType getActionType() {
        return actionType;
    }

    public List<String> getElementIds() {
        return elementIds;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void populate(WebAction from) {
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
        return "WebActionExport{" +
                "id='" + id + '\'' +
                ", actionType=" + actionType +
                ", elementIds=" + elementIds +
                ", params=" + params +
                '}';
    }
}
