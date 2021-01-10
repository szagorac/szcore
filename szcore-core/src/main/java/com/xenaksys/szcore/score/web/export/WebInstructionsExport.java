package com.xenaksys.szcore.score.web.export;

import com.xenaksys.szcore.score.web.WebScore;

import static com.xenaksys.szcore.Consts.EMPTY;

public class WebInstructionsExport {
    private String id;
    private boolean isVisible;
    private String colour;
    private String line1 = EMPTY;
    private String line2 = EMPTY;
    private String line3 = EMPTY;

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getColour() {
        return colour;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getLine3() {
        return line3;
    }

    public void populate(WebScore.WebTextState from) {
        if (from == null) {
            return;
        }
        this.id = from.getId();
        this.isVisible = from.isVisible();
        this.colour = from.getColour();
        String[] fromLines = from.getLines();
        if (fromLines.length > 0) {
            this.line1 = fromLines[0];
        }
        if (fromLines.length > 1) {
            this.line2 = fromLines[1];
        }
        if (fromLines.length > 2) {
            this.line3 = fromLines[2];
        }
    }

    @Override
    public String toString() {
        return "WebInstructionsExport{" +
                "id='" + id + '\'' +
                ", isVisible=" + isVisible +
                ", colour='" + colour + '\'' +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", line3='" + line3 + '\'' +
                '}';
    }
}
