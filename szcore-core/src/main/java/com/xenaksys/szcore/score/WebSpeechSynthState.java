package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_IS_INTERRUPT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_TEXT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_VOICE;
import static com.xenaksys.szcore.Consts.WEB_SPEECH_VOICE_RANDOM;

public class WebSpeechSynthState {
    static final Logger LOG = LoggerFactory.getLogger(WebSpeechSynthState.class);

    private static final String TILE_TEXT_TOKEN = "@TILE_TEXT@";
    private static final boolean DEFAULT_IS_PLAY_SPEECH_SYNTH_ON_CLICK = false;
    private static final String DEFAULT_SPEECH_TEXT = "I, believe. I believe in " + TILE_TEXT_TOKEN + ".";
    private static final String DEFAULT_SPEECH_VOICE = WEB_SPEECH_VOICE_RANDOM;
    private static final boolean DEFAULT_SPEECH_IS_INTERRUPT = true;

    private boolean isPlaySpeechSynthOnClick = DEFAULT_IS_PLAY_SPEECH_SYNTH_ON_CLICK;
    private String speechText = DEFAULT_SPEECH_TEXT;
    private String speechVoice = DEFAULT_SPEECH_VOICE;
    private boolean speechIsInterrupt = DEFAULT_SPEECH_IS_INTERRUPT;

    public boolean isPlaySpeechSynthOnClick() {
        return isPlaySpeechSynthOnClick;
    }

    public void setPlaySpeechSynthOnClick(boolean playSpeechSynthOnClick) {
        isPlaySpeechSynthOnClick = playSpeechSynthOnClick;
    }

    public String getSpeechText() {
        return speechText;
    }

    public void setSpeechText(String speechText) {
        this.speechText = speechText;
    }

    public String getSpeechVoice() {
        return speechVoice;
    }

    public void setSpeechVoice(String speechVoice) {
        this.speechVoice = speechVoice;
    }

    public boolean isSpeechIsInterrupt() {
        return speechIsInterrupt;
    }

    public void setSpeechIsInterrupt(boolean speechIsInterrupt) {
        this.speechIsInterrupt = speechIsInterrupt;
    }

    public boolean validate() {
        if (speechText != null && speechText.isEmpty()) {
            speechText = DEFAULT_SPEECH_TEXT;
        }
        if (speechVoice != null && speechVoice.isEmpty()) {
            speechVoice = DEFAULT_SPEECH_VOICE;
        }
        return true;
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK, isPlaySpeechSynthOnClick());
        config.put(WEB_CONFIG_SPEECH_TEXT, getSpeechText());
        config.put(WEB_CONFIG_SPEECH_VOICE, getSpeechVoice());
        config.put(WEB_CONFIG_SPEECH_IS_INTERRUPT, isSpeechIsInterrupt());
        return config;
    }

    public WebSpeechSynthState copy(WebSpeechSynthState to) {
        if (to == null) {
            to = new WebSpeechSynthState();
        }
        to.setPlaySpeechSynthOnClick(this.isPlaySpeechSynthOnClick);
        to.setSpeechText(this.speechText);
        to.setSpeechVoice(this.speechVoice);
        to.setSpeechIsInterrupt(this.speechIsInterrupt);
        return to;
    }

    @Override
    public String toString() {
        return "WebSpeechSynthState{" +
                "isPlaySpeechSynthOnClick=" + isPlaySpeechSynthOnClick +
                ", speechText='" + speechText + '\'' +
                ", speechVoice='" + speechVoice + '\'' +
                ", speechIsInterrupt=" + speechIsInterrupt +
                '}';
    }
}
