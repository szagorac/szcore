package com.xenaksys.szcore.event.web;

import java.util.List;

public class WebScoreRemoveConnectionEvent extends WebScoreInEvent {
    private final List<String> connectionIds;

    public WebScoreRemoveConnectionEvent(List<String> connectionIds, long creationTime) {
        super(null, null, null, creationTime, 0L, 0L);
        this.connectionIds = connectionIds;
    }

    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.CONNECTIONS_REMOVE;
    }

    @Override
    public String toString() {
        return "WebScoreConnectionEvent{" +
                "connectionIds=" + connectionIds +
                '}';
    }
}
