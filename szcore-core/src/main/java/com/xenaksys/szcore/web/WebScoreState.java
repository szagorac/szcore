package com.xenaksys.szcore.web;

import com.xenaksys.szcore.score.WebScore;

import java.util.List;

public class WebScoreState {
    private final WebScore.Tile[][] tiles;
    private List<WebAction> actions;

    public WebScoreState(WebScore.Tile[][] tiles, List<WebAction> currentActions) {
        this.tiles = tiles;
        this.actions = currentActions;
    }

    public WebScore.Tile[][] getTiles() {
        return tiles;
    }

    public List<WebAction> getActions() {
        return actions;
    }

}
