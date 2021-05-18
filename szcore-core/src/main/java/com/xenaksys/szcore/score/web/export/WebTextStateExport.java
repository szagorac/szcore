package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.WebScore;

import java.util.Arrays;

public class WebTextStateExport {

    private String id;
    private boolean isVisible;
    private String colour;
    private String[] lines;

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getColour() {
        return colour;
    }

    public String[] getLines() {
        return lines;
    }

    public void populate(WebScore.WebTextState from) {
        if (from == null) {
            return;
        }
        this.id = from.getId();
        this.isVisible = from.isVisible();
        this.colour = from.getColour();
        String[] fromLines = from.getLines();
        this.lines = new String[fromLines.length];
        System.arraycopy(fromLines, 0, this.lines, 0, fromLines.length);
    }

    @Override
    public String toString() {
        return "WebTextStateExport{" +
                "id='" + id + '\'' +
                ", isVisible=" + isVisible +
                ", colour='" + colour + '\'' +
                ", lines=" + Arrays.toString(lines) +
                '}';
    }
}
