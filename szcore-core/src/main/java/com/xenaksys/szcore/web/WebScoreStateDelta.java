package com.xenaksys.szcore.web;

import com.xenaksys.szcore.score.WebElementState;
import com.xenaksys.szcore.score.WebScore;
import com.xenaksys.szcore.score.WebTextState;

import java.util.List;

public class WebScoreStateDelta {
    private WebScore.Tile[][] tiles;
    private List<WebAction> actions;
    private String zoomLevel;
    private WebElementState centreShape;
    private WebElementState innerCircle;
    private WebElementState outerCircle;
    private WebTextState instructions;

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

    public void setTiles(WebScore.Tile[][] tiles) {
        this.tiles = tiles;
    }

    public void setActions(List<WebAction> actions) {
        this.actions = actions;
    }

    public void setZoomLevel(String zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public void setCentreShape(WebElementState centreShape) {
        this.centreShape = centreShape;
    }

    public void setInnerCircle(WebElementState innerCircle) {
        this.innerCircle = innerCircle;
    }

    public void setOuterCircle(WebElementState outerCircle) {
        this.outerCircle = outerCircle;
    }

    public void setInstructions(WebTextState instructions) {
        this.instructions = instructions;
    }
}
