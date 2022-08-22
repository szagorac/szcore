package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.IntRange;
import com.xenaksys.szcore.algo.SequentalIntRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MovementInfo {
    private final String movementId;
    private final Map<String, MovementSectionInfo> sections = new HashMap<>();
    private final Set<String> activeSections = new HashSet<>();

    private SequentalIntRange pageRange;
    private volatile boolean isActive;

    public MovementInfo(String movementId) {
        this.movementId = movementId;
    }

    public String getMovementId() {
        return movementId;
    }

    public void addClientPart(String clientId, String partId, String sectionId) {
        if (clientId == null || partId == null || sectionId == null) {
            return;
        }
        if (!sections.containsKey(sectionId)) {
            return;
        }
        MovementSectionInfo sectionInfo = sections.get(sectionId);
        sectionInfo.addClientPart(clientId, partId);
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

    public void addClientIdDefaultPart(String clientId, String partId) {
        for (String section : sections.keySet()) {
            addClientPart(clientId, partId, section);
        }
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
        if(firstPage == Integer.MAX_VALUE) {
            firstPage = 0;
        }
        if(lastPage == 0) {
            lastPage = firstPage;
        }
        pageRange = new SequentalIntRange(firstPage, lastPage);
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
