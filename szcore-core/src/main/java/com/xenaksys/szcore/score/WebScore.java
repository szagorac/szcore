package com.xenaksys.szcore.score;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.event.EventFactory;
import com.xenaksys.szcore.event.OutgoingWebEvent;
import com.xenaksys.szcore.event.OutgoingWebEventType;
import com.xenaksys.szcore.event.WebScoreEvent;
import com.xenaksys.szcore.model.Clock;
import com.xenaksys.szcore.model.ScoreProcessor;
import com.xenaksys.szcore.model.id.BeatId;
import com.xenaksys.szcore.web.WebAction;
import com.xenaksys.szcore.web.WebActionType;
import com.xenaksys.szcore.web.WebScoreState;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

import static com.xenaksys.szcore.Consts.*;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    public static final Comparator<Tile> CLICK_COMPARATOR = (t, t1) -> t1.getState().getClickCount() - t.getState().getClickCount();

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Clock clock;

    private Tile[][] tiles;
    private LinkedList<WebScoreEvent> events;
    private final List<WebScoreEvent> playedEvents = new ArrayList<>();
    private final Map<String, WebElementState> elementStates = new HashMap<>();
    private WebTextState instructions;
    private GranulatorConfig granulatorConfig;

    private final List<WebAction> currentActions = new ArrayList<>();
    private final List<Tile> tilesAll = new ArrayList<>(64);
    private final List<Tile> playingTiles = new ArrayList<>(4);
    private final List<Tile> playingNextTiles = new ArrayList<>(4);
    private final boolean[] visibleRows = new boolean[8];
    private final boolean[] activeRows = new boolean[8];
    private final Map<String, Integer> tileIdPageIdMap = new HashMap<>();
    private final Map<BeatId, List<WebScoreScript>> beatScripts = new HashMap<>();
    private final Map<BeatId, List<WebScoreScript>> beatResetScripts = new HashMap<>();
    private String zoomLevel = WEB_ZOOM_DEFAULT;

    private final ScriptEngineManager factory = new ScriptEngineManager();
    private final ScriptEngine jsEngine = factory.getEngineByName("nashorn");
    private final TIntObjectHashMap<Preset> presets = new TIntObjectHashMap<>();

    private TestScoreRunner testScoreRunner;

    public WebScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
    }

    public void resetState() {
        currentActions.clear();
        tilesAll.clear();
        playingTiles.clear();
        playingNextTiles.clear();
        tileIdPageIdMap.clear();
        elementStates.clear();
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

        granulatorConfig = createDefaultGranulatorConfig();

        updateServerState();
    }

    public void reset(int presetNo) {
        try {
            Preset preset = presets.get(presetNo);
            if (preset == null) {
                LOG.info("resetState: Unknown preset: {}", presetNo);
                return;
            }

            runScripts(preset.getScripts());
            updateServerStateAndPush();
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
        return 0;
    }

    public void init() {
        loadPresets();
        jsEngine.put("webScore", this);
        resetState();
    }

    public void init(LinkedList<WebScoreEvent> events) {
        this.events = events;
        init();
    }

    private void loadPresets() {
        String resetServer = "webScore.resetState()";
        String resetClient = "webScore.setAction('all', 'RESET', ['elements']);";
        String startText = "webScore.setInstructions('Union Rose', 'performance', 'test');";
        ArrayList<String> resetAll = new ArrayList<>(Arrays.asList(resetServer, resetClient, startText));
        addPreset(1, resetAll);


        String zoomCentre = "webScore.setZoomLevel('centreShape'); webScore.setAction('startZoom', 'ZOOM', ['centreShape']);";
        String visibleCentreShape = "webScore.setVisible(['centreShape'], true)";
        String invisibleInner = "webScore.setVisible(['innerCircle'], false)";
        String invisibleOuter = "webScore.setVisible(['outerCircle'], false)";
        String visibleAllRows = "webScore.setVisibleRows([1, 2, 3, 4, 5, 6, 7, 8]);";
        String activateCentre = "webScore.setActiveRows([1, 2]);";
        ArrayList<String> r2 = new ArrayList<>(Arrays.asList(resetServer, invisibleInner, invisibleOuter, visibleCentreShape, visibleAllRows, zoomCentre, activateCentre));
        addPreset(2, r2);

        String zoomInner = "webScore.setZoomLevel('innerCircle'); webScore.setAction('startZoom', 'ZOOM', ['innerCircle']);";
        String endTimeline = "webScore.setAction('end', 'TIMELINE', ['centreShape']);";
        String deactivateCentre = "webScore.deactivateRows([1,2,3])";
        ArrayList<String> r3 = new ArrayList<>(Arrays.asList(zoomInner, invisibleInner, invisibleOuter, visibleCentreShape, endTimeline, deactivateCentre));
        addPreset(3, r3);
    }

    private void addPreset(int presetNo, List<String> scripts) {
        Preset preset = new Preset(scripts);
        presets.put(presetNo, preset);
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

            tilesAll.remove(t);
        }
    }

    public void setSelectedElement(String elementId, boolean isSelected) {
        if (isTileId(elementId)) {
            Tile in = parseTileId(elementId);
            if (in == null) {
                return;
            }
            if (!isInActiveRow(in)) {
                LOG.info("Selected tile {} is not in active row", in.getId());
                return;
            }
            WebElementState state = in.getState();
            if (state.isPlaying() || state.isPlayingNext() || state.isPlayed() || !state.isVisible()) {
                return;
            }

            int i = in.getRow() - 1;
            int j = in.getColumn() - 1;
            if (i < 0 || i > tiles.length) {
                LOG.error("setSelectedElement: invalid i: {}", i);
                return;
            }
            if (j < 0 || j > tiles.length) {
                LOG.error("setSelectedElement: invalid j: {}", j);
                return;
            }

            Tile tile = tiles[i][j];
            if (!tile.getId().equals(in.getId())) {
                LOG.error("setSelectedElement: retrieved invalid element: {}", tile);
                return;
            }

            if (isSelected) {
                state.setSelected(true);
                state.incrementClickCount();
                tilesAll.sort(CLICK_COMPARATOR);
            }
        }
    }

    public boolean isInActiveRow(Tile tile) {
        return activeRows[tile.getRow() - 1];
    }

    public boolean isInVisibleRow(Tile tile) {
        return visibleRows[tile.getRow() - 1];
    }

    public List<String> getTopSelectedTiles(int quantity) {
        List<String> topSelected = new ArrayList<>();
        int count = 1;
        for (Tile t : tilesAll) {
            if (count <= quantity) {
                topSelected.add(t.getId());
                count++;
            }
        }
        LOG.info("Selected tiles to play next: {}", Arrays.toString(topSelected.toArray()));
        return topSelected;
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
        setAction("reset", "ROTATE", targets.toArray(new String[0]));
        playingTiles.clear();
    }

    public void setPlayingTiles(String[] tileIds) {
        LOG.info("setPlayingTiles: {}", Arrays.toString(tileIds));
        resetPlayingTiles();
        for (String tileId : tileIds) {
            setPlayingTile(tileId);
        }
    }

    public void setPlayingTile(String tileId) {
        Tile t = parseTileId(tileId);
        if (t == null) {
            LOG.error("setPlayedTile: invalid tileId: {}", tileId);
            return;
        }
        setPlayingTile(t.getRow(), t.getColumn());
    }

    public void setPlayingTile(int row, int col) {
        int i = row - 1;
        int j = col - 1;
        if (i < 0 || i >= tiles.length) {
            LOG.error("setPlayedTile: invalid row: {}", i);
            return;
        }
        if (j < 0 || j >= tiles[0].length) {
            LOG.error("setPlayedTile: invalid col: {}", i);
            return;
        }
        Tile t = tiles[row - 1][col - 1];
        t.getState().setPlaying(true);
        playingTiles.add(t);
    }

    public void setTileText(String tileId, String value) {
        Tile t = parseTileId(tileId);
        if (t == null) {
            LOG.error("setTileText: invalid tileId: {}", tileId);
            return;
        }
        setTileText(t.getRow(), t.getColumn(), value);
    }

    private void setTileText(int row, int col, String value) {
        int i = row - 1;
        int j = col - 1;
        if (i < 0 || i >= tiles.length) {
            LOG.error("setTileText: invalid row: {}", i);
            return;
        }
        if (j < 0 || j >= tiles[0].length) {
            LOG.error("setTileText: invalid col: {}", i);
            return;
        }
        Tile t = tiles[row - 1][col - 1];
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
        Tile t = parseTileId(tileId);
        if (t == null) {
            LOG.error("setPlayedNextTile: invalid tileId: {}", tileId);
            return;
        }
        setPlayingNextTile(t.getRow(), t.getColumn());
    }

    public void setPlayingNextTile(int row, int col) {
        int i = row - 1;
        int j = col - 1;
        if (i < 0 || i >= tiles.length) {
            LOG.error("setPlayedNextTile: invalid row: {}", i);
            return;
        }
        if (j < 0 || j >= tiles[0].length) {
            LOG.error("setPlayedNextTile: invalid col: {}", i);
            return;
        }
        Tile t = tiles[row - 1][col - 1];
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
    }

    public void resetActions() {
        currentActions.clear();
    }

    public void setAction(String actionId, String type, String[] targetIds) {
        setAction(actionId, type, targetIds, new HashMap<>());
    }

    public void setAction(String actionId, String type, String[] targetIds, Map<String, String> params) {
        LOG.info("setAction: {} target: {}", actionId, Arrays.toString(targetIds));
        try {
            WebActionType t = WebActionType.valueOf(type.toUpperCase());
            WebAction action = new WebAction(actionId, t, Arrays.asList(targetIds), params);
            currentActions.add(action);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to setAction id: {} type: {}", actionId, type);
        }
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
                    case "grain":
                        setGrainConfig(l2, value);
                        break;
                    case "envelope":
                        setGranulatorEnvelopeConfig(l2, value);
                        break;
                    case "panner":
                        setGranulatorPannerConfig(l2, value);
                        break;
                }
            }
        } catch (Exception e) {
            LOG.error("setGranulatorConfig: failed to set granulator config", e);
        }

        LOG.info("setGranulatorConfig: new config: {}", granulatorConfig);
    }

    public GranulatorConfig getGranulatorConfig() {
        return granulatorConfig;
    }

    private void setGranulatorPannerConfig(String name, Object value) {
        PannerConfig pannerConfig = granulatorConfig.getPanner();
        switch (name) {
            case "isUsePanner":
                try {
                    pannerConfig.setUsePanner(getBoolean(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set isUsePanner", e);
                }
                break;
            case "panningModel":
                try {
                    String v = PanningModel.fromName(getString(value)).getName();
                    pannerConfig.setPanningModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set panningModel", e);
                }
                break;
            case "distanceModel":
                try {
                    String v = PannerDistanceModel.fromName(getString(value)).getName();
                    pannerConfig.setDistanceModel(v);
                } catch (Exception e) {
                    LOG.error("setGranulatorPannerConfig: Failed to set distanceModel", e);
                }
                break;
            case "maxPanAngle":
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
        GrainConfig grainConfig = granulatorConfig.getGrain();
        switch (name) {
            case "sizeMs":
                try {
                    grainConfig.setSizeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set grain size", e);
                }
                break;
            case "pitchRate":
                try {
                    grainConfig.setPitchRate(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set pitchRate", e);
                }
                break;
            case "maxPositionOffsetRangeMs":
                try {
                    grainConfig.setMaxPositionOffsetRangeMs(getInt(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPositionOffsetRangeMs", e);
                }
                break;
            case "maxPitchRateRange":
                try {
                    grainConfig.setMaxPitchRateRange(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGrainConfig: Failed to set maxPitchRateRange", e);
                }
                break;
            case "timeOffsetStepMs":
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
        EnvelopeConfig envelopeConfig = granulatorConfig.getEnvelope();
        switch (name) {
            case "attackTime":
                try {
                    envelopeConfig.setAttackTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set attackTime", e);
                }
                break;
            case "decayTime":
                try {
                    envelopeConfig.setDecayTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set decayTime", e);
                }
                break;
            case "sustainTime":
                try {
                    envelopeConfig.setSustainTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set sustainTime", e);
                }
                break;
            case "releaseTime":
                try {
                    envelopeConfig.setReleaseTime(getDouble(value));
                } catch (Exception e) {
                    LOG.error("setGranulatorEnvelopeConfig: Failed to set releaseTime", e);
                }
                break;
            case "sustainLevel":
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

    private Tile parseTileId(String tileId) {
        try {
            int start = tileId.indexOf(Consts.WEB_TILE_PREFIX) + 1;
            int end = tileId.indexOf(Consts.WEB_ELEMENT_NAME_DELIMITER);
            String s = tileId.substring(start, end);
            int row = Integer.parseInt(s);
            start = end + 1;
            s = tileId.substring(start);
            int column = Integer.parseInt(s);
            String id = Consts.WEB_TILE_PREFIX + row + Consts.WEB_ELEMENT_NAME_DELIMITER + column;
            return new Tile(row, column, id);
        } catch (Exception e) {
            LOG.error("Failed to parse tileId: {}", tileId, e);
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
            scoreProcessor.onWebScoreEvent(exportState());
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
        try {
            resetActions();
            List<WebScoreScript> jsScripts = event.getScripts();
            if (jsScripts == null) {
                return;
            }
            runWebScoreScripts(jsScripts);
            updateServerStateAndPush();
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

    public class Preset {
        private final List<String> scripts;

        public Preset(List<String> scripts) {
            this.scripts = scripts;
        }

        public List<String> getScripts() {
            return scripts;
        }
    }
}
