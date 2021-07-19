package com.xenaksys.szcore.event.web.in;

import com.xenaksys.szcore.web.WebConnection;

import java.util.Set;

public class UpdateWebScoreConnectionsEvent extends WebScoreInEvent {

    private final Set<WebConnection> clientConnections;

    public UpdateWebScoreConnectionsEvent(Set<WebConnection> clientConnections, long creationTime) {
        super(null, null, null, creationTime, 0L, 0L);
        this.clientConnections = clientConnections;
    }

    public Set<WebConnection> getClientConnections() {
        return clientConnections;
    }


    @Override
    public WebScoreInEventType getWebScoreEventType() {
        return WebScoreInEventType.CONNECTIONS_UPDATE;
    }
}
