package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.web.WebAction;

import java.util.List;

public class WebScoreStateExport {
    private volatile TileExport[][] tiles;
    private final List<WebAction> actions;
    private volatile String zoomLevel;
    private final WebElementStateExport centreShape;
    private final WebElementStateExport innerCircle;
    private final WebElementStateExport outerCircle;
    private final WebInstructionsExport instructions;
    private final WebGranulatorConfigExport granulatorConfig;
    private final WebSpeechSynthConfigExport speechSynthConfig;
    private final WebSpeechSynthStateExport speechSynthState;

    public WebScoreStateExport(TileExport[][] tiles, List<WebAction> currentActions,
                               WebElementStateExport centreShape, WebElementStateExport innerCircle, WebElementStateExport outerCircle,
                               String zoomLevel, WebInstructionsExport instructions, WebGranulatorConfigExport granulatorConfig,
                               WebSpeechSynthConfigExport speechSynthConfig, WebSpeechSynthStateExport speechSynthState) {
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
    }

    public TileExport[][] getTiles() {
        return tiles;
    }

    public List<WebAction> getActions() {
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

    public WebInstructionsExport getInstructions() {
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
}
