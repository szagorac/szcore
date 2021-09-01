package com.xenaksys.szcore.score.web.audience;

import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.delegate.WebTile;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CENTRE_SHAPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ELEMENT_STATE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INNER_CIRCLE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_OUTER_CIRCLE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STAGE_ALPHA;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STATE_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILES;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ZOOM_LEVEL;

public class WebAudienceServerState {
    static final Logger LOG = LoggerFactory.getLogger(WebAudienceServerState.class);

    private volatile WebTile[][] tiles;
    private final List<WebAudienceAction> actions;
    private final PropertyChangeSupport pcs;

    private volatile String zoomLevel;
    private final Map<String, WebAudienceElementState> elementStates;
    private final WebTextState instructions;
    private volatile WebGranulatorConfig granulatorConfig;
    private volatile WebSpeechSynthConfig speechSynthConfig;
    private volatile WebSpeechSynthState speechSynthState;
    private volatile double stageAlpha;

    public WebAudienceServerState(WebTile[][] tiles, List<WebAudienceAction> currentActions, Map<String, WebAudienceElementState> elementStates,
                                  String zoomLevel, WebTextState instructions, WebGranulatorConfig granulatorConfig,
                                  WebSpeechSynthConfig speechSynthConfig, WebSpeechSynthState speechSynthState, double stageAlpha, PropertyChangeSupport pcs) {
        this.tiles = tiles;
        this.actions = currentActions;
        this.elementStates = elementStates;
        this.zoomLevel = zoomLevel;
        this.instructions = instructions;
        this.granulatorConfig = granulatorConfig;
        this.speechSynthConfig = speechSynthConfig;
        this.speechSynthState = speechSynthState;
        this.stageAlpha = stageAlpha;
        this.pcs = pcs;
    }

    public void resetDelta() {
        clearActions();
    }

    public WebTile[][] getTiles() {
        return tiles;
    }

    public WebTile getTile(int row, int col) {
        int i = row - 1;
        int j = col - 1;
        if (i < 0 || i >= tiles.length) {
            return null;
        }
        if (j < 0 || j >= tiles[0].length) {
            return null;
        }
        return tiles[i][j];
    }

    public WebTile getTile(String id) {
        return null;
    }

    public void setTiles(WebTile[][] tiles) {
        this.tiles = tiles;
        pcs.firePropertyChange(WEB_OBJ_TILES, WEB_OBJ_TILES, tiles);
    }

    public void setTile(WebTile tile, int i, int j) {
        this.tiles[i][j] = tile;
        pcs.firePropertyChange(WEB_OBJ_TILE, tile.getId(), tile);
    }

    public List<WebAudienceAction> getActions() {
        return actions;
    }

    public void clearActions() {
        actions.clear();
        pcs.firePropertyChange(WEB_OBJ_ACTIONS, WEB_OBJ_ACTIONS, null);
    }

    public void addAction(WebAudienceAction action) {
        if (action == null) {
            return;
        }
        LOG.debug("WebAudienceServerState addAction: {}", action);
        actions.add(action);
        pcs.firePropertyChange(WEB_OBJ_ACTIONS, action.getId(), action);
    }

    public Map<String, WebAudienceElementState> getElementStates() {
        return elementStates;
    }

    public void clearElementStates() {
        elementStates.clear();
        pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, WEB_OBJ_ELEMENT_STATE, null);
    }

    public WebAudienceElementState getElementState(String key) {
        return elementStates.get(key);
    }

    public void addElementState(String key, WebAudienceElementState elementState) {
        WebAudienceElementState old = elementStates.get(key);
        elementStates.put(key, elementState);
        if (!elementState.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, key, elementState);
        }
    }

    public WebAudienceElementState getCentreShape() {
        return elementStates.get(WEB_OBJ_CENTRE_SHAPE);
    }

    public WebAudienceElementState getInnerCircle() {
        return elementStates.get(WEB_OBJ_INNER_CIRCLE);
    }

    public WebAudienceElementState getOuterCircle() {
        return elementStates.get(WEB_OBJ_OUTER_CIRCLE);
    }

    public String getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(String zoomLevel) {
        String old = this.zoomLevel;
        this.zoomLevel = zoomLevel;
        if (!this.zoomLevel.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_ZOOM_LEVEL, WEB_OBJ_ZOOM_LEVEL, zoomLevel);
        }
    }

    public WebTextState getInstructions() {
        return instructions;
    }

    public void setInstructions(String value, int lineNo) {
        if (value == null) {
            value = EMPTY;
        }
        String old = instructions.getLine(lineNo);
        instructions.setLine(value, lineNo);
        if (!value.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_INSTRUCTIONS, instructions.getId(), instructions);
        }
    }

    public void setInstructionsVisible(boolean isVisible) {
        boolean old = this.instructions.isVisible();
        instructions.setVisible(isVisible);
        if (old != isVisible) {
            pcs.firePropertyChange(WEB_OBJ_INSTRUCTIONS, instructions.getId(), instructions);
        }
    }

    public void setInstructionsColour(String colour) {
        String old = instructions.getColour();
        instructions.setColour(colour);
        if (!colour.equals(old)) {
            pcs.firePropertyChange(WEB_OBJ_INSTRUCTIONS, instructions.getId(), instructions);
        }
    }

    public WebGranulatorConfig getGranulatorConfig() {
        return granulatorConfig;
    }

    public WebSpeechSynthConfig getSpeechSynthConfig() {
        return speechSynthConfig;
    }

    public WebSpeechSynthState getSpeechSynthState() {
        return speechSynthState;
    }

    public void setGranulatorConfig(WebGranulatorConfig granulatorConfig) {
        this.granulatorConfig = granulatorConfig;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_GRANULATOR, WEB_OBJ_CONFIG_GRANULATOR, granulatorConfig);
    }

    public void setSpeechSynthConfig(WebSpeechSynthConfig speechSynthConfig) {
        this.speechSynthConfig = speechSynthConfig;
        pcs.firePropertyChange(WEB_OBJ_CONFIG_SPEECH_SYNTH, WEB_OBJ_CONFIG_SPEECH_SYNTH, speechSynthConfig);
    }

    public void setSpeechSynthState(WebSpeechSynthState speechSynthState) {
        this.speechSynthState = speechSynthState;
        pcs.firePropertyChange(WEB_OBJ_STATE_SPEECH_SYNTH, WEB_OBJ_STATE_SPEECH_SYNTH, speechSynthState);
    }

    public double getStageAlpha() {
        return stageAlpha;
    }

    public void setStageAlpha(double stageAlpha) {
        this.stageAlpha = stageAlpha;
        pcs.firePropertyChange(WEB_OBJ_STAGE_ALPHA, WEB_OBJ_STAGE_ALPHA, stageAlpha);
    }
}
