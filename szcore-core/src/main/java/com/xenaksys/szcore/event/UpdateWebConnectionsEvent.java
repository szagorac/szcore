package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.WebConnection;

import java.util.Set;

public class UpdateWebConnectionsEvent extends IncomingWebEvent {

    private final Set<WebConnection> clientConnections;

    public UpdateWebConnectionsEvent(Set<WebConnection> clientConnections, long creationTime) {
        super(null, null, null, creationTime, 0L, 0L);
        this.clientConnections = clientConnections;
    }

    public Set<WebConnection> getClientConnections() {
        return clientConnections;
    }

    @Override
    public IncomingWebEventType getWebEventType() {
        return IncomingWebEventType.CONNECTIONS_UPDATE;
    }

}
