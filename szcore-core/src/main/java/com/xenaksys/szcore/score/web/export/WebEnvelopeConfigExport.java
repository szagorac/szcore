package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebEnvelopeConfig;

import java.util.HashMap;
import java.util.Map;

public class WebEnvelopeConfigExport {

    private double attackTime;
    private double decayTime;
    private double sustainTime;
    private double releaseTime;
    private double sustainLevel;

    public double getAttackTime() {
        return attackTime;
    }

    public double getDecayTime() {
        return decayTime;
    }

    public double getSustainTime() {
        return sustainTime;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public double getSustainLevel() {
        return sustainLevel;
    }

    public void populate(WebEnvelopeConfig from) {
        if (from == null) {
            return;
        }
        this.attackTime = from.getAttackTime();
        this.decayTime = from.getDecayTime();
        this.sustainTime = from.getSustainTime();
        this.releaseTime = from.getReleaseTime();
        this.sustainLevel = from.getSustainLevel();
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

    @Override
    public String toString() {
        return "WebEnvelopeConfigExport{" +
                "attackTime=" + attackTime +
                ", decayTime=" + decayTime +
                ", sustainTime=" + sustainTime +
                ", releaseTime=" + releaseTime +
                ", sustainLevel=" + sustainLevel +
                '}';
    }
}
