package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.WebScore;

public class TileExport {
    private String id;
    private int row;
    private int column;
    private WebElementStateExport state;
    private TileTextExport tileText;

    public String getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public WebElementStateExport getState() {
        return state;
    }

    public TileTextExport getTileText() {
        return tileText;
    }

    public void populate(WebScore.Tile from) {
        if (from == null) {
            return;
        }
        this.id = from.getId();
        this.row = from.getRow();
        this.column = from.getColumn();
        this.state = new WebElementStateExport();
        this.state.populate(from.getState());
        this.tileText = new TileTextExport();
        this.tileText.populate(from.getTileText());
    }

    @Override
    public String toString() {
        return "TileExport{" +
                "id='" + id + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", state=" + state +
                ", tileText=" + tileText +
                '}';
    }
}
