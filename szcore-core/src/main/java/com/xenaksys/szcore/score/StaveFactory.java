package com.xenaksys.szcore.score;


import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Stave;
import com.xenaksys.szcore.model.id.StaveId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaveFactory {
    static final Logger LOG = LoggerFactory.getLogger(StaveFactory.class);

    public static Stave createStave(int staveNo, Instrument instrument) {

        if(instrument == null) {
            LOG.error("Invalid instrument NULL");
            return null;
        }

        if(Consts.NAME_FULL_SCORE.equals(instrument.getName())) {
            return createFullScoreStave(staveNo, instrument);
        }  else {
            return createBasicStave(staveNo, instrument);
        }

    }


    public static Stave createBasicStave(int staveNo, Instrument instrument) {
        switch (staveNo) {
            case 1:
               return createBasicStaveOne(staveNo, instrument);
            case 2:
                return createBasicStaveTwo(staveNo, instrument);
            default:
                LOG.error("Unexpected stave number: " + staveNo);
        }
        return null;
    }

    public static Stave createFullScoreStave(int staveNo, Instrument instrument) {
        switch (staveNo) {
            case 1:
                return createFullScoreStaveOne(staveNo, instrument);
            case 2:
                return createFullScoreStaveTwo(staveNo, instrument);
            default:
                LOG.error("Unexpected stave number: " + staveNo);
        }
        return null;
    }

    public static Stave createBasicStaveOne(int staveNo, Instrument instrument) {
        StaveId staveId = new StaveId(instrument.getId(), staveNo);
        String oscAddress = Consts.OSC_ADDRESS_STAVE1;
        String oscAddressScoreFollower = Consts.OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE1;
        String oscAddressScoreBeater = Consts.OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE1;
        String oscAddressScoreStartMark = Consts.OSC_ADDRESS_SCORE_START_MARK_STAVE1;
        String oscAddressScoreDynamicsLine = Consts.OSC_ADDRESS_SCORE_DYNAMICS_LINE_STAVE1;
        String oscAddressScoreDynamicsBox = Consts.OSC_ADDRESS_SCORE_DYNAMICS_BOX_STAVE1;
        String oscAddressScorePressureBox = Consts.OSC_ADDRESS_SCORE_PRESSURE_BOX_STAVE1;
        double xPosition = Consts.OSC_STAVE1_X;
        double yPosition = Consts.OSC_STAVE1_Y;
        double zPosition = Consts.OSC_STAVE1_Z;
        double beaterYMinPosition = Consts.OSC_STAVE1_BEATER_Y_MIN;
        double beaterYMaxPosition = Consts.OSC_STAVE1_BEATER_Y_MAX;
        double scale = Consts.OSC_STAVE1_SCALE;
        boolean isVisible = (Consts.OSC_STAVE1_SHOW == 1);
        Stave stave = new BasicStave(staveId,
                oscAddress,
                oscAddressScoreFollower,
                oscAddressScoreBeater,
                oscAddressScoreStartMark,
                oscAddressScoreDynamicsLine,
                oscAddressScoreDynamicsBox,
                oscAddressScorePressureBox,
                xPosition,
                yPosition,
                zPosition,
                beaterYMinPosition,
                beaterYMaxPosition,
                scale,
                isVisible);
        return stave;
    }

    public static Stave createBasicStaveTwo(int staveNo, Instrument instrument) {
        StaveId staveId = new StaveId(instrument.getId(), staveNo);
        String oscAddress = Consts.OSC_ADDRESS_STAVE2;
        String oscAddressScoreFollower = Consts.OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE2;
        String oscAddressScoreBeater = Consts.OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE2;
        String oscAddressScoreStartMark = Consts.OSC_ADDRESS_SCORE_START_MARK_STAVE2;
        String oscAddressScoreDynamicsLine = Consts.OSC_ADDRESS_SCORE_DYNAMICS_LINE_STAVE2;
        String oscAddressScoreDynamicsBox = Consts.OSC_ADDRESS_SCORE_DYNAMICS_BOX_STAVE2;
        String oscAddressScorePressureBox = Consts.OSC_ADDRESS_SCORE_PRESSURE_BOX_STAVE2;
        double xPosition = Consts.OSC_STAVE2_X;
        double yPosition = Consts.OSC_STAVE2_Y;
        double zPosition = Consts.OSC_STAVE2_Z;
        double beaterYMinPosition = Consts.OSC_STAVE2_BEATER_Y_MIN;
        double beaterYMaxPosition = Consts.OSC_STAVE2_BEATER_Y_MAX;
        double scale = Consts.OSC_STAVE2_SCALE;
        boolean isVisible = (Consts.OSC_STAVE2_SHOW == 1);
        Stave stave = new BasicStave(staveId,
                oscAddress,
                oscAddressScoreFollower,
                oscAddressScoreBeater,
                oscAddressScoreStartMark,
                oscAddressScoreDynamicsLine,
                oscAddressScoreDynamicsBox,
                oscAddressScorePressureBox,
                xPosition,
                yPosition,
                zPosition,
                beaterYMinPosition,
                beaterYMaxPosition,
                scale,
                isVisible);
        return stave;
    }

    public static Stave createFullScoreStaveOne(int staveNo, Instrument instrument) {
        StaveId staveId = new StaveId(instrument.getId(), staveNo);
        String oscAddress = Consts.OSC_ADDRESS_STAVE1;
        String oscAddressScoreFollower = Consts.OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE1;
        String oscAddressScoreBeater = Consts.OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE1;
        String oscAddressScoreStartMark = Consts.OSC_ADDRESS_SCORE_START_MARK_STAVE1;
        double xPosition = Consts.OSC_FULL_SCORE_STAVE1_X;
        double yPosition = Consts.OSC_FULL_SCORE_STAVE1_Y;
        double zPosition = Consts.OSC_STAVE1_Z;
        double beaterYMinPosition = Consts.OSC_FULL_SCORE_STAVE1_BEATER_Y_MIN;
        double beaterYMaxPosition = Consts.OSC_FULL_SCORE_STAVE1_BEATER_Y_MAX;
        double scale = Consts.OSC_FULL_SCORE_SCALE;
        boolean isVisible = (Consts.OSC_STAVE1_SHOW == 1);
        Stave stave = new BasicStave(staveId,
                oscAddress,
                oscAddressScoreFollower,
                oscAddressScoreBeater,
                oscAddressScoreStartMark,
                null,
                null,
                null,
                xPosition,
                yPosition,
                zPosition,
                beaterYMinPosition,
                beaterYMaxPosition,
                scale,
                isVisible);
        return stave;
    }

    public static Stave createFullScoreStaveTwo(int staveNo, Instrument instrument) {
        StaveId staveId = new StaveId(instrument.getId(), staveNo);
        String oscAddress = Consts.OSC_ADDRESS_STAVE2;
        String oscAddressScoreFollower = Consts.OSC_ADDRESS_SCORE_FOLLOW_LINE_STAVE2;
        String oscAddressScoreBeater = Consts.OSC_ADDRESS_SCORE_FOLLOW_BEATER_STAVE2;
        String oscAddressScoreStartMark = Consts.OSC_ADDRESS_SCORE_START_MARK_STAVE2;
        double xPosition = Consts.OSC_FULL_SCORE_STAVE2_X;
        double yPosition = Consts.OSC_FULL_SCORE_STAVE2_Y;
        double zPosition = Consts.OSC_STAVE2_Z;
        double beaterYMinPosition = Consts.OSC_FULL_SCORE_STAVE2_BEATER_Y_MIN;
        double beaterYMaxPosition = Consts.OSC_FULL_SCORE_STAVE2_BEATER_Y_MAX;
        double scale = Consts.OSC_FULL_SCORE_SCALE;
        boolean isVisible = (Consts.OSC_STAVE2_SHOW == 1);
        Stave stave = new BasicStave(staveId,
                oscAddress,
                oscAddressScoreFollower,
                oscAddressScoreBeater,
                oscAddressScoreStartMark,
                null,
                null,
                null,
                xPosition,
                yPosition,
                zPosition,
                beaterYMinPosition,
                beaterYMaxPosition,
                scale,
                isVisible);
        return stave;
    }

}
