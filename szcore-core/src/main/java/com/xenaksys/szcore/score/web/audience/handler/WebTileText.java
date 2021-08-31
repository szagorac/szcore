package com.xenaksys.szcore.score.web.audience.handler;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE_TEXT;

public class WebTileText {

    private String value;
    private boolean isVisible;
    private final WebTile parent;
    private final PropertyChangeSupport pcs;

    public WebTileText(String value, boolean isVisible, WebTile parent, PropertyChangeSupport pcs) {
        this.value = value;
        this.isVisible = isVisible;
        this.parent = parent;
        this.pcs = pcs;
    }

    public String getValue() {
        return value;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setValue(String value) {
        String old = this.value;
        this.value = value;
        if (!this.value.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, parent.getId(), parent);
        }
    }

    public void setVisible(boolean visible) {
        boolean old = this.isVisible;
        this.isVisible = visible;
        if (old != this.isVisible) {
            pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, parent.getId(), parent);
        }
    }

    public WebTile getParent() {
        return parent;
    }

    public void copyTo(WebTileText other) {
        other.setVisible(isVisible());
        other.setValue(getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebTileText tileText = (WebTileText) o;
        return isVisible == tileText.isVisible && Objects.equals(value, tileText.value) && Objects.equals(parent, tileText.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isVisible, parent);
    }
}
