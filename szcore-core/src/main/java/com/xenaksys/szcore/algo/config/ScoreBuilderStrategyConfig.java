package com.xenaksys.szcore.algo.config;

import com.xenaksys.szcore.algo.SectionAssignmentType;
import com.xenaksys.szcore.algo.StrategyType;

import java.util.ArrayList;
import java.util.List;

public class ScoreBuilderStrategyConfig implements StrategyConfig{

    private final List<BuilderPageRangeConfig> pageRangeConfigs = new ArrayList<>();
    private final List<String> sections = new ArrayList<>();
    private String scoreName;
    private SectionAssignmentType assignmentType;

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

    @Override
    public StrategyType getType() {
        return StrategyType.BUILDER;
    }
}