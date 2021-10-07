package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.TextElementConfig;
import com.xenaksys.szcore.algo.config.TranspositionPageConfig;
import com.xenaksys.szcore.algo.config.TranspositionStrategyConfig;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.web.WebTextinfo;
import com.xenaksys.szcore.score.web.WebTranspositionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TranspositionStrategy implements ScoreStrategy {
    static final Logger LOG = LoggerFactory.getLogger(TranspositionStrategy.class);

    private final BasicScore szcore;
    private final TranspositionStrategyConfig config;
    private boolean isReady = false;

    public TranspositionStrategy(BasicScore szcore, TranspositionStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        if(config == null) {
            return;
        }
        isReady = true;
    }

    public WebTranspositionInfo getWebTranspositionInfo(PageId pageId, StaveId staveId) {
        if(pageId == null || staveId == null) {
            return null;
        }
        int pageNo = pageId.getPageNo();
        InstrumentId instrumentId = (InstrumentId) staveId.getInstrumentId();
        int staveNo = staveId.getStaveNo();
        TranspositionPageConfig pageConfig = config.getPageConfig(instrumentId.getName(), pageNo);
        if(pageConfig == null) {
            return null;
        }
        WebTranspositionInfo webTranspositionInfo = new WebTranspositionInfo();
        List<TextElementConfig> textConfigs = pageConfig.getTextConfigs();
        double[] yDiff = new double[textConfigs.size() -1];
        int idx = 0;
        for(TextElementConfig textConfig : textConfigs) {
            double y = 0.0;
            double x = 0.0;
            switch (staveNo) {
                case 1:
                    y = config.getTopStaveYRef() + textConfig.getDy();
                    x = config.getTopStaveXRef() + textConfig.getDx();
                    break;
                case 2:
                    y = config.getBotStaveYRef() + textConfig.getDy();
                    x = config.getBotStaveXRef() + textConfig.getDx();
                    break;
                default:
                    LOG.error("Invalid stave for transposition calc");
            }
            WebTextinfo txtInfo = new WebTextinfo(x, y, textConfig.getTxt());
            webTranspositionInfo.addTxtInfo(txtInfo);
            if(idx > 0) {
                WebTextinfo prev = webTranspositionInfo.getTxtInfo(idx - 1);
                yDiff[idx - 1] = Math.abs(prev.getY() - y);
            }
            idx++;
        }
        adjustSpacing(webTranspositionInfo, yDiff);
        return webTranspositionInfo;
    }

    private void adjustSpacing(WebTranspositionInfo webTranspositionInfo, double[] yDiff) {

    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public TranspositionStrategyConfig getConfig() {
        return config;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public boolean isActive() {
        return config.isActive();
    }

    @Override
    public StrategyType getType() {
        return StrategyType.TRANSPOSITION;
    }


}
