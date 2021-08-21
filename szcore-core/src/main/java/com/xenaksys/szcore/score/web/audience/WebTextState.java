package com.xenaksys.szcore.score.web.audience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;

public class WebTextState {
    static final Logger LOG = LoggerFactory.getLogger(WebTextState.class);

    private final String id;
    private boolean isVisible = false;
    private String colour = WEB_TEXT_BACKGROUND_COLOUR;
    private String[] lines;

    public WebTextState(String id, int lineNo) {
        this.id = id;
        this.lines = new String[lineNo];
        initLines();
    }

    private void initLines() {
        Arrays.fill(lines, EMPTY);
    }

    public String getId() {
        return id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public int getLineNo() {
        return lines.length;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int lineNo) {
        if (lineNo < 0 || lineNo > lines.length) {
            return null;
        }
        return lines[lineNo - 1];
    }

    public void setLine(String line, int lineNo) {
        if (lineNo < 0 || lineNo > lines.length) {
            LOG.error("WebTextState setLine: Invalid line No: {}", lineNo);
            return;
        }
        this.lines[lineNo - 1] = line;
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
        for (int i = 0; i < lines.length; i++) {
            other.setLine(this.lines[i], i + 1);
        }
    }
}
