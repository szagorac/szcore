package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.IntRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovementConfig {
    private String name;
    private Map<String, SectionConfig> sections = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<SectionConfig> getSections() {
        return sections.values();
    }

    public SectionConfig getSection(String name) {
        if(name == null) {
            return null;
        }
        return sections.get(name);
    }

    public void addSection(SectionConfig section) {
        if(section == null) {
            return;
        }
        this.sections.put(section.getName(), section);
    }

    public void addSections(List<SectionConfig> sections) {
        if(sections == null) {
            return;
        }
        for(SectionConfig section : sections) {
            this.sections.put(section.getName(), section);
        }
    }
    public void addSectionConfig(String name, IntRange pageRange, List<String> parts, List<String> maxConfig, List<String> webConfig) {
        SectionConfig sectionConfig = createSectionConfig(name, pageRange, parts, maxConfig, webConfig);
        addSection(sectionConfig);
    }

    public SectionConfig createSectionConfig(String name, IntRange pageRange, List<String> parts, List<String> maxConfig, List<String> webConfig) {
        return new SectionConfig(name, pageRange, parts, maxConfig, webConfig);
    }

    public class SectionConfig {
        private final String name;
        private final IntRange range;
        private final List<String> parts;
        private final List<String> maxConfig;
        private final List<String> webConfig;

        public SectionConfig(String name, IntRange range, List<String> parts, List<String> maxConfig, List<String> webConfig) {
            this.name = name;
            this.parts = parts;
            this.range = range;
            this.maxConfig = maxConfig;
            this.webConfig = webConfig;
        }

        public String getName() {
            return name;
        }

        public List<String> getParts() {
            return parts;
        }

        public IntRange getRange() {
            return range;
        }

        public List<String> getMaxConfig() {
            return maxConfig;
        }

        public List<String> getWebConfig() {
            return webConfig;
        }
    }
}
