package com.xenaksys.szcore.web;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.score.WebElementState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WebScore {
    static final Logger LOG = LoggerFactory.getLogger(WebScore.class);

    public static final Comparator<Tile> CLICK_COMPARATOR = (t, t1) -> t1.getState().getClickCount() - t.getState().getClickCount();

    private Tile[][] tiles;
    private Grid grid;
    private List<WebAction> webActions = new ArrayList<>();
    private List<Tile> tilesCol = new ArrayList<>(64);

    public void init() {
        tiles = new Tile[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                int row = i + 1;
                int col = j + 1;
                String id = createTileId(row,col);
                Tile t = new Tile(row, col, id);
                WebElementState ts = t.getState();
                ts.setVisible(true);
                ts.setPlayed(false);
                ts.setActive(false);
                ts.setClickCount(0);
                tiles[i][j] = t;
                tilesCol.add(t);
            }
        }

        grid = new Grid(Consts.WEB_ELEMENT_GRID);
        WebElementState gs = grid.getState();
        gs.setVisible(true);
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

    public void setWebActions(List<WebAction> webActions) {
        this.webActions = webActions;
    }

    public void addWebAction(WebAction webAction) {
        webActions.add(webAction);
    }

    public void deactivateTiles(int row) {
        if(row < 1 || row > tiles.length) {
            LOG.warn("deactivateTiles: invalid row: " + row);
        }

        int i = row - 1;
        for(int j = 0; j < 8; j++) {
            Tile t = tiles[i][j];
            WebElementState ts = t.getState();
            ts.setActive(false);
            tilesCol.remove(t);
        }
    }

    public void setSelectedElement(String elementId, boolean isSelected) {
        if(isTileId(elementId)) {
            Tile in = parseTileId(elementId);
            if(in == null) {
                return;
            }
            int i = in.getRow() - 1;
            int j = in.getColumn() - 1;
            if(i < 0 || i > tiles.length) {
                LOG.error("setSelectedElement: invalid i: {}", i);
                return;
            }
            if(j < 0 || j > tiles.length) {
                LOG.error("setSelectedElement: invalid j: {}", j);
                return;
            }

            Tile tile = tiles[i][j];
            if(!tile.getId().equals(in.getId())) {
                LOG.error("setSelectedElement: retrieved invalid element: {}", tile);
                return;
            }

            WebElementState state = tile.getState();
            if(isSelected) {
                state.incrementClickCount();
            }

            tilesCol.sort(CLICK_COMPARATOR);
            Tile top = tilesCol.get(0);
            LOG.debug("setSelectedElement: top tile: {}", top);
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

    class Tile {
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

        public String getId() {
            return id;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
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
//
//        @Override
//        public int compareTo(Tile o) {
//            return o.getState().getClickCount() - this.getState().getClickCount();
//        }
    }

    class Grid {
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
}
