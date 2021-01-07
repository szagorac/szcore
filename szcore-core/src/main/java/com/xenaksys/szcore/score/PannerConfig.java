package com.xenaksys.szcore.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PannerConfig {
    static final Logger LOG = LoggerFactory.getLogger(PannerConfig.class);

    private static final int MAX_PAN_ANGLE = 90;

    private static final boolean DEFAULT_IS_USE_PANNER = false;
    private static final String DEFAULT_PANNING_MODEL = PanningModel.EQUAL_POWER.getName();
    private static final String DEFAULT_DISTANCE_MODEL = PannerDistanceModel.LINEAR.getName();
    private static final int DEFAULT_MAX_PAN_ANGLE = 45;

    private boolean isUsePanner = DEFAULT_IS_USE_PANNER;
    private String panningModel = DEFAULT_PANNING_MODEL;
    private String distanceModel = DEFAULT_DISTANCE_MODEL;
    private int maxPanAngle = DEFAULT_MAX_PAN_ANGLE;

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

    public PannerConfig copy(PannerConfig to) {
        if (to == null) {
            to = new PannerConfig();
        }
        to.setUsePanner(this.isUsePanner);
        to.setPanningModel(this.panningModel);
        to.setDistanceModel(this.distanceModel);
        to.setMaxPanAngle(this.maxPanAngle);
        return to;
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
