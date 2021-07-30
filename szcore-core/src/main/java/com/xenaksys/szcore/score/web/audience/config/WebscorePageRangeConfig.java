package com.xenaksys.szcore.score.web.audience.config;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.score.web.audience.WebAudienceScorePageRangeAssignmentType;

public class WebscorePageRangeConfig {

    private Integer tileRow;
    private IntRange tileCols;
    private IntRange pageRange;
    private WebAudienceScorePageRangeAssignmentType assignmentType;

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

    public WebAudienceScorePageRangeAssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(WebAudienceScorePageRangeAssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }
}
