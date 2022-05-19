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
        double[] yDiff = new double[0];
        if(textConfigs.size() > 0) {
            yDiff = new double[textConfigs.size() - 1];
        }
        int idx = 0;
        for(TextElementConfig textConfig : textConfigs) {
            WebTextinfo txtInfo = null;
            WebRectInfo rectInfo = null;
            boolean isMod = isTxtMod(textConfig);
            switch (staveNo) {
                case 1:
                    txtInfo = createTextInfo(config.getTopStaveXRef(), config.getTopStaveYRef(), config.getTopStaveStartX(), textConfig);
                    rectInfo = createRectInfo(config.getTopStaveXRef(), config.getTopStaveYRef(), config.getTopStaveStartX(),textConfig, isMod);
                    break;
                case 2:
                    txtInfo = createTextInfo(config.getBotStaveXRef(), config.getBotStaveYRef(), config.getBotStaveStartX(), textConfig);
                    rectInfo = createRectInfo(config.getBotStaveXRef(), config.getBotStaveYRef(), config.getBotStaveStartX(), textConfig, isMod);
                    break;
                default:
                    LOG.error("Invalid stave for transposition calc");
            }
            if(txtInfo != null) {
                webTranspositionInfo.addTxtInfo(txtInfo);
                if(idx > 0) {
                    WebTextinfo prev = webTranspositionInfo.getTxtInfo(idx - 1);
                    int diffIdx = idx -1;
                    if(diffIdx < yDiff.length) {
                        yDiff[diffIdx] = Math.abs(prev.getY() - txtInfo.getY());
                    }
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

    private boolean isTxtMod(TextElementConfig textConfig) {
        if(textConfig == null || textConfig.getTxt() == null) {
            return false;
        }
        return textConfig.getTxt().length() > 1;
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

    private WebRectInfo createRectInfo(double xRef, double yRef, double startX, TextElementConfig textConfig, boolean isMod) {
        double dx = textConfig.getDx();
        boolean isExtOverlay = isExtX(xRef, startX, dx);
        if(!isExtOverlay) {
            return null;
        }
        double x = startX + dx + config.getExtRectDx();
        double y = yRef + textConfig.getDy() + config.getExtRectDy();
        double width = isMod ? config.getExtRectModWidth() : config.getExtRectWidth();
        double height = isMod ? config.getExtRectModHeight() : config.getExtRectHeight();
        return new WebRectInfo(x, y, width, height);
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
    public void reset() {

    }

    @Override
    public StrategyType getType() {
        return StrategyType.TRANSPOSITION;
    }


}
