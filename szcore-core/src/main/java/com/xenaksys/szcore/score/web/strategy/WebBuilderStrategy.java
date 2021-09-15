package com.xenaksys.szcore.score.web.strategy;

import java.util.List;

public class WebBuilderStrategy extends WebStrategy {
    private List<String> sections;
    private String assignmentType;
    private boolean isReady;

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
}
