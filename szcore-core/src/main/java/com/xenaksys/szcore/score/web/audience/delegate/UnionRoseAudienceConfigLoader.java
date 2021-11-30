package com.xenaksys.szcore.score.web.audience.delegate;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.MultiIntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.score.web.audience.WebAudienceScorePageRangeAssignmentType;
import com.xenaksys.szcore.score.web.audience.config.AudienceWebscoreConfig;
import com.xenaksys.szcore.score.web.audience.config.AudienceWebscoreConfigLoader;
import com.xenaksys.szcore.score.web.audience.config.AudienceWebscorePageRangeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xenaksys.szcore.Consts.*;

public class UnionRoseAudienceConfigLoader extends AudienceWebscoreConfigLoader {
    static final Logger LOG = LoggerFactory.getLogger(UnionRoseAudienceConfigLoader.class);

    public UnionRoseAudienceWebscoreConfig load(String workingDir) throws Exception {
        UnionRoseAudienceWebscoreConfig config = new UnionRoseAudienceWebscoreConfig();
        load(workingDir, config);
        return config;
    }

    @Override
    public void loadDelegate(AudienceWebscoreConfig parent, Map<String, Object> configMap) {
        if (!(parent instanceof UnionRoseAudienceWebscoreConfig)) {
            return;
        }
        UnionRoseAudienceWebscoreConfig config = (UnionRoseAudienceWebscoreConfig) parent;

        List<Map<String, Object>> pageRangeMappings = getListOfMaps(CONFIG_PAGE_RANGE_MAPPING, configMap);
        for (Map<String, Object> pageRangeMapping : pageRangeMappings) {
            AudienceWebscorePageRangeConfig pageRangeConfig = new AudienceWebscorePageRangeConfig();

            Integer tileRow = getInteger(CONFIG_TILE_ROW, pageRangeMapping);
            if (tileRow == null) {
                throw new RuntimeException("loadWebScore: Invalid Tile Row");
            }
            pageRangeConfig.setTileRow(tileRow);

            Map<String, Object> tileColsConfig = getMap(CONFIG_TILE_COLS, pageRangeMapping);
            int start = getInteger(CONFIG_START, tileColsConfig);
            int end = getInteger(CONFIG_END, tileColsConfig);
            IntRange tileCols = new SequentalIntRange(start, end);
            pageRangeConfig.setTileCols(tileCols);

            List<Map<String, Object>> pageRanges = getListOfMaps(CONFIG_PAGE_RANGES, pageRangeMapping);
            List<IntRange> ranges = new ArrayList<>();
            for (Map<String, Object> pageRange : pageRanges) {
                int startTileCol = getInteger(CONFIG_START, pageRange);
                int endTileCol = getInteger(CONFIG_END, pageRange);
                IntRange pRange = new SequentalIntRange(startTileCol, endTileCol);
                ranges.add(pRange);
            }
            MultiIntRange multiRange = new MultiIntRange(ranges);
            pageRangeConfig.setPageRange(multiRange);

            String assignmentTypeStr = getString(CONFIG_ASSIGNMENT_TYPE, pageRangeMapping);
            WebAudienceScorePageRangeAssignmentType assignmentType = WebAudienceScorePageRangeAssignmentType.SEQ;
            if (assignmentTypeStr != null) {
                assignmentType = WebAudienceScorePageRangeAssignmentType.valueOf(assignmentTypeStr);
            }
            pageRangeConfig.setAssignmentType(assignmentType);

            config.addPageRangeConfig(pageRangeConfig);
        }

        config.populateTileConfigs();
    }

}
