package com.xenaksys.szcore.score.web.audience.handler;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.web.audience.WebAudienceEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceEventType;
import com.xenaksys.szcore.event.web.audience.WebAudienceInstructionsEvent;
import com.xenaksys.szcore.event.web.audience.WebAudiencePlayTilesEvent;
import com.xenaksys.szcore.event.web.audience.WebAudiencePrecountEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceSelectTilesEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStateUpdateEvent;
import com.xenaksys.szcore.event.web.audience.WebAudienceStopEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.web.audience.WebAudienceChangeListener;
import com.xenaksys.szcore.score.web.audience.WebAudienceElementState;
import com.xenaksys.szcore.score.web.audience.WebAudienceScore;
import com.xenaksys.szcore.score.web.audience.WebAudienceScoreScript;
import com.xenaksys.szcore.score.web.audience.WebAudienceServerState;
import com.xenaksys.szcore.score.web.audience.WebAudienceStateDeltaTracker;
import com.xenaksys.szcore.score.web.audience.WebTextState;
import com.xenaksys.szcore.score.web.audience.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.audience.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.audience.config.WebscoreConfig;
import com.xenaksys.szcore.score.web.audience.config.WebscoreConfigLoader;
import com.xenaksys.szcore.score.web.audience.export.TileExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceInstructionsExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.audience.export.WebAudienceScoreStateExport;
import com.xenaksys.szcore.score.web.audience.export.WebElementStateExport;
import com.xenaksys.szcore.score.web.audience.export.WebGranulatorConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthConfigExport;
import com.xenaksys.szcore.score.web.audience.export.WebSpeechSynthStateExport;
import com.xenaksys.szcore.util.MathUtil;
import com.xenaksys.szcore.util.ScoreUtil;
import com.xenaksys.szcore.web.WebAudienceAction;
import com.xenaksys.szcore.web.WebAudienceActionType;
import com.xenaksys.szcore.web.WebScoreStateType;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_ALL;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_DISPLAY;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_START;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DURATION;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_LOAD_PRESET;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_VALUE;
import static com.xenaksys.szcore.Consts.WEB_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CENTRE_SHAPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INNER_CIRCLE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_OUTER_CIRCLE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE;
import static com.xenaksys.szcore.Consts.WEB_OVERLAYS;
import static com.xenaksys.szcore.Consts.WEB_SELECTED_TILES;
import static com.xenaksys.szcore.Consts.WEB_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;
import static com.xenaksys.szcore.Consts.WEB_TILE_PLAY_PAGE_DURATION_FACTOR;
import static com.xenaksys.szcore.Consts.WEB_ZOOM_DEFAULT;

public class UnionRoseWebAudienceScore extends WebAudienceScore {
    static final Logger LOG = LoggerFactory.getLogger(UnionRoseWebAudienceScore.class);

    public static final Comparator<WebTile> CLICK_COMPARATOR = (t, t1) -> t1.getState().getClickCount() - t.getState().getClickCount();
    private static final long INTERNAL_EVENT_TIME_LIMIT = 1000 * 3;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private WebAudienceStateDeltaTracker stateDeltaTracker;
    private final List<WebTile> tilesAll = new ArrayList<>(64);
    private final List<WebTile> playingTiles = new ArrayList<>(4);
    private final List<WebTile> activeTiles = new ArrayList<>(8);
    private final List<WebTile> playingNextTiles = new ArrayList<>(4);
    private final boolean[] visibleRows = new boolean[8];
    private final boolean[] activeRows = new boolean[8];
    private final Map<String, Integer> tileIdPageIdMap = new HashMap<>();

    private WebscoreConfig webscoreConfig;

    private volatile long lastPlayTilesInternalEventTime = 0L;
    private volatile long lastSelectTilesInternalEventTime = 0L;
    private volatile boolean isSortByClickCount = true;

    public UnionRoseWebAudienceScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        super(scoreProcessor, eventFactory, clock);
    }

    public WebAudienceServerState initState() {
        WebTile[][] tiles = new WebTile[8][8];
        List<WebAudienceAction> currentActions = new ArrayList<>();
        Map<String, WebAudienceElementState> elementStates = new HashMap<>();
        WebTextState instructions = new WebTextState(WEB_OBJ_INSTRUCTIONS, 3);
        WebGranulatorConfig granulatorConfig = createDefaultGranulatorConfig();
        WebSpeechSynthConfig speechSynthConfig = createDefaultSpeechSynthConfig();
        WebSpeechSynthState speechSynthState = createDefaultSpeechSynthState();

        WebAudienceServerState webAudienceServerState =  new WebAudienceServerState(tiles, currentActions, elementStates, WEB_ZOOM_DEFAULT, instructions, granulatorConfig,
                speechSynthConfig, speechSynthState, 1, pcs);

        createWebAudienceStateDeltaTracker(webAudienceServerState);
        pcs.addPropertyChangeListener(new WebAudienceChangeListener(stateDeltaTracker));
        return webAudienceServerState;
    }
    private void createWebAudienceStateDeltaTracker(WebAudienceServerState webAudienceServerState) {
        this.stateDeltaTracker = new WebAudienceStateDeltaTracker(webAudienceServerState);
    }

    public void resetState() {
        getState().clearActions();
        getState().clearElementStates();
        getState().setTiles(new WebTile[8][8]);
        getState().setZoomLevel(WEB_ZOOM_DEFAULT);

        tilesAll.clear();
        playingTiles.clear();
        playingNextTiles.clear();
        tileIdPageIdMap.clear();
        activeTiles.clear();

        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            visibleRows[i] = true;
            activeRows[i] = false;
            int clickCount = 0;

            for (int j = 0; j < 8; j++) {
                int col = j + 1;
                String id = ScoreUtil.createTileId(row, col);
                WebTile t = new WebTile(row, col, id, pcs);
                WebAudienceElementState ts = t.getState();
                ts.setVisible(visibleRows[i]);
                ts.setPlaying(false);
                ts.setActive(activeRows[i]);
                ts.setClickCount(clickCount);
                WebTileText txt = t.getTileText();
                txt.setVisible(false);
                txt.setValue(EMPTY);
                getState().setTile(t, i, j);
                tilesAll.add(t);

                populateTilePageMap(row, col, id);
            }
        }

        getState().addElementState(WEB_OBJ_CENTRE_SHAPE, new WebAudienceElementState(WEB_OBJ_CENTRE_SHAPE, pcs));
        getState().addElementState(WEB_OBJ_INNER_CIRCLE, new WebAudienceElementState(WEB_OBJ_INNER_CIRCLE, pcs));
        getState().addElementState(WEB_OBJ_OUTER_CIRCLE, new WebAudienceElementState(WEB_OBJ_OUTER_CIRCLE, pcs));


        getState().setInstructions("Welcome to", 1);
        getState().setInstructions("<span style='color:blueviolet;'>ZScore</span>", 2);
        getState().setInstructions("awaiting performance start ...", 3);
        getState().setInstructionsVisible(true);

        getState().setGranulatorConfig(createDefaultGranulatorConfig());
        getState().setSpeechSynthConfig(createDefaultSpeechSynthConfig());
        getState().setSpeechSynthState(createDefaultSpeechSynthState());

        reset(WEB_CONFIG_LOAD_PRESET);

        updateServerState();
        pushServerState();
    }

    private void populateTilePageMap(int row, int col, String tileId) {
        if (getScore() == null) {
            return;
        }
        int pageNo = getPageNo(row, col);
        tileIdPageIdMap.put(tileId, pageNo);
    }

    public void reset(int presetNo) {
        try {
            ScriptPreset preset = webscoreConfig.getPreset(presetNo);
            if (preset == null) {
                LOG.info("resetState: Unknown preset: {}", presetNo);
                return;
            }

            Map<String, Object> configs = preset.getConfigs();
            if (!configs.isEmpty()) {
                processPresetConfigs(configs);
            }

            List<String> scripts = preset.getScripts();
            if (!scripts.isEmpty()) {
                runScripts(scripts);
            }
        } catch (Exception e) {
            LOG.error("resetState: Failed to run preset: {}", presetNo, e);
        }
    }

    public void processPresetConfigs(Map<String, Object> configs) {
        for (String key : configs.keySet()) {
            switch (key) {
                case WEB_GRANULATOR:
                    updateGranulatorConfig((Map<String, Object>) configs.get(key));
                    break;
                case WEB_SPEECH_SYNTH:
                    updateSpeechSynthConfig((Map<String, Object>) configs.get(key));
                    break;
                default:
                    LOG.info("processPresetConfigs: unknown key: {}", key);
            }
        }
    }

    private void updateGranulatorConfig(Map<String, Object> conf) {
        getGranulatorConfig().update(conf);
    }

    private void updateSpeechSynthConfig(Map<String, Object> conf) {
        getSpeechSynthConfig().update(conf);
    }

    private void runScript(String script) {
        if (getJsEngine() == null) {
            return;
        }

        try {
            LOG.debug("runScript: {}", script);
            getJsEngine().eval(script);
        } catch (ScriptException e) {
            LOG.error("Failed to execute script: {}", script, e);
        }
    }

    private int getPageNo(int row, int col) {

        int pageNo = (row - 1) * 8 + col;
        if (getScore() == null || webscoreConfig == null) {
            return pageNo;
        }

        int configPageNo = webscoreConfig.getPageNo(row, col);
        if (configPageNo < 0) {
            return pageNo;
        }

        return configPageNo;
    }

    public void loadConfig(String configDir) {
        if (configDir == null) {
            return;
        }
        try {
            webscoreConfig = WebscoreConfigLoader.load(configDir);
        } catch (Exception e) {
            LOG.error("Failed to load WebAudienceScore Presets", e);
        }
    }

    public void deactivateRows(int[] rows) {
        for (int row : rows) {
            deactivateRow(row);
        }
    }

    public void deactivateRow(int row) {
        WebTile[][] tiles = getState().getTiles();
        if (row < 1 || row > tiles.length) {
            LOG.warn("deactivateTiles: invalid row: " + row);
        }

        int i = row - 1;
        for (int j = 0; j < 8; j++) {
            WebTile t = tiles[i][j];
            WebAudienceElementState ts = t.getState();
            ts.setActive(false);
            ts.setPlaying(false);
            ts.setPlayingNext(false);
            ts.setPlayed(true);
            ts.setVisible(false);

            visibleRows[i] = false;
            activeRows[i] = false;

            WebTileText txt = t.getTileText();
            txt.setVisible(false);
        }
        recalcActiveTiles();
    }

    private void recalcActiveTiles() {
        activeTiles.clear();
        for (WebTile tile : tilesAll) {
            if (tile.getState().isActive()) {
                activeTiles.add(tile);
            }
        }
    }

    public void setSelectedElement(String elementId, boolean isSelected) {
        LOG.debug("setSelectedElement: Received elementId: {} isSelected: {}", elementId, isSelected);
        if (ScoreUtil.isTileId(elementId)) {
            WebTile tile = getTile(elementId);
            if (tile == null) {
                return;
            }
            if (!isInActiveRow(tile)) {
                LOG.info("setSelectedElement: Selected tile {} is not in active row", tile.getId());
                return;
            }

            WebAudienceElementState state = tile.getState();
            if (state.isPlaying() || state.isPlayingNext() || state.isPlayed() || !state.isVisible()) {
                return;
            }

            if (isSelected) {
                state.setSelected(true);
                state.incrementClickCount();
                LOG.debug("setSelectedElement: Received elementId: {} tile: {} click count: {} isSelected: true", elementId, tile.getId(), state.getClickCount());
            } else {
                if (state.getClickCount() <= 0) {
                    state.setSelected(false);
                }
                state.decrementClickCount();
                LOG.debug("setSelectedElement: Received elementId: {} tile: {} click count: {} isSelected: {}", elementId, tile.getId(), state.getClickCount(), state.isSelected());
            }
            if (isSortByClickCount) {
                activeTiles.sort(CLICK_COMPARATOR);
            } else {
                LOG.debug("setSelectedElement: sort by click disabled, using natural order");
            }
        }
    }

    public boolean isInActiveRow(WebTile tile) {
        return activeRows[tile.getRow() - 1];
    }

    public boolean isInVisibleRow(WebTile tile) {
        return visibleRows[tile.getRow() - 1];
    }

    public List<WebTile> getTopSelectedTiles(int quantity, boolean isIncludePlayed) {
        List<WebTile> topSelected = new ArrayList<>();
        int count = 1;
        for (WebTile t : activeTiles) {
            boolean isPlayed = t.getState().isPlayed() || t.getState().isPlaying();
            if (isPlayed && !isIncludePlayed) {
                continue;
            }
            if (count <= quantity) {
                topSelected.add(t);
                count++;
            } else {
                break;
            }
        }
        return topSelected;
    }

    public List<Integer> prepareNextTilesToPlay(int pageQuantity) {
        List<WebTile> topTiles = null;
        if (!playingNextTiles.isEmpty()) {
            topTiles = new ArrayList<>(playingNextTiles);
        } else {
            topTiles = getTopSelectedTiles(pageQuantity, false);
        }
        List<Integer> pageIds = new ArrayList<>(topTiles.size());
        List<String> tileIds = new ArrayList<>(topTiles.size());
        if (topTiles.size() < pageQuantity) {
            LOG.warn("prepareNextTilesToPlay: not enough pages retrieved: {} expected: {}", topTiles.size(), pageQuantity);
            ArrayList<String> nextTileIds = calculateNextTilesToPlay();
            LOG.warn("prepareNextTilesToPlay: calculated tiles to play: {}", nextTileIds);
            for (String tileId : nextTileIds) {
                if (topTiles.size() < pageQuantity) {
                    topTiles.add(getTile(tileId));
                } else {
                    break;
                }
            }
        }
        for (WebTile tile : topTiles) {
            String tileId = tile.getId();
            Integer pageNo = tileIdPageIdMap.get(tileId);
            if (pageNo == null) {
                LOG.error("prepareNextTilesToPlay: Failed to find pageId for tile id: {}", tileId);
            } else {
                pageIds.add(pageNo);
                tileIds.add(tileId);
            }
            LOG.debug("prepareNextTilesToPlay: Found pageId: {} for tile id: {}", pageNo, tileId);
        }

        WebAudienceSelectTilesEvent playTilesEvent = getEventFactory().createWebAudienceSelectTilesEvent(tileIds, getClock().getSystemTimeMillis());
        processWebAudienceEvent(playTilesEvent);

        return pageIds;
    }

    public void resetClickCounts() {
        for (WebTile t : tilesAll) {
            t.getState().setClickCount(0);
        }
    }

    public void setVisibleRows(int[] rows) {
        WebTile[][] tiles = getState().getTiles();
        TIntList tintRows = new TIntArrayList(rows);
        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            visibleRows[i] = tintRows.contains(row);
            for (int j = 0; j < 8; j++) {
                WebTile t = tiles[i][j];
                WebAudienceElementState ts = t.getState();
                ts.setVisible(visibleRows[i]);
            }
        }
    }

    public void setZoomLevel(String zoomLevel) {
        getState().setZoomLevel(zoomLevel);
    }

    public void setInstructions(String l1, String l2, String l3) {
        setInstructions(l1, l2, l3, WEB_TEXT_BACKGROUND_COLOUR, true);
    }

    public void setInstructions(String l1, String l2, String l3, boolean isVisible) {
        setInstructions(l1, l2, l3, WEB_TEXT_BACKGROUND_COLOUR, isVisible);
    }

    public void setInstructions(boolean isVisible) {
        setInstructions(EMPTY, EMPTY, EMPTY, WEB_TEXT_BACKGROUND_COLOUR, isVisible);
    }

    public void setInstructions(String l1, String l2, String l3, String colour, boolean isVisible) {
        getState().setInstructions(l1, 1);
        getState().setInstructions(l2, 2);
        getState().setInstructions(l3, 3);
        getState().setInstructionsColour(colour);
        getState().setInstructionsVisible(isVisible);
    }

    public void setVisible(String[] elementIds, boolean isVisible) {
        LOG.debug("setVisible: {}", Arrays.toString(elementIds));
        for (String elementId : elementIds) {
            WebAudienceElementState elementState = getState().getElementState(elementId);
            if (elementState != null) {
                elementState.setVisible(isVisible);
            }
        }
    }

    public void setTileTexts(String[] tileIds, String[] values) {
        LOG.debug("setTileTexts: {}  {}", Arrays.toString(tileIds), Arrays.toString(values));

        for (int i = 0; i < tileIds.length; i++) {
            String tileId = tileIds[i];
            String value;
            if (values.length > i) {
                value = values[i];
            } else {
                value = values[values.length - 1];
                LOG.warn("setTileTexts: Invalid number of entries, using value {}", value);
            }
            setTileText(tileId, value);
        }
    }

    public void resetPlayingTiles() {
        List<String> targets = new ArrayList<>();
        for (WebTile t : playingTiles) {
            t.getState().setPlaying(false);
            t.getState().setPlayed(true);
            t.getState().setVisible(false);
            WebTileText txt = t.getTileText();
            txt.setVisible(false);
            targets.add(t.getId());
        }
//        setAction(WEB_ACTION_ID_RESET, WebAudienceActionType.ROTATE.name(), targets.toArray(new String[0]));
        playingTiles.clear();
    }

    public boolean isPlayTilesInternalEventTime() {
        long now = System.currentTimeMillis();
        long diff = now - (lastPlayTilesInternalEventTime + INTERNAL_EVENT_TIME_LIMIT);
        return diff > 0;
    }

    public boolean isSelectTilesInternalEventTime() {
        long now = System.currentTimeMillis();
        long diff = now - (lastSelectTilesInternalEventTime + INTERNAL_EVENT_TIME_LIMIT);
        return diff > 0;
    }

    public boolean playNextTilesInternal(WebAudiencePlayTilesEvent event) {
        if (!isPlayTilesInternalEventTime()) {
            return false;
        }

        if (playingNextTiles.isEmpty()) {
            if (playingTiles.isEmpty()) {
                LOG.warn("playNextTiles: Can not find tiles to play, both playingTiles and playingNextTiles are empty");
                return false;
            }
            ArrayList<String> tileIds = calculateNextTilesToPlay();
            if (!tileIds.isEmpty()) {
                playTiles(tileIds.toArray(new String[0]));
            }
        } else {
            String[] tileIds = new String[playingNextTiles.size()];
            for (int i = 0; i < tileIds.length; i++) {
                WebTile tile = playingNextTiles.get(i);
                tileIds[i] = tile.getId();
            }
            playTiles(tileIds);
        }

        lastPlayTilesInternalEventTime = System.currentTimeMillis();
        return true;
    }

    public ArrayList<String> calculateNextTilesToPlay() {
        ArrayList<String> tileIds = new ArrayList<>(playingTiles.size());
        for (WebTile tile : playingTiles) {
            WebTile next = getNextTileToPlay(tile);
            if (next != null) {
                tileIds.add(next.getId());
            }
        }
        return tileIds;
    }

    public boolean selectNextTilesInternal(WebAudienceSelectTilesEvent event) {
        if (!isSelectTilesInternalEventTime()) {
            return false;
        }
        List<String> tileIds = event.getTileIds();
        setPlayingNextTiles(tileIds);
        lastSelectTilesInternalEventTime = System.currentTimeMillis();
        return true;
    }

    public WebTile getNextTileToPlay(WebTile tile) {
        int col = tile.getColumn() - 1;
        int row = tile.getRow() - 1;
        WebTile[][] tiles = getState().getTiles();
        for (int j = col + 1; j < tiles[row].length; j++) {
            WebTile next = tiles[row][++col];
            if (!next.getState().isPlayed()) {
                return next;
            }
        }
        return null;
    }

    public void playTiles(String[] tileIds) {
        LOG.debug("playTiles: {}", Arrays.toString(tileIds));
        resetPlayingNextTiles();
        if (tileIds == null || tileIds.length == 0) {
            return;
        }
        setPlayingTiles(tileIds);
        setPlayTileActions(tileIds);
    }

    public void setPlayTileActions(String[] tileIds) {
        String first = tileIds[0];
        Integer pageNo = tileIdPageIdMap.get(first);
        getTempPageId().setPageNo(pageNo);
        Page page = getScoreProcessor().getScore().getPage(getTempPageId());

        double duration = 0.0;
        if (page != null) {
            long durationMs = page.getDurationMs();
            duration = MathUtil.roundTo2DecimalPlaces(durationMs / 1000.0);
            LOG.debug("setPlayTileActions: calculated duration: {} for page: {}", duration, page.getId());
            duration = MathUtil.roundTo2DecimalPlaces(duration * WEB_TILE_PLAY_PAGE_DURATION_FACTOR);
        }

        Map<String, Object> params = new HashMap<>(2);
        params.put(WEB_CONFIG_DURATION, duration);
        params.put(WEB_CONFIG_VALUE, 0);
        setAction(WEB_ACTION_ID_START, WebAudienceActionType.ALPHA.name(), tileIds, params);
    }

    public void setPlayingTiles(String[] tileIds) {
        LOG.debug("setPlayingTiles: {}", Arrays.toString(tileIds));
        resetPlayingTiles();
        for (String tileId : tileIds) {
            setPlayingTile(tileId);
        }
    }

    public void setPlayingTile(String tileId) {
        WebTile t = getTile(tileId);
        if (t == null) {
            LOG.error("setPlayedTile: invalid tileId: {}", tileId);
            return;
        }
        t.getState().setPlaying(true);
        playingTiles.add(t);
    }

    public void setTileText(String tileId, String value) {
        WebTile t = getTile(tileId);
        if (t == null) {
            LOG.error("setTileText: invalid tileId: {}", tileId);
            return;
        }
        t.setText(value);
    }

    public void resetPlayingNextTiles() {
        for (WebTile t : playingNextTiles) {
            t.getState().setPlayingNext(false);
        }
        playingNextTiles.clear();
    }

    public void setPlayingNextTiles(List<String> tileIds) {
        LOG.debug("setPlayingNextTiles: {}", tileIds);
        resetPlayingNextTiles();
        for (String tileId : tileIds) {
            setPlayingNextTile(tileId);
        }
    }

    public void setPlayingNextTile(String tileId) {
        WebTile t = getTile(tileId);
        if (t == null) {
            LOG.error("setPlayedNextTile: invalid tileId: {}", tileId);
            return;
        }
        t.getState().setPlayingNext(true);
        playingNextTiles.add(t);
    }

    public void setActiveRows(int[] rows) {
        setActiveRows(rows, true);
    }

    public void setActiveRows(int[] rows, boolean isSortByClickCount) {
        LOG.debug("setActiveRows: {}", Arrays.toString(rows));
        this.isSortByClickCount = isSortByClickCount;
        WebTile[][] tiles = getState().getTiles();
        TIntList tintRows = new TIntArrayList(rows);
        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            activeRows[i] = tintRows.contains(row);
            for (int j = 0; j < 8; j++) {
                WebTile t = tiles[i][j];
                WebAudienceElementState ts = t.getState();
                ts.setActive(activeRows[i]);
            }
        }
        recalcActiveTiles();
    }

    public void resetStateDelta() {
        getState().resetDelta();
        stateDeltaTracker.reset();
    }

    public void resetSelectedTiles() {
        String[] target = {WEB_SELECTED_TILES};
        setAction(WEB_ACTION_ID_ALL, WebAudienceActionType.RESET.name(), target, null);
    }

    public void removeOverlays() {
        String[] target = {WEB_OVERLAYS};
        setAction(WEB_ACTION_ID_DISPLAY, WebAudienceActionType.DEACTIVATE.name(), target, null);
    }

    private WebTile getTile(String tileId) {
        try {
            WebTile[][] tiles = getState().getTiles();
            int start = tileId.indexOf(Consts.WEB_TILE_PREFIX) + 1;
            int end = tileId.indexOf(Consts.WEB_ELEMENT_NAME_DELIMITER);
            String s = tileId.substring(start, end);
            int row = Integer.parseInt(s);
            start = end + 1;
            s = tileId.substring(start);
            int column = Integer.parseInt(s);

            int i = row - 1;
            int j = column - 1;
            if (i < 0 || i >= tiles.length) {
                LOG.error("parseTileId: invalid row: {}", i);
                return null;
            }
            if (j < 0 || j >= tiles[0].length) {
                LOG.error("parseTileId: invalid col: {}", i);
                return null;
            }

            return tiles[i][j];
        } catch (Exception e) {
            LOG.error("parseTileId: Failed to parse tileId: {}", tileId, e);
        }
        return null;
    }

    public WebAudienceScoreStateExport exportState() {
        WebAudienceServerState state = getState();
        WebTile[][] tiles = state.getTiles();
        TileExport[][] tes = new TileExport[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                WebTile t = tiles[i][j];
                TileExport te = new TileExport();
                te.populate(t);
                tes[i][j] = te;
            }
        }

        WebElementStateExport centreShape = new WebElementStateExport();
        centreShape.populate(state.getElementState(WEB_OBJ_CENTRE_SHAPE));

        WebElementStateExport innerCircle = new WebElementStateExport();
        innerCircle.populate(state.getElementState(WEB_OBJ_INNER_CIRCLE));

        WebElementStateExport outerCircle = new WebElementStateExport();
        outerCircle.populate(state.getElementState(WEB_OBJ_OUTER_CIRCLE));

        WebAudienceInstructionsExport instructions = new WebAudienceInstructionsExport();
        instructions.populate(state.getInstructions());

        WebGranulatorConfigExport granulatorConfig = new WebGranulatorConfigExport();
        granulatorConfig.populate(state.getGranulatorConfig());

        WebSpeechSynthConfigExport speechSynthConfigExport = new WebSpeechSynthConfigExport();
        speechSynthConfigExport.populate(state.getSpeechSynthConfig());

        WebSpeechSynthStateExport speechSynthStateExport = new WebSpeechSynthStateExport();
        speechSynthStateExport.populate(state.getSpeechSynthState());

        List<WebAudienceAction> actions = state.getActions();
        LOG.debug("WebAudienceScoreStateExport sending actions: {}", actions);

        return new WebAudienceScoreStateExport(tes, actions, centreShape, innerCircle, outerCircle, state.getZoomLevel(),
                instructions, granulatorConfig, speechSynthConfigExport, speechSynthStateExport, state.getStageAlpha());
    }

    public void updateServerState() {
        try {
            getScoreProcessor().onWebAudienceStateChange(exportState());
        } catch (Exception e) {
            LOG.error("Failed to process updateServerState", e);
        }
    }

    public void updateServerStateDelta() {
        try {
            if (stateDeltaTracker.hasChanges()) {
                getScoreProcessor().onWebAudienceStateDeltaChange(stateDeltaTracker.getDeltaExport());
            }
        } catch (Exception e) {
            LOG.error("Failed to process updateServerState", e);
        }
    }

    public void processWebAudienceEvent(WebAudienceEvent event) {
        LOG.debug("processWebScoreEvent: execute event: {}", event);
        WebAudienceEventType type = event.getWebAudienceEventType();
        try {
            resetStateDelta();
            boolean isSendStateUpdate = true;
            switch (type) {
                case INSTRUCTIONS:
                    isSendStateUpdate = processInstructionsEvent((WebAudienceInstructionsEvent) event);
                    break;
                case PRECOUNT:
                    isSendStateUpdate = processPrecountEvent((WebAudiencePrecountEvent) event);
                    break;
                case STOP:
                    isSendStateUpdate = processStopAll((WebAudienceStopEvent) event);
                    break;
                case PLAY_TILES:
                    isSendStateUpdate = playNextTilesInternal((WebAudiencePlayTilesEvent) event);
                    break;
                case SELECT_TILES:
                    isSendStateUpdate = selectNextTilesInternal((WebAudienceSelectTilesEvent) event);
                    break;
                case STATE_UPDATE:
                    isSendStateUpdate = updateState((WebAudienceStateUpdateEvent) event);
                    break;
                case RESET:
                case SCRIPT:
                    List<WebAudienceScoreScript> jsScripts = event.getScripts();
                    if (jsScripts == null) {
                        return;
                    }
                    runWebScoreScripts(jsScripts);
                    break;
                default:
                    LOG.warn("processWebScoreEvent: Ignoring event {}", type);
            }
            boolean isClickCountAdded = addClickCounts();
            isSendStateUpdate = isSendStateUpdate || isClickCountAdded;
            if (isSendStateUpdate) {
                updateServerStateAndPush();
            }
        } catch (Exception e) {
            LOG.error("Failed to evaluate script", e);
        }
    }

    public boolean updateState(WebAudienceStateUpdateEvent event) {
        WebScoreStateType propType = event.getPropertyType();
        Object value = event.getPropertyValue();

        switch (propType) {
            case STAGE_ALPHA:
                getState().setStageAlpha((double) value);
                return true;
            default:
                LOG.error("updateState: unknown property type: {}", propType);
        }

        return false;
    }

    private boolean addClickCounts() {
        boolean isUpdate = false;
        for (WebTile tile : activeTiles) {
            WebAudienceElementState tileState = tile.getState();
            if (!tileState.isPlayed() && tileState.getClickCount() > 0) {
                pcs.firePropertyChange(WEB_OBJ_TILE, tile.getId(), tile);
                if (!isUpdate) {
                    isUpdate = true;
                }
            }
        }
        return isUpdate;
    }

    private boolean processInstructionsEvent(WebAudienceInstructionsEvent event) {
        setInstructions(event.getL1(), event.getL2(), event.getL3(), event.isVisible());
        return true;
    }

    public WebAudienceScoreStateDeltaExport getStateDeltaExport() {
        return stateDeltaTracker.getDeltaExport();
    }

    private double getDouble(Object value) {
        double v;
        if (value instanceof String) {
            v = Double.parseDouble((String) value);
        } else {
            v = (Double) value;
        }
        return v;
    }

    private int getInt(Object value) {
        int v;
        if (value instanceof String) {
            v = Integer.parseInt((String) value);
        } else {
            v = (Integer) value;
        }
        return v;
    }

    private String getString(Object value) {
        String v;
        if (value instanceof String) {
            v = (String) value;
        } else {
            v = value.toString();
        }
        return v;
    }

    private boolean getBoolean(Object value) {
        boolean v;
        if (value instanceof String) {
            v = Boolean.parseBoolean((String) value);
        } else {
            v = (Boolean) value;
        }
        return v;
    }
}