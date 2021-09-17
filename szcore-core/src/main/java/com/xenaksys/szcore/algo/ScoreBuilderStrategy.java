package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.model.PageInfo;
import com.xenaksys.szcore.model.SectionInfo;
import com.xenaksys.szcore.score.BasicScore;
import com.xenaksys.szcore.web.WebClientInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBuilderStrategy implements ScoreStrategy {
    private final BasicScore szcore;
    private final ScoreBuilderStrategyConfig config;
    private final Map<String, SectionInfo> sectionInfos = new ConcurrentHashMap<>();
    private final List<String> sectionOrder = new ArrayList<>();
    private List<PageInfo> pageOrder = new ArrayList<>();
    private String[] instruments;
    private String defaultInstrument;
    private boolean isReady = false;

    public ScoreBuilderStrategy(BasicScore szcore, ScoreBuilderStrategyConfig config) {
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
    }

    private SectionInfo getOrCreateSectionInfo(final String section) {
        return sectionInfos.computeIfAbsent(section, k -> new SectionInfo(section));
    }

    public BasicScore getSzcore() {
        return szcore;
    }

    public ScoreBuilderStrategyConfig getConfig() {
        return config;
    }

    public List<String> getSections() {
        return config.getSections();
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
        sectionOrder.add(section);
        if(owner != null) {
            SectionInfo sectionInfo = getOrCreateSectionInfo(section);
            sectionInfo.setOwner(owner);
        }
        this.isReady = isBuildComplete();
    }

    public boolean isReady() {
        return isReady;
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
        return StrategyType.BUILDER;
    }

    public void setInstruments(String[] instruments) {
        this.instruments = instruments;
    }

    public void setDefaultInstrument(String instrumentDefault) {
        this.defaultInstrument = instrumentDefault;
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

    public String getSection(int pageNo) {
        int index = pageNo - 1;
        if(index < 0 || index >= pageOrder.size()) {
            return null;
        }
        PageInfo pageInfo = pageOrder.get(index);
        if(pageInfo == null) {
            return null;
        }
        return pageInfo.getSection();
    }

    public String getClientInstrument(String section, WebClientInfo client) {
        SectionInfo sectionInfo = sectionInfos.get(section);
        String instrument = sectionInfo.getClientInstrument(client.getClientId());
        if(instrument == null) {
            instrument = sectionInfo.getClientInstrument(client.getClientAddr());
        }
        return instrument;
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

    public Map<String, String> getSectionClientInstruments(String section) {
        if(section == null) {
            return null;
        }
        SectionInfo info = getSectionInfo(section);
        if(info == null) {
            return null;
        }
        return info.getClientInstruments();
    }
}