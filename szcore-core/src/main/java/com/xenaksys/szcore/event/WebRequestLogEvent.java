package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.ZsWebRequest;

public class WebRequestLogEvent extends IncomingWebEvent {

    private final ZsWebRequest zsRequest;

    public WebRequestLogEvent(ZsWebRequest zsRequest, long creationTime) {
        super(null, null, null, creationTime, 0L, 0L);
        this.zsRequest = zsRequest;
    }

    public ZsWebRequest getZsRequest() {
        return zsRequest;
    }

    @Override
    public IncomingWebEventType getWebEventType() {
        return IncomingWebEventType.REQUEST_LOG;
    }

}
