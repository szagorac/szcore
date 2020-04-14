package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvelopeConfig {
    static final Logger LOG = LoggerFactory.getLogger(EnvelopeConfig.class);

    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 1.0;

    // All values 0 ->1. time values are a fraction of total envelope time = 1
    private double attackTime;
    private double decayTime;
    private double sustainTime;
    private double releaseTime;
    private double sustainLevel;

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
            LOG.info("validate: max envelope time breach, setting default adsr 0.5, 0.0. 0.0. 0.5");
            attackTime = 0.5;
            decayTime = 0.0;
            sustainTime = 0.0;
            releaseTime = 0.5;
        }

        return true;
    }

    public double validateMinMax(double value, String valueName) {
        if (value < MIN_VALUE) {
            LOG.info("validate: invalid {}, setting to {}", valueName, MIN_VALUE);
            return MIN_VALUE;
        } else if (value > MAX_VALUE) {
            LOG.info("validate: invalid {}, setting to {}", valueName, MAX_VALUE);
            return MAX_VALUE;
        }
        return value;
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
