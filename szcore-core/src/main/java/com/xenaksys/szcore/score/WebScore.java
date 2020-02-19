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
    private Grid grid;
    private WebAction lastAction;

    private final List<WebAction> webActions = new ArrayList<>();
    private final List<Tile> tilesAll = new ArrayList<>(64);
    private final boolean[] visibleRows = new boolean[8];
    private final boolean[] activeRows = new boolean[8];
    private final Map<String, Integer> tileIdPageIdMap = new HashMap<>();

    private ScriptEngineManager factory = new ScriptEngineManager();
    private ScriptEngine jsEngine = factory.getEngineByName("nashorn");
    private LinkedList<WebScoreEvent> events;

    private TestScoreRunner testScoreRunner;

    public WebScore(ScoreProcessor scoreProcessor, EventFactory eventFactory, Clock clock) {
        this.scoreProcessor = scoreProcessor;
        this.eventFactory = eventFactory;
        this.clock = clock;
    }

    public void init(LinkedList<WebScoreEvent> events) {
        this.events = events;
        jsEngine.put("webScore", this);
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
                ts.setPlayed(false);
                ts.setActive(activeRows[i]);
                ts.setClickCount(clickCount);
                tiles[i][j] = t;
                tilesAll.add(t);
                //TODO page mapping
                tileIdPageIdMap.put(id, row);
            }
        }

        grid = new Grid(Consts.WEB_ELEMENT_GRID);
        WebElementState gs = grid.getState();
        gs.setVisible(false);

        updateServerState();
    }

    public void startTestScore() {
        testScoreRunner = new TestScoreRunner(events);
        testScoreRunner.init();
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<WebAction> getWebActions() {
        return webActions;
    }

    public void addWebAction(WebAction webAction) {
        webActions.add(webAction);
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
        if(testScoreRunner != null) {
            testScoreRunner.start();
        }

        if (isTileId(elementId)) {
            Tile in = parseTileId(elementId);
            if (in == null) {
                return;
            }
            if (!isInActiveRow(in)) {
                LOG.info("Selected tile {} is not in active row", in.getId());
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

            WebElementState state = tile.getState();
            if (isSelected) {
                state.setSelected(true);
                state.incrementClickCount();
                tilesAll.sort(CLICK_COMPARATOR);
            }

            checkState();
        }
    }

    private boolean isInActiveRow(Tile tile) {
        return activeRows[tile.getRow() - 1];
    }

    private boolean isInVisibleRow(Tile tile) {
        return visibleRows[tile.getRow() - 1];
    }

    private void checkState() {
        int clickNo = 3;
        List<String> toMakeInvisible = new ArrayList<>();
        for (Tile t : tilesAll) {
            if (t.getState().getClickCount() >= clickNo) {
                toMakeInvisible.add(t.getId());
                t.getState().setVisible(false);
            }
        }
        if (toMakeInvisible.isEmpty()) {
            return;
        }

        WebAction action = new WebAction("invisible", WebActionType.INVISIBLE, toMakeInvisible);
        WebScoreState export = exportState();
        export.setAction(action);

        try {
            scoreProcessor.onWebScoreEvent(export);
        } catch (Exception e) {
            LOG.error("Failed to process onWebScoreEvent", e);
            //TODO repeat action.
        }

        lastAction = action;
    }

    public void onStart(int[] rows) {
        LOG.info("onStart: active rows: {}", Arrays.toString(rows));
        TIntList tintRows = new TIntArrayList(rows);
        setActiveRows(tintRows);
        updateServerStateAndPush();
    }

    public void updateServerStateAndPush() {
        updateServerState();
        pushServerState();
    }

    public void setVisibleRows(TIntList rows) {
        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            visibleRows[i] = rows.contains(row);
            for (int j = 0; j < 8; j++) {
                Tile t = tiles[i][j];
                WebElementState ts = t.getState();
                ts.setVisible(visibleRows[i]);
            }
        }
    }

    public void setActiveRows(TIntList rows) {
        for (int i = 0; i < 8; i++) {
            int row = i + 1;
            activeRows[i] = rows.contains(row);
            for (int j = 0; j < 8; j++) {
                Tile t = tiles[i][j];
                WebElementState ts = t.getState();
                ts.setActive(activeRows[i]);
            }
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
        Grid g = new Grid(grid.getId());
        grid.getState().copyTo(g.getState());
        WebScoreState webScoreState = new WebScoreState(ts, g);

        return webScoreState;
    }

    public void updateServerState() {
        try {
            WebScoreState export = exportState();
            scoreProcessor.onWebScoreEvent(export);
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
            String js = event.getScript();
            if(js == null) {
                return;
            }
            Object out = jsEngine.eval(js);
            LOG.info("processWebScoreEvent: script out: {}", out);
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
