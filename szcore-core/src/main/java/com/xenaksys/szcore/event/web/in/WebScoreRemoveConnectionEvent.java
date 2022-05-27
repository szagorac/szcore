package com.xenaksys.szcore.event.web.in;

import java.util.List;

public class WebScoreRemoveConnectionEvent extends WebScoreInEvent {
    private final List<String> connectionIds;

    public WebScoreRemoveConnectionEvent(List<String> connectionIds, long creationTime) {
        super(null, null, null, null, creationTime, 0L, 0L, null);
        this.connectionIds = connectionIds;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.CONNECTIONS_REMOVE;
    }

    public List<String> getConnectionIds() {
        return connectionIds;
    }

    @Override
    public String toString() {
        return "WebScoreRemoveConnectionEvent{" +
                "connectionIds=" + connectionIds +
                '}';
    }
}
