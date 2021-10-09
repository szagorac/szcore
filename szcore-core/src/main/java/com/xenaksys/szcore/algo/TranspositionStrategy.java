package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.TextElementConfig;
import com.xenaksys.szcore.algo.config.TranspositionPageConfig;
import com.xenaksys.szcore.algo.config.TranspositionStrategyConfig;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.model.id.PageId;
import com.xenaksys.szcore.model.id.StaveId;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.score.web.WebRectInfo;
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
            WebTextinfo txtInfo = null;
            WebRectInfo rectInfo = null;
            switch (staveNo) {
                case 1:
                    txtInfo = createTextInfo(config.getTopStaveXRef(), config.getTopStaveYRef(), config.getTopStaveStartX(), textConfig);
                    rectInfo = createRectInfo(config.getTopStaveXRef(), config.getTopStaveYRef(), config.getTopStaveStartX(),textConfig);
                    break;
                case 2:
                    txtInfo = createTextInfo(config.getBotStaveXRef(), config.getBotStaveYRef(), config.getBotStaveStartX(), textConfig);
                    rectInfo = createRectInfo(config.getBotStaveXRef(), config.getBotStaveYRef(), config.getBotStaveStartX(), textConfig);
                    break;
                default:
                    LOG.error("Invalid stave for transposition calc");
            }
            if(txtInfo != null) {
                webTranspositionInfo.addTxtInfo(txtInfo);
                if(idx > 0) {
                    WebTextinfo prev = webTranspositionInfo.getTxtInfo(idx - 1);
                    yDiff[idx - 1] = Math.abs(prev.getY() - txtInfo.getY());
                }
            }
            if(rectInfo != null) {
                webTranspositionInfo.addRectInfo(rectInfo);
            }
            idx++;
        }
        adjustSpacing(webTranspositionInfo, yDiff);
        return webTranspositionInfo;
    }

    private WebTextinfo createTextInfo(double xRef, double yRef, double startX, TextElementConfig textConfig) {
        double dx = textConfig.getDx();
        boolean isExtOverlay = isExtX(xRef, startX, dx);
        double x;
        double y = yRef + textConfig.getDy();
        if(isExtOverlay) {
            x = startX + dx;
        } else {
            x = xRef + dx;
        }
        return new WebTextinfo(x, y, textConfig.getTxt());
    }

    private WebRectInfo createRectInfo(double xRef, double yRef, double startX, TextElementConfig textConfig) {
        double dx = textConfig.getDx();
        boolean isExtOverlay = isExtX(xRef, startX, dx);
        if(!isExtOverlay) {
            return null;
        }
        double x = startX + dx + config.getExtRectDx();
        double y = yRef + textConfig.getDy() + config.getExtRectDy();
        return new WebRectInfo(x, y, config.getExtRectWidth(), config.getExtRectHeight());
    }

    private boolean isExtX(double xRef, double startX, double dx) {
        return dx > (startX - xRef);
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
