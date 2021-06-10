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
    private final String oscAddressScoreDynamicsMidLine;
    private final String oscAddressScoreDynamicsBox;
    private final String oscAddressScorePressureBox;
    private final String oscAddressScorePressureLine;
    private final String oscAddressScorePressureMidLine;
    private final String oscAddressScoreSpeedBox;
    private final String oscAddressScoreSpeedLine;
    private final String oscAddressScoreSpeedMidLine;
    private final String oscAddressScorePositionBox;
    private final String oscAddressScorePositionLine;
    private final String oscAddressScorePositionOrdLine;
    private final String oscAddressScorePositionBridgeLine;
    private final String oscAddressScoreContentBox;
    private final String oscAddressScoreContentLine;
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
    private volatile double pressureValue;
    private volatile double speedValue;
    private volatile double positionValue;
    private volatile double contentValue;

    public BasicStave(StaveId id,
                      String oscAddress,
                      String oscAddressScoreFollower,
                      String oscAddressScoreBeater,
                      String oscAddressScoreStartMark,
                      String oscAddressScoreDynamicsLine,
                      String oscAddressScoreDynamicsMidLine,
                      String oscAddressScoreDynamicsBox,
                      String oscAddressScorePressureBox,
                      String oscAddressScorePressureLine,
                      String oscAddressScorePressureMidLine,
                      String oscAddressScoreSpeedBox,
                      String oscAddressScoreSpeedLine,
                      String oscAddressScoreSpeedMidLine,
                      String oscAddressScorePositionBox,
                      String oscAddressScorePositionLine,
                      String oscAddressScorePositionOrdLine,
                      String oscAddressScorePositionBridgeLine,
                      String oscAddressScoreContentBox,
                      String oscAddressScoreContentLine,
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
        this.oscAddressScoreDynamicsMidLine = oscAddressScoreDynamicsMidLine;
        this.oscAddressScoreDynamicsBox = oscAddressScoreDynamicsBox;
        this.oscAddressScorePressureBox = oscAddressScorePressureBox;
        this.oscAddressScorePressureLine = oscAddressScorePressureLine;
        this.oscAddressScorePressureMidLine = oscAddressScorePressureMidLine;
        this.oscAddressScoreSpeedBox = oscAddressScoreSpeedBox;
        this.oscAddressScoreSpeedLine = oscAddressScoreSpeedLine;
        this.oscAddressScoreSpeedMidLine = oscAddressScoreSpeedMidLine;
        this.oscAddressScorePositionBox = oscAddressScorePositionBox;
        this.oscAddressScorePositionLine = oscAddressScorePositionLine;
        this.oscAddressScorePositionOrdLine = oscAddressScorePositionOrdLine;
        this.oscAddressScorePositionBridgeLine = oscAddressScorePositionBridgeLine;
        this.oscAddressScoreContentBox = oscAddressScoreContentBox;
        this.oscAddressScoreContentLine = oscAddressScoreContentLine;
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

    @Override
    public String getOscAddressScorePressureLine() {
        return oscAddressScorePressureLine;
    }

    @Override
    public String getOscAddressScorePressureMidLine() {
        return oscAddressScorePressureMidLine;
    }

    @Override
    public String getOscAddressScoreSpeedBox() {
        return oscAddressScoreSpeedBox;
    }

    @Override
    public String getOscAddressScoreSpeedLine() {
        return oscAddressScoreSpeedLine;
    }

    @Override
    public String getOscAddressScoreSpeedMidLine() {
        return oscAddressScoreSpeedMidLine;
    }

    @Override
    public String getOscAddressScorePositionBox() {
        return oscAddressScorePositionBox;
    }

    @Override
    public String getOscAddressScorePositionLine() {
        return oscAddressScorePositionLine;
    }

    @Override
    public String getOscAddressScorePositionOrdLine() {
        return oscAddressScorePositionOrdLine;
    }

    @Override
    public String getOscAddressScorePositionBridgeLine() {
        return oscAddressScorePositionBridgeLine;
    }

    @Override
    public String getOscAddressScoreContentBox() {
        return oscAddressScoreContentBox;
    }

    @Override
    public String getOscAddressScoreContentLine() {
        return oscAddressScoreContentLine;
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

    @Override
    public String getOscAddressScoreDynamicsMidLine() {
        return oscAddressScoreDynamicsMidLine;
    }

    public double getDynamicsValue() {
        return dynamicsValue;
    }

    public void setDynamicsValue(double dynamicsValue) {
        this.dynamicsValue = dynamicsValue;
    }

    @Override
    public double getPressureValue() {
        return pressureValue;
    }

    @Override
    public void setPressureValue(double value) {
        this.pressureValue = value;
    }

    @Override
    public double getSpeedValue() {
        return speedValue;
    }

    @Override
    public void setSpeedValue(double value) {
        this.speedValue = value;
    }

    @Override
    public double getPositionValue() {
        return positionValue;
    }

    @Override
    public void setPositionValue(double value) {
        this.positionValue = value;
    }

    @Override
    public double getContentValue() {
        return contentValue;
    }

    @Override
    public void setContentValue(double value) {
        this.contentValue = value;
    }

    @Override
    public double getBeaterYPositionDelta() {
        return beaterYPositionDelta;
    }

    @Override
    public StaveId getStaveId() {
        return id;
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
