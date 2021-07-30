package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.config.WebPannerConfig;

import java.util.HashMap;
import java.util.Map;

import static com.xenaksys.szcore.Consts.DOT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DISTANCE_MODEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PAN_ANGLE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNING_MODEL;

public class WebPannerConfigExport {

    private boolean isUsePanner;
    private String panningModel;
    private String distanceModel;
    private int maxPanAngle;

    public boolean isUsePanner() {
        return isUsePanner;
    }

    public String getPanningModel() {
        return panningModel;
    }

    public String getDistanceModel() {
        return distanceModel;
    }

    public int getMaxPanAngle() {
        return maxPanAngle;
    }

    public void populate(WebPannerConfig from) {
        if (from == null) {
            return;
        }
        this.isUsePanner = from.isUsePanner();
        this.panningModel = from.getPanningModel();
        this.distanceModel = from.getDistanceModel();
        this.maxPanAngle = from.getMaxPanAngle();
    }

    public Map<String, Object> toJsMap() {
        Map<String, Object> config = new HashMap<>();
        config.put(WEB_CONFIG_PANNER + DOT + WEB_CONFIG_IS_USE_PANNER, isUsePanner());
        config.put(WEB_CONFIG_PANNER + DOT + WEB_CONFIG_PANNING_MODEL, getPanningModel());
        config.put(WEB_CONFIG_PANNER + DOT + WEB_CONFIG_DISTANCE_MODEL, getDistanceModel());
        config.put(WEB_CONFIG_PANNER + DOT + WEB_CONFIG_MAX_PAN_ANGLE, getMaxPanAngle());
        return config;
    }

    @Override
    public String toString() {
        return "WebPannerConfigExport{" +
                "isUsePanner=" + isUsePanner +
                ", panningModel='" + panningModel + '\'' +
                ", distanceModel='" + distanceModel + '\'' +
                ", maxPanAngle=" + maxPanAngle +
                '}';
    }
}
