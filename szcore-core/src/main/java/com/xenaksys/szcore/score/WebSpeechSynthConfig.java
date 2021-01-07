package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_INTERRUPT_TIMEOUT_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_INTERRUPT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_LANG;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_UTTERANCES;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_UTTERANCE_TIMEOUT_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_VOLUME;

public class WebSpeechSynthConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebSpeechSynthConfig.class);

    private static final double MIN_VOLUME = 0.0;
    private static final double MAX_VOLUME = 1.0;
    private static final double MIN_PITCH = 0.0;
    private static final double MAX_PITCH = 2.0;
    private static final double MIN_RATE = 0.1;
    private static final double MAX_RATE = 10.0;
    private static final String DEFAULT_LANG = "en-GB";
    private static final double DEFAULT_VOLUME = 1.0;
    private static final double DEFAULT_PITCH = 1.0;
    private static final double DEFAULT_RATE = 0.7;
    private static final int DEFAULT_MAX_VOICE_LOAD_ATTEMPTS = 10;
    private static final int DEFAULT_MAX_UTTERANCES = 5;
    private static final int DEFAULT_UTTERANCE_TIMEOUT_SEC = 30;
    private static final int DEFAULT_INTERRUPT_TIMEOUT_MS = 250;
    private static final boolean DEFAULT_IS_INTERRUPT = false;

    private double volume = DEFAULT_VOLUME;
    private double pitch = DEFAULT_PITCH;
    private double rate = DEFAULT_RATE;
    private String lang = DEFAULT_LANG;
    private int maxVoiceLoadAttempts = DEFAULT_MAX_VOICE_LOAD_ATTEMPTS;
    private int maxUtterances = DEFAULT_MAX_UTTERANCES;
    private int utteranceTimeoutSec = DEFAULT_UTTERANCE_TIMEOUT_SEC;
    private boolean isInterrupt = DEFAULT_IS_INTERRUPT;
    private int interruptTimeout = DEFAULT_INTERRUPT_TIMEOUT_MS;

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getMaxVoiceLoadAttempts() {
        return maxVoiceLoadAttempts;
    }

    public void setMaxVoiceLoadAttempts(int maxVoiceLoadAttempts) {
        this.maxVoiceLoadAttempts = maxVoiceLoadAttempts;
    }

    public int getMaxUtterances() {
        return maxUtterances;
    }

    public void setMaxUtterances(int maxUtterances) {
        this.maxUtterances = maxUtterances;
    }

    public int getUtteranceTimeoutSec() {
        return utteranceTimeoutSec;
    }

    public void setUtteranceTimeoutSec(int utteranceTimeoutSec) {
        this.utteranceTimeoutSec = utteranceTimeoutSec;
    }

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public void setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
    }

    public int getInterruptTimeout() {
        return interruptTimeout;
    }

    public void setInterruptTimeout(int interruptTimeout) {
        this.interruptTimeout = interruptTimeout;
    }

    public boolean validate() {
        if (volume < MIN_VOLUME) {
            LOG.info("validate: invalid volume, setting to {}", MIN_VOLUME);
            volume = MIN_VOLUME;
        } else if (volume > MAX_VOLUME) {
            LOG.info("validate: invalid volume, setting to {}", MAX_VOLUME);
            volume = MAX_VOLUME;
        }
        if (pitch < MIN_PITCH) {
            LOG.info("validate: invalid pitch, setting to {}", MIN_PITCH);
            pitch = MIN_PITCH;
        } else if (pitch > MAX_PITCH) {
            LOG.info("validate: invalid pitch, setting to {}", MAX_PITCH);
            pitch = MAX_PITCH;
        }
        if (rate < MIN_RATE) {
            LOG.info("validate: invalid rate, setting to {}", MIN_RATE);
            rate = MIN_RATE;
        } else if (rate > MAX_RATE) {
            LOG.info("validate: invalid rate, setting to {}", MAX_RATE);
            rate = MAX_RATE;
        }
        if (!isValidLang(lang)) {
            LOG.info("validate: invalid lang, setting to {}", DEFAULT_LANG);
            lang = DEFAULT_LANG;
        }
        if (maxVoiceLoadAttempts < 1 || maxVoiceLoadAttempts > 10) {
            LOG.info("validate: invalid maxVoiceLoadAttempts, setting to {}", DEFAULT_MAX_VOICE_LOAD_ATTEMPTS);
            maxVoiceLoadAttempts = DEFAULT_MAX_VOICE_LOAD_ATTEMPTS;
        }
        if (maxUtterances < 1 || maxUtterances > 10) {
            LOG.info("validate: invalid maxUtterances, setting to {}", DEFAULT_MAX_UTTERANCES);
            maxUtterances = DEFAULT_MAX_UTTERANCES;
        }
        if (utteranceTimeoutSec < 1 || utteranceTimeoutSec > 60) {
            LOG.info("validate: invalid utteranceTimeoutSec, setting to {}", DEFAULT_UTTERANCE_TIMEOUT_SEC);
            utteranceTimeoutSec = DEFAULT_UTTERANCE_TIMEOUT_SEC;
        }
        if (interruptTimeout < 10 || interruptTimeout > 1000) {
            LOG.info("validate: invalid interruptTimeout, setting to {}", DEFAULT_INTERRUPT_TIMEOUT_MS);
            interruptTimeout = DEFAULT_INTERRUPT_TIMEOUT_MS;
        }

        return true;
    }

    private static boolean isValidLang(String lang) {
        return isValid(Locale.forLanguageTag(lang));
    }

    private static boolean isValid(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null;
        } catch (MissingResourceException e) {
            return false;
        }
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

    public WebSpeechSynthConfig copy(WebSpeechSynthConfig to) {
        if (to == null) {
            to = new WebSpeechSynthConfig();
        }
        to.setVolume(this.volume);
        to.setPitch(this.pitch);
        to.setRate(this.rate);
        to.setLang(this.lang);
        to.setMaxVoiceLoadAttempts(this.maxVoiceLoadAttempts);
        to.setMaxUtterances(this.maxUtterances);
        to.setUtteranceTimeoutSec(this.utteranceTimeoutSec);
        to.setInterrupt(this.isInterrupt);
        to.setInterruptTimeout(this.interruptTimeout);
        return to;
    }

    @Override
    public String toString() {
        return "WebSpeechSynthConfig{" +
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
