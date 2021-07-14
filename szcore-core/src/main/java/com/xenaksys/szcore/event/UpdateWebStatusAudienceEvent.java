package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.WebConnection;

import java.util.Set;

public class UpdateWebStatusAudienceEvent extends IncomingWebAudienceEvent {

    private final Set<WebConnection> clientConnections;

    public UpdateWebStatusAudienceEvent(Set<WebConnection> clientConnections, long creationTime) {
        super(null, null, null, creationTime, 0L, 0L);
        this.clientConnections = clientConnections;
    }

    public Set<WebConnection> getClientConnections() {
        return clientConnections;
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.CONNECTIONS_UPDATE;
    }

}
