package com.xenaksys.szcore.event.web.audience;


import com.xenaksys.szcore.score.web.audience.AudioComponentType;

public class WebAudienceAudioEvent extends WebAudienceEvent {

    private final AudioComponentType componentType;
    private final WebAudienceAudioEventType audioEventType;
    private final double value;
    private final int durationMs;

    public WebAudienceAudioEvent(AudioComponentType componentType, WebAudienceAudioEventType eventType, double value, int durationMs, long creationTime) {
        super(null, null, creationTime);
        this.componentType = componentType;
        this.audioEventType = eventType;
        this.value = value;
        this.durationMs = durationMs;
    }

    public WebAudienceEventType getWebAudienceEventType() {
        return WebAudienceEventType.AUDIO;
    }

    public AudioComponentType getComponentType() {
        return componentType;
    }

    public WebAudienceAudioEventType getAudioEventType() {
        return audioEventType;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WebAudienceAudioEvent{" +
                "componentType=" + componentType +
                ", audioEventType=" + audioEventType +
                ", value=" + value +
                ", millis=" + durationMs +
                '}';
    }
}
