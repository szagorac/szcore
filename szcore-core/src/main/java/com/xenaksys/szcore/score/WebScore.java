package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.event.WebScoreEventType;
import com.xenaksys.szcore.event.WebScoreInstructionsEvent;
import com.xenaksys.szcore.event.WebScorePrecountEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.model.id.MutablePageId;
import com.xenaksys.szcore.util.MathUtil;
import com.xenaksys.szcore.web.WebAction;
import com.xenaksys.szcore.web.WebActionType;
import com.xenaksys.szcore.web.WebScoreState;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_CONFIG;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_RESET;
import static com.xenaksys.szcore.Consts.WEB_ACTION_ID_START;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ATTACK_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DECAY_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DISTANCE_MODEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_DURATION;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_ENVELOPE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_GRAIN;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_IS_USE_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PAN_ANGLE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_PITCH_RATE_RANGE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNER;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PANNING_MODEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_PITCH_RATE;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_RELEASE_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SIZE_MS;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SUSTAIN_LEVEL;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_SUSTAIN_TIME;
import static com.xenaksys.szcore.Consts.WEB_CONFIG_TIME_OFFSET_STEPS_MS;
import static com.xenaksys.szcore.Consts.WEB_GRANULATOR;
import static com.xenaksys.szcore.Consts.WEB_SCORE_ID;
import static com.xenaksys.szcore.Consts.WEB_TEXT_BACKGROUND_COLOUR;
import static com.xenaksys.szcore.Consts.WEB_TILE_PLAY_PAGE_DURATION_FACTOR;
import static com.xenaksys.szcore.Consts.WEB_ZOOM_DEFAULT;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    public static final Comparator<Tile> CLICK_COMPARATOR = (t, t1) -> t1.getState().getClickCount() - t.getState().getClickCount();
    private static final long INTERNAL_EVENT_TIME_LIMIT = 1000 * 3;

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Clock clock;

    private Tile[][] tiles;
    private LinkedList<WebScoreEvent> events;
    private final List<WebScoreEvent> playedEvents = new ArrayList<>();
    private final Map<String, WebElementState> elementStates = new HashMap<>();
    private WebTextState instructions;
    private GranulatorConfig defaultGranulatorConfig;

    private final List<WebAction> currentActions = new ArrayList<>();
    private final List<Tile> tilesAll = new ArrayList<>(64);
    private final List<Tile> playingTiles = new ArrayList<>(4);
    private final List<Tile> activeTiles = new ArrayList<>(8);
    private final List<Tile> playingNextTiles = new ArrayList<>(4);
    private final boolean[] visibleRows = new boolean[8];
    private final boolean[] activeRows = new boolean[8];
    private final Map<String, Integer> tileIdPageIdMap = new HashMap<>();
    private final Map<BeatId, List<WebScoreScript>> beatScripts = new HashMap<>();
    private final Map<BeatId, List<WebScoreScript>> beatResetScripts = new HashMap<>();
    private String zoomLevel = WEB_ZOOM_DEFAULT;

    private final ScriptEngineManager factory = new ScriptEngineManager();
    private final ScriptEngine jsEngine = factory.getEngineByName("nashorn");
    private WebscorePresetConfig webscorePresetConfig;

    private final MutablePageId tempPageId;
    private volatile long lastPlayTilesInternalEventTime = 0L;
    private volatile long lastSelectTilesInternalEventTime = 0L;

    private TestScoreRunner testScoreRunner;

    public WebScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
        this.tempPageId = createTempPage();
    }

    private MutablePageId createTempPage() {
        int pageNo = 0;
        Score score = scoreProcessor.getScore();
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
        currentActions.clear();
        tilesAll.clear();
        playingTiles.clear();
        playingNextTiles.clear();
        tileIdPageIdMap.clear();
        elementStates.clear();
        activeTiles.clear();
        tiles = new Tile[8][8];
        zoomLevel = WEB_ZOOM_DEFAULT;

        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            visibleRows[i] = true;
            activeRows[i] = false;
            int clickCount = 0;

            for (int j = 0; j < 8; j++) {
                int col = j + 1;
                String id = createTileId(row, col);
                Tile t = new Tile(row, col, id);
                WebElementState ts = t.getState();
                ts.setVisible(visibleRows[i]);
                ts.setPlaying(false);
                ts.setActive(activeRows[i]);
                ts.setClickCount(clickCount);
                TileText txt = t.getTileText();
                txt.setVisible(false);
                txt.setValue(EMPTY);
                tiles[i][j] = t;
                tilesAll.add(t);
                int pageNo = getPageNo(row, col);
                tileIdPageIdMap.put(id, pageNo);
            }
        }

        elementStates.put("centreShape", new WebElementState("centreShape"));
        elementStates.put("innerCircle", new WebElementState("innerCircle"));
        elementStates.put("outerCircle", new WebElementState("outerCircle"));

        instructions = new WebTextState("instructions");
        instructions.setLine1("Welcome to");
        instructions.setLine2("<span style='color:blueviolet;'>ZScore</span>");
        instructions.setLine3("awaiting performance start ...");
        instructions.setVisible(true);

        defaultGranulatorConfig = createDefaultGranulatorConfig();

        updateServerState();
    }

    public void reset(int presetNo) {
        try {
            ScriptPreset preset = webscorePresetConfig.getPreset(presetNo);
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
        // row 1 : col - 1
        // row 2 : the same
        // row 3 :
        return ThreadLocalRandom.current().nextInt(1, 3 + 1);
    }

    public void init(String configDir) {
        loadPresets(configDir);
        jsEngine.put(WEB_SCORE_ID, this);
        resetState();
    }

    public void init(LinkedList<WebScoreEvent> events, String configDir) {
        this.events = events;
        init(configDir);
    }

    private void loadPresets(String configDir) {
        if (configDir == null) {
            return;
        }
        try {
            webscorePresetConfig = WebscoreConfigLoader.loadWebScorePresets(configDir);
        } catch (Exception e) {
            LOG.error("Failed to load WebScore Presets", e);
        }
    }

    public void initTestScore() {
        if (events.isEmpty()) {
            events.addAll(playedEvents);
            playedEvents.clear();
        }
        testScoreRunner = new TestScoreRunner(events);
        testScoreRunner.init();
    }

    public void deactivateRows(int[] rows) {
        for (int row : rows) {
            deactivateRow(row);
        }
    }

    public void deactivateRow(int row) {
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
        if (isTileId(elementId)) {
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
            LOG.warn("getTopSelectedPage: not enough pages retrieved: {} expected: {}", topTiles.size(), pageQuantity);
            ArrayList<String> nextTileIds = calculateNextTilesToPlay();
            LOG.warn("getTopSelectedPage: calculated tiles to play: {}", nextTileIds);
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
                LOG.error("getTopSelectedPage: Failed to find pageId for tile id: {}", tileId);
            } else {
                pageIds.add(pageNo);
                tileIds.add(tileId);
            }
            LOG.info("getTopSelectedPage: Found pageId: {} for tile id: {}", pageNo, tileId);
        }
        selectNextTilesInternal(tileIds);
        return pageIds;
    }

    public void resetClickCounts() {
        for (Tile t : tilesAll) {
            t.getState().setClickCount(0);
        }
    }

    public void startScore() {
        LOG.info("startScore: ");
        if (testScoreRunner != null) {
            testScoreRunner.start();
        }
    }

    public void updateServerStateAndPush() {
        updateServerState();
        pushServerState();
    }

    public void setVisibleRows(int[] rows) {
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
        this.zoomLevel = zoomLevel;
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
        this.instructions.setLine1(l1);
        this.instructions.setLine2(l2);
        this.instructions.setLine3(l3);
        this.instructions.setColour(colour);
        this.instructions.setVisible(isVisible);
    }

    public void setVisible(String[] elementIds, boolean isVisible) {
        LOG.info("setVisible: {}", Arrays.toString(elementIds));
        for (String elementId : elementIds) {
            WebElementState state = elementStates.get(elementId);
            if (state != null) {
                state.setVisible(isVisible);
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
        setAction(WEB_ACTION_ID_RESET, WebActionType.ROTATE.name(), targets.toArray(new String[0]));
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

    public void playNextTilesInternal() {
        if (!isPlayTilesInternalEventTime()) {
            return;
        }
        resetActions();

        if (playingNextTiles.isEmpty()) {
            if (playingTiles.isEmpty()) {
                LOG.warn("playNextTiles: Can not find tiles to play, both playingTiles and playingNextTiles are empty");
                return;
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

        updateServerStateAndPush();
        lastPlayTilesInternalEventTime = System.currentTimeMillis();
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

    public void selectNextTilesInternal(List<String> tileIds) {
        if (!isSelectTilesInternalEventTime()) {
            return;
        }
        resetActions();
        setPlayingNextTiles(tileIds.toArray(new String[0]));
        updateServerStateAndPush();
        lastSelectTilesInternalEventTime = System.currentTimeMillis();
    }

    public Tile getNextTileToPlay(Tile tile) {
        int col = tile.getColumn() - 1;
        int row = tile.getRow() - 1;
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

    public void setPlayingNextTiles(String[] tileIds) {
        LOG.info("setPlayingNextTiles: {}", Arrays.toString(tileIds));
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

    public void resetActions() {
        currentActions.clear();
    }

    public void setAction(String actionId, String type, String[] targetIds) {
        setAction(actionId, type, targetIds, new HashMap<>());
    }

    public void setAction(String actionId, String type, String[] targetIds, Map<String, Object> params) {
        LOG.info("setAction: {} target: {}", actionId, Arrays.toString(targetIds));
        try {
            WebActionType t = WebActionType.valueOf(type.toUpperCase());
            WebAction action = new WebAction(actionId, t, Arrays.asList(targetIds), params);
            currentActions.add(action);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to setAction id: {} type: {}", actionId, type);
        }
    }

    public void resetGranulator() {
        String[] target = {WEB_GRANULATOR};
        Map<String, Object> params = defaultGranulatorConfig.toJsMap();
        setAction(WEB_ACTION_ID_CONFIG, WebActionType.AUDIO.name(), target, params);
    }

    public void setGranulatorConfig(Map<String, Object> params) {
        if (params == null) {
            LOG.error("setGranulatorConfig: invalid params");
            return;
        }

        try {
            for (String param : params.keySet()) {
                Object value = params.get(param);
                LOG.info("setGranulatorConfig: setting config param: {} value: {}", param, value);

                String[] names = param.split("\\.");
                if (names.length != 2) {
                    LOG.error("setGranulatorConfig: invalid param names {}, will not use", Arrays.toString(names));
                    continue;
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
            }
        } catch (Exception e) {
            LOG.error("setGranulatorConfig: failed to set granulator config", e);
        }

        LOG.info("setGranulatorConfig: new config: {}", defaultGranulatorConfig);
    }

    public GranulatorConfig getGranulatorConfig() {
        return defaultGranulatorConfig;
    }

    private void setGranulatorPannerConfig(String name, Object value) {
        PannerConfig pannerConfig = defaultGranulatorConfig.getPanner();
        switch (name) {
            case WEB_CONFIG_IS_USE_PANNER:
                try {
                    pannerConfig.setUsePanner(getBoolean(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set isUsePanner", e);
                }
                break;
            case WEB_CONFIG_PANNING_MODEL:
                try {
                    String v = PanningModel.fromName(getString(value)).getName();
                    pannerConfig.setPanningModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set panningModel", e);
                }
                break;
            case WEB_CONFIG_DISTANCE_MODEL:
                try {
                    String v = PannerDistanceModel.fromName(getString(value)).getName();
                    pannerConfig.setDistanceModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set distanceModel", e);
                }
                break;
            case WEB_CONFIG_MAX_PAN_ANGLE:
                try {
                    pannerConfig.setMaxPanAngle(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set maxPanAngle", e);
                }
                break;
            default:
                LOG.error("setGranulatorPannerConfig: Invalid Granulator Panner param: {}", name);
        }
    }

    private void setGrainConfig(String name, Object value) {
        GrainConfig grainConfig = defaultGranulatorConfig.getGrain();
        switch (name) {
            case WEB_CONFIG_SIZE_MS:
                try {
                    grainConfig.setSizeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set grain size", e);
                }
                break;
            case WEB_CONFIG_PITCH_RATE:
                try {
                    grainConfig.setPitchRate(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set pitchRate", e);
                }
                break;
            case WEB_CONFIG_MAX_POSITION_OFFSET_RANGE_MS:
                try {
                    grainConfig.setMaxPositionOffsetRangeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPositionOffsetRangeMs", e);
                }
                break;
            case WEB_CONFIG_MAX_PITCH_RATE_RANGE:
                try {
                    grainConfig.setMaxPitchRateRange(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPitchRateRange", e);
                }
                break;
            case WEB_CONFIG_TIME_OFFSET_STEPS_MS:
                try {
                    grainConfig.setTimeOffsetStepMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set timeOffsetStepMs", e);
                }
                break;
            default:
                LOG.error("setGrainConfig: Invalid Grain Config Param: {}", name);
        }
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

    private void setGranulatorEnvelopeConfig(String name, Object value) {
        EnvelopeConfig envelopeConfig = defaultGranulatorConfig.getEnvelope();
        switch (name) {
            case WEB_CONFIG_ATTACK_TIME:
                try {
                    envelopeConfig.setAttackTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set attackTime", e);
                }
                break;
            case WEB_CONFIG_DECAY_TIME:
                try {
                    envelopeConfig.setDecayTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set decayTime", e);
                }
                break;
            case WEB_CONFIG_SUSTAIN_TIME:
                try {
                    envelopeConfig.setSustainTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainTime", e);
                }
                break;
            case WEB_CONFIG_RELEASE_TIME:
                try {
                    envelopeConfig.setReleaseTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set releaseTime", e);
                }
                break;
            case WEB_CONFIG_SUSTAIN_LEVEL:
                try {
                    envelopeConfig.setSustainLevel(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainLevel", e);
                }
                break;
            default:
                LOG.error("setGranulatorEnvelopeConfig: Invalid Grain Config Param: {}", name);
        }
    }

    public boolean isTileId(String elementId) {
        return elementId.startsWith(Consts.WEB_TILE_PREFIX);
    }

    private String createTileId(int row, int column) {
        return Consts.WEB_TILE_PREFIX + row + Consts.WEB_ELEMENT_NAME_DELIMITER + column;
    }

    private Tile getTile(String tileId) {
        try {
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

    public WebScoreState exportState() {
        Tile[][] ts = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Tile t = tiles[i][j];
                Tile ot = new Tile(t.getRow(), t.getColumn(), t.getId());
                t.copyTo(ot);
                ts[i][j] = ot;
            }
        }
        WebElementState centreShape = elementStates.get("centreShape");
        WebElementState innerCircle = elementStates.get("innerCircle");
        WebElementState outerCircle = elementStates.get("outerCircle");

        return new WebScoreState(ts, currentActions, centreShape, innerCircle, outerCircle, zoomLevel, instructions);
    }

    public void updateServerState() {
        try {
            scoreProcessor.onWebScoreStateChange(exportState());
        } catch (Exception e) {
            LOG.error("Failed to process pushServerState", e);
        }
    }

    public void pushServerState() {
        try {
            BeatId beatId = null;
            String eventId = null;
            OutgoingWebEventType eventType = OutgoingWebEventType.PUSH_SERVER_STATE;
            long creationTime = clock.getSystemTimeMillis();
            OutgoingWebEvent outgoingWebEvent = eventFactory.createOutgoingWebEvent(beatId, eventId, eventType, creationTime);
            scoreProcessor.onOutgoingWebEvent(outgoingWebEvent);
        } catch (Exception e) {
            LOG.error("Failed to process pushServerState", e);
        }
    }

    public void processWebScoreEvent(WebScoreEvent event) {
        LOG.info("processWebScoreEvent: execute event: {}", event);
        WebScoreEventType type = event.getWebScoreEventType();
        try {
            resetActions();
            boolean isStateUpdate = true;

            switch (type) {
                case INSTRUCTIONS:
                    isStateUpdate = processInstructionsEvent((WebScoreInstructionsEvent) event);
                    break;
                case PRECOUNT:
                    isStateUpdate = processPrecountEvent((WebScorePrecountEvent) event);
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

            if (isStateUpdate) {
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

    private GranulatorConfig createDefaultGranulatorConfig() {
        GranulatorConfig granulatorConfig = new GranulatorConfig();

        granulatorConfig.setMasterGainVal(1.0);
        granulatorConfig.setMaxGrains(12);
        granulatorConfig.setPlayDurationSec(10);
        granulatorConfig.setPlayStartOffsetSec(0.0);
        granulatorConfig.setBufferPositionPlayRate(1.0);
        granulatorConfig.setAudioStopToleranceMs(5);

        GrainConfig grainConfig = new GrainConfig();
        grainConfig.setMaxPitchRateRange(0.0);
        grainConfig.setMaxPositionOffsetRangeMs(10);
        grainConfig.setPitchRate(1.0);
        grainConfig.setSizeMs(100);
        grainConfig.setTimeOffsetStepMs(10);
        granulatorConfig.setGrain(grainConfig);

        EnvelopeConfig envelopeConfig = new EnvelopeConfig();
        envelopeConfig.setAttackTime(0.4);
        envelopeConfig.setDecayTime(0.0);
        envelopeConfig.setSustainTime(0.2);
        envelopeConfig.setReleaseTime(0.4);
        envelopeConfig.setSustainLevel(1.0);
        granulatorConfig.setEnvelope(envelopeConfig);

        PannerConfig pannerConfig = new PannerConfig();
        pannerConfig.setUsePanner(false);
        pannerConfig.setPanningModel(PanningModel.EQUAL_POWER.getName());
        pannerConfig.setDistanceModel(PannerDistanceModel.LINEAR.getName());
        pannerConfig.setMaxPanAngle(45);
        granulatorConfig.setPanner(pannerConfig);

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
            this.tileText = new TileText(EMPTY, false);
        }

        public WebElementState getState() {
            return state;
        }

        public void setState(WebElementState state) {
            state.copyTo(this.state);
        }

        public void setText(TileText txt) {
            txt.copyTo(this.tileText);
        }

        public void setText(String txt) {
            tileText.setValue(txt);
            tileText.setVisible(true);
        }

        public String getId() {
            return id;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
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

        public TileText(String value, boolean isVisible) {
            this.value = value;
            this.isVisible = isVisible;
        }

        public String getValue() {
            return value;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public void copyTo(TileText other) {
            other.setVisible(isVisible());
            other.setValue(getValue());
        }
    }

    public class Grid {
        private final String id;
        private final WebElementState state;

        public Grid(String id) {
            this.id = id;
            state = new WebElementState(id);
        }

        public WebElementState getState() {
            return state;
        }

        public String getId() {
            return id;
        }
    }

    public class TestScoreRunner {
        private Thread runner;
        private final LinkedList<WebScoreEvent> events;
        private volatile boolean isRunning;

        public TestScoreRunner(LinkedList<WebScoreEvent> events) {
            this.events = events;
        }

        public void init() {
            runner = new Thread(() -> {
                isRunning = true;
                LOG.info("TestScoreRunner START");
                try {
                    Thread.sleep(3000);

                    while (!events.isEmpty()) {
                        WebScoreEvent event = events.remove();
                        processWebScoreEvent(event);
                        playedEvents.add(event);
                        Thread.sleep(3000);
                    }

                } catch (Exception e) {
                    LOG.error("Interrupted TestScoreRunner", e);
                }
                LOG.info("TestScoreRunner END");
                isRunning = false;
            });
        }

        public void start() {
            if (isRunning) {
                return;
            }
            runner.start();
        }

    }
}
