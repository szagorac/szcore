package com.xenaksys.szcore.score.overlay;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.osc.ElementAlphaEvent;
import com.xenaksys.szcore.event.osc.ElementColorEvent;
import com.xenaksys.szcore.event.osc.ElementYPositionEvent;
import com.xenaksys.szcore.event.osc.OverlayTextEvent;
import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.MutableClock;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.OverlayElementType;
import com.xenaksys.szcore.score.OverlayType;
import com.xenaksys.szcore.score.delegate.ScoreProcessorDelegate;
import com.xenaksys.szcore.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OverlayProcessor {
    static final Logger LOG = LoggerFactory.getLogger(OverlayProcessor.class);

    public static final double DYNAMICS_LINE_Y_MAX = 0.074;      //half  0.037
    public static final double DYNAMICS_LINE_Y_MIN = 0.0;
    public static final double DYNAMICS_LINE1_Y_MID_POSITION = -0.077;
    public static final double DYNAMICS_LINE1_Y_MIN_POSITION = -0.04;
    public static final double DYNAMICS_LINE1_Y_MAX_POSITION = -0.114;
    public static final double DYNAMICS_LINE2_Y_MID_POSITION = 0.713;
    public static final double DYNAMICS_LINE2_Y_MAX_POSITION = 0.676;
    public static final double DYNAMICS_LINE2_Y_MIN_POSITION = 0.75;

    public static final double PRESSURE_LINE_Y_MAX = 0.055;  //half 0.0275
    public static final double PRESSURE_LINE_Y_MIN = 0.0;
    public static final double PRESSURE_LINE1_Y_MID_POSITION = -0.4;
    public static final double PRESSURE_LINE1_Y_MIN_POSITION = -0.3725;
    public static final double  PRESSURE_LINE1_Y_MAX_POSITION = -0.4275;
    public static final double  PRESSURE_LINE2_Y_MID_POSITION = 0.391;
    public static final double  PRESSURE_LINE2_Y_MAX_POSITION = 0.3635;
    public static final double  PRESSURE_LINE2_Y_MIN_POSITION = 0.4185;

    public static final double  SPEED_LINE_Y_MAX = 0.045;      //half  0.0275
    public static final double  SPEED_LINE_Y_MIN = 0.0;
    public static final double  SPEED_LINE1_Y_MID_POSITION = -0.457;
    public static final double  SPEED_LINE1_Y_MIN_POSITION = -0.435;
    public static final double  SPEED_LINE1_Y_MAX_POSITION = -0.48;
    public static final double  SPEED_LINE2_Y_MID_POSITION = 0.334;
    public static final double  SPEED_LINE2_Y_MAX_POSITION = 0.308;
    public static final double  SPEED_LINE2_Y_MIN_POSITION = 0.355;

    public static final double  POSITION_LINE_Y_MAX = 0.128;      //half 0.0675
    public static final double  POSITION_LINE_Y_MIN = 0.0;
    public static final double  POSITION_LINE1_Y_MID_POSITION = -0.555;
    public static final double  POSITION_LINE1_Y_MIN_POSITION = -0.492;
    public static final double  POSITION_LINE1_Y_MAX_POSITION = -0.62;
    public static final double  POSITION_LINE2_Y_MID_POSITION = 0.236;
    public static final double  POSITION_LINE2_Y_MAX_POSITION = 0.17;
    public static final double  POSITION_LINE2_Y_MIN_POSITION = 0.298;

    public static final double  CONTENT_LINE_Y_MAX = 0.244;      //half 0.125
    public static final double  CONTENT_LINE_Y_MIN = 0.0;
    public static final double  CONTENT_LINE1_Y_MID_POSITION = -0.245;
    public static final double  CONTENT_LINE1_Y_MIN_POSITION = -0.125;
    public static final double  CONTENT_LINE1_Y_MAX_POSITION = -0.365;
    public static final double  CONTENT_LINE2_Y_MID_POSITION = 0.545;
    public static final double  CONTENT_LINE2_Y_MAX_POSITION = 0.425;
    public static final double  CONTENT_LINE2_Y_MIN_POSITION = 0.665;

    public static final double  TIMBRE_LINE_Y_MAX = 0.128;      //half 0.0675
    public static final double  TIMBRE_LINE1_Y_MIN_POSITION = -0.492;
    public static final double  TIMBRE_LINE2_Y_MIN_POSITION = 0.298;

    private final ValueScaler dynamicsValueScaler = new ValueScaler(0.0, 100.0, 0.0, DYNAMICS_LINE_Y_MAX);
    private final ValueScaler dynamicsForteColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler dynamicsPianoColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private final ValueScaler pressureLineValueScaler = new ValueScaler(0.0, 100.0, PRESSURE_LINE_Y_MAX, 0.0);
    private final ValueScaler pressureColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler speedValueScaler = new ValueScaler(0.0, 100.0, 0.0, SPEED_LINE_Y_MAX);
    private final ValueScaler speedFastColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler speedSlowColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);
    private final ValueScaler positionValueScaler = new ValueScaler(0.0, 100.0, POSITION_LINE_Y_MAX, 0.0);
    private final ValueScaler contentValueScaler = new ValueScaler(0.0, 100.0, 0.0, CONTENT_LINE_Y_MAX);
    private final ValueScaler timbreValueScaler = new ValueScaler(0.0, 100.0,  0.0, TIMBRE_LINE_Y_MAX);
    private final ValueScaler timbreUpColorValueScaler = new ValueScaler(50.0, 100.0, 255, 0);
    private final ValueScaler timbreDownColorValueScaler = new ValueScaler(0.0, 50.0, 0, 255);

    private final ScoreProcessorDelegate scoreProcessor;
    private final EventFactory eventFactory;
    private final MutableClock clock;

    public OverlayProcessor(ScoreProcessorDelegate scoreProcessor, EventFactory eventFactory, MutableClock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
    }

    public void setOverlayText(OverlayType type, String txt, boolean isVisible, List<Id> instrumentIds) {
        if (type == null) {
            LOG.error("setOverlayValue: invalid type");
            return;
        }
        switch (type) {
            case PITCH:
                onPitchTextChange(txt, isVisible, instrumentIds);
                break;
            default:
                LOG.error("setOverlayValue: invalid overlay type {}", type);
        }
    }

    private void onPitchTextChange(String txt, boolean isVisible, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendTextEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    public void sendTextEvent(Id instrumentId, Stave stave, String txt, boolean isVisible) {
        LOG.debug("sendContentLineYEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);
        StaveId staveId = stave.getStaveId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());
        OverlayTextEvent contentEvent = eventFactory.createOverlayTextEvent(staveId, txt, isVisible,
                OverlayType.PITCH, destination, clock.getSystemTimeMillis());
        contentEvent.setVisible(isVisible);
        scoreProcessor.process(contentEvent);
    }

    public void setOverlayValue(OverlayType type, long value, List<Id> instrumentIds) {
        if (type == null) {
            LOG.error("setOverlayValue: invalid type");
            return;
        }
        switch (type) {
            case DYNAMICS:
                onDynamicsValueChange(value, instrumentIds);
                break;
            case SPEED:
                onSpeedValueChange(value, instrumentIds);
                break;
            case POSITION:
                onPositionValueChange(value, instrumentIds);
                break;
            case PRESSURE:
                onPressureValueChange(value, instrumentIds);
                break;
            case PITCH:
                onContentValueChange(value, instrumentIds);
                break;
            case TIMBRE:
                onTimbreValueChange(value, instrumentIds);
                break;
            default:
                LOG.error("setOverlayValue: invalid overlay type {}", type);
        }
    }

    public void onUseOverlayLine(OverlayType type, Boolean value, List<Id> instrumentIds) {
        if(type == null) {
            LOG.error("onUseOverlayLine: invalid type");
            return;
        }
        switch (type) {
            case DYNAMICS:
                setDynamicsLine(value, instrumentIds);
                break;
            case SPEED:
                setSpeedLine(value, instrumentIds);
                break;
            case POSITION:
                setPositionLine(value, instrumentIds);
                break;
            case PRESSURE:
                setPressureLine(value, instrumentIds);
                break;
            case PITCH:
                setContentLine(value, instrumentIds);
                break;
            case TIMBRE:
                setTimbreLine(value, instrumentIds);
                break;
            default:
                LOG.error("onUseOverlayLine: invalid overlay type {}", type);
        }
    }

    public void onUseOverlay(OverlayType type, Boolean value, List<Id> instrumentIds) {
        if(type == null) {
            LOG.error("onUseOverlay: invalid type");
            return;
        }
        switch (type) {
            case DYNAMICS:
                setDynamicsOverlay(value, instrumentIds);
                break;
            case SPEED:
                setSpeedOverlay(value, instrumentIds);
                break;
            case POSITION:
                setPositionOverlay(value, instrumentIds);
                break;
            case PRESSURE:
                setPressureOverlay(value, instrumentIds);
                break;
            case PITCH:
                setContentOverlay(value, instrumentIds);
                break;
            case PITCH_STAVE:
                setPitchStaveOverlay(value, instrumentIds);
                break;
            case TIMBRE:
                setTimbreOverlay(value, instrumentIds);
                break;
            default:
                LOG.error("onUseOverlay: invalid overlay type {}", type);
        }
    }


    public void sendDynamicsYPositionEvent(Id instrumentId, Stave stave, double yDelta, long unscaledValue) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());

        //dynamics y position
        String address = stave.getOscAddressScoreDynamicsLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = getDynamicsLine1YMinPosition() - yDelta;
                break;
            case 2:
                y = getDynamicsLine2YMinPosition() - yDelta;
                break;
            default:
                LOG.error("Invalid stave dynamics Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getDynamicsValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * getDynamicsLineYMax() / 100.0);
        if (diff < threshold) {
            LOG.debug("addDynamicsYPositionEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.debug("addDynamicsYPositionEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent dynYEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, unscaledValue, OverlayType.DYNAMICS, clock.getSystemTimeMillis());
        dynYEvent.setYPosition(y);
        stave.setDynamicsValue(y);

        scoreProcessor.process(dynYEvent);
    }

    public void sendSpeedYPositionEvent(Id instrumentId, Stave stave, double yDelta, long unscaledValue) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());

        //speed y position
        String address = stave.getOscAddressScoreSpeedLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = getSpeedLine1YMinPosition() - yDelta;
                break;
            case 2:
                y = getSpeedLine2YMinPosition() - yDelta;
                break;
            default:
                LOG.error("Invalid stave speed Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getSpeedValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * getSpeedLineYMax() / 100.0);
        if (diff < threshold) {
            LOG.debug("sendSpeedYPositionEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.debug("sendSpeedYPositionEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent speedEvent = eventFactory.createElementYPositionEvent(address, destination, staveId,
                unscaledValue, OverlayType.SPEED, clock.getSystemTimeMillis());
        speedEvent.setYPosition(y);
        stave.setSpeedValue(y);

        scoreProcessor.process(speedEvent);
    }

    public void sendPositionLineYEvent(Id instrumentId, Stave stave, double yDelta, long unscaledValue) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());

        //position line y position
        String address = stave.getOscAddressScorePositionLine();

        int staveNo = staveId.getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = getPositionLine1YMinPosition() - yDelta;
                break;
            case 2:
                y = getPositionLine2YMinPosition() - yDelta;
                break;
            default:
                LOG.error("Invalid stave position Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getPositionValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * getPositionLineYMax() / 100.0);
        if (diff < threshold) {
            LOG.debug("sendPositionLineYEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.debug("sendPositionLineYEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent positionEvent = eventFactory.createElementYPositionEvent(address, destination, staveId,
                unscaledValue, OverlayType.POSITION, clock.getSystemTimeMillis());
        positionEvent.setYPosition(y);
        stave.setPositionValue(y);

        scoreProcessor.process(positionEvent);
    }

    public void sendContentLineYEvent(Id instrumentId, Stave stave, double yDelta, long unscaledValue) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());

        String address = stave.getOscAddressScoreContentLine();

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = getContentLine1YMinPosition() - yDelta;
                break;
            case 2:
                y = getContentLine2YMinPosition() - yDelta;
                break;
            default:
                LOG.error("Invalid stave position Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getContentValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * getContentLineYMax() / 100.0);
        if (diff < threshold) {
            LOG.debug("sendContentLineYEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.debug("sendContentLineYEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent contentEvent = eventFactory.createElementYPositionEvent(address, destination, staveId,
                unscaledValue, OverlayType.PITCH, clock.getSystemTimeMillis());
        contentEvent.setYPosition(y);
        stave.setContentValue(y);

        scoreProcessor.process(contentEvent);
    }

    public void sendTimbreLineYEvent(Id instrumentId, Stave stave, double yDelta, long unscaledValue) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());

        //timbre line y position
        String address = stave.getOscAddressScoreTimbreLine();

        int staveNo = staveId.getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = getTimbreLine1YMinPosition() - yDelta;
                break;
            case 2:
                y = getTimbreLine2YMinPosition() - yDelta;
                break;
            default:
                LOG.error("Invalid stave position Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getTimbreValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * getTimbreLineYMax() / 100.0);
        if (diff < threshold) {
            LOG.debug("sendTimbreLineYEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        LOG.debug("sendTimbreLineYEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent positionEvent = eventFactory.createElementYPositionEvent(address, destination, staveId,
                unscaledValue, OverlayType.TIMBRE, clock.getSystemTimeMillis());
        positionEvent.setYPosition(y);
        stave.setTimbreValue(y);

        scoreProcessor.process(positionEvent);
    }

    public void sendPressureChangeEvent(Id instrumentId, Stave stave, double yDelta, long unscaledValue) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());

        int staveNo = ((StaveId) stave.getId()).getStaveNo();
        double y;
        switch (staveNo) {
            case 1:
                y = getPressureLine1YMinPosition() - yDelta;
                break;
            case 2:
                y = getPressureLine2YMinPosition() - yDelta;
                break;
            default:
                LOG.error("Invalid stave pressure Y position event");
                return;
        }

        y = MathUtil.roundTo5DecimalPlaces(y);
        double current = stave.getPressureValue();
        double diff = Math.abs(y - current);
        double threshold = Math.abs(2 * getPressureLineYMax() / 100.0);
        if (diff < threshold) {
            LOG.debug("sendPressureChangeEvent y position change: {} is less than threshold: {}, ignoring update ... ", diff, threshold);
            return;
        }

        String address = stave.getOscAddressScorePressureLine();
        LOG.debug("sendPressureChangeEvent sending y position: {} to: {} addr: '{}' yDelta: {} ", y, address, instrumentId, yDelta);

        ElementYPositionEvent pressureEvent = eventFactory.createElementYPositionEvent(address, destination, staveId, unscaledValue, OverlayType.PRESSURE, clock.getSystemTimeMillis());
        pressureEvent.setYPosition(y);
        stave.setPressureValue(y);

        scoreProcessor.process(pressureEvent);
    }

    private void onDynamicsValueChange(long value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        int r = 255;
        int g = 255;
        int b = 255;
        double scaled = getDynamicsValueScaler().scaleValue(value);
        LOG.debug("onDynamicsValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        if (value > 50) {
            g = (int) Math.round(getDynamicsForteColorScaler().scaleValue(value));
            b = g;
        } else if (value < 50) {
            r = (int) Math.round(getDynamicsPianoColorScaler().scaleValue(value));
            g = r;
        }

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayColorEvent(instrumentId, stave, r, g, b, OverlayType.DYNAMICS, stave.getOscAddressScoreDynamicsBox());
                sendDynamicsYPositionEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    private void setDynamicsOverlay(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setDynamicsOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.DYNAMICS, OverlayElementType.DYNAMICS_BOX,
                        stave.getOscAddressScoreDynamicsBox());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.DYNAMICS, OverlayElementType.DYNAMICS_MID_LINE,
                        stave.getOscAddressScoreDynamicsMidLine());
            }
        }
    }

    private void setDynamicsLine(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setDynamicsLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayLineAlphaEvent(instrumentId, stave, value, OverlayType.DYNAMICS, OverlayElementType.DYNAMICS_LINE);
            }
        }
    }

    private void onPressureValueChange(long value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {

                if (value > 50) {
                    int scaled = (int) Math.round(getPressureColorScaler().scaleValue(value));
                    LOG.debug("onPressureValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));
                    sendOverlayColorEvent(instrumentId, stave, scaled, scaled, scaled, OverlayType.PRESSURE, stave.getOscAddressScorePressureBox());
                }

                double scaled = getPressureLineValueScaler().scaleValue(value);
                sendPressureChangeEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    private void setPressureOverlay(Boolean value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        LOG.debug("setPressureOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.PRESSURE, OverlayElementType.PRESSURE_BOX,
                        stave.getOscAddressScorePressureBox());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.PRESSURE, OverlayElementType.PRESSURE_MID_LINE,
                        stave.getOscAddressScorePressureMidLine());
            }
        }
    }

    private void setPressureLine(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setPressureLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayLineAlphaEvent(instrumentId, stave, value, OverlayType.PRESSURE, OverlayElementType.PRESSURE_LINE);
            }
        }
    }

    private void onSpeedValueChange(long value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }

        int r = 255;
        int g = 255;
        int b = 255;
        double scaled = getSpeedValueScaler().scaleValue(value);
        LOG.debug("onSpeedValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        if (value > 50) {
            b = (int) Math.round(getSpeedFastColorScaler().scaleValue(value));
            r = b;
        } else if (value < 50) {
            g = (int) Math.round(getSpeedSlowColorScaler().scaleValue(value));
        }

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayColorEvent(instrumentId, stave, r, g, b, OverlayType.SPEED, stave.getOscAddressScoreSpeedBox());
                sendSpeedYPositionEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    private void setSpeedOverlay(Boolean value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        LOG.debug("setSpeedOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.SPEED, OverlayElementType.SPEED_BOX,
                        stave.getOscAddressScoreSpeedBox());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.SPEED, OverlayElementType.SPEED_MID_LINE,
                        stave.getOscAddressScoreSpeedMidLine());
            }
        }
    }

    private void setSpeedLine(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setSpeedLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayLineAlphaEvent(instrumentId, stave, value, OverlayType.SPEED, OverlayElementType.SPEED_LINE);
            }
        }
    }

    private void onPositionValueChange(long value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        double scaled = getPositionValueScaler().scaleValue(value);
        LOG.debug("onPositionValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendPositionLineYEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    private void setPositionOverlay(Boolean value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        LOG.debug("setPositionOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.POSITION, OverlayElementType.POSITION_BOX,
                        stave.getOscAddressScorePositionBox());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.POSITION, OverlayElementType.POSITION_ORD_LINE,
                        stave.getOscAddressScorePositionOrdLine());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.POSITION, OverlayElementType.POSITION_BRIDGE_LINE,
                        stave.getOscAddressScorePositionBridgeLine());
            }
        }
    }

    private void setPositionLine(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setPositionLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayLineAlphaEvent(instrumentId, stave, value, OverlayType.POSITION, OverlayElementType.POSITION_LINE);
            }
        }
    }

    private void onTimbreValueChange(long value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        int r = 255;
        int g = 255;
        int b = 255;
        double scaled = getTimbreValueScaler().scaleValue(value);
        LOG.debug("onTimbreValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        if (value > 50) {
            b = (int) Math.round(getTimbreUpColorScaler().scaleValue(value));
        } else if (value < 50) {
            r = (int) Math.round(getTimbreDownColorScaler().scaleValue(value));
            g = r;
            b = r;
        }

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayColorEvent(instrumentId, stave, r, g, b, OverlayType.TIMBRE, stave.getOscAddressScoreTimbreBox());
                sendTimbreLineYEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    private void setTimbreOverlay(Boolean value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        LOG.debug("setTimbreOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.TIMBRE, OverlayElementType.TIMBRE_BOX,
                        stave.getOscAddressScoreTimbreBox());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.TIMBRE, OverlayElementType.TIMBRE_ORD_LINE,
                        stave.getOscAddressScoreTimbreOrdLine());
            }
        }
    }

    private void setTimbreLine(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setTimbreLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayLineAlphaEvent(instrumentId, stave, value, OverlayType.TIMBRE, OverlayElementType.TIMBRE_LINE);
            }
        }
    }

    private void onContentValueChange(long value, List<Id> instrumentIds) {
        if (instrumentIds == null) {
            return;
        }
        double scaled = getContentValueScaler().scaleValue(value);
        LOG.debug("onContentValueChange scaled value: {} to: {}, instruments: {}", value, scaled, Arrays.toString(instrumentIds.toArray()));

        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendContentLineYEvent(instrumentId, stave, scaled, value);
            }
        }
    }

    private void setContentOverlay(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setContentOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.PITCH, OverlayElementType.PITCH_BOX, stave.getOscAddressScoreContentBox());
            }
        }
    }

    private void setContentLine(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setContentLine value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayLineAlphaEvent(instrumentId, stave, value, OverlayType.PITCH, OverlayElementType.PITCH_LINE);
            }
        }
    }

    private void setPitchStaveOverlay(Boolean value, List<Id> instrumentIds) {
        LOG.debug("setPitchStaveOverlay value: {}, instruments: {}", value, Arrays.toString(instrumentIds.toArray()));
        for (Id instrumentId : instrumentIds) {
            Instrument instrument = scoreProcessor.getScore().getInstrument(instrumentId);
            if (inNotOverlayInstrument(instrument)) {
                continue;
            }
            Collection<Stave> staves = scoreProcessor.getScore().getInstrumentStaves(instrumentId);
            for (Stave stave : staves) {
                sendOverlayElementAlphaEvent(instrumentId, stave, value, OverlayType.PITCH_STAVE, OverlayElementType.PITCH_STAVE_BOX, stave.getOscAddressScoreContentBox());
                sendOverlayElementPenAlphaEvent(instrumentId, stave, value, OverlayType.PITCH_STAVE, OverlayElementType.PITCH_STAVE_MID_LINE, stave.getOscAddressScoreContentStaveOrdLine());
            }
        }
    }

    private boolean inNotOverlayInstrument(Instrument instrument) {
        return instrument.isAv() || Consts.NAME_FULL_SCORE.equalsIgnoreCase(instrument.getName());
    }

    public void sendOverlayLineAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled, OverlayType overlayType, OverlayElementType overlayElementType) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());
        int alpha = 0;
        if (isEnabled) {
            alpha = 255;
        }
        sendElementPenAlphaEvent(instrumentId, staveId, isEnabled, overlayType, overlayElementType, stave.getOscAddressScoreDynamicsLine(), destination, alpha);
    }

    public void sendOverlayElementAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled, OverlayType overlayType, OverlayElementType overlayElementType, String address) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());
        int alpha = 0;
        if (isEnabled) {
            alpha = 255;
        }
        ElementAlphaEvent aEvent = eventFactory.createElementAlphaEvent(staveId, isEnabled, overlayType, overlayElementType,
                address, destination, clock.getSystemTimeMillis());
        aEvent.setAlpha(alpha);
        LOG.debug("sendOverlayElementAlphaEvent sending alpha: {} to: {} addr: '{}'", alpha, instrumentId, address);
        scoreProcessor.process(aEvent);
    }

    public void sendOverlayElementPenAlphaEvent(Id instrumentId, Stave stave, boolean isEnabled, OverlayType overlayType, OverlayElementType overlayElementType, String address) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());
        int alpha = 0;
        if (isEnabled) {
            alpha = 255;
        }
        sendElementPenAlphaEvent(instrumentId, staveId, isEnabled, overlayType, overlayElementType, address, destination, alpha);
    }

    public void sendElementPenAlphaEvent(Id instrumentId, StaveId staveId, boolean isEnabled, OverlayType overlayType,
                                         OverlayElementType overlayElementType, String address, String destination, int alpha) {
        ElementAlphaEvent dynYEvent = eventFactory.createElementPenAlphaEvent(staveId, isEnabled, overlayType,
                overlayElementType, address, destination, clock.getSystemTimeMillis());
        dynYEvent.setAlpha(alpha);
        LOG.debug("sendElementPenAlphaEvent sending alpha: {} to: {} addr: '{}'", alpha, instrumentId, address);
        scoreProcessor.process(dynYEvent);
    }

    public void sendOverlayColorEvent(Id instrumentId, Stave stave, int r, int g, int b, OverlayType overlayType, String address) {
        StaveId staveId = (StaveId) stave.getId();
        String destination = scoreProcessor.getScore().getOscDestination(staveId.getInstrumentId());
        ElementColorEvent colorEvent = eventFactory.createElementColorEvent(staveId, overlayType, address, destination, clock.getSystemTimeMillis());
        colorEvent.setColor(r, g, b);
        LOG.debug("sendOverlayColorEvent sending r: {}  g: {}  b: {} to: {} addr: '{}'", r, g, b, instrumentId, address);
        scoreProcessor.process(colorEvent);
    }

    public ValueScaler getDynamicsValueScaler () {
        return dynamicsValueScaler;
    }
    public ValueScaler getDynamicsForteColorScaler () {
        return dynamicsForteColorValueScaler;
    }
    public ValueScaler getDynamicsPianoColorScaler () {
        return dynamicsPianoColorValueScaler;
    }
    public ValueScaler getPressureLineValueScaler () {
        return pressureLineValueScaler;
    }
    public ValueScaler getPressureColorScaler () {
        return pressureColorValueScaler;
    }
    public ValueScaler getSpeedValueScaler () {
        return speedValueScaler;
    }
    public ValueScaler getSpeedFastColorScaler () {
        return speedFastColorValueScaler;
    }
    public ValueScaler getSpeedSlowColorScaler () {
        return speedSlowColorValueScaler;
    }
    public ValueScaler getPositionValueScaler () {
        return positionValueScaler;
    }
    public ValueScaler getContentValueScaler () {
        return contentValueScaler;
    }
    public ValueScaler getTimbreValueScaler () {
        return timbreValueScaler;
    }
    public ValueScaler getTimbreUpColorScaler () {
        return timbreUpColorValueScaler;
    }
    public ValueScaler getTimbreDownColorScaler () {
        return timbreDownColorValueScaler;
    }
    public double getDynamicsLineYMax () {
        return DYNAMICS_LINE_Y_MAX;
    }
    public double getDynamicsLine1YMinPosition () {
        return DYNAMICS_LINE1_Y_MIN_POSITION;
    }
    public double getDynamicsLine2YMinPosition () {
        return DYNAMICS_LINE2_Y_MIN_POSITION;
    }
    public double getPressureLineYMax () {
        return PRESSURE_LINE_Y_MAX;
    }
    public double getPressureLine1YMinPosition () {
        return PRESSURE_LINE1_Y_MIN_POSITION;
    }
    public double getPressureLine2YMinPosition () {
        return PRESSURE_LINE2_Y_MIN_POSITION;
    }
    public double getSpeedLineYMax () {
        return SPEED_LINE_Y_MAX;
    }
    public double getSpeedLine1YMinPosition () {
        return SPEED_LINE1_Y_MIN_POSITION;
    }
    public double getSpeedLine2YMinPosition () {
        return SPEED_LINE2_Y_MIN_POSITION;
    }
    public double getPositionLineYMax () {
        return POSITION_LINE_Y_MAX;
    }
    public double getPositionLine1YMinPosition () {
        return POSITION_LINE1_Y_MIN_POSITION;
    }
    public double getPositionLine2YMinPosition () {
        return POSITION_LINE2_Y_MIN_POSITION;
    }
    public double getContentLineYMax () {
        return CONTENT_LINE_Y_MAX;
    }
    public double getContentLine1YMinPosition () {
        return CONTENT_LINE1_Y_MIN_POSITION;
    }
    public double getContentLine2YMinPosition () {
        return CONTENT_LINE2_Y_MIN_POSITION;
    }
    public double getTimbreLineYMax () {
        return TIMBRE_LINE_Y_MAX;
    }
    public double getTimbreLine1YMinPosition () {
        return TIMBRE_LINE1_Y_MIN_POSITION;
    }
    public double getTimbreLine2YMinPosition () {
        return TIMBRE_LINE2_Y_MIN_POSITION;
    }
}
