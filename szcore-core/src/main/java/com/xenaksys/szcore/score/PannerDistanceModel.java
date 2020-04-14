package com.xenaksys.szcore.score;

public enum PannerDistanceModel {
    LINEAR("linear"), INVERSE("inverse"), EXPONENTIAL("exponential");

    private final String name;

    PannerDistanceModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PannerDistanceModel fromName(String name) {
        for (PannerDistanceModel panningModel : values()) {
            if (panningModel.getName().equals(name)) {
                return panningModel;
            }
        }
        return LINEAR;
    }
}
