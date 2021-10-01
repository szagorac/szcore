package com.xenaksys.szcore.score.overlay;

import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.score.delegate.ScoreProcessorDelegate;

public class DialogsOverlayProcessor extends OverlayProcessor{

    public static final double DLG_DYNAMICS_LINE_Y_MAX = 0.074;      //half  0.037
    public static final double DLG_DYNAMICS_LINE1_Y_MIN_POSITION = -0.04;
    public static final double DLG_DYNAMICS_LINE2_Y_MIN_POSITION = 0.75;

    public static final double  DLG_CONTENT_LINE_Y_MAX = 0.244;      //half 0.125
    public static final double  DLG_CONTENT_LINE1_Y_MIN_POSITION = -0.125;
    public static final double  DLG_CONTENT_LINE2_Y_MIN_POSITION = 0.665;

    private final ValueScaler dlgDynamicsValueScaler = new ValueScaler(0.0, 100.0, 0.0, DLG_DYNAMICS_LINE_Y_MAX);
    private final ValueScaler dlgContentValueScaler = new ValueScaler(0.0, 100.0, 0.0, DLG_CONTENT_LINE_Y_MAX);

    public DialogsOverlayProcessor(ScoreProcessorDelegate scoreProcessor, EventFactory eventFactory, MutableClock clock) {
        super(scoreProcessor, eventFactory, clock);
    }

    public ValueScaler getContentValueScaler () {
        return dlgContentValueScaler;
    }
    public ValueScaler getDynamicsValueScaler () {
        return dlgDynamicsValueScaler;
    }
    public double getDynamicsLineYMax () {
        return DLG_DYNAMICS_LINE_Y_MAX;
    }
    public double getDynamicsLine1YMinPosition () {
        return DLG_DYNAMICS_LINE1_Y_MIN_POSITION;
    }
    public double getDynamicsLine2YMinPosition () {
        return DLG_DYNAMICS_LINE2_Y_MIN_POSITION;
    }

    public double getContentLineYMax () {
        return DLG_CONTENT_LINE_Y_MAX;
    }
    public double getContentLine1YMinPosition () {
        return DLG_CONTENT_LINE1_Y_MIN_POSITION;
    }
    public double getContentLine2YMinPosition () {
        return DLG_CONTENT_LINE2_Y_MIN_POSITION;
    }
}
