package com.xenaksys.szcore.score.delegate.web.unionrose;

import com.xenaksys.szcore.score.web.audience.*;
import com.xenaksys.szcore.score.web.audience.export.TileExport;
import com.xenaksys.szcore.score.web.audience.export.WebElementStateExport;
import com.xenaksys.szcore.util.ScoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.xenaksys.szcore.Consts.*;

public class UnionRoseWebAudienceStateDeltaTracker extends WebAudienceStateDeltaTracker {
    static final Logger LOG = LoggerFactory.getLogger(UnionRoseWebAudienceStateDeltaTracker.class);

    private final UnionRoseWebAudienceServerState state;

    public UnionRoseWebAudienceStateDeltaTracker(UnionRoseWebAudienceServerState state) {
        this.state = state;
    }

    public void processUpdate(String propertyName, String id, Object newValue) {
        if (propertyName == null) {
            LOG.error("processUpdate: invalid propertyName (null) id: {} newValue: {}", id, newValue);
            return;
        }

        switch (propertyName) {
            case WEB_OBJ_ELEMENT_STATE:
                processElementState(id, newValue);
                break;
            case WEB_OBJ_TILE_TEXT:
                processTileText(id, newValue);
                break;
            case WEB_OBJ_WEB_TEXT:
                processWebText(id, newValue);
                break;
            case WEB_OBJ_CENTRE_SHAPE:
                processElementState(id, newValue);
                break;
            case WEB_OBJ_INNER_CIRCLE:
                processElementState(id, newValue);
                break;
            case WEB_OBJ_OUTER_CIRCLE:
                processElementState(id, newValue);
                break;
            case WEB_OBJ_INSTRUCTIONS:
                processInstructions(id, newValue);
                break;
            case WEB_OBJ_TILES:
                processTiles(id, newValue);
                break;
            case WEB_OBJ_TILE:
                processTile(id, newValue);
                break;
            case WEB_OBJ_ACTIONS:
                processAction(id, newValue);
                break;
            case WEB_OBJ_ZOOM_LEVEL:
                processZoomLevel(id, newValue);
                break;
            case WEB_OBJ_CONFIG_GRANULATOR:
            case WEB_OBJ_CONFIG_GRAIN:
            case WEB_OBJ_CONFIG_GRAIN_ENVELOPE:
            case WEB_OBJ_CONFIG_GRAIN_PANNER:
                processGranulatorConfig(id, newValue);
                break;
            case WEB_OBJ_CONFIG_SPEECH_SYNTH:
                processSpeechSynthConfig(id, newValue);
                break;
            case WEB_OBJ_STATE_SPEECH_SYNTH:
                processSpeechSynthState(id, newValue);
                break;
            case WEB_OBJ_STAGE_ALPHA:
                processStageAlpha(id, newValue);
                break;
            default:
                LOG.error("processUpdate: Unknown propertyName: {}", propertyName);
        }
    }

    private void processZoomLevel(String id, Object newValue) {
        if (id == null || newValue == null) {
            return;
        }
        if (newValue instanceof String) {
            addZoomLevel((String) newValue);
        } else {
            LOG.error("processAction: invalid object type: {}", newValue);
        }
    }

    private void processStageAlpha(String id, Object newValue) {
        if (id == null || newValue == null) {
            return;
        }
        if (newValue instanceof Integer) {
            addStageAlpha(1.0 * (Integer) newValue);
        }
        if (newValue instanceof Double) {
            addStageAlpha((Double) newValue);
        } else {
            LOG.error("processStageAlpha: invalid object type: {}", newValue);
        }
    }

    private void addStageAlpha(Double stageAlpha) {
        if (stageAlpha == null) {
            return;
        }
        addDelta(WEB_OBJ_STAGE_ALPHA, stageAlpha);
    }

    private void addZoomLevel(String zoomLevel) {
        if (zoomLevel == null) {
            return;
        }
        addDelta(WEB_OBJ_ZOOM_LEVEL, zoomLevel);
    }

    private void processTileText(String id, Object newValue) {
        if (id == null) {
            return;
        }
        boolean isTileId = ScoreUtil.isTileId(id);
        if (isTileId) {
            addTile(id, newValue);
            return;
        }
    }

