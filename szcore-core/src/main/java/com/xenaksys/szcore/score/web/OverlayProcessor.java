package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xenaksys.szcore.Consts.WEB_DYNAMICS_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.WEB_OVRL_POS_BOT_Y_MIN;
import static com.xenaksys.szcore.Consts.WEB_OVRL_POS_TOP_Y_MIN;
import static com.xenaksys.szcore.Consts.WEB_OVRL_POS_Y_LENGTH;
import static com.xenaksys.szcore.Consts.WEB_PITCH_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.WEB_PRESSURE_LINE_Y_MAX;
import static com.xenaksys.szcore.Consts.WEB_SPEED_LINE_Y_MAX;

public class OverlayProcessor {
    static final Logger LOG = LoggerFactory.getLogger(OverlayProcessor.class);

    private final ValueScaler dynamicsValueScaler = new ValueScaler(0.0, 100.0, 0.0, WEB_DYNAMICS_LINE_Y_MAX);
    private final ValueScaler dynamicsForteColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler dynamicsPianoColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private final ValueScaler pressureLineValueScaler = new ValueScaler(0.0, 100.0, WEB_PRESSURE_LINE_Y_MAX, 0.0);
    private final ValueScaler pressureColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler speedValueScaler = new ValueScaler(0.0, 100.0, 0.0, WEB_SPEED_LINE_Y_MAX);
    private final ValueScaler speedFastColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler speedSlowColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private final ValueScaler positionValueScaler = new ValueScaler(0.0, 100.0, 0.0, WEB_OVRL_POS_Y_LENGTH);
    private final ValueScaler contentValueScaler = new ValueScaler(0.0, 100.0, 0.0, WEB_PITCH_LINE_Y_MAX);

    private volatile double dynamicsValue;
    private volatile double pressureValue;
    private volatile double speedValue;
    private volatile double positionValue;
    private volatile double contentValue;

    public Double calculateValue(StaveId staveId, OverlayType overlayType, long unscaledValue) {
        if(overlayType == null) {
            return null;
        }
        switch (overlayType) {
            case POSITION:
                return calculatePositionLineY(staveId, unscaledValue);
            default:
                LOG.error("calculateValue: invalid overlayType: {}", overlayType);
        }
        return null;
    }

    public Double calculatePositionLineY(StaveId staveId, long unscaledValue) {
        double yDelta = positionValueScaler.scaleValue(unscaledValue);
        Double out = calculateLineY(positionValueScaler, WEB_OVRL_POS_TOP_Y_MIN, WEB_OVRL_POS_BOT_Y_MIN, WEB_OVRL_POS_Y_LENGTH, getPositionValue(), staveId, unscaledValue);
        LOG.debug("calculatePositionLineY: sending y position: {} yDelta: {} ", out, yDelta);
        if(out == null) {
            return out;
        }
        setPositionValue(out);
        return out;
    }

    public Double calculateLineY(ValueScaler scaler, double minYStave1, double minYStave2, double maxY, double current, StaveId staveId, long unscaledValue) {
        int staveNo = staveId.getStaveNo();
        double yDelta = scaler.scaleValue(unscaledValue);
        double out;
        switch (staveNo) {
            case 1:
                out = minYStave1 + yDelta;
                break;
            case 2:
                out = minYStave2 + yDelta;
                break;
            default:
                LOG.error("calculateLineY: Invalid stave No: {}", staveNo);
                return null;
        }
        out = MathUtil.roundTo2DecimalPlaces(out);
        double diff = Math.abs(out - current);
        double threshold = Math.abs(2 * maxY / 100.0);
        if (diff < threshold) {
            return null;
        }
        return out;
    }

    public double getDynamicsValue() {
        return dynamicsValue;
    }

    public void setDynamicsValue(double dynamicsValue) {
        this.dynamicsValue = dynamicsValue;
    }

    public double getPressureValue() {
        return pressureValue;
    }

    public void setPressureValue(double pressureValue) {
        this.pressureValue = pressureValue;
    }

    public double getSpeedValue() {
        return speedValue;
    }

    public void setSpeedValue(double speedValue) {
        this.speedValue = speedValue;
    }

    public double getPositionValue() {
        return positionValue;
    }

    public void setPositionValue(double positionValue) {
        this.positionValue = positionValue;
    }

    public double getContentValue() {
        return contentValue;
    }

    public void setContentValue(double contentValue) {
        this.contentValue = contentValue;
    }


}
