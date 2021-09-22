package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SectionAssignmentType;
import com.xenaksys.szcore.algo.StrategyType;

import java.util.ArrayList;
import java.util.List;

public class ScoreBuilderStrategyConfig implements StrategyConfig{

    private final List<BuilderPageRangeConfig> pageRangeConfigs = new ArrayList<>();
    private final List<String> sections = new ArrayList<>();
    private boolean isActive;
    private String scoreName;
    private SectionAssignmentType assignmentType;
    private boolean isStopOnSectionEnd;

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public List<BuilderPageRangeConfig> getPageRangeConfigs() {
        return pageRangeConfigs;
    }

    public void addPageRangeConfig(BuilderPageRangeConfig pageRangeConfig) {
        if (pageRangeConfig == null) {
            return;
        }
        pageRangeConfigs.add(pageRangeConfig);
    }

    public void addSections(List<String> sections) {
        this.sections.addAll(sections);
    }

    public List<String> getSections() {
        return sections;
    }

    public SectionAssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(SectionAssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public IntRange getSectionPageRange(String section) {
        if(section == null) {
            return null;
        }
        for(BuilderPageRangeConfig pageRangeConfig : pageRangeConfigs) {
            if(section.equals(pageRangeConfig.getSectionName())) {
                return pageRangeConfig.getRange();
            }
        }
        return null;
    }

    public boolean isStopOnSectionEnd() {
        return isStopOnSectionEnd;
    }

    public void setStopOnSectionEnd(boolean stopOnSectionEnd) {
        isStopOnSectionEnd = stopOnSectionEnd;
    }

    @Override
    public StrategyType getType() {
        return StrategyType.BUILDER;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}