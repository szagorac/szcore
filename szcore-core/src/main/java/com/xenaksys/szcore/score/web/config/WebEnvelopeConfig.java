package com.xenaksys.szcore.score.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_ATTACK_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DECAY_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_RELEASE_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SUSTAIN_LEVEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SUSTAIN_TIME;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN_ENVELOPE;

public class WebEnvelopeConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebEnvelopeConfig.class);

    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 1.0;

    private static final double DEFAULT_ATTACK_TIME = 0.4;
    private static final double DEFAULT_DECAY_TIME = 0.0;
    private static final double DEFAULT_SUSTAIN_TIME = 0.2;
    private static final double DEFAULT_RELEASE_TIME = 0.4;
    private static final double DEFAULT_SUSTAIN_LEVEL = 1.0;

    // All values 0 ->1. time values are a fraction of total envelope time = 1
    private double attackTime = DEFAULT_ATTACK_TIME;
    private double decayTime = DEFAULT_DECAY_TIME;
    private double sustainTime = DEFAULT_SUSTAIN_TIME;
    private double releaseTime = DEFAULT_RELEASE_TIME;
    private double sustainLevel = DEFAULT_SUSTAIN_LEVEL;

    private final PropertyChangeSupport pcs;

    public WebEnvelopeConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public double getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(double attackTime) {
        this.attackTime = attackTime;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_ENVELOPE, WEB_CONFIG_ATTACK_TIME, attackTime);
    }

    public double getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(double decayTime) {
        this.decayTime = decayTime;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_ENVELOPE, WEB_CONFIG_DECAY_TIME, decayTime);
    }

    public double getSustainTime() {
        return sustainTime;
    }

    public void setSustainTime(double sustainTime) {
        this.sustainTime = sustainTime;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_ENVELOPE, WEB_CONFIG_SUSTAIN_TIME, sustainTime);
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(double releaseTime) {
        this.releaseTime = releaseTime;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_ENVELOPE, WEB_CONFIG_RELEASE_TIME, releaseTime);
    }

    public double getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_ENVELOPE, WEB_CONFIG_SUSTAIN_LEVEL, sustainLevel);
    }

    public boolean validate() {
        setAttackTime(validateMinMax(attackTime, WEB_CONFIG_ATTACK_TIME));
        setDecayTime(validateMinMax(decayTime, WEB_CONFIG_DECAY_TIME));
        setSustainTime(validateMinMax(sustainTime, WEB_CONFIG_SUSTAIN_TIME));
        setReleaseTime(validateMinMax(releaseTime, WEB_CONFIG_RELEASE_TIME));
        setSustainLevel(validateMinMax(sustainLevel, WEB_CONFIG_SUSTAIN_LEVEL));

        double totalTime = attackTime + decayTime + sustainTime + releaseTime;
        if (totalTime > 1.0) {
            LOG.info("validate: max envelope time breach, setting default adsr a: {} d: {} s: {} r: {}",
                    DEFAULT_ATTACK_TIME, DEFAULT_DECAY_TIME, DEFAULT_SUSTAIN_TIME, DEFAULT_RELEASE_TIME);
            setAttackTime(DEFAULT_ATTACK_TIME);
            setDecayTime(DEFAULT_DECAY_TIME);
            setSustainTime(DEFAULT_SUSTAIN_TIME);
            setReleaseTime(DEFAULT_RELEASE_TIME);
        }

        return true;
    }

    public double validateMinMax(double value, String valueName) {
        if (value < MIN_VALUE) {
            LOG.info("validate: invalid {} value: {}, setting to {}", valueName, value, MIN_VALUE);
            return MIN_VALUE;
        } else if (value > MAX_VALUE) {
            LOG.info("validate: invalid {} value: {}, setting to {}", valueName, value, MAX_VALUE);
            return MAX_VALUE;
        }
        return value;
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("envelope.attackTime", getAttackTime());
        config.put("envelope.decayTime", getDecayTime());
        config.put("envelope.sustainTime", getSustainTime());
        config.put("envelope.releaseTime", getReleaseTime());
        config.put("envelope.sustainLevel", getSustainLevel());
        return config;
    }

    public WebEnvelopeConfig copy(WebEnvelopeConfig to) {
        if (to == null) {
            to = new WebEnvelopeConfig(pcs);
        }
        to.setAttackTime(this.attackTime);
        to.setDecayTime(this.decayTime);
        to.setSustainTime(this.sustainTime);
        to.setReleaseTime(this.releaseTime);
        to.setSustainLevel(this.sustainLevel);
        return to;
    }

    @Override
    public String toString() {
        return "WebEnvelopeConfig{" +
                "attackTime=" + attackTime +
                ", decayTime=" + decayTime +
                ", sustainTime=" + sustainTime +
                ", releaseTime=" + releaseTime +
                ", sustainLevel=" + sustainLevel +
                '}';
    }
}
