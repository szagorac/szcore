package com.xenaksys.szcore.event;


public class ElementSelectedEvent extends IncomingWebEvent {
    private final String elementId;
    private final boolean isSelected;

    public ElementSelectedEvent(String elementId, boolean isSelected, String sourceAddr, String requestPath, String eventId,
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
    public IncomingWebEventType getWebEventType() {
        return IncomingWebEventType.ELEMENT_SELECTED;
    }

}
