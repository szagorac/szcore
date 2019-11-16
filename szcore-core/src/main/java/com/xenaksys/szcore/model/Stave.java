package com.xenaksys.szcore.model;

public interface Stave extends Identifiable {

    String getOscAddress();

    String getOscAddressScoreFollower();

    String getOscAddressScoreBeater();

    String getOscAddressScoreStartMark();

    String getOscAddressScoreDynamicsLine();

    String getOscAddressScoreDynamicsBox();

    String getOscAddressScorePressureBox();

    double getDynamicsValue();

    void setDynamicsValue(double dynamicsValue);

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
