package com.xenaksys.szcore.score.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.event.WebScoreEventType;
import com.xenaksys.szcore.event.WebScoreInstructionsEvent;
import com.xenaksys.szcore.event.WebScorePlayTilesEvent;
import com.xenaksys.szcore.event.WebScorePrecountEvent;
import com.xenaksys.szcore.event.WebScoreSelectTilesEvent;
import com.xenaksys.szcore.event.WebScoreStopEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.MutablePageId;
import com.xenaksys.szcore.score.PannerDistanceModel;
import com.xenaksys.szcore.score.PanningModel;
import com.xenaksys.szcore.score.web.config.WebEnvelopeConfig;
import com.xenaksys.szcore.score.web.config.WebGrainConfig;
import com.xenaksys.szcore.score.web.config.WebGranulatorConfig;
import com.xenaksys.szcore.score.web.config.WebPannerConfig;
import com.xenaksys.szcore.score.web.config.WebSpeechSynthConfig;
import com.xenaksys.szcore.score.web.config.WebSpeechSynthState;
import com.xenaksys.szcore.score.web.config.WebscoreConfig;
import com.xenaksys.szcore.score.web.config.WebscoreConfigLoader;
import com.xenaksys.szcore.score.web.export.TileExport;
import com.xenaksys.szcore.score.web.export.WebElementStateExport;
import com.xenaksys.szcore.score.web.export.WebGranulatorConfigExport;
import com.xenaksys.szcore.score.web.export.WebInstructionsExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateDeltaExport;
import com.xenaksys.szcore.score.web.export.WebScoreStateExport;
import com.xenaksys.szcore.score.web.export.WebSpeechSynthConfigExport;
import com.xenaksys.szcore.score.web.export.WebSpeechSynthStateExport;
import com.xenaksys.szcore.util.MathUtil;
import com.xenaksys.szcore.util.ScoreUtil;
import com.xenaksys.szcore.web.WebAction;
import com.xenaksys.szcore.web.WebActionType;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_CONFIG;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_PLAY;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_RAMP_LINEAR;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_RAMP_SIN;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_START;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_STATE;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_STOP;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_AMPLITUDE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ATTACK_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_BUFFER_POSITION_PLAY_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DECAY_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DISTANCE_MODEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DURATION;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_END_VALUE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_FREQUENCY;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_INTERRUPT_TIMEOUT_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_INTERRUPT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_LANG;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MASTER_GAIN_VAL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_GRAINS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PAN_ANGLE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PITCH_RATE_RANGE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_UTTERANCES;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNING_MODEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PARAM_NAME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_DURATION_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PLAY_START_OFFSET_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_RELEASE_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SIZE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_IS_INTERRUPT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_TEXT;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SPEECH_VOICE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SUSTAIN_LEVEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SUSTAIN_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_TIME_OFFSET_STEPS_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_UTTERANCE_TIMEOUT_SEC;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_VOLUME;
import static com.xenaksys.szcore.Consts.WEB_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ACTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CENTRE_SHAPE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_OBJ_CONFIG_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ELEMENT_STATE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INNER_CIRCLE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_INSTRUCTIONS;
import static com.xenaksys.szcore.Consts.WEB_OBJ_OUTER_CIRCLE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_STATE_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILES;
import static com.xenaksys.szcore.Consts.WEB_OBJ_TILE_TEXT;
import static com.xenaksys.szcore.Consts.WEB_OBJ_ZOOM_LEVEL;
import static com.xenaksys.szcore.Consts.WEB_SCORE_ID;
import static com.xenaksys.szcore.Consts.WEB_SPEECH_SYNTH;
import static com.xenaksys.szcore.Consts.WEB_TARGET_ALL;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;
import static com.xenaksys.szcore.Consts.WEB_TILE_PLAY_PAGE_DURATION_FACTOR;
import static com.xenaksys.szcore.Consts.WEB_ZOOM_DEFAULT;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    public static final Comparator<Tile> CLICK_COMPARATOR = (t, t1) -> t1.getState().getClickCount() - t.getState().getClickCount();
    private static final long INTERNAL_EVENT_TIME_LIMIT = 1000 * 3;

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Score score;
    private final Clock clock;
    private final WebScoreServerState state;
    private final WebScoreStateDeltaTracker stateDeltaTracker;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final List<Tile> tilesAll = new ArrayList<>(64);
    private final List<Tile> playingTiles = new ArrayList<>(4);
    private final List<Tile> activeTiles = new ArrayList<>(8);
    private final List<Tile> playingNextTiles = new ArrayList<>(4);
    private final boolean[] visibleRows = new boolean[8];
    private final boolean[] activeRows = new boolean[8];
    private final Map<String, Integer> tileIdPageIdMap = new HashMap<>();
    private final Map<BeatId, List<WebScoreScript>> beatScripts = new HashMap<>();
    private final Map<BeatId, List<WebScoreScript>> beatResetScripts = new HashMap<>();

    private final ScriptEngineManager factory = new ScriptEngineManager();
    private final ScriptEngine jsEngine = factory.getEngineByName("nashorn");
    private WebscoreConfig webscoreConfig;

    private final MutablePageId tempPageId;
    private volatile long lastPlayTilesInternalEventTime = 0L;
    private volatile long lastSelectTilesInternalEventTime = 0L;

    public WebScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.score = scoreProcessor.getScore();
        this.tempPageId = createTempPage();
        this.state = initState();
        this.stateDeltaTracker = new WebScoreStateDeltaTracker(state);
    }

    private WebScoreServerState initState() {
        pcs.addPropertyChangeListener(new WebScoreChangeListener());

        Tile[][] tiles = new Tile[8][8];
        List<WebAction> currentActions = new ArrayList<>();
        Map<String, WebElementState> elementStates = new HashMap<>();
        WebTextState instructions = new WebTextState(WEB_OBJ_INSTRUCTIONS, 3);
        WebGranulatorConfig granulatorConfig = createDefaultGranulatorConfig();
        WebSpeechSynthConfig speechSynthConfig = createDefaultSpeechSynthConfig();
        WebSpeechSynthState speechSynthState = createDefaultSpeechSynthState();

        return new WebScoreServerState(tiles, currentActions, elementStates, WEB_ZOOM_DEFAULT, instructions, granulatorConfig,
                speechSynthConfig, speechSynthState);
    }

    private MutablePageId createTempPage() {
        int pageNo = 0;
        if (score == null) {
            return null;
        }
        Collection<Instrument> instruments = score.getInstruments();
        Instrument instrument = null;
        if (instruments != null || !instruments.isEmpty()) {
            instrument = instruments.iterator().next();
        }
        return new MutablePageId(pageNo, instrument.getId(), score.getId());
    }

    public void resetState() {
        state.clearActions();
        state.clearElementStates();
        state.setTiles(new Tile[8][8]);
        state.setZoomLevel(WEB_ZOOM_DEFAULT);

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
                Tile t = new Tile(row, col, id);
                WebElementState ts = t.getState();
                ts.setVisible(visibleRows[i]);
                ts.setPlaying(false);
                ts.setActive(activeRows[i]);
                ts.setClickCount(clickCount);
                TileText txt = t.getTileText();
                txt.setVisible(false);
                txt.setValue(EMPTY);
                state.setTile(t, i, j);
                tilesAll.add(t);

                populateTilePageMap(row, col, id);
            }
        }

        state.addElementState(WEB_OBJ_CENTRE_SHAPE, new WebElementState(WEB_OBJ_CENTRE_SHAPE));
        state.addElementState(WEB_OBJ_INNER_CIRCLE, new WebElementState(WEB_OBJ_INNER_CIRCLE));
        state.addElementState(WEB_OBJ_OUTER_CIRCLE, new WebElementState(WEB_OBJ_OUTER_CIRCLE));


        state.setInstructions("Welcome to", 1);
        state.setInstructions("<span style='color:blueviolet;'>ZScore</span>", 2);
        state.setInstructions("awaiting performance start ...", 3);
        state.setInstructionsVisible(true);

        state.setGranulatorConfig(createDefaultGranulatorConfig());
        state.setSpeechSynthConfig(createDefaultSpeechSynthConfig());
        state.setSpeechSynthState(createDefaultSpeechSynthState());

        updateServerState();
    }

    private void populateTilePageMap(int row, int col, String tileId) {
        if (score == null) {
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

            runScripts(preset.getScripts());
        } catch (Exception e) {
            LOG.error("resetState: Failed to run preset: {}", presetNo, e);
        }
    }

    public void runScripts(List<String> scripts) {
        for (String js : scripts) {
            runScript(js);
        }
    }

    public void runWebScoreScripts(List<WebScoreScript> scripts) {
        for (WebScoreScript js : scripts) {
            runScript(js.getContent());
        }
    }

    private void runScript(String script) {
        if (jsEngine == null) {
            return;
        }

        try {
            LOG.debug("runScript: {}", script);
            jsEngine.eval(script);
        } catch (ScriptException e) {
            LOG.error("Failed to execute script: {}", script, e);
        }
    }

    private int getPageNo(int row, int col) {

        int pageNo = (row - 1) * 8 + col;
        if (score == null || webscoreConfig == null) {
            return pageNo;
        }

        int configPageNo = webscoreConfig.getPageNo(row, col);
        if (configPageNo < 0) {
            return pageNo;
        }

        return configPageNo;
    }

    public void init(String configDir) {
        loadConfig(configDir);
        jsEngine.put(WEB_SCORE_ID, this);
        resetState();
    }

    private void loadConfig(String configDir) {
        if (configDir == null) {
            return;
        }
        try {
            webscoreConfig = WebscoreConfigLoader.load(configDir);
        } catch (Exception e) {
            LOG.error("Failed to load WebScore Presets", e);
        }
    }

    public void deactivateRows(int[] rows) {
        for (int row : rows) {
            deactivateRow(row);
        }
    }

    public void deactivateRow(int row) {
        Tile[][] tiles = state.getTiles();
        if (row < 1 || row > tiles.length) {
            LOG.warn("deactivateTiles: invalid row: " + row);
        }

        int i = row - 1;
        for (int j = 0; j < 8; j++) {
            Tile t = tiles[i][j];
            WebElementState ts = t.getState();
            ts.setActive(false);
            ts.setPlaying(false);
            ts.setPlayingNext(false);
            ts.setPlayed(true);
            ts.setVisible(false);

            visibleRows[i] = false;
            activeRows[i] = false;

            TileText txt = t.getTileText();
            txt.setVisible(false);
        }
        recalcActiveTiles();
    }

    private void recalcActiveTiles() {
        activeTiles.clear();
        for (Tile tile : tilesAll) {
            if (tile.getState().isActive()) {
                activeTiles.add(tile);
            }
        }
    }

    public void setSelectedElement(String elementId, boolean isSelected) {
        LOG.info("setSelectedElement: Received elementId: {} isSelected: {}", elementId, isSelected);
        if (ScoreUtil.isTileId(elementId)) {
            Tile tile = getTile(elementId);
            if (tile == null) {
                return;
            }
            if (!isInActiveRow(tile)) {
                LOG.info("setSelectedElement: Selected tile {} is not in active row", tile.getId());
                return;
            }

            WebElementState state = tile.getState();
            if (state.isPlaying() || state.isPlayingNext() || state.isPlayed() || !state.isVisible()) {
                return;
            }

            if (isSelected) {
                state.setSelected(true);
                state.incrementClickCount();
                LOG.info("setSelectedElement: Received elementId: {} tile: {} click count: {} isSelected: true", elementId, tile.getId(), state.getClickCount());
            } else {
                if (state.getClickCount() <= 0) {
                    state.setSelected(false);
                }
                state.decrementClickCount();
                LOG.info("setSelectedElement: Received elementId: {} tile: {} click count: {} isSelected: {}", elementId, tile.getId(), state.getClickCount(), state.isSelected());
            }
            activeTiles.sort(CLICK_COMPARATOR);
        }
    }

    public boolean isInActiveRow(Tile tile) {
        return activeRows[tile.getRow() - 1];
    }

    public boolean isInVisibleRow(Tile tile) {
        return visibleRows[tile.getRow() - 1];
    }

    public List<Tile> getTopSelectedTiles(int quantity, boolean isIncludePlayed) {
        List<Tile> topSelected = new ArrayList<>();
        int count = 1;
        for (Tile t : activeTiles) {
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
        List<Tile> topTiles = null;
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
        for (Tile tile : topTiles) {
            String tileId = tile.getId();
            Integer pageNo = tileIdPageIdMap.get(tileId);
            if (pageNo == null) {
                LOG.error("prepareNextTilesToPlay: Failed to find pageId for tile id: {}", tileId);
            } else {
                pageIds.add(pageNo);
                tileIds.add(tileId);
            }
            LOG.info("prepareNextTilesToPlay: Found pageId: {} for tile id: {}", pageNo, tileId);
        }

        WebScoreSelectTilesEvent playTilesEvent = eventFactory.createWebScoreSelectTilesEvent(tileIds, clock.getSystemTimeMillis());
        processWebScoreEvent(playTilesEvent);

        return pageIds;
    }

    public void resetClickCounts() {
        for (Tile t : tilesAll) {
            t.getState().setClickCount(0);
        }
    }

    public void startScore() {
        LOG.info("startScore: ");
    }

    public void updateServerStateAndPush() {
        updateServerState();
        pushServerState();
    }

    public void setVisibleRows(int[] rows) {
        Tile[][] tiles = state.getTiles();
        TIntList tintRows = new TIntArrayList(rows);
        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            visibleRows[i] = tintRows.contains(row);
            for (int j = 0; j < 8; j++) {
                Tile t = tiles[i][j];
                WebElementState ts = t.getState();
                ts.setVisible(visibleRows[i]);
            }
        }
    }

    public void setZoomLevel(String zoomLevel) {
        state.setZoomLevel(zoomLevel);
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
        state.setInstructions(l1, 1);
        state.setInstructions(l2, 2);
        state.setInstructions(l3, 3);
        state.setInstructionsColour(colour);
        state.setInstructionsVisible(isVisible);
    }

    public void setVisible(String[] elementIds, boolean isVisible) {
        LOG.info("setVisible: {}", Arrays.toString(elementIds));
        for (String elementId : elementIds) {
            WebElementState elementState = state.getElementState(elementId);
            if (elementState != null) {
                elementState.setVisible(isVisible);
            }
        }
    }

    public void setTileTexts(String[] tileIds, String[] values) {
        LOG.info("setTileTexts: {}  {}", Arrays.toString(tileIds), Arrays.toString(values));

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
        for (Tile t : playingTiles) {
            t.getState().setPlaying(false);
            t.getState().setPlayed(true);
            t.getState().setVisible(false);
            TileText txt = t.getTileText();
            txt.setVisible(false);
            targets.add(t.getId());
        }
//        setAction(WEB_ACTION_ID_RESET, WebActionType.ROTATE.name(), targets.toArray(new String[0]));
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

    public boolean playNextTilesInternal(WebScorePlayTilesEvent event) {
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
                Tile tile = playingNextTiles.get(i);
                tileIds[i] = tile.getId();
            }
            playTiles(tileIds);
        }

        lastPlayTilesInternalEventTime = System.currentTimeMillis();
        return true;
    }

    public ArrayList<String> calculateNextTilesToPlay() {
        ArrayList<String> tileIds = new ArrayList<>(playingTiles.size());
        for (Tile tile : playingTiles) {
            Tile next = getNextTileToPlay(tile);
            if (next != null) {
                tileIds.add(next.getId());
            }
        }
        return tileIds;
    }

    public boolean selectNextTilesInternal(WebScoreSelectTilesEvent event) {
        if (!isSelectTilesInternalEventTime()) {
            return false;
        }
        List<String> tileIds = event.getTileIds();
        setPlayingNextTiles(tileIds);
        lastSelectTilesInternalEventTime = System.currentTimeMillis();
        return true;
    }

    public Tile getNextTileToPlay(Tile tile) {
        int col = tile.getColumn() - 1;
        int row = tile.getRow() - 1;
        Tile[][] tiles = state.getTiles();
        for (int j = col + 1; j < tiles[row].length; j++) {
            Tile next = tiles[row][++col];
            if (!next.getState().isPlayed()) {
                return next;
            }
        }
        return null;
    }

    public void playTiles(String[] tileIds) {
        LOG.info("playTiles: {}", Arrays.toString(tileIds));
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
        tempPageId.setPageNo(pageNo);
        Page page = scoreProcessor.getScore().getPage(tempPageId);

        double duration = 0.0;
        if (page != null) {
            long durationMs = page.getDurationMs();
            duration = MathUtil.roundTo2DecimalPlaces(durationMs / 1000.0);
            LOG.info("setPlayTileActions: calculated duration: {} for page: {}", duration, page.getId());
            duration = MathUtil.roundTo2DecimalPlaces(duration * WEB_TILE_PLAY_PAGE_DURATION_FACTOR);
        }

        Map<String, Object> params = new HashMap<>(1);
        params.put(WEB_CONFIG_DURATION, duration);
        setAction(WEB_ACTION_ID_START, WebActionType.DISSOLVE.name(), tileIds, params);
    }

    public void setPlayingTiles(String[] tileIds) {
        LOG.info("setPlayingTiles: {}", Arrays.toString(tileIds));
        resetPlayingTiles();
        for (String tileId : tileIds) {
            setPlayingTile(tileId);
        }
    }

    public void setPlayingTile(String tileId) {
        Tile t = getTile(tileId);
        if (t == null) {
            LOG.error("setPlayedTile: invalid tileId: {}", tileId);
            return;
        }
        t.getState().setPlaying(true);
        playingTiles.add(t);
    }

    public void setTileText(String tileId, String value) {
        Tile t = getTile(tileId);
        if (t == null) {
            LOG.error("setTileText: invalid tileId: {}", tileId);
            return;
        }
        t.setText(value);
    }

    public void resetPlayingNextTiles() {
        for (Tile t : playingNextTiles) {
            t.getState().setPlayingNext(false);
        }
        playingNextTiles.clear();
    }

    public void setPlayingNextTiles(List<String> tileIds) {
        LOG.info("setPlayingNextTiles: {}", tileIds);
        resetPlayingNextTiles();
        for (String tileId : tileIds) {
            setPlayingNextTile(tileId);
        }
    }

    public void setPlayingNextTile(String tileId) {
        Tile t = getTile(tileId);
        if (t == null) {
            LOG.error("setPlayedNextTile: invalid tileId: {}", tileId);
            return;
        }
        t.getState().setPlayingNext(true);
        playingNextTiles.add(t);
    }

    public void setActiveRows(int[] rows) {
        LOG.info("setActiveRows: {}", Arrays.toString(rows));
        Tile[][] tiles = state.getTiles();
        TIntList tintRows = new TIntArrayList(rows);
        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            activeRows[i] = tintRows.contains(row);
            for (int j = 0; j < 8; j++) {
                Tile t = tiles[i][j];
                WebElementState ts = t.getState();
                ts.setActive(activeRows[i]);
            }
        }
        recalcActiveTiles();
    }

    public void resetStateDelta() {
        state.resetDelta();
        stateDeltaTracker.reset();
    }

    public void setAction(String actionId, String type, String[] targetIds) {
        setAction(actionId, type, targetIds, new HashMap<>());
    }

    public void setAction(String actionId, String type, String[] targetIds, Map<String, Object> params) {
        LOG.info("setAction: {} target: {}", actionId, Arrays.toString(targetIds));
        try {
            WebAction action = createAction(actionId, type, targetIds, params);
            state.addAction(action);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to setAction id: {} type: {}", actionId, type);
        }
    }

    public WebAction createAction(String actionId, String type, String[] targetIds, Map<String, Object> params) {
        WebActionType t = WebActionType.valueOf(type.toUpperCase());
        return new WebAction(actionId, t, Arrays.asList(targetIds), params);
    }

    public boolean processStopAll(WebScoreStopEvent event) {
        sendStopAll();
        return true;
    }

    public void sendStopAll() {
        String[] target = {WEB_TARGET_ALL};
        setAction(WEB_ACTION_ID_STOP, WebActionType.STOP.name(), target, null);
    }

    public void sendGranulatorConfig() {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = state.getGranulatorConfig().toJsMap();
        setAction(WEB_ACTION_ID_CONFIG, WebActionType.AUDIO.name(), target, params);
    }

    public void playGranulator() {
        String[] target = {WEB_GRANULATOR};
        WebAction action = createAction(WEB_ACTION_ID_PLAY, WebActionType.AUDIO.name(), target, null);
        state.addAction(action);
    }

    public void stopGranulator() {
        String[] target = {WEB_GRANULATOR};
        setAction(WEB_ACTION_ID_STOP, WebActionType.AUDIO.name(), target, null);
    }

    public void validateGranulatorConfig() {
        state.getGranulatorConfig().validate();
    }

    public void granulatorRampLinear(String paramName, Object endValue, int durationMs) {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = state.getGranulatorConfig().toJsMap();
        params.put(WEB_CONFIG_PARAM_NAME, paramName);
        params.put(WEB_CONFIG_END_VALUE, endValue);
        params.put(WEB_CONFIG_DURATION, durationMs);
        setAction(WEB_ACTION_ID_RAMP_LINEAR, WebActionType.AUDIO.name(), target, null);
    }

    public void granulatorRampSin(String paramName, Double amplitude, Double frequency, int durationMs) {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = state.getGranulatorConfig().toJsMap();
        params.put(WEB_CONFIG_PARAM_NAME, paramName);
        params.put(WEB_CONFIG_AMPLITUDE, amplitude);
        params.put(WEB_CONFIG_FREQUENCY, frequency);
        params.put(WEB_CONFIG_DURATION, durationMs);
        setAction(WEB_ACTION_ID_RAMP_SIN, WebActionType.AUDIO.name(), target, null);
    }

    public void sendSpeechSynthConfig() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthConfig().toJsMap();
        setAction(WEB_ACTION_ID_CONFIG, WebActionType.AUDIO.name(), target, params);
    }

    public void validateSpeechSynthConfig() {
        state.getSpeechSynthConfig().validate();
    }

    public void sendSpeechSynthState() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        setAction(WEB_ACTION_ID_STATE, WebActionType.AUDIO.name(), target, params);
    }

    public void validateSpeechSynthState() {
        state.getSpeechSynthState().validate();
    }

    public void enableSpeechSynth() {
        state.getSpeechSynthState().setPlaySpeechSynthOnClick(true);
        sendSpeechSynthState();
    }

    public void disableSpeechSynth() {
        state.getSpeechSynthState().setPlaySpeechSynthOnClick(false);
        sendSpeechSynthState();
    }

    public void setSpeechText(String text) {
        state.getSpeechSynthState().setSpeechText(text);
        sendSpeechSynthState();
    }

    public void setSpeechVoice(String voice) {
        state.getSpeechSynthState().setSpeechVoice(voice);
        sendSpeechSynthState();
    }

    public void setSpeechInterrupt(boolean isInterrupt) {
        state.getSpeechSynthState().setSpeechIsInterrupt(isInterrupt);
        sendSpeechSynthState();
    }

    public void speak() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        setAction(WEB_ACTION_ID_PLAY, WebActionType.AUDIO.name(), target, params);
    }

    public void speak(String text) {
        if (text != null && !text.isEmpty()) {
            LOG.warn("speak: Invalid text to speak: {}", text);
        }
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        params.put(WEB_CONFIG_SPEECH_TEXT, text);
        setAction(WEB_ACTION_ID_PLAY, WebActionType.AUDIO.name(), target, params);
    }

    public void stopSpeech() {
        String[] target = {WEB_SPEECH_SYNTH};
        Map<String, Object> params = state.getSpeechSynthState().toJsMap();
        setAction(WEB_ACTION_ID_STOP, WebActionType.AUDIO.name(), target, params);
    }

    public void setSpeechSynthConfigParam(String name, Object value) {
        try {
            LOG.info("setSpeechSynthConfigParam: setting config param: {} value: {}", name, value);
            WebSpeechSynthConfig config = state.getSpeechSynthConfig();
            switch (name) {
                case WEB_CONFIG_VOLUME:
                    try {
                        config.setVolume(getDouble(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set volume", e);
                    }
                    break;
                case WEB_CONFIG_PITCH:
                    try {
                        config.setPitch(getDouble(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set pitch", e);
                    }
                    break;
                case WEB_CONFIG_RATE:
                    try {
                        config.setRate(getDouble(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set rate", e);
                    }
                    break;
                case WEB_CONFIG_LANG:
                    try {
                        config.setLang(getString(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set lang", e);
                    }
                    break;
                case WEB_CONFIG_MAX_VOICE_LOAD_ATTEMPTS:
                    try {
                        config.setMaxVoiceLoadAttempts(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set MaxVoiceLoadAttempts", e);
                    }
                    break;
                case WEB_CONFIG_MAX_UTTERANCES:
                    try {
                        config.setMaxUtterances(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set MaxUtterances", e);
                    }
                    break;
                case WEB_CONFIG_UTTERANCE_TIMEOUT_SEC:
                    try {
                        config.setUtteranceTimeoutSec(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set UtteranceTimeoutSec", e);
                    }
                    break;
                case WEB_CONFIG_IS_INTERRUPT:
                    try {
                        config.setInterrupt(getBoolean(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set isInterrupt", e);
                    }
                    break;
                case WEB_CONFIG_INTERRUPT_TIMEOUT_MS:
                    try {
                        config.setInterruptTimeout(getInt(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthConfigParam: Failed to set InterruptTimeout", e);
                    }
                    break;
                default:
                    LOG.error("setSpeechSynthConfigParam: Invalid Speech Synth Config Param: {}", name);
            }
        } catch (Exception e) {
            LOG.error("setSpeechSynthConfigParam: failed to set speech synth config for name: {}, value: {}", name, value, e);
        }
    }

    public void setSpeechSynthConfig(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setSpeechSynthConfig: invalid params");
            return;
        }
        for (String param : params.keySet()) {
            Object value = params.get(param);
            setSpeechSynthConfigParam(param, value);
        }
        state.getSpeechSynthConfig().validate();
        LOG.info("setSpeechSynthConfig: new config: {}", state.getSpeechSynthConfig());
    }

    public WebSpeechSynthConfig getSpeechSynthConfig() {
        return state.getSpeechSynthConfig();
    }

    public void setSpeechSynthStateParam(String name, Object value) {
        try {
            LOG.info("setSpeechSynthStateParam: setting config param: {} value: {}", name, value);
            WebSpeechSynthState speechSynthState = state.getSpeechSynthState();
            switch (name) {
                case WEB_CONFIG_IS_PLAY_SPEECH_ON_CLICK:
                    try {
                        speechSynthState.setPlaySpeechSynthOnClick(getBoolean(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set PlaySpeechSynthOnClick", e);
                    }
                    break;
                case WEB_CONFIG_SPEECH_TEXT:
                    try {
                        speechSynthState.setSpeechText(getString(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set SpeechText", e);
                    }
                    break;
                case WEB_CONFIG_SPEECH_VOICE:
                    try {
                        speechSynthState.setSpeechVoice(getString(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set SpeechVoice", e);
                    }
                    break;
                case WEB_CONFIG_SPEECH_IS_INTERRUPT:
                    try {
                        speechSynthState.setSpeechIsInterrupt(getBoolean(value));
                    } catch (Exception e) {
                        LOG.error("setSpeechSynthStateParam: Failed to set SpeechIsInterrupt", e);
                    }
                    break;
                default:
                    LOG.error("setSpeechSynthStateParam: Invalid Speech Synth Config Param: {}", name);
            }
        } catch (Exception e) {
            LOG.error("setSpeechSynthStateParam: failed to set speech synth state for name: {}, value: {}", name, value, e);
        }
    }

    public void setSpeechSynthState(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setSpeechSynthState: invalid params");
            return;
        }
        for (String param : params.keySet()) {
            Object value = params.get(param);
            setSpeechSynthStateParam(param, value);
        }
        state.getSpeechSynthState().validate();
        LOG.info("setSpeechSynthState: new config: {}", state.getSpeechSynthState());
    }

    public void setGranulatorConfigParam(String name, Object value) {
        try {
            LOG.info("setGranulatorConfig: setting config param: {} value: {}", name, value);
            String[] names = name.split("\\.");
            if (names.length == 1) {
                setGranulatorBaseConfig(names[0], value);
                return;
            }
            if (names.length != 2) {
                LOG.error("setGranulatorConfig: invalid param names {}, will not use", Arrays.toString(names));
                return;
            }
            String l1 = names[0];
            String l2 = names[1];
            switch (l1) {
                case WEB_CONFIG_GRAIN:
                    setGrainConfig(l2, value);
                    break;
                case WEB_CONFIG_ENVELOPE:
                    setGranulatorEnvelopeConfig(l2, value);
                    break;
                case WEB_CONFIG_PANNER:
                    setGranulatorPannerConfig(l2, value);
                    break;
            }
        } catch (Exception e) {
            LOG.error("setGranulatorConfigParam: failed to set granulator config for name: {}, value: {}", name, value, e);
        }
    }

    public void setGranulatorBaseConfig(String name, Object value) {
        LOG.info("setGranulatorBaseConfig: setting config param: {} value: {}", name, value);
        WebGranulatorConfig config = state.getGranulatorConfig();
        switch (name) {
            case WEB_CONFIG_MASTER_GAIN_VAL:
                try {
                    config.setMasterGainVal(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set MasterGainVal", e);
                }
                break;
            case WEB_CONFIG_PLAY_DURATION_SEC:
                try {
                    config.setPlayDurationSec(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set PlayDurationSec", e);
                }
                break;
            case WEB_CONFIG_PLAY_START_OFFSET_SEC:
                try {
                    config.setPlayStartOffsetSec(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set PlayStartOffsetSec", e);
                }
                break;
            case WEB_CONFIG_MAX_GRAINS:
                try {
                    config.setMaxGrains(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set MaxGrains", e);
                }
                break;
            case WEB_CONFIG_BUFFER_POSITION_PLAY_RATE:
                try {
                    config.setBufferPositionPlayRate(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set BufferPositionPlayRate", e);
                }
                break;
            case WEB_CONFIG_AUDIO_STOP_TOLERANCE_MS:
                try {
                    config.setAudioStopToleranceMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorBaseConfig: Failed to set BufferPositionPlayRate", e);
                }
                break;
            default:
                LOG.error("setGranulatorBaseConfig: Invalid Speech Synth Config Param: {}", name);
        }
    }

    public void setGranulatorConfig(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setGranulatorConfig: invalid params");
            return;
        }
        for (String param : params.keySet()) {
            Object value = params.get(param);
            setGranulatorConfigParam(param, value);
        }
        state.getGranulatorConfig().validate();
        LOG.info("setGranulatorConfig: new config: {}", state.getGranulatorConfig());
    }

    public WebGranulatorConfig getGranulatorConfig() {
        return state.getGranulatorConfig();
    }

    private void setGranulatorPannerConfig(String name, Object value) {
        WebPannerConfig config = state.getGranulatorConfig().getPanner();
        switch (name) {
            case WEB_CONFIG_IS_USE_PANNER:
                try {
                    config.setUsePanner(getBoolean(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set isUsePanner", e);
                }
                break;
            case WEB_CONFIG_PANNING_MODEL:
                try {
                    String v = PanningModel.fromName(getString(value)).getName();
                    config.setPanningModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set panningModel", e);
                }
                break;
            case WEB_CONFIG_DISTANCE_MODEL:
                try {
                    String v = PannerDistanceModel.fromName(getString(value)).getName();
                    config.setDistanceModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set distanceModel", e);
                }
                break;
            case WEB_CONFIG_MAX_PAN_ANGLE:
                try {
                    config.setMaxPanAngle(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set maxPanAngle", e);
                }
                break;
            default:
                LOG.error("setGranulatorPannerConfig: Invalid Granulator Panner param: {}", name);
        }
    }

    private void setGrainConfig(String name, Object value) {
        WebGrainConfig config = state.getGranulatorConfig().getGrain();
        switch (name) {
            case WEB_CONFIG_SIZE_MS:
                try {
                    config.setSizeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set grain size", e);
                }
                break;
            case WEB_CONFIG_PITCH_RATE:
                try {
                    config.setPitchRate(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set pitchRate", e);
                }
                break;
            case WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS:
                try {
                    config.setMaxPositionOffsetRangeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPositionOffsetRangeMs", e);
                }
                break;
            case WEB_CONFIG_MAX_PITCH_RATE_RANGE:
                try {
                    config.setMaxPitchRateRange(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPitchRateRange", e);
                }
                break;
            case WEB_CONFIG_TIME_OFFSET_STEPS_MS:
                try {
                    config.setTimeOffsetStepMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set timeOffsetStepMs", e);
                }
                break;
            default:
                LOG.error("setGrainConfig: Invalid Grain Config Param: {}", name);
        }
    }

    private void setGranulatorEnvelopeConfig(String name, Object value) {
        WebEnvelopeConfig config = state.getGranulatorConfig().getEnvelope();
        switch (name) {
            case WEB_CONFIG_ATTACK_TIME:
                try {
                    config.setAttackTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set attackTime", e);
                }
                break;
            case WEB_CONFIG_DECAY_TIME:
                try {
                    config.setDecayTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set decayTime", e);
                }
                break;
            case WEB_CONFIG_SUSTAIN_TIME:
                try {
                    config.setSustainTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainTime", e);
                }
                break;
            case WEB_CONFIG_RELEASE_TIME:
                try {
                    config.setReleaseTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set releaseTime", e);
                }
                break;
            case WEB_CONFIG_SUSTAIN_LEVEL:
                try {
                    config.setSustainLevel(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainLevel", e);
                }
                break;
            default:
                LOG.error("setGranulatorEnvelopeConfig: Invalid Grain Config Param: {}", name);
        }
    }

    private Tile getTile(String tileId) {
        try {
            Tile[][] tiles = state.getTiles();
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

    public WebScoreStateExport createExportDelta() {
        return null;
    }

    public WebScoreStateExport exportState() {
        Tile[][] tiles = state.getTiles();
        TileExport[][] tes = new TileExport[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile t = tiles[i][j];
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

        WebInstructionsExport instructions = new WebInstructionsExport();
        instructions.populate(state.getInstructions());

        WebGranulatorConfigExport granulatorConfig = new WebGranulatorConfigExport();
        granulatorConfig.populate(state.getGranulatorConfig());

        WebSpeechSynthConfigExport speechSynthConfigExport = new WebSpeechSynthConfigExport();
        speechSynthConfigExport.populate(state.getSpeechSynthConfig());

        WebSpeechSynthStateExport speechSynthStateExport = new WebSpeechSynthStateExport();
        speechSynthStateExport.populate(state.getSpeechSynthState());

        List<WebAction> actions = state.getActions();
        LOG.info("WebScoreStateExport sending actions: {}", actions);

        return new WebScoreStateExport(tes, actions, centreShape, innerCircle, outerCircle, state.getZoomLevel(),
                instructions, granulatorConfig, speechSynthConfigExport, speechSynthStateExport);
    }

    public void updateServerState() {
        try {
            scoreProcessor.onWebScoreStateChange(exportState());
        } catch (Exception e) {
            LOG.error("Failed to process updateServerState", e);
        }
    }

    public void pushServerState() {
        sendOutgoingWebEvent(OutgoingWebEventType.PUSH_SERVER_STATE);
    }

    public void pushServerStateDelta() {
        sendOutgoingWebEvent(OutgoingWebEventType.PUSH_SERVER_STATE_DELTA);
    }

    public void sendOutgoingWebEvent(OutgoingWebEventType eventType) {
        try {
            long creationTime = clock.getSystemTimeMillis();
            OutgoingWebEvent outgoingWebEvent = eventFactory.createOutgoingWebEvent(null, null, eventType, creationTime);
            scoreProcessor.onOutgoingWebEvent(outgoingWebEvent);
        } catch (Exception e) {
            LOG.error("Failed to process sendOutgoingWebEvent, type: {}", eventType, e);
        }
    }

    public void processWebScoreEvent(WebScoreEvent event) {
        LOG.info("processWebScoreEvent: execute event: {}", event);
        WebScoreEventType type = event.getWebScoreEventType();
        try {
            resetStateDelta();
            boolean isSendStateUpdate = true;
            switch (type) {
                case INSTRUCTIONS:
                    isSendStateUpdate = processInstructionsEvent((WebScoreInstructionsEvent) event);
                    break;
                case PRECOUNT:
                    isSendStateUpdate = processPrecountEvent((WebScorePrecountEvent) event);
                    break;
                case STOP:
                    isSendStateUpdate = processStopAll((WebScoreStopEvent) event);
                    break;
                case PLAY_TILES:
                    isSendStateUpdate = playNextTilesInternal((WebScorePlayTilesEvent) event);
                    break;
                case SELECT_TILES:
                    isSendStateUpdate = selectNextTilesInternal((WebScoreSelectTilesEvent) event);
                    break;
                case RESET:
                case SCRIPT:
                    List<WebScoreScript> jsScripts = event.getScripts();
                    if (jsScripts == null) {
                        return;
                    }
                    runWebScoreScripts(jsScripts);
                    break;
                default:
                    LOG.warn("processWebScoreEvent: Ignoring event {}", type);
            }
            if (isSendStateUpdate) {
                updateServerStateAndPush();
            }
        } catch (Exception e) {
            LOG.error("Failed to evaluate script", e);
        }
    }

    public void addBeatScript(BeatId beatId, WebScoreScript webScoreScript) {
        if (beatId == null || webScoreScript == null) {
            return;
        }

        List<WebScoreScript> scripts = beatScripts.computeIfAbsent(beatId, k -> new ArrayList<>());

        if (webScoreScript.isResetPoint()) {
            addResetScript(beatId, webScoreScript);
            if (!webScoreScript.isResetOnly()) {
                scripts.add(webScoreScript);
            }
        } else {
            scripts.add(webScoreScript);
        }
    }

    public void addResetScript(BeatId beatId, WebScoreScript webScoreScript) {
        if (beatId == null || webScoreScript == null) {
            return;
        }

        List<WebScoreScript> scripts = beatResetScripts.computeIfAbsent(beatId, k -> new ArrayList<>());
        scripts.add(webScoreScript);
    }

    public List<WebScoreScript> getBeatScripts(BeatId beatId) {
        return beatScripts.get(beatId);
    }

    public List<WebScoreScript> getBeatResetScripts(BeatId beatId) {
        if (beatResetScripts.containsKey(beatId)) {
            return beatResetScripts.get(beatId);
        }

        ArrayList<BeatId> beats = new ArrayList<>(beatResetScripts.keySet());
        Collections.sort(beats);
        int outIndex = Collections.binarySearch(beats, beatId);
        int idx = outIndex;
        if (outIndex < 0) {
            idx += 1;
            idx *= (-1);
            idx -= 1;
        }
        BeatId outId = beats.get(idx);
        return beatResetScripts.get(outId);
    }

    private WebSpeechSynthConfig createDefaultSpeechSynthConfig() {
        WebSpeechSynthConfig speechSynthConfig = new WebSpeechSynthConfig(pcs);
        return speechSynthConfig;
    }

    private WebSpeechSynthState createDefaultSpeechSynthState() {
        WebSpeechSynthState webSpeechSynthState = new WebSpeechSynthState(pcs);
        return webSpeechSynthState;
    }

    private WebGranulatorConfig createDefaultGranulatorConfig() {
        WebGranulatorConfig granulatorConfig = new WebGranulatorConfig(pcs);
        return granulatorConfig;
    }

    private boolean processInstructionsEvent(WebScoreInstructionsEvent event) {
        setInstructions(event.getL1(), event.getL2(), event.getL3(), event.isVisible());
        return true;
    }

    public boolean processPrecountEvent(WebScorePrecountEvent event) {
        int count = event.getCount();
        boolean isOn = event.getIsOn();
        int colourId = event.getColourId();

        LOG.info("processPrecountEvent: count: {}, isOn: {}, colId: {}", count, isOn, colourId);
        if (count == 1 && isOn && colourId == 4) {
            reset(0);
            return true;
        } else if (count == 1 && isOn && colourId == 3) {
            reset(-1);
            return true;
        }

        return false;
    }

    public WebScoreStateDeltaExport getStateDeltaExport() {
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

    public class Tile {
        private final String id;
        private final int row;
        private final int column;
        private final WebElementState state;
        private final TileText tileText;

        public Tile(int row, int column, String id) {
            this.id = id;
            this.row = row;
            this.column = column;
            this.state = new WebElementState(id);
            this.tileText = new TileText(EMPTY, false, this);
        }

        public WebElementState getState() {
            return state;
        }

        public void setState(WebElementState newState) {
            WebElementState old = new WebElementState(this.state.id);
            this.state.copyTo(old);
            newState.copyTo(this.state);
            if (!this.state.equals(old)) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, this.id, this);
            }
        }

        public void setText(TileText txt) {
            TileText old = new TileText(this.tileText.getValue(), this.tileText.isVisible(), this.tileText.getParent());
            txt.copyTo(this.tileText);
            if (!this.tileText.equals(old)) {
                pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, this.id, this);
            }
        }

        public void setText(String txt) {
            TileText old = new TileText(this.tileText.getValue(), this.tileText.isVisible(), this.tileText.getParent());
            this.tileText.setValue(txt);
            this.tileText.setVisible(true);
            if (!this.tileText.equals(old)) {
                pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, this.id, this);
            }
        }

        public String getId() {
            return id;
        }

        public int getRow() {
            return row;
        }

        public int getRowIndex() {
            return row - 1;
        }

        public int getColumn() {
            return column;
        }

        public int getColumnIndex() {
            return column - 1;
        }

        public TileText getTileText() {
            return tileText;
        }

        public void copyTo(Tile other) {
            state.copyTo(other.getState());
            tileText.copyTo(other.getTileText());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Tile)) return false;
            Tile tile = (Tile) o;
            return getId().equals(tile.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }

        @Override
        public String toString() {
            return "Tile{" +
                    "id='" + id + '\'' +
                    ", row=" + row +
                    ", column=" + column +
                    ", state=" + state +
                    '}';
        }
    }

    public class TileText {
        private String value;
        private boolean isVisible;
        private final Tile parent;

        public TileText(String value, boolean isVisible, Tile parent) {
            this.value = value;
            this.isVisible = isVisible;
            this.parent = parent;
        }

        public String getValue() {
            return value;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setValue(String value) {
            String old = this.value;
            this.value = value;
            if (!this.value.equals(old)) {
                pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, parent.getId(), parent);
            }
        }

        public void setVisible(boolean visible) {
            boolean old = this.isVisible;
            this.isVisible = visible;
            if (old != this.isVisible) {
                pcs.firePropertyChange(WEB_OBJ_TILE_TEXT, parent.getId(), parent);
            }
        }

        public Tile getParent() {
            return parent;
        }

        public void copyTo(TileText other) {
            other.setVisible(isVisible());
            other.setValue(getValue());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TileText tileText = (TileText) o;
            return isVisible == tileText.isVisible && Objects.equals(value, tileText.value) && Objects.equals(parent, tileText.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, isVisible, parent);
        }
    }

    public class WebElementState {
        private final String id;
        private boolean isActive;
        private boolean isPlaying;
        private boolean isPlayingNext;
        private boolean isPlayed;
        private boolean isVisible;
        private boolean isSelected;
        private int clickCount;

        public WebElementState(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public int getClickCount() {
            return clickCount;
        }

        public void setClickCount(int clickCount) {
            int old = this.clickCount;
            this.clickCount = clickCount;
            if (old != this.clickCount) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public void incrementClickCount() {
            this.clickCount++;
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }

        public void decrementClickCount() {
            this.clickCount--;
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            boolean old = this.isActive;
            this.isActive = active;
            if (old != this.isActive) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setVisible(boolean visible) {
            boolean old = this.isVisible;
            this.isVisible = visible;
            if (old != this.isVisible) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            boolean old = this.isPlaying;
            this.isPlaying = playing;
            if (old != this.isPlaying) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            boolean old = this.isSelected;
            this.isSelected = selected;
            if (old != this.isSelected) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public boolean isPlayingNext() {
            return isPlayingNext;
        }

        public void setPlayingNext(boolean playingNext) {
            boolean old = this.isPlayingNext;
            this.isPlayingNext = playingNext;
            if (old != this.isPlayingNext) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public boolean isPlayed() {
            return isPlayed;
        }

        public void setPlayed(boolean played) {
            boolean old = this.isPlayed;
            this.isPlayed = played;
            if (old != this.isPlayed) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, id, this);
            }
        }

        public void copyTo(WebElementState other) {
            other.setClickCount(getClickCount());
            other.setActive(isActive());
            other.setVisible(isVisible());
            other.setPlaying(isPlaying());
            other.setPlayingNext(isPlayingNext());
            other.setPlayed(isPlayed());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WebElementState that = (WebElementState) o;
            return isActive == that.isActive && isPlaying == that.isPlaying && isPlayingNext == that.isPlayingNext && isPlayed == that.isPlayed && isVisible == that.isVisible && isSelected == that.isSelected && clickCount == that.clickCount && id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, isActive, isPlaying, isPlayingNext, isPlayed, isVisible, isSelected, clickCount);
        }

        @Override
        public String toString() {
            return "WebElementState{" +
                    "id='" + id + '\'' +
                    ", isActive=" + isActive +
                    ", isPlaying=" + isPlaying +
                    ", isPlayingNext=" + isPlayingNext +
                    ", isPlayed=" + isPlayed +
                    ", isVisible=" + isVisible +
                    ", clickCount=" + clickCount +
                    '}';
        }
    }

    public class WebTextState {
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

        @Override
        public String toString() {
            return "WebTextState{" +
                    "id='" + id + '\'' +
                    ", isVisible = " + isVisible +
                    ", colour = " + colour +
                    ", lines = " + Arrays.toString(lines) +
                    '}';
        }
    }

    public class WebScoreServerState {
        private volatile WebScore.Tile[][] tiles;
        private final List<WebAction> actions;
        private volatile String zoomLevel;
        private final Map<String, WebElementState> elementStates;
        private final WebTextState instructions;
        private volatile WebGranulatorConfig granulatorConfig;
        private volatile WebSpeechSynthConfig speechSynthConfig;
        private volatile WebSpeechSynthState speechSynthState;

        public WebScoreServerState(WebScore.Tile[][] tiles, List<WebAction> currentActions, Map<String, WebElementState> elementStates,
                                   String zoomLevel, WebTextState instructions, WebGranulatorConfig granulatorConfig,
                                   WebSpeechSynthConfig speechSynthConfig, WebSpeechSynthState speechSynthState) {
            this.tiles = tiles;
            this.actions = currentActions;
            this.elementStates = elementStates;
            this.zoomLevel = zoomLevel;
            this.instructions = instructions;
            this.granulatorConfig = granulatorConfig;
            this.speechSynthConfig = speechSynthConfig;
            this.speechSynthState = speechSynthState;
        }

        public void resetDelta() {
            clearActions();
        }

        public WebScore.Tile[][] getTiles() {
            return tiles;
        }

        public WebScore.Tile getTile(int row, int col) {
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

        public WebScore.Tile getTile(String id) {
            return null;
        }

        public void setTiles(WebScore.Tile[][] tiles) {
            this.tiles = tiles;
            pcs.firePropertyChange(WEB_OBJ_TILES, WEB_OBJ_TILES, tiles);
        }

        public void setTile(WebScore.Tile tile, int i, int j) {
            this.tiles[i][j] = tile;
            pcs.firePropertyChange(WEB_OBJ_TILE, tile.getId(), tile);
        }

        public List<WebAction> getActions() {
            return actions;
        }

        public void clearActions() {
            actions.clear();
            pcs.firePropertyChange(WEB_OBJ_ACTIONS, WEB_OBJ_ACTIONS, null);
        }

        public void addAction(WebAction action) {
            if (action == null) {
                return;
            }
            LOG.info("WebScoreServerState addAction: {}", action);
            actions.add(action);
            pcs.firePropertyChange(WEB_OBJ_ACTIONS, action.getId(), action);
        }

        public Map<String, WebElementState> getElementStates() {
            return elementStates;
        }

        public void clearElementStates() {
            elementStates.clear();
            pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, WEB_OBJ_ELEMENT_STATE, null);
        }

        public WebElementState getElementState(String key) {
            return elementStates.get(key);
        }

        public void addElementState(String key, WebElementState elementState) {
            WebElementState old = elementStates.get(key);
            elementStates.put(key, elementState);
            if (!elementState.equals(old)) {
                pcs.firePropertyChange(WEB_OBJ_ELEMENT_STATE, key, elementState);
            }
        }

        public WebElementState getCentreShape() {
            return elementStates.get(WEB_OBJ_CENTRE_SHAPE);
        }

        public WebElementState getInnerCircle() {
            return elementStates.get(WEB_OBJ_INNER_CIRCLE);
        }

        public WebElementState getOuterCircle() {
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
    }

    public class WebScoreChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent changeEvent) {
            if (changeEvent == null) {
                return;
            }
            Object idObj = changeEvent.getOldValue();
            if (!(idObj instanceof String)) {
                LOG.error("propertyChange: invalid object id type, expected String");
                return;
            }
            String objId = (String) idObj;
            stateDeltaTracker.processUpdate(changeEvent.getPropertyName(), objId, changeEvent.getNewValue());
        }
    }
}
