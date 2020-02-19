package com.xenaksys.szcore.web;

import com.xenaksys.szcore.score.WebScore;

public class WebScoreState {
    private final WebScore.Tile[][] tiles;
    private final WebScore.Grid grid;

    private WebAction action;

    public WebScoreState(WebScore.Tile[][] tiles, WebScore.Grid grid) {
        this.tiles = tiles;
        this.grid = grid;
    }

    public WebScore.Tile[][] getTiles() {
        return tiles;
    }

    public WebScore.Grid getGrid() {
        return grid;
    }

    public WebAction getAction() {
        return action;
    }

    public void setAction(WebAction action) {
        this.action = action;
    }
}
