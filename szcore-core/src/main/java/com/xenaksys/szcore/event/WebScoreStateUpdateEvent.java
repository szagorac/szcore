package com.xenaksys.szcore.event;


import com.xenaksys.szcore.web.WebScoreStateType;

public class WebScoreStateUpdateEvent extends WebScoreEvent {

    private final WebScoreStateType propertyType;
    private final Object propertyValue;

    public WebScoreStateUpdateEvent(WebScoreStateType propertyType, Object propertyValue, long creationTime) {
        super(null, null, creationTime);
        this.propertyType = propertyType;
        this.propertyValue = propertyValue;
    }

    public WebScoreEventType getWebScoreEventType() {
        return WebScoreEventType.STATE_UPDATE;
    }

    public WebScoreStateType getPropertyType() {
        return propertyType;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String toString() {
        return "WebScoreStateUpdateEvent{" +
                "propertyType='" + propertyType + '\'' +
                ", propertyValue=" + propertyValue +
                '}';
    }
}
