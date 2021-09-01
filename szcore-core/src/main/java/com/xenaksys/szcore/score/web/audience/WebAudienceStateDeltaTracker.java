package com.xenaksys.szcore.score.web.audience;

import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.delegate.WebTile;
import com.xenaksys.szcore.score.web.audience.delegate.WebTileText;
import com.xenaksys.szcore.score.web.audience.export.TileExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceActionExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceInstructionsExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebElementStateExport;
import com.xenaksys.szcore.score.web.audience.export.WebGranulatorConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthStateExport;
import com.xenaksys.szcore.util.ScoreUtil;
import com.xenaksys.szcore.web.WebAudienceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CENTRE_SHAPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRAIN_PANNER;
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
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE_TEXT;
import static com.xenaksys.szcore.Consts.WEB_OBJ_WEB_TEXT;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ZOOM_LEVEL;

public class WebAudienceStateDeltaTracker {
    static final Logger LOG = LoggerFactory.getLogger(WebAudienceStateDeltaTracker.class);

    private final HashMap<String, Object> delta = new HashMap<>();
    private final WebAudienceServerState state;

    public WebAudienceStateDeltaTracker(WebAudienceServerState state) {
        this.state = state;
    }

    public void addDelta(String key, Object value) {
        delta.put(key, value);
    }

    public WebAudienceScoreStateDeltaExport getDeltaExport() {
        return new WebAudienceScoreStateDeltaExport(delta);
    }

    public void reset() {
        delta.clear();
    }

    public boolean hasChanges() {
        return !delta.isEmpty();
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

    private void processGranulatorConfig(String id, Object newValue) {
        addGranulatorConfig();
    }

    private void addGranulatorConfig() {
        WebGranulatorConfig config = state.getGranulatorConfig();
        WebGranulatorConfigExport export = new WebGranulatorConfigExport();
        export.populate(config);
        delta.put(WEB_OBJ_CONFIG_GRANULATOR, export);
    }

    private void processSpeechSynthConfig(String id, Object newValue) {
        addSpeechSynthConfig();
    }

    private void addSpeechSynthConfig() {
        WebSpeechSynthConfig config = state.getSpeechSynthConfig();
        WebSpeechSynthConfigExport export = new WebSpeechSynthConfigExport();
        export.populate(config);
        delta.put(WEB_OBJ_CONFIG_SPEECH_SYNTH, export);
    }

    private void processSpeechSynthState(String id, Object newValue) {
        addSpeechSynthState();
    }

    private void addSpeechSynthState() {
        WebSpeechSynthState config = state.getSpeechSynthState();
        WebSpeechSynthStateExport export = new WebSpeechSynthStateExport();
        export.populate(config);
        delta.put(WEB_OBJ_STATE_SPEECH_SYNTH, export);
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
        delta.put(WEB_OBJ_STAGE_ALPHA, stageAlpha);
    }

    private void processAction(String id, Object newValue) {
        if (id == null || newValue == null) {
            return;
        }
        if (newValue instanceof WebAudienceAction) {
            addAction((WebAudienceAction) newValue);
        } else {
            LOG.error("processAction: invalid object type: {}", newValue);
        }
    }

    private void addZoomLevel(String zoomLevel) {
        if (zoomLevel == null) {
            return;
        }
        delta.put(WEB_OBJ_ZOOM_LEVEL, zoomLevel);
    }

    private void addAction(WebAudienceAction action) {
        if (action == null) {
            return;
        }
        ArrayList<WebAudienceActionExport> actions = (ArrayList<WebAudienceActionExport>) delta.computeIfAbsent(WEB_OBJ_ACTIONS, s -> new ArrayList<TileExport>());
        WebAudienceActionExport actionExport = new WebAudienceActionExport();
        actionExport.populate(action);
        actions.add(actionExport);
    }

    private void processInstructions(String id, Object newValue) {
        processWebText(id, newValue);
    }

    private void processWebText(String id, Object newValue) {
        if (id == null) {
            return;
        }
        switch (id) {
            case WEB_OBJ_INSTRUCTIONS:
                addInstructions((WebTextState) newValue);
                break;
            default:
                LOG.error("processWebText: Invalid id: {}", id);
        }
    }

    public void addInstructions(WebTextState instructions) {
        if (instructions == null) {
            return;
        }
        WebAudienceInstructionsExport textExport = new WebAudienceInstructionsExport();
        textExport.populate(instructions);
        delta.put(WEB_OBJ_INSTRUCTIONS, textExport);
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
        delta.put(id, export);
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
            addTileElementState(id,(WebAudienceElementState)newValue);
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

        ArrayList<TileExport> tiles = (ArrayList<TileExport>) delta.computeIfAbsent(WEB_OBJ_TILES, s -> new ArrayList<TileExport>());
        tiles.remove(tileExport);
        tiles.add(tileExport);
    }

    public void addTileText(String tileId, WebTileText tileText) {
        addTile(tileId);
    }
}
