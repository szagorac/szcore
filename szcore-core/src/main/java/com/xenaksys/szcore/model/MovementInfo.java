package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MovementInfo {
    static final Logger LOG = LoggerFactory.getLogger(MovementInfo.class);

    private final String movementId;
    private final Map<String, MovementSectionInfo> sections = new HashMap<>();
    private final Set<String> activeSections = new HashSet<>();
    private final List<List<String>> sectionsOrder = new ArrayList<>();

    private SequentalIntRange pageRange;
    private Set<String> parts = new HashSet<>();
    private List<String> scoreParts = new ArrayList<>();
    private int startPage;
    private volatile boolean isActive;
    private volatile int currentSectionOrderIndex;
    private volatile String currentSection;
    private volatile String nextSection;

    public MovementInfo(String movementId) {
        this.movementId = movementId;
    }

    public String getMovementId() {
        return movementId;
    }


    public List<MovementSectionInfo> getSections() {
        return new ArrayList<>(sections.values());
    }

    public MovementSectionInfo getSection(String sectionId) {
        if (sectionId == null) {
            return null;
        }
        return sections.get(sectionId);
    }

    public void addSection(MovementSectionInfo section) {
        if (section == null) {
            return;
        }
        sections.put(section.getSectionId(), section);
        recalcPageRange();
    }

    public void addSectionOrder(List<List<String>> sections) {
        if (sections == null) {
            return;
        }
        sectionsOrder.clear();
        sectionsOrder.addAll(sections);
    }

    public List<List<String>> getSectionsOrder() {
        return sectionsOrder;
    }

    public String getHighestVoteSection() {
        if(currentSectionOrderIndex < 0 || currentSectionOrderIndex >= sectionsOrder.size()) {
            LOG.error("getHighestVoteSection: invalid currentSectionOrderIndex: {}", currentSectionOrderIndex);
            return currentSection;
        }
        List<String> voteSections = sectionsOrder.get(currentSectionOrderIndex);
        int maxVote = Integer.MIN_VALUE;
        String maxVoteSection = null;
        for(String voteSection : voteSections) {
            MovementSectionInfo sectionInfo = getSection(voteSection);
            int sectionVote = sectionInfo.getVoteInfo().getCurrent();
            if(sectionVote > maxVote) {
                maxVoteSection = voteSection;
                maxVote = sectionVote;
            }
        }
        LOG.info("getHighestVoteSection: selected section: {} votes: {}", maxVoteSection, maxVote);
        return maxVoteSection;
    }

    public void activateSection(String sectionId) {
        if (sectionId == null) {
            return;
        }
        MovementSectionInfo sectionInfo = sections.get(sectionId);
        if (sectionInfo == null) {
            return;
        }
        sectionInfo.setActive(true);
        activeSections.add(sectionId);
    }

    public void deActivateSection(String sectionId) {
        if (sectionId == null) {
            return;
        }
        MovementSectionInfo sectionInfo = sections.get(sectionId);
        if (sectionInfo == null) {
            return;
        }
        sectionInfo.setActive(false);
        activeSections.remove(sectionId);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public SequentalIntRange getPageRange() {
        return pageRange;
    }

    public void recalcPageRange() {
        int firstPage = Integer.MAX_VALUE;
        int lastPage = 0;
        for (MovementSectionInfo sectionInfo : sections.values()) {
            IntRange sectionRange = sectionInfo.getPageRange();
            int sectionFirst = sectionRange.getStart();
            if(sectionFirst < firstPage) {
                firstPage = sectionFirst;
            }
            int sectionEnd = sectionRange.getEnd();
            if(sectionEnd > lastPage) {
                lastPage = sectionEnd;
            }
        }
        if (firstPage == Integer.MAX_VALUE) {
            firstPage = 0;
        }
        if (lastPage == 0) {
            lastPage = firstPage;
        }
        pageRange = new SequentalIntRange(firstPage, lastPage);
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public Set<String> getParts() {
        return parts;
    }

    public void addParts(List<String> parts) {
        this.parts.addAll(parts);
    }

    public List<String> getScoreParts() {
        return scoreParts;
    }

    public void addScoreParts(List<String> scoreParts) {
        this.scoreParts.addAll(scoreParts);
    }

    public boolean isScorePart(String part) {
        return getScoreParts().contains(part);
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public MovementSectionInfo getCurrentSectionInfo() {
        if(currentSection == null) {
            return null;
        }
        return sections.get(currentSection);
    }

    public MovementSectionInfo getNextSectionInfo() {
        if(nextSection == null) {
            return null;
        }
        return sections.get(nextSection);
    }

    public void setCurrentSection(String sectionName) {
        MovementSectionInfo sectionInfo = getSection(sectionName);
        if(sectionInfo == null) {
            LOG.error("setCurrentSection: invalid sectionInfo for section: {}", sectionName);
            return;
        }
        if(sectionName.equals(currentSection)) {
            return;
        }
        LOG.info("setCurrentSection: sectionName: {}", sectionName);
        this.currentSection = sectionName;
    }

    public int getCurrentSectionOrderIndex() {
        return currentSectionOrderIndex;
    }

    public void setCurrentSectionOrderIndex(int sectionOrderIndex) {
        if(sectionOrderIndex < 0 || sectionOrderIndex >= sectionsOrder.size()) {
            LOG.error("setCurrentSectionOrderIndex: invalid index: {}", sectionOrderIndex);
            return;
        }
        this.currentSectionOrderIndex = sectionOrderIndex;
    }

    public String getNextSection() {
        return nextSection;
    }

    public void setNextSection(String section) {
        this.nextSection = section;
    }

    public int getNextSectionStartPage() {
        if(nextSection == null) {
            LOG.error("getNextSectionStartPage: invalid next section");
            return -1;
        }
        MovementSectionInfo sectionInfo = getNextSectionInfo();
        if(sectionInfo == null) {
            LOG.error("getNextSectionStartPage: invalid next section info");
            return -1;
        }
        return sectionInfo.getStartPageNo();
    }

    public int getCurrentSectionNextPage() {
        MovementSectionInfo sectionInfo = getCurrentSectionInfo();
        if(sectionInfo == null) {
            return -1;
        }

        return sectionInfo.getNextPage();
    }

    public boolean onPageStart(int currentPage) {
        MovementSectionInfo currentSectionInfo = getCurrentSectionInfo();
        LOG.info("onPageStart: currentPage: {}, currentSection: {}", currentPage, currentSectionInfo);
        boolean isNextSection = true;
        if(currentSectionInfo != null) {
            if(currentSectionInfo.isSectionPage(currentPage) && currentSectionInfo.isActive()) {
                isNextSection = false;
                currentSectionInfo.setCurrentPlayPage(currentPage);
            }
        }
        if(isNextSection) {
            MovementSectionInfo nextSectionInfo = getNextSectionInfo();
            if (nextSectionInfo != null) {
                setCurrentSection(nextSectionInfo.getSectionId());
                nextSectionInfo.recalculatePlayPageRange(currentPage);
                nextSectionInfo.setCurrentPlayPage(currentPage);
                nextSectionInfo.setActive(true);
                LOG.info("onPageStart: starting next section {}", nextSectionInfo);
            } else {
                if(currentSectionInfo != null) {
                    LOG.info("onPageStart: invalid next Section, replaying current section");
                    currentSectionInfo.setCurrentPlayPage(currentPage);
                    setCurrentSection(currentSectionInfo.getSectionId());
                    currentSectionInfo.recalculatePlayPageRange(currentPage);
                    currentSectionInfo.setCurrentPlayPage(currentPage);
                    currentSectionInfo.setActive(true);
                }
            }
            setNextSection(null);
        }
        return isNextSection;
    }

    public void setSectionStartPage() {
        MovementSectionInfo currentSectionInfo = getCurrentSectionInfo();
        if(currentSectionInfo == null) {
            LOG.error("setSectionStartPage: invalid current movement section");
            setDefaultSection();
        }
        currentSectionInfo = getCurrentSectionInfo();
        if(currentSectionInfo == null) {
            LOG.error("setSectionStartPage: failed to set default section, ignoring ...");
            return;
        }
        currentSectionInfo.setCurrentPlayPage(startPage);
    }

    public void setDefaultSection() {
        List<List<String>>  sectionsOrder = getSectionsOrder();
        if(sectionsOrder == null || sectionsOrder.isEmpty()) {
            LOG.info("setDefaultSection: invalid sectionsOrder");
            return;
        }
        List<String> first = sectionsOrder.get(0);
        if(first == null || first.isEmpty()) {
            LOG.info("setDefaultSection: invalid first section in sections order");
            return;
        }
        String defaultSection = first.get(0);
        if (defaultSection == null) {
            LOG.info("setDefaultSection: invalid default section");
            return;
        }
        setCurrentSection(defaultSection);
    }

    public boolean isSourcePageInSection(int pageNo, String section) {
        if (section == null) {
            return false;
        }
        MovementSectionInfo sectionInfo = getSection(section);
        if (sectionInfo == null) {
            return false;
        }
        return sectionInfo.isSectionSourcePage(pageNo);
    }

    public void resetOnNewPosition() {
        for (MovementSectionInfo movementSectionInfo : sections.values()) {
            movementSectionInfo.resetOnNewPosition();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovementInfo that = (MovementInfo) o;
        return movementId.equals(that.movementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movementId);
    }

    @Override
    public String toString() {
        return "MovementInfo{" +
                "movementId='" + movementId + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
