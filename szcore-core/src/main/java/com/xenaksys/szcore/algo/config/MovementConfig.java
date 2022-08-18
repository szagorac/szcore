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
    public void addSectionConfig(String name, List<String> parts, IntRange range) {
        SectionConfig sectionConfig = createSectionConfig(name, parts, range);
        addSection(sectionConfig);
    }

    public SectionConfig createSectionConfig(String name, List<String> parts, IntRange range) {
        return new SectionConfig(name, parts, range);
    }

    public class SectionConfig {
        private final String name;
        private final List<String> parts;
        private final IntRange range;

        public SectionConfig(String name, List<String> parts, IntRange range) {
            this.name = name;
            this.parts = parts;
            this.range = range;
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
    }
}
