package com.xenaksys.szcore.model;

public interface Stave extends Identifiable {

    String getOscAddress();

    String getOscAddressScoreFollower();

    String getOscAddressScoreBeater();

    String getOscAddressScoreStartMark();

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
