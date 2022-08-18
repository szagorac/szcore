package com.xenaksys.szcore.score.web.overlay;

import com.xenaksys.szcore.algo.ValueScaler;

public class SymphoneaWebOverlayProcessor extends WebOverlayProcessor {
    double DLG_DYNAMICS_Y_SIZE = 32.5;
    double DLG_DYNAMICS_TOP_Y_MIN = 247.5;
    double DLG_DYNAMICS_BOT_Y_MIN = 469;

    double DLG_PITCH_Y_SIZE = 96.5;
    double DLG_PITCH_TOP_Y_MIN = 149.5;
    double DLG_PITCH_BOT_Y_MIN = 370.5;


    private final ValueScaler dlgDynamicsValueScaler = new ValueScaler(0.0, 100.0, DLG_DYNAMICS_Y_SIZE, 0.0);
    private final ValueScaler dlgContentValueScaler = new ValueScaler(0.0, 100.0, DLG_PITCH_Y_SIZE, 0.0);

    public ValueScaler getDynamicsValueScaler() {
        return dlgDynamicsValueScaler;
    }

    public ValueScaler getContentValueScaler() {
        return dlgContentValueScaler;
    }

    public double getDynamicsYSize() {
        return DLG_DYNAMICS_Y_SIZE;
    }

    public double getDynamicsTopYMin() {
        return DLG_DYNAMICS_TOP_Y_MIN;
    }

    public double getDynamicsBotYMin() {
        return DLG_DYNAMICS_BOT_Y_MIN;
    }

    public double getPitchYSize() {
        return DLG_PITCH_Y_SIZE;
    }

    public double getPitchTopYMin() {
        return DLG_PITCH_TOP_Y_MIN;
    }

    public double getPitchBotYMin() {
        return DLG_PITCH_BOT_Y_MIN;
    }
}
