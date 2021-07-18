package com.xenaksys.szcore.event.osc;


import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.IncomingWebAudienceEventType;

public class ElementSelectedAudienceEvent extends IncomingWebAudienceEvent {
    private final String elementId;
    private final boolean isSelected;

    public ElementSelectedAudienceEvent(String elementId, boolean isSelected, String sourceAddr, String requestPath, String eventId,
                                        long creationTime, long clientEventCreatedTime, long clientEventSentTime) {
        super(eventId, sourceAddr, requestPath, creationTime, clientEventCreatedTime, clientEventSentTime);
        this.elementId = elementId;
        this.isSelected = isSelected;
    }

    public String getElementId() {
        return elementId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public IncomingWebAudienceEventType getWebEventType() {
        return IncomingWebAudienceEventType.ELEMENT_SELECTED;
    }

}
