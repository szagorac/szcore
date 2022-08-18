package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.IntRange;

import java.util.*;

public class MovementSectionInfo {
    private final String sectionId;
    private IntRange pageRange;
    private volatile boolean isActive;
    private volatile int currentPage;
    private final Map<String, String> clientPart = new HashMap<>();
    private final Map<String, List<String>> partClients = new HashMap<>();
    private final VoteInfo voteInfo = new VoteInfo();

    public MovementSectionInfo(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void addClientPart(String clientId, String partId) {
        if (clientId == null || partId == null) {
            return;
        }
        clientPart.put(clientId, partId);
        updatePartClient(clientId, partId);
    }

    private void updatePartClient(String clientId, String partId) {
        removeClientFromOtherPart(clientId, partId);
        List<String> clients = partClients.computeIfAbsent(partId, k -> new ArrayList<>());
        if (!clients.contains(clientId)) {
            clients.add(clientId);
        }
    }

    private void removeClientFromOtherPart(String clientId, String partId) {
        for (String instrument : partClients.keySet()) {
            if (instrument.equals(partId)) {
                continue;
            }
            List<String> clients = partClients.get(instrument);
            clients.remove(clientId);
        }
    }

    public String getClientPart(String clientId) {
        if (clientId == null) {
            return null;
        }
        return clientPart.get(clientId);
    }

    public Map<String, String> getClientParts() {
        return clientPart;
    }

    public Map<String, List<String>> getPartClients() {
        return partClients;
    }

    public void setPageRange(IntRange sectionPageRange) {
        this.pageRange = sectionPageRange;
    }

    public IntRange getPageRange() {
        return pageRange;
    }

    public int getEndPageNo() {
        IntRange intRange = getPageRange();
        if (intRange == null) {
            return -1;
        }
        return intRange.getEnd();
    }

    public int getStartPageNo() {
        IntRange intRange = getPageRange();
        if (intRange == null) {
            return -1;
        }
        return intRange.getStart();
    }

    public Set<String> getClients() {
        return clientPart.keySet();
    }

    public VoteInfo getVoteInfo() {
        return voteInfo;
    }

    public void populateVoteInfo(int current, int min, int max, int avg, int voterNo) {
        voteInfo.populate(current, min, max, avg, voterNo);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovementSectionInfo that = (MovementSectionInfo) o;
        return sectionId.equals(that.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }

    @Override
    public String toString() {
        return "SectionInfo{" +
                "sectionId='" + sectionId + '\'' +
                ", clientInstrument=" + clientPart +
                ", voteInfo=" + voteInfo +
                '}';
    }
}
