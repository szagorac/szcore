package com.xenaksys.szcore.util;

import com.xenaksys.szcore.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreUtil {
    private static Logger LOG = LoggerFactory.getLogger(ScoreUtil.class);

    public static boolean isTileId(String elementId) {
        return elementId != null && elementId.startsWith(Consts.WEB_TILE_PREFIX);
    }

    public static String createTileId(int row, int column) {
        return Consts.WEB_TILE_PREFIX + row + Consts.WEB_ELEMENT_NAME_DELIMITER + column;
    }

    public static int getRowFromTileId(String tileId) {
        int out = -1;
        if (!isTileId(tileId)) {
            return out;
        }

        int startIndex = Consts.WEB_TILE_PREFIX.length();
        int endIndex = tileId.indexOf(Consts.WEB_ELEMENT_NAME_DELIMITER);
        String rowStr = tileId.substring(startIndex, endIndex);

        try {
            out = Integer.parseInt(rowStr);
        } catch (NumberFormatException e) {
            LOG.error("getRowIndexFromTileId: invalid tileId: " + tileId);

        }

        return out;
    }

    public static int getColFromTileId(String tileId) {
        int out = -1;
        if (!isTileId(tileId)) {
            return out;
        }

        int startIndex = tileId.indexOf(Consts.WEB_ELEMENT_NAME_DELIMITER) + 1;
        String colStr = tileId.substring(startIndex);

        try {
            out = Integer.parseInt(colStr);
        } catch (NumberFormatException e) {
            LOG.error("getRowIndexFromTileId: invalid tileId: " + tileId);

        }

        return out;
    }
}
