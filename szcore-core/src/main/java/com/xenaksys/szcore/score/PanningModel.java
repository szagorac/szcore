package com.xenaksys.szcore.score;

public enum PanningModel {
    EQUAL_POWER("equalpower"), HRTF("HRTF");

    private final String name;

    PanningModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PanningModel fromName(String name) {
        for (PanningModel panningModel : values()) {
            if (panningModel.getName().equals(name)) {
                return panningModel;
            }
        }
        return EQUAL_POWER;
    }
}
