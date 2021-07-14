package com.xenaksys.szcore.event;


import com.xenaksys.szcore.web.WebScoreStateType;

public class WebAudienceStateUpdateEvent extends WebAudienceEvent {

    private final WebScoreStateType propertyType;
    private final Object propertyValue;

    public WebAudienceStateUpdateEvent(WebScoreStateType propertyType, Object propertyValue, long creationTime) {
        super(null, null, creationTime);
        this.propertyType = propertyType;
        this.propertyValue = propertyValue;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.STATE_UPDATE;
    }

    public WebScoreStateType getPropertyType() {
        return propertyType;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    @Override
    public String toString() {
        return "WebAudienceStateUpdateEvent{" +
                "propertyType='" + propertyType + '\'' +
                ", propertyValue=" + propertyValue +
                '}';
    }
}
