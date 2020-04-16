package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PannerConfig {
    static final Logger LOG = LoggerFactory.getLogger(PannerConfig.class);

    private static final int MAX_PAN_ANGLE = 90;

    private boolean isUsePanner;
    private String panningModel;
    private String distanceModel;
    private int maxPanAngle;

    public boolean isUsePanner() {
        return isUsePanner;
    }

    public void setUsePanner(boolean usePanner) {
        isUsePanner = usePanner;
    }

    public String getPanningModel() {
        return panningModel;
    }

    public void setPanningModel(String panningModel) {
        this.panningModel = panningModel;
    }

    public String getDistanceModel() {
        return distanceModel;
    }

    public void setDistanceModel(String distanceModel) {
        this.distanceModel = distanceModel;
    }

    public int getMaxPanAngle() {
        return maxPanAngle;
    }

    public void setMaxPanAngle(int maxPanAngle) {
        this.maxPanAngle = maxPanAngle;
    }

    public boolean validate() {
        if (maxPanAngle > MAX_PAN_ANGLE) {
            LOG.info("validate: invalid maxPanAngle, setting to {}", MAX_PAN_ANGLE);
            maxPanAngle = MAX_PAN_ANGLE;
        }

        distanceModel = PannerDistanceModel.fromName(getDistanceModel()).getName();
        panningModel = PanningModel.fromName(getPanningModel()).getName();

        return true;
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put("panner.isUsePanner", isUsePanner());
        config.put("panner.panningModel", getPanningModel());
        config.put("panner.distanceModel", getDistanceModel());
        config.put("panner.maxPanAngle", getMaxPanAngle());
        return config;
    }

    @Override
    public String toString() {
        return "PannerConfig{" +
                "isUsePanner=" + isUsePanner +
                ", panningModel='" + panningModel + '\'' +
                ", distanceModel='" + distanceModel + '\'' +
                ", maxPanAngle=" + maxPanAngle +
                '}';
    }
}
