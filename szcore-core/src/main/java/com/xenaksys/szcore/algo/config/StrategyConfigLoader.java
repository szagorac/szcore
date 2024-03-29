package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.Consts;
import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.MultiIntRange;
import com.xenaksys.szcore.algo.SectionAssignmentType;
import com.xenaksys.szcore.algo.SequentalIntRange;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.Score;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.score.YamlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.xenaksys.szcore.Consts.CONFIG_ALL;
import static com.xenaksys.szcore.Consts.CONFIG_ASSIGNMENT_TYPE;
import static com.xenaksys.szcore.Consts.CONFIG_BOTTOM_STAVE_START_X;
import static com.xenaksys.szcore.Consts.CONFIG_BOTTOM_STAVE_X_REF;
import static com.xenaksys.szcore.Consts.CONFIG_BOTTOM_STAVE_Y_REF;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER1;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER2;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER3;
import static com.xenaksys.szcore.Consts.CONFIG_BUFFER4;
import static com.xenaksys.szcore.Consts.CONFIG_BUILDER_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_DX;
import static com.xenaksys.szcore.Consts.CONFIG_DY;
import static com.xenaksys.szcore.Consts.CONFIG_DYNAMIC_MOVEMENT_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_END;
import static com.xenaksys.szcore.Consts.CONFIG_EXT_RECT_DX;
import static com.xenaksys.szcore.Consts.CONFIG_EXT_RECT_DY;
import static com.xenaksys.szcore.Consts.CONFIG_EXT_RECT_HEIGHT;
import static com.xenaksys.szcore.Consts.CONFIG_EXT_RECT_MOD_HEIGHT;
import static com.xenaksys.szcore.Consts.CONFIG_EXT_RECT_MOD_WIDTH;
import static com.xenaksys.szcore.Consts.CONFIG_EXT_RECT_WIDTH;
import static com.xenaksys.szcore.Consts.CONFIG_GRANULATOR;
import static com.xenaksys.szcore.Consts.CONFIG_GROOVE;
import static com.xenaksys.szcore.Consts.CONFIG_ID;
import static com.xenaksys.szcore.Consts.CONFIG_INSTRUMENTS;
import static com.xenaksys.szcore.Consts.CONFIG_IS_ACTIVE;
import static com.xenaksys.szcore.Consts.CONFIG_IS_INTERRUPT_ON_PAGE_END;
import static com.xenaksys.szcore.Consts.CONFIG_IS_RND_ACTIVE;
import static com.xenaksys.szcore.Consts.CONFIG_MAX;
import static com.xenaksys.szcore.Consts.CONFIG_MAX_CONFIGS;
import static com.xenaksys.szcore.Consts.CONFIG_MIN_X_DISTANCE;
import static com.xenaksys.szcore.Consts.CONFIG_MIN_Y_DISTANCE;
import static com.xenaksys.szcore.Consts.CONFIG_MOVEMENTS;
import static com.xenaksys.szcore.Consts.CONFIG_NAME;
import static com.xenaksys.szcore.Consts.CONFIG_PAGES;
import static com.xenaksys.szcore.Consts.CONFIG_PAGE_NO;
import static com.xenaksys.szcore.Consts.CONFIG_PAGE_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_PAGE_RANGES;
import static com.xenaksys.szcore.Consts.CONFIG_PART;
import static com.xenaksys.szcore.Consts.CONFIG_PARTS;
import static com.xenaksys.szcore.Consts.CONFIG_PRESET;
import static com.xenaksys.szcore.Consts.CONFIG_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_RND_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_SCORE_NAME;
import static com.xenaksys.szcore.Consts.CONFIG_SCORE_PARTS;
import static com.xenaksys.szcore.Consts.CONFIG_SCRIPTS;
import static com.xenaksys.szcore.Consts.CONFIG_SECTIONS;
import static com.xenaksys.szcore.Consts.CONFIG_SECTIONS_ORDER;
import static com.xenaksys.szcore.Consts.CONFIG_SELECTION_RANGE;
import static com.xenaksys.szcore.Consts.CONFIG_START;
import static com.xenaksys.szcore.Consts.CONFIG_START_PAGE;
import static com.xenaksys.szcore.Consts.CONFIG_STOP_ON_MOVEMENT_END;
import static com.xenaksys.szcore.Consts.CONFIG_STOP_ON_SECTION_END;
import static com.xenaksys.szcore.Consts.CONFIG_TEXT_ELEMENTS;
import static com.xenaksys.szcore.Consts.CONFIG_TOP_STAVE_START_X;
import static com.xenaksys.szcore.Consts.CONFIG_TOP_STAVE_X_REF;
import static com.xenaksys.szcore.Consts.CONFIG_TOP_STAVE_Y_REF;
import static com.xenaksys.szcore.Consts.CONFIG_TRANSPOSITION_STRATEGY;
import static com.xenaksys.szcore.Consts.CONFIG_TXT;
import static com.xenaksys.szcore.Consts.CONFIG_WEB;
import static com.xenaksys.szcore.Consts.CONFIG_WEB_CONFIGS;
import static com.xenaksys.szcore.Consts.EMPTY;
import static com.xenaksys.szcore.Consts.NAME_FULL_SCORE;
import static com.xenaksys.szcore.Consts.STRATEGY_CONFIG_FILE_SUFFIX;
import static com.xenaksys.szcore.Consts.YAML_FILE_EXTENSION;