    public void processElementState(String id, Object newValue) {
        if (id == null) {
            return;
        }
        boolean isTileId = ScoreUtil.isTileId(id);
        if (isTileId) {
            addTile(id, newValue);
            return;
        }

        switch (id) {
            case WEB_OBJ_ELEMENT_STATE:
                addElementState(newValue);
                break;
            case WEB_OBJ_CENTRE_SHAPE:
            case WEB_OBJ_INNER_CIRCLE:
            case WEB_OBJ_OUTER_CIRCLE:
                addElementState(id, newValue);
                break;
            default:
                LOG.error("processElementState: Unknown element id: {}", id);
        }
    }

    public void addElementState(String id, Object newValue) {
        WebAudienceElementState elementState = state.getElementState(id);
        if (elementState == null) {
            return;
        }
        WebElementStateExport export = new WebElementStateExport();
        export.populate(elementState);
        addDelta(id, export);
    }

    public void addElementState(Object newValue) {
        if (!(newValue instanceof WebAudienceElementState)) {
            return;
        }
        WebAudienceElementState elementState = (WebAudienceElementState) newValue;
        String id = elementState.getId();

        boolean isTileId = ScoreUtil.isTileId(id);
        if (isTileId) {
            addTile(id, newValue);
            return;
        }

        switch (id) {
            case WEB_OBJ_CENTRE_SHAPE:
            case WEB_OBJ_INNER_CIRCLE:
            case WEB_OBJ_OUTER_CIRCLE:
                addElementState(id, newValue);
                break;
            default:
                LOG.error("addElementState: Unknown element id: {}", id);
        }
    }

    public void processTiles(String id, Object newValue) {
        if (id == null) {
            return;
        }
        if (newValue instanceof WebTile[][]) {
            addTiles((WebTile[][]) newValue);
        } else {
            LOG.error("processTiles: invalid object type: {}", newValue);
        }
    }

    public void addTiles(WebTile[][] tiles) {
        for (WebTile[] value : tiles) {
            for (int j = 0; j < tiles[0].length; j++) {
                WebTile tile = value[j];
                if (tile == null) {
                    continue;
                }
                addTile(tile.getId(), tile);
            }
        }
    }

    public void addTile(String id, Object newValue) {
        if (newValue instanceof WebTile) {
            addTile(id, (WebTile) newValue);
        } else if (newValue instanceof WebAudienceElementState) {
            addTileElementState(id, (WebAudienceElementState) newValue);
        } else if (newValue instanceof WebTileText) {
            addTileText(id, (WebTileText) newValue);
        }
    }

    public void addTileElementState(String id, WebAudienceElementState tileElementState) {
        addTile(id);
    }

    public void addTile(String id) {
        int row = ScoreUtil.getRowFromTileId(id);
        if (row < 0) {
            return;
        }
        int col = ScoreUtil.getColFromTileId(id);
        if (col < 0) {
            return;
        }
        WebTile tile = state.getTile(row, col);
        if (tile == null) {
            return;
        }
        addTile(tile.getId(), tile);
    }

    public void processTile(String id, Object newValue) {
        if (id == null) {
            return;
        }
        if (newValue instanceof WebTile) {
            WebTile tile = (WebTile) newValue;
            addTile(tile.getId(), tile);
        } else {
            LOG.error("processTile: invalid object type: {}", newValue);
        }
    }

    public void addTile(String id, WebTile tile) {
        if (tile == null) {
            return;
        }
        TileExport tileExport = new TileExport();
        tileExport.populate(tile);

        ArrayList<TileExport> tiles = (ArrayList<TileExport>) getDelta().computeIfAbsent(WEB_OBJ_TILES, s -> new ArrayList<TileExport>());
        tiles.remove(tileExport);
        tiles.add(tileExport);
    }

    public void addTileText(String tileId, WebTileText tileText) {
        addTile(tileId);
    }

    @Override
    public WebAudienceServerState getState() {
        return state;
    }

    public UnionRoseWebAudienceServerState getDelegateState() {
        return state;
    }
}
