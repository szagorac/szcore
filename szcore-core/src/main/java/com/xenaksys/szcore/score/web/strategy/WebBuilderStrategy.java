package com.xenaksys.szcore.score.web.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebBuilderStrategy extends WebStrategy {
    private List<String> sections;
    private String assignmentType;
    private boolean isReady;
    private Map<String, String> sectionOwners;

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void setSectionOwners(Map<String, String> sectionOwners) {
        this.sectionOwners = sectionOwners;
    }

    public void addSectionOwner(String section, String owner) {
        if(this.sectionOwners == null) {
            this.sectionOwners = new HashMap<>();
        }
        this.sectionOwners.put(section, owner);
    }

    public Map<String, String> getSectionOwners() {
        return sectionOwners;
    }
}
