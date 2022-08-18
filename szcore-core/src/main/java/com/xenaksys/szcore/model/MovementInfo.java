package com.xenaksys.szcore.model;

import java.util.*;

public class MovementInfo {
    private final String movementId;
    private final Map<String, MovementSectionInfo> sections = new HashMap<>();
    private final Set<String> activeSections = new HashSet<>();

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
