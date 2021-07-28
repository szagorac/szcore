package com.xenaksys.szcore.util;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.ValueScaler;
import com.xenaksys.szcore.model.id.StaveId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtil {
    static final Logger LOG = LoggerFactory.getLogger(WebUtil.class);
    private static final ValueScaler alphaScaler = new ValueScaler(0.0, 255.0, 0.0, 1.0);

    public static String getWebStaveId(StaveId staveId) {
        int staveNo = staveId.getStaveNo();
        String webStaveId = null;
        switch (staveNo) {
            case 1:
                webStaveId = Consts.WEB_SCORE_STAVE_TOP;
                break;
            case 2:
                webStaveId = Consts.WEB_SCORE_STAVE_BOTTOM;
                break;
            default:
                LOG.error("getWebStaveId: Unexpected stave number: " + staveNo);
        }
        return webStaveId;
    }

    public static Double convertToOpacity(int alpha) {
        return alphaScaler.scaleValue(1.0 * alpha);
    }

}
