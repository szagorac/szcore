package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.WebScore;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileExport that = (TileExport) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
