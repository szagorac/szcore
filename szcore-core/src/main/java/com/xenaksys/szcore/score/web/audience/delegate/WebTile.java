package com.xenaksys.szcore.score.web.audience.delegate;

import com.xenaksys.szcore.score.web.audience.WebAudienceElementState;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ELEMENT_STATE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE_TEXT;

public class WebTile {

    private final String id;
    private final int row;
    private final int column;
    private final WebAudienceElementState state;
    private final WebTileText tileText;
    private final PropertyChangeSupport pcs;

    public WebTile(int row, int column, String id, PropertyChangeSupport pcs) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.pcs = pcs;
        this.state = new WebAudienceElementState(id, pcs);
        this.tileText = new WebTileText(EMPTY, false, this, pcs);
    }

    public WebAudienceElementState getState() {
        return state;
    }

    public void setState(WebAudienceElementState newState) {
        WebAudienceElementState old = new WebAudienceElementState(this.state.getId(), pcs);
        this.state.copyTo(old);
        newState.copyTo(this.state);
        if (!this.state.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, this.id, this);
        }
    }

    public void setText(WebTileText txt) {
        WebTileText old = new WebTileText(this.tileText.getValue(), this.tileText.isVisible(), this.tileText.getParent(), pcs);
        txt.copyTo(this.tileText);
        if (!this.tileText.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, this.id, this);
        }
    }

    public void setText(String txt) {
        WebTileText old = new WebTileText(this.tileText.getValue(), this.tileText.isVisible(), this.tileText.getParent(), pcs);
        this.tileText.setValue(txt);
        this.tileText.setVisible(true);
        if (!this.tileText.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, this.id, this);
        }
    }

    public String getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getRowIndex() {
        return row - 1;
    }

    public int getColumn() {
        return column;
    }

    public int getColumnIndex() {
        return column - 1;
    }

    public WebTileText getTileText() {
        return tileText;
    }

    public void copyTo(WebTile other) {
        state.copyTo(other.getState());
        tileText.copyTo(other.getTileText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebTile)) return false;
        WebTile tile = (WebTile) o;
        return getId().equals(tile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Tile{" +
                "id='" + id + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", state=" + state +
                '}';
    }
}
