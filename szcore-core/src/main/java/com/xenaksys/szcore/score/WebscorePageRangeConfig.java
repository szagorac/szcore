package com.xenaksys.szcore.score;

import com.xenaksys.szcore.algo.IntRange;

public class WebscorePageRangeConfig {

    private Integer tileRow;
    private IntRange tileCols;
    private IntRange pageRange;
    private WebscorePageRangeAssignmentType assignmentType;

    public Integer getTileRow() {
        return tileRow;
    }

    public void setTileRow(Integer tileRow) {
        this.tileRow = tileRow;
    }

    public IntRange getTileCols() {
        return tileCols;
    }

    public void setTileCols(IntRange tileCols) {
        this.tileCols = tileCols;
    }

    public IntRange getPageRange() {
        return pageRange;
    }

    public void setPageRange(IntRange pageRange) {
        this.pageRange = pageRange;
    }

    public WebscorePageRangeAssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(WebscorePageRangeAssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }
}
