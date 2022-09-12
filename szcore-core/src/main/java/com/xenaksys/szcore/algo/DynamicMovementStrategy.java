package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.DynamicMovementStrategyConfig;
import com.xenaksys.szcore.algo.config.ExternalScoreConfig;
import com.xenaksys.szcore.algo.config.MovementConfig;
import com.xenaksys.szcore.model.ExtScoreInfo;
import com.xenaksys.szcore.model.Instrument;
import com.xenaksys.szcore.model.MaxScoreInfo;
import com.xenaksys.szcore.model.MovementInfo;
import com.xenaksys.szcore.model.MovementSectionInfo;
import com.xenaksys.szcore.model.Page;
import com.xenaksys.szcore.model.id.InstrumentId;
import com.xenaksys.szcore.score.BasicScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicMovementStrategy implements ScoreStrategy {
    static final Logger LOG = LoggerFactory.getLogger(DynamicMovementStrategy.class);
    private static final long RECALC_TIME_LIMIT = 1000 * 3;

    private final BasicScore szcore;
    private final DynamicMovementStrategyConfig config;
    private final Map<String, MovementInfo> movementInfos = new ConcurrentHashMap<>();
    private final List<String> movementOrder = new ArrayList<>();
    private String[] instruments;
    private String[] dynamicParts;
    private String defaultPart;
    private boolean isReady = false;
    private String currentMovement;
    private String nextMovement;
    private volatile String nextSectionOverride;
    private volatile boolean isStop;
    private volatile boolean isCurrentSectionOverride;
    private volatile SelectionStrategy selectionStrategy;
    private int lastScorePage;
    private int sourcePageNo = 0;
    private long lastSectionRecalc = 0L;
    private long lastPageRecalc = 0L;
    protected long lastSourcePageRecalc = 0L;
    protected boolean isUpdateClients = false;

    public DynamicMovementStrategy(BasicScore szcore, DynamicMovementStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        if (config == null) {
            return;
        }
        List<MovementConfig> movementsConfig = getMovementConfigs();
        if (movementsConfig == null) {
            return;
        }
        for (MovementConfig movementConfig : movementsConfig) {
            MovementInfo movementInfo = getOrCreateMovementInfo(movementConfig);
            movementInfos.put(movementInfo.getMovementId(), movementInfo);
            movementOrder.add(movementInfo.getMovementId());
        }
        if (movementsConfig.size() > 0) {
            this.currentMovement = movementsConfig.get(0).getName();
        }

        isReady = true;
    }

    private MovementInfo getOrCreateMovementInfo(final MovementConfig movementConfig) {
        if (movementInfos.containsKey(movementConfig.getName())) {
            return movementInfos.get(movementConfig.getName());
        }
        MovementInfo movementInfo = new MovementInfo(movementConfig.getName());
        Collection<MovementConfig.SectionConfig> sectionConfigs = movementConfig.getSections();
        for (MovementConfig.SectionConfig sectionConfig : sectionConfigs) {
            MovementSectionInfo sectionInfo = new MovementSectionInfo(sectionConfig.getName());
            sectionInfo.setPageRange(sectionConfig.getRange());
            sectionInfo.setActive(false);
            sectionInfo.setParts(sectionConfig.getParts());
            movementInfo.addSection(sectionInfo);

            List<ExtScoreInfo> maxInfos = new ArrayList<>();
            List<String> maxConfigIds = sectionConfig.getMaxConfig();
            for(String maxConfigId : maxConfigIds) {
                ExternalScoreConfig conf = config.getMaxConfig(maxConfigId);
                if(conf != null) {
                    MaxScoreInfo maxInfo = new MaxScoreInfo(maxConfigId);
                    maxInfo.setPreset(conf.getPreset());
                    maxInfo.setScripts(conf.getScripts());
                    Map<String, String> targetValues = conf.getTargetValues();
                    maxInfo.setTargetValues(targetValues);
                    maxInfos.add(maxInfo);
                }

            }
            sectionInfo.setMaxConfigs(maxInfos);

            List<ExtScoreInfo> webInfos = new ArrayList<>();
            List<String> webConfigIds = sectionConfig.getWebConfig();
            for(String webConfigId : webConfigIds) {
                ExternalScoreConfig conf = config.getWebConfig(webConfigId);
                if(conf != null) {
                    ExtScoreInfo webInfo = new ExtScoreInfo(webConfigId);
                    webInfo.setPreset(conf.getPreset());
                    webInfo.setScripts(conf.getScripts());
                    Map<String, String> targetValues = conf.getTargetValues();
                    webInfo.setTargetValues(targetValues);
                    webInfos.add(webInfo);
                }
            }
            sectionInfo.setWebConfigs(webInfos);
            sectionInfo.setInterruptOnPageEnd(sectionConfig.isInterruptOnPageEnd());
        }
        movementInfo.addSectionOrder(movementConfig.getSectionsOrder());
        movementInfo.setStartPage(movementConfig.getStartPage());
        movementInfo.addScoreParts(getScoreParts());

        movementInfos.put(movementInfo.getMovementId(), movementInfo);
        return movementInfo;
    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public DynamicMovementStrategyConfig getConfig() {
        return config;
    }

    public List<MovementConfig> getMovementConfigs() {
        return config.getMovements();
    }

    public List<String> getScoreParts() {
        return config.getScoreParts();
    }

    public boolean isScorePart(String part) {
        return config.getScoreParts().contains(part);
    }

    public List<String> getMovementOrder() {
        return movementOrder;
    }

    public boolean isSectionOwned(String section) {
        return movementInfos.containsKey(section) && movementInfos.get(section) != null;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public boolean isActive() {
        return config.isActive();
    }

    private boolean isBuildComplete() {
        List<MovementConfig> movements = getMovementConfigs();
        if (movements == null) {
            return true;
        }

        for (MovementConfig movement : movements) {
            if (!movementOrder.contains(movement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.DYNAMIC;
    }

    public void setInstruments(String[] instruments) {
        this.instruments = instruments;
    }

    public String[] getInstruments() {
        return instruments;
    }

    public List<String> getCurrentSectionParts() {
        MovementSectionInfo sectionInfo = getCurrentMovementSection();
        if(sectionInfo == null) {
            return null;
        }
        return sectionInfo.getParts();
    }

    public void setDynamicParts(String[] instruments) {
        this.dynamicParts = instruments;
    }

    public String[] getDynamicParts() {
        return dynamicParts;
    }

    public void setDefaultPart(String instrumentDefault) {
        this.defaultPart = instrumentDefault;
    }

    public String getDefaultPart() {
        return defaultPart;
    }

    public void addClientId(String clientId) {
        if (clientId == null) {
            return;
        }
        if (defaultPart == null) {
            return;
        }
        for (MovementInfo movementInfo : movementInfos.values()) {
            movementInfo.addClientIdDefaultPart(clientId, defaultPart);
        }
    }

    public void addClientInstrument(String clientId, String instrumentId) {
        String mov = null;
        String section = null;
        MovementInfo movementInfo = getCurrentMovementInfo();
        if(movementInfo != null) {
            mov = movementInfo.getMovementId();
        }
        MovementSectionInfo sectionInfo = getCurrentMovementSection();
        if(sectionInfo != null) {
            section = sectionInfo.getSectionId();
        }

        addClientInstrument(mov, section, clientId, instrumentId);
    }

    public void addClientInstrument(String movement, String section, String clientId, String instrumentId) {
        if (movement == null || section == null || clientId == null || instrumentId == null) {
            return;
        }
        MovementInfo movementInfo = getMovementInfo(movement);
        if (movementInfo == null) {
            return;
        }
        movementInfo.addClientPart(clientId, instrumentId, section);
    }

    public MovementInfo getMovementInfo(String movementId) {
        if (movementId == null) {
            return null;
        }
        return movementInfos.get(movementId);
    }

    public List<MovementInfo> getMovementInfos() {
        return new ArrayList<>(movementInfos.values());
    }

    public boolean isStopOnMovementEnd() {
        return config.isStopOnMovementEnd();
    }


    public MovementSectionInfo getCurrentMovementSection() {
        if(currentMovement == null) {
            return  null;
        }
        MovementInfo movementInfo = getMovement(currentMovement);
        return movementInfo.getCurrentSectionInfo();
    }

    public List<String> getPartClients(String instrument) {
        MovementSectionInfo sectionInfo = getCurrentMovementSection();
        return sectionInfo.getPartClients().get(instrument);
    }

    public Map<String, List<String>> getPartClientsMap() {
        MovementSectionInfo sectionInfo = getCurrentMovementSection();
        return sectionInfo.getPartClients();
    }

    public String getCurrentSection() {
        MovementInfo movementInfo = getCurrentMovementInfo();
        if(movementInfo != null) {
            return movementInfo.getCurrentSection();
        }
        return null;
    }

    public String getNextSection() {
        MovementInfo movementInfo = getCurrentMovementInfo();
        if(movementInfo != null) {
            return movementInfo.getNextSection();
        }
        return null;
    }

    public String getCurrentMovement() {
        return currentMovement;
    }

    public MovementInfo getCurrentMovementInfo() {
        if(currentMovement == null) {
            return null;
        }
        return movementInfos.get(currentMovement);
    }

    public void setCurrentMovement(String currentMovement) {
        if(currentMovement == null || currentMovement.isEmpty()) {
            return;
        }
        this.currentMovement = currentMovement;
        setNextMovement();
    }

    public void setNextMovement() {
        if (currentMovement == null) {
            return;
        }
        int idx = movementOrder.indexOf(currentMovement);
        if (idx < 0 || idx >= movementOrder.size() - 1) {
            nextMovement = null;
            return;
        }
        idx++;
        nextMovement = movementOrder.get(idx);
    }

    public String getNextMovement() {
        return nextMovement;
    }

    public void onMovementStart(String movement) {
        if (currentMovement != null && !currentMovement.equals(movement)) {
            LOG.error("onSectionStart: Unexpected movement start: {}, expected: {}", movement, currentMovement);
        }
        MovementInfo movementInfo = movementInfos.get(movement);
        movementInfo.setActive(true);
        setNextMovement();
    }

    public void onMovementEnd() {
        onMovementEnd(currentMovement);
    }

    public void onMovementEnd(String movement) {
        if (currentMovement != null && !currentMovement.equals(movement)) {
            LOG.error("onSectionEnd: Unexpected movement end: {}, expected: {}", movement, currentMovement);
        }
        MovementInfo sectionInfo = movementInfos.get(movement);
        sectionInfo.setActive(false);
        setCurrentMovement(getNextMovement());
    }

    public void reset() {
        isReady = false;
        movementInfos.clear();
        movementOrder.clear();
    }

    public MovementInfo getMovement(String mvtName) {
        return movementInfos.get(mvtName);
    }

    public void setCurrentSection(String sectionName) {
        if(sectionName == null || sectionName.isEmpty()) {
            return;
        }
        String currentMov = getCurrentMovement();
        if(currentMov == null) {
            LOG.error("setCurrentSection: invalid current movement for section: {}", sectionName);
            return;
        }
        MovementInfo movementInfo = movementInfos.get(currentMov);
        if(movementInfo == null) {
            LOG.error("setCurrentSection: invalid movement info for section: {}", sectionName);
            return;
        }
        String current = movementInfo.getCurrentSection();
        if(current == null || isCurrentSectionOverride) {
            movementInfo.setCurrentSection(sectionName);
            MovementSectionInfo currentSectionInfo = movementInfo.getCurrentSectionInfo();
            currentSectionInfo.setActive(true);
        }
    }

    public void setCurrentSectionOrderIndex(Integer orderIndex) {
        if(orderIndex == null || orderIndex < 0) {
            return;
        }
        String currentMov = getCurrentMovement();
        if(currentMov == null) {
            LOG.error("setCurrentSectionOrderIndex: invalid current movement");
            return;
        }
        MovementInfo movementInfo = movementInfos.get(currentMov);
        if(movementInfo == null) {
            LOG.error("setCurrentSectionOrderIndex: invalid movement info");
            return;
        }
        movementInfo.setCurrentSectionOrderIndex(orderIndex);
    }

    public void setStopMovement(boolean isStop) {
        MovementInfo movementInfo = getCurrentMovementInfo();
        if(movementInfo == null) {
            return;
        }
        setStop(isStop);
    }

    public int getSectionEndPage() {
        MovementSectionInfo sectionInfo = getCurrentMovementSection();
        if(sectionInfo == null) {
            return -1;
        }
        return sectionInfo.getPlayEndPageNo();
    }

    public void setNextSection() {
        if(!isRecalcTime(lastSectionRecalc)) {
            return;
        }
        MovementInfo mvt = getCurrentMovementInfo();
        if(mvt == null) {
            LOG.error("setNextSection: invalid current movement");
            return;
        }

        if(selectionStrategy == null) {
            selectionStrategy = SelectionStrategy.HIGHEST_VOTE;
        }
        switch (selectionStrategy) {
            case HIGHEST_VOTE:
                String highestVoteSection = mvt.getHighestVoteSection();
                mvt.setNextSection(highestVoteSection);
                LOG.info("setNextSection: HIGHEST_VOTE, highestVoteSection {}", highestVoteSection);
                lastSectionRecalc = System.currentTimeMillis();
                break;
            case OVERRIDE:
                if(nextSectionOverride == null) {
                    LOG.error("setNextSection: OVERRIDE, invalid next section override, using current section {}", mvt.getCurrentSection());
                    nextSectionOverride = mvt.getCurrentSection();
                }
                mvt.setNextSection(nextSectionOverride);
                LOG.info("setNextSection: OVERRIDE, nextSectionOverride {}", nextSectionOverride);
                lastSectionRecalc = System.currentTimeMillis();
                break;
            default:
                LOG.error("setNextSection: Unknown selection strategy {}", selectionStrategy);
        }
    }

    public boolean isRecalcTime(long lastRecalcTime) {
        long now = System.currentTimeMillis();
        long diff = now - (lastRecalcTime + RECALC_TIME_LIMIT);
        return diff > 0;
    }

    public String getNextSectionOverride() {
        return nextSectionOverride;
    }

    public void setNextSectionOverride(String nextSectionOverride) {
        if(nextSectionOverride == null || nextSectionOverride.isEmpty()) {
            return;
        }
        MovementInfo mvtInfo = getCurrentMovementInfo();
        if(mvtInfo == null) {
            LOG.error("setNextSectionOverride: Invalid current movement");
            return;
        }
        MovementSectionInfo section = mvtInfo.getSection(nextSectionOverride);
        if(section == null) {
            LOG.error("setNextSectionOverride: Invalid next section override: {}", nextSectionOverride);
            return;
        }
        LOG.info("setNextSectionOverride: setting next section override: {}", nextSectionOverride);
        this.nextSectionOverride = nextSectionOverride;
    }

    public void setNextSectionOverride(Boolean isSectionOverride) {
        if(isSectionOverride == null) {
            return;
        }
        if(isSectionOverride) {
            setSelectionStrategy(SelectionStrategy.OVERRIDE);
        } else {
            setSelectionStrategy(SelectionStrategy.HIGHEST_VOTE);
        }

        LOG.info("setNextSectionOverride: SelectionStrategy: {}", selectionStrategy);
    }

    public void setCurrentSectionOverride(Boolean isSectionOverride) {
        if(isSectionOverride == null) {
            return;
        }
        this.isCurrentSectionOverride = isSectionOverride;
        LOG.info("setCurrentSectionOverride: isSectionOverride: {}", isSectionOverride);
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public int getNextSectionStartPage() {
        MovementInfo mvtInfo = getCurrentMovementInfo();
        if(mvtInfo == null) {
            LOG.error("getNextSectionStartPage: Invalid current movement");
            return -1;
        }
        return mvtInfo.getNextSectionStartPage();
    }

    public String getNextSectionName() {
        MovementInfo mvtInfo = getCurrentMovementInfo();
        if(mvtInfo == null) {
            LOG.error("getNextSectionStartPage: Invalid current movement");
            return null;
        }
        return mvtInfo.getNextSection();
    }

    public int getCurrentSectionNextPage() {
        MovementInfo mvtInfo = getCurrentMovementInfo();
        if(mvtInfo == null) {
            LOG.error("getCurrentSectionNextPage: Invalid current movement");
            return -1;
        }
        return mvtInfo.getCurrentSectionNextPage();
    }

    public void onPageStart(int currentPage) {
        if(!isRecalcTime(lastPageRecalc)) {
            isUpdateClients = false;
            return;
        }
        MovementInfo mvtInfo = getCurrentMovementInfo();
        if(mvtInfo == null) {
            LOG.error("onPageStart: Invalid current movement");
            return;
        }
        isUpdateClients =  mvtInfo.onPageStart(currentPage);
        lastPageRecalc = System.currentTimeMillis();
    }

    public int calcSourcePage(int currentPageNo) {
        if(!isRecalcTime(lastSourcePageRecalc)) {
            isUpdateClients = false;
            return this.sourcePageNo;
        }

        int sourcePageNo = 0;
        MovementSectionInfo sectionInfo = getCurrentMovementSection();
        int sectionPlayStartPage = sectionInfo.getPlayStartPageNo();
        int sectionPlayEndPage = sectionInfo.getPlayEndPageNo();
        int sectionPageDuration = sectionPlayEndPage - sectionPlayStartPage;
        boolean isInterrupt = sectionInfo.isInterruptOnPageEnd();
        if(sectionPageDuration < 0) {
            LOG.error("Unexpected section page duration: {}", sectionPageDuration);
        }
        int nextPageNo = currentPageNo + 1;
        if(currentPageNo < sectionPlayStartPage) {
            LOG.error("prepareNextDynamicStrategyPage: Unexpected current page: {} before current section start {}", currentPageNo, sectionPlayStartPage);
            sourcePageNo = sectionInfo.getStartPageNo();
            sectionInfo.setActive(true);
        } else if(currentPageNo == sectionPlayStartPage ) {
            if(sectionPageDuration == 0 || isInterrupt) {
                setNextSection();
                String nextSection = getNextSectionName();
                if (sectionInfo.getSectionId().equals(nextSection)){
                    sourcePageNo = processCurrentSectionNextPage(sectionInfo);
                } else {
                    sectionInfo.setActive(false);
                    sourcePageNo = getNextSectionStartPage();
                }
                LOG.info("prepareNextDynamicStrategyPage: sectionPageDuration == 0 sourcePageNo: {} nextPageNo {}", sourcePageNo, nextPageNo);
            } else {
                sourcePageNo = processCurrentSectionNextPage(sectionInfo);
                LOG.info("prepareNextDynamicStrategyPage: sectionPageDuration != 0 sourcePageNo: {} nextPageNo {}", sourcePageNo, nextPageNo);
            }
        } else {
            if(sectionPlayEndPage == currentPageNo ) {
                sourcePageNo = processNextSectionStartPage(sectionInfo);
                LOG.info("prepareNextDynamicStrategyPage: sectionPlayEndPage == currentPage sourcePageNo: {} nextPageNo {}", sourcePageNo, nextPageNo);
            } else {
                // currentPage > sectionPlayStartPage
                sourcePageNo = processCurrentSectionNextPage(sectionInfo);
                LOG.info("prepareNextDynamicStrategyPage: currentPage > sectionPlayStartPage sourcePageNo: {} nextPageNo {}", sourcePageNo, nextPageNo);
            }
        }
        if(this.sourcePageNo != sourcePageNo) {
            isUpdateClients = true;
        }
        this.sourcePageNo = sourcePageNo;
        this.lastSourcePageRecalc = System.currentTimeMillis();
        return sourcePageNo;
    }

    public boolean isUpdateClients() {
        return isUpdateClients;
    }

    private int processCurrentSectionNextPage(MovementSectionInfo sectionInfo) {
        sectionInfo.setActive(true);
        return getCurrentSectionNextPage();
    }

    private int processNextSectionStartPage(MovementSectionInfo sectionInfo) {
        setNextSection();
        sectionInfo.setActive(false);
        return getNextSectionStartPage();
    }

    public void setLastScorePage() {
        Instrument defaultInst = szcore.getInstrument(defaultPart);
        if(defaultInst == null) {
            return;
        }
        Page lastPage = szcore.getLastInstrumentPage(defaultInst.getId());
        if(lastPage == null) {
            return;
        }
        lastScorePage = lastPage.getPageNo();
    }

    public void deleteTempPages() {
        if(lastScorePage <= 0) {
            return;
        }

        Collection<Instrument> instruments = szcore.getInstruments();
        for(Instrument instrument : instruments) {
            Page lastPage = szcore.getLastInstrumentPage(instrument.getId());
            int lastPageNo = lastPage.getPageNo();
            if(lastPageNo == lastScorePage) {
                continue;
            }
            for(int i = lastScorePage + 1; i <= lastPageNo; i++) {
                Page page = szcore.getPageNo(i, (InstrumentId) instrument.getId());
                szcore.deletePage(page);
            }
        }
    }

    public void resetOnNewPosition() {
        setStop(false);
        deleteTempPages();
        for(MovementInfo movementInfo : movementInfos.values()) {
            movementInfo.resetOnNewPosition();
        }
    }

    enum SelectionStrategy {
        OVERRIDE, HIGHEST_VOTE
    }
}
