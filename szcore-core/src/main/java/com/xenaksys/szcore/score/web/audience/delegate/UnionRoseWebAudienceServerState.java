package com.xenaksys.szcore.score.web.audience.delegate;

import com.xenaksys.szcore.score.web.audience.WebAudienceElementState;
import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebTextState;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.*;

public class UnionRoseWebAudienceServerState extends WebAudienceServerState {
    static final Logger LOG = LoggerFactory.getLogger(UnionRoseWebAudienceServerState.class);

    private volatile WebTile[][] tiles;
    private volatile String zoomLevel;
    private final Map<String, WebAudienceElementState> elementStates;
    private volatile double stageAlpha;

    public UnionRoseWebAudienceServerState(WebTile[][] tiles, List<WebAudienceAction> currentActions, Map<String, WebAudienceElementState> elementStates,
                                           String zoomLevel, WebTextState instructions, WebGranulatorConfig granulatorConfig,
                                           WebSpeechSynthConfig speechSynthConfig, WebSpeechSynthState speechSynthState, double stageAlpha, PropertyChangeSupport pcs) {
        super(currentActions, instructions, granulatorConfig, speechSynthConfig, speechSynthState, pcs);
        this.tiles = tiles;
        this.elementStates = elementStates;
        this.zoomLevel = zoomLevel;
        this.stageAlpha = stageAlpha;
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
        getPcs().firePropertyChange(WEB_OBJ_TILES, WEB_OBJ_TILES, tiles);
    }

    public void setTile(WebTile tile, int i, int j) {
        this.tiles[i][j] = tile;
        getPcs().firePropertyChange(WEB_OBJ_TILE, tile.getId(), tile);
    }

    public Map<String, WebAudienceElementState> getElementStates() {
        return elementStates;
    }

    public void clearElementStates() {
        elementStates.clear();
        getPcs().firePropertyChange(WEB_OBJ_ELEMENT_STATE, WEB_OBJ_ELEMENT_STATE, null);
    }

    public WebAudienceElementState getElementState(String key) {
        return elementStates.get(key);
    }

    public void addElementState(String key, WebAudienceElementState elementState) {
        WebAudienceElementState old = elementStates.get(key);
        elementStates.put(key, elementState);
        if (!elementState.equals(old)) {
            getPcs().firePropertyChange(WEB_OBJ_ELEMENT_STATE, key, elementState);
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
            getPcs().firePropertyChange(WEB_OBJ_ZOOM_LEVEL, WEB_OBJ_ZOOM_LEVEL, zoomLevel);
        }
    }

    public double getStageAlpha() {
        return stageAlpha;
    }

    public void setStageAlpha(double stageAlpha) {
        this.stageAlpha = stageAlpha;
        getPcs().firePropertyChange(WEB_OBJ_STAGE_ALPHA, WEB_OBJ_STAGE_ALPHA, stageAlpha);
    }
}
