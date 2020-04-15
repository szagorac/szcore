package com.xenaksys.szcore.web;

import com.xenaksys.szcore.score.WebElementState;
import com.xenaksys.szcore.score.WebScore;
import com.xenaksys.szcore.score.WebTextState;

import java.util.List;

public class WebScoreState {
    private final WebScore.Tile[][] tiles;
    private final List<WebAction> actions;
    private final String zoomLevel;
    private final WebElementState centreShape;
    private final WebElementState innerCircle;
    private final WebElementState outerCircle;
    private final WebTextState instructions;

    public WebScoreState(WebScore.Tile[][] tiles, List<WebAction> currentActions, WebElementState centreShape,
                         WebElementState innerCircle, WebElementState outerCircle, String zoomLevel,
                         WebTextState instructions) {
        this.tiles = tiles;
        this.actions = currentActions;
        this.centreShape = centreShape;
        this.innerCircle = innerCircle;
        this.outerCircle = outerCircle;
        this.zoomLevel = zoomLevel;
        this.instructions = instructions;
    }

    public WebScore.Tile[][] getTiles() {
        return tiles;
    }

    public List<WebAction> getActions() {
        return actions;
    }

    public WebElementState getCentreShape() {
        return centreShape;
    }

    public WebElementState getInnerCircle() {
        return innerCircle;
    }

    public WebElementState getOuterCircle() {
        return outerCircle;
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public WebTextState getInstructions() {
        return instructions;
    }
}
