package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.DynamicMovementStrategyConfig;
import com.xenaksys.szcore.algo.config.MovementConfig;
import com.xenaksys.szcore.model.PageInfo;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.web.WebClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicMovementStrategy implements ScoreStrategy {
    static final Logger LOG = LoggerFactory.getLogger(DynamicMovementStrategy.class);

    private final BasicScore szcore;
    private final DynamicMovementStrategyConfig config;
    private final Map<String, SectionInfo> sectionInfos = new ConcurrentHashMap<>();
    private final List<String> sectionOrder = new ArrayList<>();
    private List<PageInfo> pageOrder = new ArrayList<>();
    private String[] instruments;
    private String[] dynamicInstruments;
    private String defaultInstrument;
    private boolean isReady = false;
    private String currentSection;
    private int currentSectionIndex;
    private String nextSection;

    public DynamicMovementStrategy(BasicScore szcore, DynamicMovementStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        if(config == null) {
            return;
        }
        List<String> sections = getSections();
        if(sections == null) {
            return;
        }
        for(String section : sections) {
            SectionInfo sectionInfo = getOrCreateSectionInfo(section);
            sectionInfo.setPageRange(config.getSectionPageRange(section));
        }
        if(sections.size() > 0) {
            this.currentSection = sections.get(0);
        } else {
            isReady = true;
        }
    }

    private SectionInfo getOrCreateSectionInfo(final String section) {
        return sectionInfos.computeIfAbsent(section, k -> new SectionInfo(section));
    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public DynamicMovementStrategyConfig getConfig() {
        return config;
    }

    public List<MovementConfig> getMovements() {
        return config.getMovements();
    }

    public List<String> getSectionOrder() {
        return sectionOrder;
    }

    public boolean isSectionOwned(String section) {
        return sectionInfos.containsKey(section) && sectionInfos.get(section) != null;
    }

    public void appendSection(String section, String owner) {
        if(section == null) {
            return;
        }
        if(!sectionOrder.contains(section)) {
            sectionOrder.add(section);
        }
        if(owner != null) {
            SectionInfo sectionInfo = getOrCreateSectionInfo(section);
            sectionInfo.setOwner(owner);
        }
        String first = sectionOrder.get(0);
        if(!first.equals(this.currentSection)) {
            this.currentSection = first;
        }
        this.isReady = isBuildComplete();
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public boolean isActive() {
        return config.isActive();
    }

    private boolean isBuildComplete() {
        List<String> sections = getSections();
        if(sections == null) {
            return true;
        }

        for(String section : sections) {
            if(!sectionOrder.contains(section)) {
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

    public void setDynamicInstruments(String[] instruments) {
        this.dynamicInstruments = instruments;
    }

    public String[] getDynamicInstruments() {
        return dynamicInstruments;
    }

    public void setDefaultInstrument(String instrumentDefault) {
        this.defaultInstrument = instrumentDefault;
    }

    public String getDefaultInstrument() {
        return defaultInstrument;
    }

    public void addClientId(String clientId) {
        if(clientId == null) {
            return;
        }
        if(defaultInstrument == null) {
            return;
        }
        for(SectionInfo sectionInfo : sectionInfos.values()) {
            String instrument = sectionInfo.getClientInstrument(clientId);
            if(instrument == null) {
                sectionInfo.addClientInstrument(clientId, defaultInstrument);
            }
        }
    }

    public void addClientInstrumentAndResetOthersToDefault(String section, String clientId, String instrumentId) {
        addClientInstrument(section, clientId, instrumentId);
        resetOtherClientsToDefaultInstrument(section, clientId);
    }

    public void addClientInstrument(String section, String clientId, String instrumentId) {
        if(section == null || clientId == null || instrumentId == null) {
            return;
        }
        SectionInfo info = getSectionInfo(section);
        if(info == null) {
            return;
        }
        info.addClientInstrument(clientId, instrumentId);
    }

    public void resetOtherClientsToDefaultInstrument(String section, String clientId) {
        if(section == null || clientId == null || defaultInstrument == null) {
            return;
        }
        SectionInfo info = getSectionInfo(section);
        if(info == null) {
            return;
        }
        Set<String> clients = info.getClients();
        for(String client : clients) {
            if(!client.equals(clientId)) {
                info.addClientInstrument(client, defaultInstrument);
            }
        }
    }

    public String getSectionOwner(String section) {
        if(section == null) {
            return null;
        }
        SectionInfo info = getSectionInfo(section);
        if(info == null) {
            return null;
        }
        return info.getOwner();
    }

    public SectionInfo getSectionInfo(String section) {
        if(section == null) {
            return null;
        }
        return sectionInfos.get(section);
    }

    public List<SectionInfo> getSectionInfos() {
        return new ArrayList<>(sectionInfos.values());
    }

    public List<String> getOrphanSections() {
        List<String> out = new ArrayList<>();
        List<SectionInfo> infos = getSectionInfos();
        for(SectionInfo sectionInfo : infos) {
            if (sectionInfo.getOwner() == null) {
                out.add(sectionInfo.getSectionId());
            }
        }
        return out;
    }

    public void setPageOrder() {
        pageOrder.clear();
        int pageNo = 0;
        for(String section : sectionOrder) {
            SectionInfo sectionInfo = getSectionInfo(section);
            IntRange intRange = sectionInfo.getPageRange();
            if(intRange == null) {
                continue;
            }
            int[] range = intRange.getFullRange();
            for (int j : range) {
                pageNo++;
                PageInfo pageInfo = new PageInfo(pageNo, j, section);
                pageOrder.add(pageInfo);
            }
        }
    }

    public List<String> getSectionsForPageNo(int pageNo) {
        List<String> out = new ArrayList<>();

        for(PageInfo pageInfo : pageOrder) {
            if(pageInfo.getDisplayPageNo() == pageNo) {
                out.add(pageInfo.getSection());
            }
        }
        return out;
    }

    public String getClientInstrument(String section, WebClientInfo client) {
        SectionInfo sectionInfo = sectionInfos.get(section);
        String instrument = sectionInfo.getClientInstrument(client.getClientId());
        if(instrument == null) {
            instrument = sectionInfo.getClientInstrument(client.getClientAddr());
        }
        return instrument;
    }

    public Map<String, List<String>> getInstrumentClientsMap(String section) {
        SectionInfo sectionInfo = sectionInfos.get(section);
        return sectionInfo.getInstrumentClients();
    }

    public List<String> getInstrumentClients(String section, String instrument) {
        SectionInfo sectionInfo = sectionInfos.get(section);
        return sectionInfo.getInstrumentClients().get(instrument);
    }

    public IntRange getSectionPageRange(String section) {
        if(section == null) {
            return null;
        }
        SectionInfo info = getSectionInfo(section);
        if(info == null) {
            return null;
        }
        return info.getPageRange();
    }

    public boolean isStopOnSectionEnd() {
        return config.isStopOnSectionEnd();
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(String currentSection) {
        this.currentSection = currentSection;
        setNextSection();
    }

    public void setNextSection() {
        if(currentSection == null) {
            return;
        }
        int idx = sectionOrder.indexOf(currentSection);
        if(idx < 0 || idx >= sectionOrder.size() -1 ) {
            nextSection = null;
            return;
        }
        idx++;
        nextSection = sectionOrder.get(idx);
    }

    public String getNextSection() {
        return nextSection;
    }

    public void onSectionStart(String section) {
        if(currentSection != null && !currentSection.equals(section)) {
            LOG.error("onSectionStart: Unexpected section start: {}, expected: {}", section, currentSection);
        }
        SectionInfo sectionInfo = sectionInfos.get(section);
        sectionInfo.setActive(true);
        setNextSection();
    }

    public void onSectionEnd() {
        onSectionEnd(currentSection);
    }

    public void onSectionEnd(String section) {
        if(currentSection != null && !currentSection.equals(section)) {
            LOG.error("onSectionEnd: Unexpected section end: {}, expected: {}", section, currentSection);
        }
        SectionInfo sectionInfo = sectionInfos.get(section);
        sectionInfo.setActive(false);
        setCurrentSection(getNextSection());
    }

    public void shuffleSectionOrder() {
        if(sectionOrder.size() > 1) {
            Collections.shuffle(sectionOrder);
            setCurrentSection(sectionOrder.get(0));
        }
    }

    public void reset() {
        isReady = false;
        sectionInfos.clear();
        sectionOrder.clear();
        pageOrder.clear();
    }
}
