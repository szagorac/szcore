package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_IS_INTERRUPT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_TEXT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_VOICE;

public class WebSpeechSynthStateExport {
    private boolean isPlaySpeechSynthOnClick;
    private String speechText;
    private String speechVoice;
    private boolean speechIsInterrupt;

    public boolean isPlaySpeechSynthOnClick() {
        return isPlaySpeechSynthOnClick;
    }

    public String getSpeechText() {
        return speechText;
    }

    public String getSpeechVoice() {
        return speechVoice;
    }

    public boolean isSpeechIsInterrupt() {
        return speechIsInterrupt;
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK, isPlaySpeechSynthOnClick());
        config.put(WEB_CONFIG_SPEECH_TEXT, getSpeechText());
        config.put(WEB_CONFIG_SPEECH_VOICE, getSpeechVoice());
        config.put(WEB_CONFIG_SPEECH_IS_INTERRUPT, isSpeechIsInterrupt());
        return config;
    }


    public void populate(WebSpeechSynthState from) {
        if (from == null) {
            return;
        }
        this.isPlaySpeechSynthOnClick = from.isPlaySpeechSynthOnClick();
        this.speechText = from.getSpeechText();
        this.speechVoice = from.getSpeechVoice();
        this.speechIsInterrupt = from.isSpeechIsInterrupt();
    }

    @Override
    public String toString() {
        return "WebSpeechSynthStateExport{" +
                "isPlaySpeechSynthOnClick=" + isPlaySpeechSynthOnClick +
                ", speechText='" + speechText + '\'' +
                ", speechVoice='" + speechVoice + '\'' +
                ", speechIsInterrupt=" + speechIsInterrupt +
                '}';
    }
}
