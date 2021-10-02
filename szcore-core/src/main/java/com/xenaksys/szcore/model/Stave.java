package com.xenaksys.szcore.model;

import com.xenaksys.szcore.model.id.StaveId;

public interface Stave extends Identifiable {

    String getOscAddress();

    String getOscAddressScoreFollower();

    String getOscAddressScoreBeater();

    String getOscAddressScoreStartMark();

    String getOscAddressScoreDynamicsLine();

    String getOscAddressScoreDynamicsMidLine();

    String getOscAddressScoreDynamicsBox();

    String getOscAddressScorePressureBox();

    String getOscAddressScorePressureLine();

    String getOscAddressScorePressureMidLine();

    String getOscAddressScoreSpeedBox();

    String getOscAddressScoreSpeedLine();

    String getOscAddressScoreSpeedMidLine();

    String getOscAddressScorePositionBox();

    String getOscAddressScorePositionLine();

    String getOscAddressScorePositionOrdLine();

    String getOscAddressScorePositionBridgeLine();

    String getOscAddressScoreContentBox();

    String getOscAddressScoreContentLine();

    String getOscAddressScoreTimbreBox();

    String getOscAddressScoreTimbreLine();

    String getOscAddressScoreTimbreOrdLine();

    String getOscAddressScoreContentStaveBox();

    String getOscAddressScoreContentStaveOrdLine();

    double getDynamicsValue();

    void setDynamicsValue(double value);

    double getPressureValue();

    void setPressureValue(double value);

    double getSpeedValue();

    void setSpeedValue(double value);

    double getPositionValue();

    void setPositionValue(double value);

    double getContentValue();

    void setContentValue(double value);

    double getTimbreValue();

    void setTimbreValue(double value);

    double getxPosition();

    double getyPosition();

    double getzPosition();

    double getScale();

    boolean isVisible();

    boolean isActive();

    double getBeaterYPositionMin();

    double getBeaterYPositionMax();

    double getBeaterYPositionDelta();

    StaveId getStaveId();
}
