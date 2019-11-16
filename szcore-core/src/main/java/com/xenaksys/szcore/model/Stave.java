package com.xenaksys.szcore.model;

public interface Stave extends Identifiable {

    String getOscAddress();

    String getOscAddressScoreFollower();

    String getOscAddressScoreBeater();

    String getOscAddressScoreStartMark();

    String getOscAddressScoreDynamicsLine();

    String getOscAddressScoreDynamicsBox();

    String getOscAddressScorePressureBox();

    String getOscAddressScorePressureLine();

    String getOscAddressScoreSpeedBox();

    String getOscAddressScoreSpeedLine();

    String getOscAddressScorePositionBox();

    String getOscAddressScorePositionLine();

    double getDynamicsValue();

    void setDynamicsValue(double value);

    double getPressureValue();

    void setPressureValue(double value);

    double getSpeedValue();

    void setSpeedValue(double value);

    double getPositionValue();

    void setPositionValue(double value);

    double getxPosition();

    double getyPosition();

    double getzPosition();

    double getScale();

    boolean isVisible();

    boolean isActive();

    double getBeaterYPositionMin();

    double getBeaterYPositionMax();

    double getBeaterYPositionDelta();
}
