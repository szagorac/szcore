package com.xenaksys.szcore.event;

import com.xenaksys.szcore.web.ZsWebRequest;

public class WebAudienceRequestLogEvent extends IncomingWebAudienceEvent {

    private final ZsWebRequest zsRequest;

    public WebAudienceRequestLogEvent(ZsWebRequest zsRequest, long creationTime) {
        super(null, null, null, creationTime, 0L, 0L);
        this.zsRequest = zsRequest;
    }

    public ZsWebRequest getZsRequest() {
        return zsRequest;
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.REQUEST_LOG;
    }

}
