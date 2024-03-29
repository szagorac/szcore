package com.xenaksys.szcore.score.web.audience.export;

import com.xenaksys.szcore.score.web.audience.WebTileText;

public class TileTextExport {
    private String value;
    private boolean isVisible;

    public String getValue() {
        return value;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void populate(WebTileText from) {
        if (from == null) {
            return;
        }
        this.value = from.getValue();
        this.isVisible = from.isVisible();
    }

    @Override
    public String toString() {
        return "TileTextExport{" +
                "value='" + value + '\'' +
                ", isVisible=" + isVisible +
                '}';
    }
}
