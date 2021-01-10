package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.config.WebPannerConfig;

import java.util.HashMap;
import java.util.Map;

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
        config.put("panner.isUsePanner", isUsePanner());
        config.put("panner.panningModel", getPanningModel());
        config.put("panner.distanceModel", getDistanceModel());
        config.put("panner.maxPanAngle", getMaxPanAngle());
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
