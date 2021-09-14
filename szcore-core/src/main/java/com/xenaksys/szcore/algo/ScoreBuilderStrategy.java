package com.xenaksys.szcore.algo;

import com.xenaksys.szcore.algo.config.ScoreBuilderStrategyConfig;
import com.xenaksys.szcore.score.BasicScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreBuilderStrategy implements ScoreStrategy {
    private final BasicScore szcore;
    private final ScoreBuilderStrategyConfig config;
    private final Map<String, SectionInfo> sectionInfos = new ConcurrentHashMap<>();
    private final List<String> sectionOrder = new ArrayList<>();
    private String[] instruments;
    private String defaultInstrument;

    public ScoreBuilderStrategy(BasicScore szcore, ScoreBuilderStrategyConfig config) {
        this.szcore = szcore;
        this.config = config;
    }

    public void init() {
        if(config == null) {
            return;
        }
        List<String> sections = config.getSections();
        if(sections == null) {
            return;
        }
        for(String section : sections) {
            getOrCreateSectionInfo(section);
        }
    }

    private SectionInfo getOrCreateSectionInfo(String section) {
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

    class SectionInfo {
        private final String sectionId;
        private String owner;
        private final Map<String, String> clientInstrument = new HashMap<>();

        public SectionInfo(String sectionId) {
            this.sectionId = sectionId;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getOwner() {
            return owner;
        }

        public void addClientInstrument(String clientId, String instrumentId) {
            if(clientId == null || instrumentId == null) {
                return;
            }
            clientInstrument.put(clientId, instrumentId);
        }

        public String getClientInstrument(String clientId) {
            if(clientId == null) {
                return null;
            }
            return clientInstrument.get(clientId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SectionInfo that = (SectionInfo) o;
            return sectionId.equals(that.sectionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sectionId);
        }

        @Override
        public String toString() {
            return "SectionInfo{" +
                    "sectionId='" + sectionId + '\'' +
                    ", owner='" + owner + '\'' +
                    ", clientInstrument=" + clientInstrument +
                    '}';
        }
    }
}
