package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebSpeechSynthConfig;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_INTERRUPT_TIMEOUT_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_INTERRUPT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_LANG;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_UTTERANCES;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_UTTERANCE_TIMEOUT_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_VOLUME;

public class WebSpeechSynthConfigExport {

    private double volume;
    private double pitch;
    private double rate;
    private String lang;
    private int maxVoiceLoadAttempts;
    private int maxUtterances;
    private int utteranceTimeoutSec;
    private boolean isInterrupt;
    private int interruptTimeout;

    public double getVolume() {
        return volume;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRate() {
        return rate;
    }

    public String getLang() {
        return lang;
    }

    public int getMaxVoiceLoadAttempts() {
        return maxVoiceLoadAttempts;
    }

    public int getMaxUtterances() {
        return maxUtterances;
    }

    public int getUtteranceTimeoutSec() {
        return utteranceTimeoutSec;
    }

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public int getInterruptTimeout() {
        return interruptTimeout;
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_VOLUME, getVolume());
        config.put(WEB_CONFIG_PITCH, getPitch());
        config.put(WEB_CONFIG_RATE, getRate());
        config.put(WEB_CONFIG_LANG, getLang());
        config.put(WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS, getMaxVoiceLoadAttempts());
        config.put(WEB_CONFIG_MAX_UTTERANCES, getMaxUtterances());
        config.put(WEB_CONFIG_UTTERANCE_TIMEOUT_SEC, getUtteranceTimeoutSec());
        config.put(WEB_CONFIG_IS_INTERRUPT, isInterrupt());
        config.put(WEB_CONFIG_INTERRUPT_TIMEOUT_MS, getInterruptTimeout());
        return config;
    }

    public void populate(WebSpeechSynthConfig from) {
        if (from == null) {
            return;
        }
        this.volume = from.getVolume();
        this.pitch = from.getPitch();
        this.rate = from.getRate();
        this.lang = from.getLang();
        this.maxVoiceLoadAttempts = from.getMaxVoiceLoadAttempts();
        this.maxUtterances = from.getMaxUtterances();
        this.utteranceTimeoutSec = from.getUtteranceTimeoutSec();
        this.isInterrupt = from.isInterrupt();
        this.interruptTimeout = from.getInterruptTimeout();
    }

    @Override
    public String toString() {
        return "WebSpeechSynthConfigExport{" +
                "volume=" + volume +
                ", pitch=" + pitch +
                ", rate=" + rate +
                ", lang='" + lang + '\'' +
                ", maxVoiceLoadAttempts=" + maxVoiceLoadAttempts +
                ", maxUtterances=" + maxUtterances +
                ", utteranceTimeoutSec=" + utteranceTimeoutSec +
                ", isInterrupt=" + isInterrupt +
                ", interruptTimeout=" + interruptTimeout +
                '}';
    }
}
