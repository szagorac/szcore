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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    public static final Comparator<Tile> CLICK_COMPARATOR = (t, t1) -> t1.getState().getClickCount() - t.getState().getClickCount();

    private final ScoreProcessor scoreProcessor;
    private final EventFactory eventFactory;
    private final Clock clock;

    private Tile[][] tiles;
    private LinkedList<WebScoreEvent> events;
    private List<WebScoreEvent> playedEvents = new ArrayList<>();
    private final Map<String, WebElementState> elementStates = new HashMap<>();

    private final List<WebAction> currentActions = new ArrayList<>();
    private final List<Tile> tilesAll = new ArrayList<>(64);
    private final List<Tile> playingTiles = new ArrayList<>(4);
    private final List<Tile> playingNextTiles = new ArrayList<>(4);
    private final boolean[] visibleRows = new boolean[8];
    private final boolean[] activeRows = new boolean[8];
    private final Map<String, Integer> tileIdPageIdMap = new HashMap<>();

    private ScriptEngineManager factory = new ScriptEngineManager();
    private ScriptEngine jsEngine = factory.getEngineByName("nashorn");

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
                tiles[i][j] = t;
                tilesAll.add(t);
                //TODO page mapping
                tileIdPageIdMap.put(id, row);
            }
        }

        elementStates.put("centreShape", new WebElementState("centreShape"));
        elementStates.put("innerCircle", new WebElementState("innerCircle"));
        elementStates.put("outerCircle", new WebElementState("outerCircle"));

        updateServerState();
    }

    public void init(LinkedList<WebScoreEvent> events) {
        this.events = events;
        jsEngine.put("webScore", this);
        resetState();
    }

    public void initTestScore() {
        if(events.isEmpty()) {
            events.addAll(playedEvents);
            playedEvents.clear();
        }
        testScoreRunner = new TestScoreRunner(events);
        testScoreRunner.init();
    }

    public void deactivateTiles(int row) {
        if (row < 1 || row > tiles.length) {
            LOG.warn("deactivateTiles: invalid row: " + row);
        }

        int i = row - 1;
        for (int j = 0; j < 8; j++) {
            Tile t = tiles[i][j];
            WebElementState ts = t.getState();
            ts.setActive(false);
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
            if(state.isPlaying() || state.isPlayingNext() || state.isPlayed() || !state.isVisible()) {
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
        if(testScoreRunner != null) {
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

    public void setVisible(String[] elementIds) {
        LOG.info("setVisible: {}", Arrays.toString(elementIds));
        for(String elementId : elementIds) {
            WebElementState state = elementStates.get(elementId);
            if(state != null) {
                state.setVisible(true);
            }
        }
    }

    public void resetPlayingTiles() {
        for(Tile t : playingTiles) {
            t.getState().setPlaying(false);
            t.getState().setPlayed(true);
            t.getState().setVisible(false);
        }
        playingTiles.clear();
    }

    public void setPlayingTiles(String[] tileIds) {
        LOG.info("setPlayingTiles: {}", Arrays.toString(tileIds));
        resetPlayingTiles();
        for(String tileId : tileIds) {
            setPlayingTile(tileId);
        }
    }

    public void setPlayingTile(String tileId) {
        Tile t = parseTileId(tileId);
        if(t == null) {
            LOG.error("setPlayedTile: invalid tileId: {}", tileId);
            return;
        }
        setPlayingTile(t.getRow(), t.getColumn());
    }

    public void setPlayingTile(int row, int col) {
        int i = row -1;
        int j = col - 1;
        if(i < 0 || i >= tiles.length) {
            LOG.error("setPlayedTile: invalid row: {}", i);
            return;
        }
        if(j < 0 || j >= tiles[0].length) {
            LOG.error("setPlayedTile: invalid col: {}", i);
            return;
        }
        Tile t = tiles[row - 1][col - 1];
        t.getState().setPlaying(true);
        playingTiles.add(t);
    }

    public void resetPlayingNextTiles() {
        for(Tile t : playingNextTiles) {
            t.getState().setPlayingNext(false);
        }
        playingNextTiles.clear();
    }

    public void setPlayingNextTiles(String[] tileIds) {
        LOG.info("setPlayingNextTiles: {}", Arrays.toString(tileIds));
        resetPlayingNextTiles();
        for(String tileId : tileIds) {
            setPlayingNextTile(tileId);
        }
    }

    public void setPlayingNextTile(String tileId) {
        Tile t = parseTileId(tileId);
        if(t == null) {
            LOG.error("setPlayedNextTile: invalid tileId: {}", tileId);
            return;
        }
        setPlayingNextTile(t.getRow(), t.getColumn());
    }

    public void setPlayingNextTile(int row, int col) {
        int i = row -1;
        int j = col - 1;
        if(i < 0 || i >= tiles.length) {
            LOG.error("setPlayedNextTile: invalid row: {}", i);
            return;
        }
        if(j < 0 || j >= tiles[0].length) {
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
        LOG.info("setAction: {} target: {}", actionId, Arrays.toString(targetIds));
        try {
            WebActionType t = WebActionType.valueOf(type.toUpperCase());
            WebAction action = new WebAction(actionId, t, Arrays.asList(targetIds));
            currentActions.add(action);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to setAction id: {} type: {}", actionId, type);
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

        return new WebScoreState(ts, currentActions, centreShape, innerCircle, outerCircle);
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

    private void processWebScoreEvent(WebScoreEvent event) {
        LOG.info("processWebScoreEvent: execute event: {}", event);
        try {
            List<String> jsScripts = event.getScripts();
            if(jsScripts == null) {
                return;
            }
            for(String js : jsScripts) {
                Object out = jsEngine.eval(js);
                LOG.info("processWebScoreEvent: script out: {}", out);
            }
            updateServerStateAndPush();
        } catch (Exception e) {
            LOG.error("Failed to evaluate script", e);
        }
    }

    public class Tile {
        private final String id;
        private final int row;
        private final int column;
        private final WebElementState state;

        public Tile(int row, int column, String id) {
            this.id = id;
            this.row = row;
            this.column = column;
            state = new WebElementState(id);
        }

        public WebElementState getState() {
            return state;
        }

        public void setState(WebElementState state) {
            state.copyTo(this.state);
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

        public void copyTo(Tile other) {
            state.copyTo(other.getState());
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
            if(isRunning) {
                return;
            }
            runner.start();
        }

    }

}
