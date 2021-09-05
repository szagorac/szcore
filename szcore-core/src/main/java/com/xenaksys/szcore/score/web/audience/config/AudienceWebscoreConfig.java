package com.xenaksys.szcore.score.web.audience.config;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.model.ScriptPreset;
import com.xenaksys.szcore.score.web.audience.WebAudienceScorePageRangeAssignmentType;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

public class AudienceWebscoreConfig {

    private String scoreName;
    private final List<AudienceWebscorePageRangeConfig> pageRangeConfigs = new ArrayList<>();
    private final TIntObjectHashMap<ScriptPreset> presets = new TIntObjectHashMap<>();
    private int[][] tilePageMap;

    public void addPreset(ScriptPreset preset) {
        presets.put(preset.getId(), preset);
    }

    public ScriptPreset getPreset(int id) {
        return presets.get(id);
    }

    public TIntObjectHashMap<ScriptPreset> getPresets() {
        return presets;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public List<AudienceWebscorePageRangeConfig> getPageRangeConfigs() {
        return pageRangeConfigs;
    }

    public int[][] getTilePageMap() {
        return tilePageMap;
    }

    public int getPageNo(int row, int col) {
        int rowIndex = row - 1;
        int colIndex = col - 1;
        if (rowIndex < 0 || rowIndex > tilePageMap.length) {
            return -1;
        }
        if (colIndex < 0 || colIndex > tilePageMap[0].length) {
            return -1;
        }
        return tilePageMap[rowIndex][colIndex];
    }

    public void addPageRangeConfig(AudienceWebscorePageRangeConfig pageRangeConfig) {
        pageRangeConfigs.add(pageRangeConfig);
    }

    public void populateTileConfigs() {
        if (tilePageMap == null) {
            int rowSize = getRowsSize();
            int colSize = getColsSize();
            tilePageMap = new int[rowSize][colSize];
        }

        for (AudienceWebscorePageRangeConfig pageRangeConfig : pageRangeConfigs) {
            int row = pageRangeConfig.getTileRow();

            IntRange cols = pageRangeConfig.getTileCols();
            int colStart = cols.getStart();
            int colEnd = cols.getEnd();
            List<Integer> pageIdx = new ArrayList<>();

            IntRange pageRanges = pageRangeConfig.getPageRange();
            WebAudienceScorePageRangeAssignmentType assignmentType = pageRangeConfig.getAssignmentType();

            int rowIndex = row - 1;
            int pageNo = pageRanges.getStart();
            int pIdx = 0;
            for (int i = colStart; i <= colEnd; i++) {
                int colIndex = i - 1;
                switch (assignmentType) {
                    case SEQ:
                        pageNo = pageRanges.getElement(pIdx);
                        pIdx++;
                        break;
                    case RND:
                        pageNo = pageRanges.getRndValueFromRange();
                        break;
                }
                tilePageMap[rowIndex][colIndex] = pageNo;
            }
        }
    }

    private int getRowsSize() {
        int out = 0;
        for (AudienceWebscorePageRangeConfig pageRangeConfig : pageRangeConfigs) {
            int row = pageRangeConfig.getTileRow();
            if (row > out) {
                out = row;
            }
        }
        return out;
    }

    private Integer getColsSize() {
        int out = 0;
        for (AudienceWebscorePageRangeConfig pageRangeConfig : pageRangeConfigs) {
            IntRange cols = pageRangeConfig.getTileCols();
            int end = cols.getEnd();
            if (end > out) {
                out = end;
            }
        }
        return out;
    }
}
