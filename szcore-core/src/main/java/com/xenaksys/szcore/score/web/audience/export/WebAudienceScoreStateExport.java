package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.web.WebAudienceAction;

import java.util.List;

public class WebAudienceScoreStateExport {
    private volatile TileExport[][] tiles;
    private final List<WebAudienceAction> actions;
    private volatile String zoomLevel;
    private final WebElementStateExport centreShape;
    private final WebElementStateExport innerCircle;
    private final WebElementStateExport outerCircle;
    private final WebAudienceInstructionsExport instructions;
    private final WebGranulatorConfigExport granulatorConfig;
    private final WebSpeechSynthConfigExport speechSynthConfig;
    private final WebSpeechSynthStateExport speechSynthState;
    private volatile double stageAlpha;

    public WebAudienceScoreStateExport(TileExport[][] tiles, List<WebAudienceAction> currentActions,
                                       WebElementStateExport centreShape, WebElementStateExport innerCircle, WebElementStateExport outerCircle,
                                       String zoomLevel, WebAudienceInstructionsExport instructions, WebGranulatorConfigExport granulatorConfig,
                                       WebSpeechSynthConfigExport speechSynthConfig, WebSpeechSynthStateExport speechSynthState, double stageAlpha) {
        this.tiles = tiles;
        this.actions = currentActions;
        this.centreShape = centreShape;
        this.innerCircle = innerCircle;
        this.outerCircle = outerCircle;
        this.zoomLevel = zoomLevel;
        this.instructions = instructions;
        this.granulatorConfig = granulatorConfig;
        this.speechSynthConfig = speechSynthConfig;
        this.speechSynthState = speechSynthState;
        this.stageAlpha = stageAlpha;
    }

    public TileExport[][] getTiles() {
        return tiles;
    }

    public List<WebAudienceAction> getActions() {
        return actions;
    }

    public WebElementStateExport getCentreShape() {
        return centreShape;
    }

    public WebElementStateExport getInnerCircle() {
        return innerCircle;
    }

    public WebElementStateExport getOuterCircle() {
        return outerCircle;
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public WebAudienceInstructionsExport getInstructions() {
        return instructions;
    }

    public WebGranulatorConfigExport getGranulatorConfig() {
        return granulatorConfig;
    }

    public WebSpeechSynthConfigExport getSpeechSynthConfig() {
        return speechSynthConfig;
    }

    public WebSpeechSynthStateExport getSpeechSynthState() {
        return speechSynthState;
    }

    public double getStageAlpha() {
        return stageAlpha;
    }
}
