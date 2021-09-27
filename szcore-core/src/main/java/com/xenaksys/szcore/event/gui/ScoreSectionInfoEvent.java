package com.xenaksys.szcore.event.gui;

import com.xenaksys.szcore.model.Id;
import com.xenaksys.szcore.model.SectionInfo;

import java.util.List;

public class ScoreSectionInfoEvent extends ClientEvent {

    private final List<SectionInfo> sectionInfos;
    private final List<String> sectionOrder;
    private final Id scoreId;
    private final boolean isReady;
    private final String currentSection;
    private final String nextSection;

    public ScoreSectionInfoEvent(Id scoreId, List<SectionInfo> sectionInfos, List<String> sectionOrder, boolean isReady, String currentSection, String nextSection, long creationTime) {
        super(creationTime);
        this.scoreId = scoreId;
        this.sectionInfos = sectionInfos;
        this.sectionOrder = sectionOrder;
        this.isReady = isReady;
        this.currentSection = currentSection;
        this.nextSection = nextSection;
    }

    public Id getScoreId() {
        return scoreId;
    }

    public List<SectionInfo> getSectionInfos() {
        return sectionInfos;
    }

    public List<String> getSectionOrder() {
        return sectionOrder;
    }

    public boolean isReady() {
        return isReady;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public String getNextSection() {
        return nextSection;
    }

    @Override
    public ClientEventType getClientEventType() {
        return ClientEventType.SECTION_INFO;
    }

    @Override
    public String toString() {
        return "ScoreSectionInfoEvent{" +
                "scoreId=" + scoreId +
                ", sectionInfos=" + sectionInfos +
                ", sectionOrder=" + sectionOrder +
                ", isReady=" + isReady +
                '}';
    }
}