public class StrategyConfigLoader extends YamlLoader {
    static final Logger LOG = LoggerFactory.getLogger(StrategyConfigLoader.class);

    private static final String[] MAX_CONFIGS = {CONFIG_BUFFER1, CONFIG_BUFFER2, CONFIG_BUFFER3, CONFIG_BUFFER4, CONFIG_GRANULATOR, CONFIG_GROOVE};

    public static ScoreRandomisationStrategyConfig loadRndStrategyConfig(String workingDir, Score score) throws Exception {
        String path = workingDir + Consts.SLASH + STRATEGY_CONFIG_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }

        return loadRndStrategyConfig(file, score);
    }

    public static File loadFile(String workingDir) throws Exception {
        String path = workingDir + Consts.SLASH + STRATEGY_CONFIG_FILE_SUFFIX + YAML_FILE_EXTENSION;
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("loadStrategyConfig: Invalid Strategy Config File: " + path);
        }

        return file;
    }

    public static Map<String, Object> loadConfig(String workingDir) throws Exception {
        File file = loadFile(workingDir);
        return loadConfig(file);
    }

    public static Map<String, Object> loadConfig(File file) throws Exception {
        return loadYaml(file);
    }

    public static ScoreRandomisationStrategyConfig loadRndStrategyConfig(File file, Score score) throws Exception {
        Map<String, Object> configMap = loadYaml(file);
        return loadRndStrategyConfig(configMap, score);
    }

    public static TranspositionStrategyConfig loadTranspositionConfig(Map<String, Object> configMap, Score score) throws Exception {
        if(!configMap.containsKey(CONFIG_TRANSPOSITION_STRATEGY)) {
            return null;
        }

        TranspositionStrategyConfig config = new TranspositionStrategyConfig();
        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadStrategyConfig: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        Object yamlRndStrategyObj = configMap.get(CONFIG_TRANSPOSITION_STRATEGY);
        if (yamlRndStrategyObj == null) {
            return config;
        }
        Map<String, Object> transpositionStrategyConfig = (Map<String, Object>) yamlRndStrategyObj;

        List<InstrumentId> participatingScoreInsts = new ArrayList<>();
        Collection<Instrument> scoreInstruments = score.getInstruments();
        for (Instrument instrument : scoreInstruments) {
            if (!instrument.isAv() && !instrument.getName().equals(NAME_FULL_SCORE)) {
                participatingScoreInsts.add((InstrumentId) instrument.getId());
            }
        }

        Boolean isActiveConfig = getBoolean(CONFIG_IS_ACTIVE, transpositionStrategyConfig);
        if(isActiveConfig == null) {
            isActiveConfig = false;
        }
        config.setActive(isActiveConfig);


        Double topStaveYRef = getDouble(CONFIG_TOP_STAVE_Y_REF, transpositionStrategyConfig);
        if(topStaveYRef != null) {
            config.setTopStaveYRef(topStaveYRef);
        }

        Double topStaveXRef = getDouble(CONFIG_TOP_STAVE_X_REF, transpositionStrategyConfig);
        if(topStaveXRef != null) {
            config.setTopStaveXRef(topStaveXRef);
        }

        Double botStaveYRef = getDouble(CONFIG_BOTTOM_STAVE_Y_REF, transpositionStrategyConfig);
        if(botStaveYRef != null) {
            config.setBotStaveYRef(botStaveYRef);
        }

        Double botStaveXRef = getDouble(CONFIG_BOTTOM_STAVE_X_REF, transpositionStrategyConfig);
        if(botStaveXRef != null) {
            config.setBotStaveXRef(botStaveXRef);
        }

        Double minYDistance = getDouble(CONFIG_MIN_Y_DISTANCE, transpositionStrategyConfig);
        if(minYDistance != null) {
            config.setMinYdistance(minYDistance);
        }

        Double minXDistance = getDouble(CONFIG_MIN_X_DISTANCE, transpositionStrategyConfig);
        if(minXDistance != null) {
            config.setMinXdistance(minXDistance);
        }

        Double topStaveStartX = getDouble(CONFIG_TOP_STAVE_START_X, transpositionStrategyConfig);
        if(topStaveStartX != null) {
            config.setTopStaveStartX(topStaveStartX);
        }

        Double botStaveStarX = getDouble(CONFIG_BOTTOM_STAVE_START_X, transpositionStrategyConfig);
        if(botStaveStarX != null) {
            config.setBotStaveStartX(botStaveStarX);
        }

        Double extRectDx = getDouble(CONFIG_EXT_RECT_DX, transpositionStrategyConfig);
        if(extRectDx != null) {
            config.setExtRectDx(extRectDx);
        }

        Double extRectDy = getDouble(CONFIG_EXT_RECT_DY, transpositionStrategyConfig);
        if(extRectDy != null) {
            config.setExtRectDy(extRectDy);
        }

        Double extRectWidth = getDouble(CONFIG_EXT_RECT_WIDTH, transpositionStrategyConfig);
        if(extRectWidth != null) {
            config.setExtRectWidth(extRectWidth);
        }

        Double extRectHeight = getDouble(CONFIG_EXT_RECT_HEIGHT, transpositionStrategyConfig);
        if(extRectHeight != null) {
            config.setExtRectHeight(extRectHeight);
        }

        Double extRectModWidth = getDouble(CONFIG_EXT_RECT_MOD_WIDTH, transpositionStrategyConfig);
        if(extRectModWidth != null) {
            config.setExtRectModWidth(extRectModWidth);
        }

        Double extRectModHeight = getDouble(CONFIG_EXT_RECT_MOD_HEIGHT, transpositionStrategyConfig);
        if(extRectModHeight != null) {
            config.setExtRectModHeight(extRectModHeight);
        }

        List<Map<String, Object>> pageConfigs = getListOfMaps(CONFIG_PAGES, transpositionStrategyConfig);
        for (Map<String, Object> pageConfig : pageConfigs) {
            Integer pageNo = getInteger(CONFIG_PAGE_NO, pageConfig);
            if(pageNo == null) {
                continue;
            }
            String part = getString(CONFIG_PART, pageConfig);
            if(part == null) {
                continue;
            }
            TranspositionPageConfig pConf = new TranspositionPageConfig(pageNo, part);
            config.addPageConfig(pConf);

            List<Map<String, Object>> textElements = getListOfMaps(CONFIG_TEXT_ELEMENTS, pageConfig);
            for (Map<String, Object> textElement : textElements) {
                Double dx = getDouble(CONFIG_DX, textElement);
                if(dx == null) {
                    dx = 0.0;
                }
                Double dy = getDouble(CONFIG_DY, textElement);
                if(dy == null) {
                    dy = 0.0;
                }
                String txt = getString(CONFIG_TXT, textElement);
                TextElementConfig textElementConfig = new TextElementConfig(dx, dy, txt);
                pConf.addTextConfig(textElementConfig);
            }
        }

        return config;
    }

    public static ScoreRandomisationStrategyConfig loadRndStrategyConfig(Map<String, Object> configMap, Score score) throws Exception {
        if(!configMap.containsKey(CONFIG_RND_STRATEGY)) {
            return null;
        }
        ScoreRandomisationStrategyConfig config = new ScoreRandomisationStrategyConfig();
        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadStrategyConfig: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        Object yamlRndStrategyObj = configMap.get(CONFIG_RND_STRATEGY);
        if (yamlRndStrategyObj == null) {
            return config;
        }
        Map<String, Object> rndStrategyConfig = (Map<String, Object>) yamlRndStrategyObj;

        List<InstrumentId> participatingScoreInsts = new ArrayList<>();
        Collection<Instrument> scoreInstruments = score.getInstruments();
        for (Instrument instrument : scoreInstruments) {
            if (!instrument.isAv() && !instrument.getName().equals(NAME_FULL_SCORE)) {
                participatingScoreInsts.add((InstrumentId) instrument.getId());
            }
        }

        Boolean isActiveConfig = getBoolean(CONFIG_IS_ACTIVE, rndStrategyConfig);
        if(isActiveConfig == null) {
            isActiveConfig = false;
        }
        config.setActive(isActiveConfig);

        List<Map<String, Object>> instActiveRangesConfigs = getListOfMaps(CONFIG_PAGE_RANGES, rndStrategyConfig);
        for (Map<String, Object> instConfig : instActiveRangesConfigs) {
            List<InstrumentId> participatingInsts = new ArrayList<>();
            List<String> configInstruments = getStrList(CONFIG_INSTRUMENTS, instConfig);
            if(configInstruments != null) {
                for (String inst : configInstruments) {
                    if (CONFIG_ALL.equals(inst)) {
                        participatingInsts.addAll(participatingScoreInsts);
                        break;
                    }
                    for (InstrumentId instrumentId : participatingScoreInsts) {
                        if (inst.equals(instrumentId.getName())) {
                            participatingInsts.add(instrumentId);
                        }
                    }
                }
            }

            Map<String, Object> activeRangeConfig = getMap(CONFIG_RANGE, instConfig);
            Integer start =  null;
            Integer end =  null;
            if(activeRangeConfig != null) {
                start = getInteger(CONFIG_START, activeRangeConfig);
                end = getInteger(CONFIG_END, activeRangeConfig);
            }
            if(start == null || end == null) {
                LOG.error("loadRndStrategyConfig: invalid start - end range");
                continue;
            }

            IntRange activeRange = new SequentalIntRange(start, end);
            Boolean isActive = getBoolean(CONFIG_IS_RND_ACTIVE, instConfig);
            IntRange selRange = activeRange;
            if( isActive == null ) {
                isActive = false;
            }
            if (isActive) {
                List<IntRange> ranges = new ArrayList<>();
                List<Map<String, Object>> selectionRangeConfig = getListOfMaps(CONFIG_SELECTION_RANGE, instConfig);
                for (Map<String, Object> selectionRange : selectionRangeConfig) {
                    Integer selStart = getInteger(CONFIG_START, selectionRange);
                    Integer selEnd = getInteger(CONFIG_END, selectionRange);
                    if(selStart != null && selEnd != null) {
                        IntRange range = new SequentalIntRange(selStart, selEnd);
                        ranges.add(range);
                    }
                }
                selRange = new MultiIntRange(ranges);
            }

            RndPageRangeConfig pageRangeConfig = new RndPageRangeConfig(isActive, participatingInsts, activeRange, selRange);

            config.addPageRangeConfig(pageRangeConfig);
        }

        return config;
    }

    public static ScoreBuilderStrategyConfig loadBuilderStrategyConfig(Map<String, Object> configMap, Score score) throws Exception {
        if(!configMap.containsKey(CONFIG_BUILDER_STRATEGY)) {
            return null;
        }
        ScoreBuilderStrategyConfig config = new ScoreBuilderStrategyConfig();
        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadStrategyConfig: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        Object yamlBiulderStrategyObj = configMap.get(CONFIG_BUILDER_STRATEGY);
        if (yamlBiulderStrategyObj == null) {
            return config;
        }
        Map<String, Object> builderStrategyConfig = (Map<String, Object>) yamlBiulderStrategyObj;

        List<InstrumentId> participatingScoreInsts = new ArrayList<>();
        Collection<Instrument> scoreInstruments = score.getInstruments();
        for (Instrument instrument : scoreInstruments) {
            if (!instrument.isAv() && !instrument.getName().equals(NAME_FULL_SCORE)) {
                participatingScoreInsts.add((InstrumentId) instrument.getId());
            }
        }

        Boolean isActiveConfig = getBoolean(CONFIG_IS_ACTIVE, builderStrategyConfig);
        if(isActiveConfig == null) {
            isActiveConfig = false;
        }
        config.setActive(isActiveConfig);

        List<Object> sectionsConfig = getList(CONFIG_SECTIONS, builderStrategyConfig);
        if(sectionsConfig != null) {
            List<String> sections = new ArrayList<>();
            for (Object sectionConfig : sectionsConfig) {
                if (!(sectionConfig instanceof String)) {
                    LOG.error("loadBuilderStrategyConfig: invalid section name config");
                    continue;
                }
                String sectionName = (String) sectionConfig;
                sections.add(sectionName.trim());
            }
            config.addSections(sections);
        }

        String assignmentTypeConfig = getString(CONFIG_ASSIGNMENT_TYPE, builderStrategyConfig);
        if(assignmentTypeConfig != null) {
            SectionAssignmentType assignmentType = SectionAssignmentType.MANUAL;
            try {
                assignmentType = SectionAssignmentType.valueOf(assignmentTypeConfig.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                LOG.error("loadBuilderStrategyConfig: invalid assignment type config");
            }
            config.setAssignmentType(assignmentType);
        }

        Boolean isStopOnSectionEnd = getBoolean(CONFIG_STOP_ON_SECTION_END, builderStrategyConfig);
        if(isStopOnSectionEnd != null) {
            config.setStopOnSectionEnd(isStopOnSectionEnd);
        }

        List<Map<String, Object>> instActiveRangesConfigs = getListOfMaps(CONFIG_PAGE_RANGES, builderStrategyConfig);
        for (Map<String, Object> instConfig : instActiveRangesConfigs) {
            List<InstrumentId> participatingInsts = new ArrayList<>();
            List<String> configInstruments = getStrList(CONFIG_INSTRUMENTS, instConfig);
            if(configInstruments != null) {
                for (String inst : configInstruments) {
                    if (CONFIG_ALL.equals(inst)) {
                        participatingInsts.addAll(participatingScoreInsts);
                        break;
                    }
                    for (InstrumentId instrumentId : participatingScoreInsts) {
                        if (inst.equals(instrumentId.getName())) {
                            participatingInsts.add(instrumentId);
                        }
                    }
                }
            }

            Map<String, Object> activeRangeConfig = getMap(CONFIG_RANGE, instConfig);
            Integer start = null;
            Integer end = null;
            if (activeRangeConfig != null) {
                start = getInteger(CONFIG_START, activeRangeConfig);
                end = getInteger(CONFIG_END, activeRangeConfig);
            }
            if (start == null || end == null) {
                LOG.error("loadBuilderStrategyConfig: invalid start - end range");
                continue;
            }

            String sectionName = getString(CONFIG_NAME, instConfig);
            if (sectionName == null) {
                sectionName = EMPTY;
            }

            IntRange range = new SequentalIntRange(start, end);
            BuilderPageRangeConfig pageRangeConfig = new BuilderPageRangeConfig(participatingInsts, range, sectionName);

            config.addPageRangeConfig(pageRangeConfig);
        }

        return config;
    }

    public static DynamicMovementStrategyConfig loadDynamicMovementStrategyConfig(Map<String, Object> configMap, Score score) throws Exception {
        if(!configMap.containsKey(CONFIG_DYNAMIC_MOVEMENT_STRATEGY)) {
            return null;
        }
        DynamicMovementStrategyConfig config = new DynamicMovementStrategyConfig();
        Object yamlScoreName = configMap.get(CONFIG_SCORE_NAME);
        if (yamlScoreName == null) {
            throw new RuntimeException("loadDynamicMovementStrategyConfig: invalid score name");
        }
        String scoreName = (String) yamlScoreName;
        config.setScoreName(scoreName);

        Object yamlBiulderStrategyObj = configMap.get(CONFIG_DYNAMIC_MOVEMENT_STRATEGY);
        if (yamlBiulderStrategyObj == null) {
            return config;
        }
        Map<String, Object> movementStrategyConfig = (Map<String, Object>) yamlBiulderStrategyObj;

        List<InstrumentId> participatingScoreInsts = new ArrayList<>();
        Collection<Instrument> scoreInstruments = score.getInstruments();
        for (Instrument instrument : scoreInstruments) {
            if (!instrument.isAv() && !instrument.getName().equals(NAME_FULL_SCORE)) {
                participatingScoreInsts.add((InstrumentId) instrument.getId());
            }
        }

        Boolean isActiveConfig = getBoolean(CONFIG_IS_ACTIVE, movementStrategyConfig);
        if(isActiveConfig == null) {
            isActiveConfig = false;
        }
        config.setActive(isActiveConfig);


        List<Map<String, Object>> movementsConfig = getListOfMaps(CONFIG_MOVEMENTS, movementStrategyConfig);
        List<MovementConfig> movements = new ArrayList<>();
        for (Map<String, Object> movementConfig : movementsConfig) {
            MovementConfig movement = new MovementConfig();
            String name = getString(CONFIG_NAME, movementConfig);
            movement.setName(name);

            List<Map<String, Object>> sectionsConfig = getListOfMaps(CONFIG_SECTIONS, movementConfig);
            for (Map<String, Object> sectionConfig : sectionsConfig) {
                String sectionName = getString(CONFIG_NAME, sectionConfig);
                Map<String, Object> pageRangeConfig = getMap(CONFIG_PAGE_RANGE, sectionConfig);
                Integer start = null;
                Integer end = null;
                if (pageRangeConfig != null) {
                    start = getInteger(CONFIG_START, pageRangeConfig);
                    end = getInteger(CONFIG_END, pageRangeConfig);
                }
                if (start == null || end == null) {
                    LOG.error("loadDynamicMovementStrategyConfig: movement section invalid start - end range");
                    continue;
                }
                IntRange pageRange = new SequentalIntRange(start, end);
                List<String> partsConfig = getStrList(CONFIG_PARTS, sectionConfig);
                List<String> parts = new ArrayList<>();
                if(partsConfig != null) {
                    parts.addAll(partsConfig);
                }
                List<String> maxConfigs = getStrList(CONFIG_MAX, sectionConfig);
                List<String> max = new ArrayList<>();
                if (maxConfigs != null) {
                    max.addAll(maxConfigs);
                }
                List<String> webConfigs = getStrList(CONFIG_WEB, sectionConfig);
                List<String> web = new ArrayList<>();
                if (webConfigs != null) {
                    web.addAll(webConfigs);
                }
                boolean isInterrupt = false;
                Boolean isInterruptOnPageEnd = getBoolean(CONFIG_IS_INTERRUPT_ON_PAGE_END, sectionConfig);
                if (isInterruptOnPageEnd != null) {
                    isInterrupt = isInterruptOnPageEnd;
                }

                movement.addSectionConfig(sectionName, pageRange, parts, max, web, isInterrupt);
            }
            List<List<String>> sectionsOrderConfigs = getListOfStrList(CONFIG_SECTIONS_ORDER, movementConfig);
            movement.addSectionsOrder(sectionsOrderConfigs);
            Integer startPage = getInteger(CONFIG_START_PAGE, movementConfig);
            if (startPage != null) {
                movement.setStartPage(startPage);
            }

            movements.add(movement);
        }
        config.addMovements(movements);

        List<Object> partsConfig = getList(CONFIG_PARTS, movementStrategyConfig);
        if(partsConfig != null) {
            List<String> parts = new ArrayList<>();
            for (Object partConfig : partsConfig) {
                if (!(partConfig instanceof String)) {
                    LOG.error("loadBuilderStrategyConfig: invalid part config");
                    continue;
                }
                String partName = (String) partConfig;
                parts.add(partName.trim());
            }
            config.addParts(parts);
        }

        List<Object> scorePartsConfig = getList(CONFIG_SCORE_PARTS, movementStrategyConfig);
        if(scorePartsConfig != null) {
            List<String> scoreParts = new ArrayList<>();
            for (Object partConfig : scorePartsConfig) {
                if (!(partConfig instanceof String)) {
                    LOG.error("loadBuilderStrategyConfig: invalid score part config");
                    continue;
                }
                String partName = (String) partConfig;
                scoreParts.add(partName.trim());
            }
            config.addScoreParts(scoreParts);
        }

        Boolean isStopOnMovementEnd = getBoolean(CONFIG_STOP_ON_MOVEMENT_END, movementStrategyConfig);
        if (isStopOnMovementEnd != null) {
            config.setStopOnMovementEnd(isStopOnMovementEnd);
        }

        List<Map<String, Object>> maxConfigs = getListOfMaps(CONFIG_MAX_CONFIGS, movementStrategyConfig);
        List<ExternalScoreConfig> maxConfs = new ArrayList<>();
        for (Map<String, Object> maxConfig : maxConfigs) {
            ExternalScoreConfig max = new ExternalScoreConfig();
            String id = getString(CONFIG_ID, maxConfig);
            max.setId(id);
            Integer preset = getInteger(CONFIG_PRESET, maxConfig);
            if(preset != null) {
                max.setPreset(preset);
            }

            List<String> scripts = new ArrayList<>();
            List<String> scriptsConfig = getStrList(CONFIG_SCRIPTS, maxConfig);
            if(scriptsConfig != null) {
                scripts.addAll(scriptsConfig);
            }
            max.setScripts(scripts);

            Map<String, String> targetValues = new HashMap<>();
            for(String target : MAX_CONFIGS) {
                String targetConf = getString(target, maxConfig);
                if(targetConf != null) {
                    targetValues.put(target, targetConf);
                }
            }
            max.setTargetValues(targetValues);
            maxConfs.add(max);
        }
        config.addMaxConfigs(maxConfs);

        List<Map<String, Object>> webConfigs = getListOfMaps(CONFIG_WEB_CONFIGS, movementStrategyConfig);
        List<ExternalScoreConfig> webConfs = new ArrayList<>();
        for (Map<String, Object> webConfig : webConfigs) {
            ExternalScoreConfig web = new ExternalScoreConfig();
            String id = getString(CONFIG_ID, webConfig);
            web.setId(id);
            Integer preset = getInteger(CONFIG_PRESET, webConfig);
            if(preset != null) {
                web.setPreset(preset);
            }

            List<String> scripts = new ArrayList<>();
            List<String> scriptsConfig = getStrList(CONFIG_SCRIPTS, webConfig);
            if(scriptsConfig != null) {
                scripts.addAll(scriptsConfig);
            }
            web.setScripts(scripts);
            webConfs.add(web);
        }
        config.addWebConfigs(webConfs);

        return config;
    }
}
