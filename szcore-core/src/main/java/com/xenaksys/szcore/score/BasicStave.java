package com.xenaksys.szcore.score;

import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.id.StaveId;

public class BasicStave implements Stave {
    private final StaveId id;
    private final String oscAddress;
    private final String oscAddressScoreFollower;
    private final String oscAddressScoreBeater;
    private final String oscAddressScoreStartMark;
    private final String oscAddressScoreDynamicsLine;
    private final String oscAddressScoreDynamicsBox;
    private final String oscAddressScorePressureBox;
    private final double xPosition;
    private final double yPosition;
    private final double zPosition;
    private final double beaterYPositionMin;
    private final double beaterYPositionMax;
    private final double beaterYPositionDelta;
    private final double scale;
    private final boolean isVisible;
    private volatile boolean isActive;
    private volatile double dynamicsValue;

    public BasicStave(StaveId id,
                      String oscAddress,
                      String oscAddressScoreFollower,
                      String oscAddressScoreBeater,
                      String oscAddressScoreStartMark,
                      String oscAddressScoreDynamicsLine,
                      String oscAddressScoreDynamicsBox,
                      String oscAddressScorePressureBox,
                      double xPosition,
                      double yPosition,
                      double zPosition,
                      double beaterYPositionMin,
                      double beaterYPositionMax,
                      double scale,
                      boolean isVisible) {
        this.id = id;
        this.oscAddress = oscAddress;
        this.oscAddressScoreFollower = oscAddressScoreFollower;
        this.oscAddressScoreBeater = oscAddressScoreBeater;
        this.oscAddressScoreStartMark = oscAddressScoreStartMark;
        this.oscAddressScoreDynamicsLine = oscAddressScoreDynamicsLine;
        this.oscAddressScoreDynamicsBox = oscAddressScoreDynamicsBox;
        this.oscAddressScorePressureBox = oscAddressScorePressureBox;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.zPosition = zPosition;
        this.scale = scale;
        this.isVisible = isVisible;
        this.isActive = false;
        this.beaterYPositionMax = beaterYPositionMax;
        this.beaterYPositionMin = beaterYPositionMin;
        this.beaterYPositionDelta = Math.abs(beaterYPositionMax - beaterYPositionMin);
    }

    public String getOscAddress() {
        return oscAddress;
    }

    public String getOscAddressScoreFollower() {
        return oscAddressScoreFollower;
    }

    public String getOscAddressScoreBeater() {
        return oscAddressScoreBeater;
    }

    public String getOscAddressScoreStartMark() {
        return oscAddressScoreStartMark;
    }

    public String getOscAddressScoreDynamicsBox() {
        return oscAddressScoreDynamicsBox;
    }

    @Override
    public String getOscAddressScorePressureBox() {
        return oscAddressScorePressureBox;
    }

    public double getxPosition() {
        return xPosition;
    }

    public double getyPosition() {
        return yPosition;
    }

    public double getzPosition() {
        return zPosition;
    }

    public double getScale() {
        return scale;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getBeaterYPositionMin() {
        return beaterYPositionMin;
    }

    public double getBeaterYPositionMax() {
        return beaterYPositionMax;
    }

    public String getOscAddressScoreDynamicsLine() {
        return oscAddressScoreDynamicsLine;
    }

    public double getDynamicsValue() {
        return dynamicsValue;
    }

    public void setDynamicsValue(double dynamicsValue) {
        this.dynamicsValue = dynamicsValue;
    }

    @Override
    public double getBeaterYPositionDelta() {
        return beaterYPositionDelta;
    }

    @Override
    public StaveId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicStave)) return false;

        BasicStave that = (BasicStave) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BasicStave{" +
                "id=" + id +
                ", oscAddress='" + oscAddress + '\'' +
                ", oscAddressScoreFollower='" + oscAddressScoreFollower + '\'' +
                ", oscAddressScoreBeater='" + oscAddressScoreBeater + '\'' +
                ", oscAddressScoreStartMark='" + oscAddressScoreStartMark + '\'' +
                ", oscAddressScoreDynamicsLine='" + oscAddressScoreDynamicsLine + '\'' +
                ", xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                ", zPosition=" + zPosition +
                ", beaterYPositionMin=" + beaterYPositionMin +
                ", beaterYPositionMax=" + beaterYPositionMax +
                ", beaterYPositionDelta=" + beaterYPositionDelta +
                ", scale=" + scale +
                ", isVisible=" + isVisible +
                ", isActive=" + isActive +
                '}';
    }
}
