package com.xenaksys.szcore.score.web.config;

import com.xenaksys.szcore.score.PannerDistanceModel;
import com.xenaksys.szcore.score.PanningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.WEB_CONFIG_DISTANCE_MODEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PAN_ANGLE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNING_MODEL;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN_PANNER;

public class WebPannerConfig {
    static final Logger LOG = LoggerFactory.getLogger(WebPannerConfig.class);

    private static final int MAX_PAN_ANGLE = 90;

    private static final boolean DEFAULT_IS_USE_PANNER = false;
    private static final String DEFAULT_PANNING_MODEL = PanningModel.EQUAL_POWER.getName();
    private static final String DEFAULT_DISTANCE_MODEL = PannerDistanceModel.LINEAR.getName();
    private static final int DEFAULT_MAX_PAN_ANGLE = 45;

    private boolean isUsePanner = DEFAULT_IS_USE_PANNER;
    private String panningModel = DEFAULT_PANNING_MODEL;
    private String distanceModel = DEFAULT_DISTANCE_MODEL;
    private int maxPanAngle = DEFAULT_MAX_PAN_ANGLE;

    private final PropertyChangeSupport pcs;

    public WebPannerConfig(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    public boolean isUsePanner() {
        return isUsePanner;
    }

    public void setUsePanner(boolean usePanner) {
        isUsePanner = usePanner;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_PANNER, WEB_CONFIG_IS_USE_PANNER, usePanner);
    }

    public String getPanningModel() {
        return panningModel;
    }

    public void setPanningModel(String panningModel) {
        this.panningModel = panningModel;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_PANNER, WEB_CONFIG_PANNING_MODEL, panningModel);
    }

    public String getDistanceModel() {
        return distanceModel;
    }

    public void setDistanceModel(String distanceModel) {
        this.distanceModel = distanceModel;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_PANNER, WEB_CONFIG_DISTANCE_MODEL, distanceModel);
    }

    public int getMaxPanAngle() {
        return maxPanAngle;
    }

    public void setMaxPanAngle(int maxPanAngle) {
        this.maxPanAngle = maxPanAngle;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRAIN_PANNER, WEB_CONFIG_MAX_PAN_ANGLE, maxPanAngle);
    }

    public boolean validate() {
        if (maxPanAngle > MAX_PAN_ANGLE) {
            LOG.info("validate: invalid maxPanAngle, setting to {}", MAX_PAN_ANGLE);
            setMaxPanAngle(MAX_PAN_ANGLE);
        }

        setDistanceModel(PannerDistanceModel.fromName(getDistanceModel()).getName());
        setPanningModel(PanningModel.fromName(getPanningModel()).getName());

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

    public WebPannerConfig copy(WebPannerConfig to) {
        if (to == null) {
            to = new WebPannerConfig(pcs);
        }
        to.setUsePanner(this.isUsePanner);
        to.setPanningModel(this.panningModel);
        to.setDistanceModel(this.distanceModel);
        to.setMaxPanAngle(this.maxPanAngle);
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebPannerConfig that = (WebPannerConfig) o;
        return isUsePanner == that.isUsePanner && maxPanAngle == that.maxPanAngle && Objects.equals(panningModel, that.panningModel) && Objects.equals(distanceModel, that.distanceModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isUsePanner, panningModel, distanceModel, maxPanAngle);
    }

    @Override
    public String toString() {
        return "WebPannerConfig{" +
                "isUsePanner=" + isUsePanner +
                ", panningModel='" + panningModel + '\'' +
                ", distanceModel='" + distanceModel + '\'' +
                ", maxPanAngle=" + maxPanAngle +
                '}';
    }
}
