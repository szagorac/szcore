package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EnvelopeConfig {
    static final Logger LOG = LoggerFactory.getLogger(EnvelopeConfig.class);

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

    public double getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(double attackTime) {
        this.attackTime = attackTime;
    }

    public double getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(double decayTime) {
        this.decayTime = decayTime;
    }

    public double getSustainTime() {
        return sustainTime;
    }

    public void setSustainTime(double sustainTime) {
        this.sustainTime = sustainTime;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(double releaseTime) {
        this.releaseTime = releaseTime;
    }

    public double getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    public boolean validate() {
        attackTime = validateMinMax(attackTime, "attackTime");
        decayTime = validateMinMax(decayTime, "decayTime");
        sustainTime = validateMinMax(sustainTime, "sustainTime");
        releaseTime = validateMinMax(releaseTime, "releaseTime");
        sustainLevel = validateMinMax(sustainLevel, "sustainLevel");

        double totalTime = attackTime + decayTime + sustainTime + releaseTime;
        if(totalTime > 1.0) {
            LOG.info("validate: max envelope time breach, setting default adsr a: {} d: {} s: {} r: {}",
                    DEFAULT_ATTACK_TIME, DEFAULT_DECAY_TIME, DEFAULT_SUSTAIN_TIME, DEFAULT_RELEASE_TIME);
            attackTime = DEFAULT_ATTACK_TIME;
            decayTime = DEFAULT_DECAY_TIME;
            sustainTime = DEFAULT_SUSTAIN_TIME;
            releaseTime = DEFAULT_RELEASE_TIME;
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

    public EnvelopeConfig copy(EnvelopeConfig to) {
        if (to == null) {
            to = new EnvelopeConfig();
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
        return "EnvelopeConfig{" +
                "attackTime=" + attackTime +
                ", decayTime=" + decayTime +
                ", sustainTime=" + sustainTime +
                ", releaseTime=" + releaseTime +
                ", sustainLevel=" + sustainLevel +
                '}';
    }
}
