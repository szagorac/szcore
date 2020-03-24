package com.xenaksys.szcore.score;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;

public class WebTextState {
    private final String id;
    private boolean isVisible = false;
    private String line1 = EMPTY;
    private String line2 = EMPTY;
    private String line3 = EMPTY;
    private String colour = WEB_TEXT_BACKGROUND_COLOUR;

    public WebTextState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void copyTo(WebTextState other) {
        other.setVisible(isVisible());
        other.setColour(getColour());
        other.setLine1(getLine1());
        other.setLine2(getLine2());
        other.setLine3(getLine3());
    }

    @Override
    public String toString() {
        return "WebTextState{" +
                "id='" + id + '\'' +
                ", isVisible = " + isVisible +
                ", colour = " + colour +
                ", line1 = " + line1 +
                ", line2 = " + line2 +
                ", line3 = " + line3 +
                '}';
    }
}
