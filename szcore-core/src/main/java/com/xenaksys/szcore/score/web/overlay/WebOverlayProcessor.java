package com.xenaksys.szcore.score.web.overlay;

import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebOverlayProcessor {
    static final Logger LOG = LoggerFactory.getLogger(WebOverlayProcessor.class);

    double DYNAMICS_Y_SIZE = 22;
    double DYNAMICS_TOP_Y_MIN = 259;
    double DYNAMICS_BOT_Y_MIN = 480;

    double PRESSURE_Y_SIZE = 15;
    double PRESSURE_TOP_Y_MIN = 171;
    double PRESSURE_BOT_Y_MIN = 392;

    double SPEED_Y_SIZE = 15;
    double SPEED_TOP_Y_MIN = 154;
    double SPEED_BOT_Y_MIN = 375;

    double POSITION_Y_SIZE = 38.0;
    double POSITION_TOP_Y_MIN = 115.0;
    double POSITION_BOT_Y_MIN = 336.0;

    double PITCH_Y_SIZE = 69;
    double PITCH_TOP_Y_MIN = 188;
    double PITCH_BOT_Y_MIN = 409;

    double TIMBRE_Y_SIZE = 32.5;
    double TIMBRE_TOP_Y_MIN = 115;
    double TIMBRE_BOT_Y_MIN = 336;


    private final ValueScaler dynamicsValueScaler = new ValueScaler(0.0, 100.0, DYNAMICS_Y_SIZE,0.0);
    private final ValueScaler dynamicsForteColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler dynamicsPianoColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private final ValueScaler pressureLineValueScaler = new ValueScaler(0.0, 100.0, 0.0, PRESSURE_Y_SIZE);
    private final ValueScaler pressureColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler speedValueScaler = new ValueScaler(0.0, 100.0, SPEED_Y_SIZE,0.0);
    private final ValueScaler speedFastColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler speedSlowColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private final ValueScaler positionValueScaler = new ValueScaler(0.0, 100.0, 0.0, POSITION_Y_SIZE);
    private final ValueScaler contentValueScaler = new ValueScaler(0.0, 100.0, PITCH_Y_SIZE,0.0 );
    private final ValueScaler timbreLineValueScaler = new ValueScaler(0.0, 100.0, TIMBRE_Y_SIZE, 0.0);

    private volatile double dynamicsValue;
    private volatile double pressureValue;
    private volatile double speedValue;
    private volatile double positionValue;
    private volatile double contentValue;
    private volatile double timbreValue;

    public Double calculateValue(StaveId staveId, OverlayType overlayType, long unscaledValue) {
        if(overlayType == null) {
            return null;
        }
        switch (overlayType) {
            case POSITION:
                return calculatePositionLineY(staveId, unscaledValue);
            case SPEED:
                return calculateSpeedLineY(staveId, unscaledValue);
            case PRESSURE:
                return calculatePressureLineY(staveId, unscaledValue);
            case PITCH:
                return calculatePitchLineY(staveId, unscaledValue);
            case DYNAMICS:
                return calculateDynamicsLineY(staveId, unscaledValue);
            case TIMBRE:
                return calculateTimbreLineY(staveId, unscaledValue);
            default:
                LOG.error("calculateValue: invalid overlayType: {}", overlayType);
        }
        return null;
    }

    public Double calculateDynamicsLineY(StaveId staveId, long unscaledValue) {
        Double out = calculateLineY(getDynamicsValueScaler(), getDynamicsTopYMin(), getDynamicsBotYMin(), getDynamicsYSize(), getDynamicsValue(), staveId, unscaledValue);
        if(out == null) {
            return out;
        }
        setDynamicsValue(out);
        return out;
    }

    public Double calculatePitchLineY(StaveId staveId, long unscaledValue) {
        Double out = calculateLineY(getContentValueScaler(), getPitchTopYMin(), getPitchBotYMin(), getPitchYSize(), getContentValue(), staveId, unscaledValue);
        if(out == null) {
            return out;
        }
        setContentValue(out);
        return out;
    }

    public Double calculatePressureLineY(StaveId staveId, long unscaledValue) {
        Double out = calculateLineY(getPressureLineValueScaler(), getPressureTopYMin(), getPressureBotYMin(), getPressureYSize(), getPressureValue(), staveId, unscaledValue);
        if(out == null) {
            return out;
        }
        setPressureValue(out);
        return out;
    }

    public Double calculateTimbreLineY(StaveId staveId, long unscaledValue) {
        Double out = calculateLineY(getTimbreLineValueScaler(), getTimbreTopYMin(), getTimbreBotYMin(), getTimbreYSize(), getTimbreValue(), staveId, unscaledValue);
        if(out == null) {
            return out;
        }
        setTimbreValue(out);
        return out;
    }

    public Double calculateSpeedLineY(StaveId staveId, long unscaledValue) {
        Double out = calculateLineY(getSpeedValueScaler(), getSpeedTopYMin(), getSpeedBotYMin(), getSpeedYSize(), getSpeedValue(), staveId, unscaledValue);
        if(out == null) {
            return out;
        }
        setSpeedValue(out);
        return out;
    }

    public Double calculatePositionLineY(StaveId staveId, long unscaledValue) {
        Double out = calculateLineY(getPositionValueScaler(), getPositionTopYMin(), getPositionBotYMin(), getPositionYSize(), getPositionValue(), staveId, unscaledValue);
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

    public double getTimbreValue() {
        return timbreValue;
    }

    public void setTimbreValue(double timbreValue) {
        this.timbreValue = timbreValue;
    }

    public ValueScaler getDynamicsValueScaler() {
        return dynamicsValueScaler;
    }
    public ValueScaler getPressureLineValueScaler() {
        return pressureLineValueScaler;
    }
    public ValueScaler getSpeedValueScaler() {
        return speedValueScaler;
    }
    public ValueScaler getPositionValueScaler() {
        return positionValueScaler;
    }
    public ValueScaler getContentValueScaler() {
        return contentValueScaler;
    }
    public ValueScaler getTimbreLineValueScaler() {
        return timbreLineValueScaler;
    }
    public double getDynamicsYSize() {
        return DYNAMICS_Y_SIZE;
    }
    public double getDynamicsTopYMin() {
        return DYNAMICS_TOP_Y_MIN;
    }
    public double getDynamicsBotYMin() {
        return DYNAMICS_BOT_Y_MIN;
    }
    public double getPressureYSize() {
        return PRESSURE_Y_SIZE;
    }
    public double getPressureTopYMin() {
        return PRESSURE_TOP_Y_MIN;
    }
    public double getPressureBotYMin() {
        return PRESSURE_BOT_Y_MIN;
    }
    public double getSpeedYSize() {
        return SPEED_Y_SIZE;
    }
    public double getSpeedTopYMin() {
        return SPEED_TOP_Y_MIN;
    }
    public double getSpeedBotYMin() {
        return SPEED_BOT_Y_MIN;
    }
    public double getPositionYSize() {
        return POSITION_Y_SIZE;
    }
    public double getPositionTopYMin() {
        return POSITION_TOP_Y_MIN;
    }
    public double getPositionBotYMin() {
        return POSITION_BOT_Y_MIN;
    }
    public double getPitchYSize() {
        return PITCH_Y_SIZE;
    }
    public double getPitchTopYMin() {
        return PITCH_TOP_Y_MIN;
    }
    public double getPitchBotYMin() {
        return PITCH_BOT_Y_MIN;
    }
    public double getTimbreYSize() {
        return TIMBRE_Y_SIZE;
    }
    public double getTimbreTopYMin() {
        return TIMBRE_TOP_Y_MIN;
    }
    public double getTimbreBotYMin() {
        return TIMBRE_BOT_Y_MIN;
    }
}
