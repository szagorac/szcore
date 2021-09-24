package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.IntRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SectionInfo {
    private final String sectionId;
    private String owner;
    private IntRange pageRange;
    private final Map<String, String> clientInstrument = new HashMap<>();
    private final Map<String, List<String>> instrumentClients = new HashMap<>();

    public SectionInfo(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void addClientInstrument(String clientId, String instrumentId) {
        if(clientId == null || instrumentId == null) {
            return;
        }
        clientInstrument.put(clientId, instrumentId);
        updateInstrumentClient(clientId, instrumentId);
    }

    private void updateInstrumentClient(String clientId, String instrumentId) {
        removeClientFromOtherInstrument(clientId, instrumentId);
        List<String> clients = instrumentClients.computeIfAbsent(instrumentId, k -> new ArrayList<>());
        if(!clients.contains(clientId)) {
            clients.add(clientId);
        }
    }

    private void removeClientFromOtherInstrument(String clientId, String instrumentId) {
        for(String instrument : instrumentClients.keySet()) {
            if(instrument.equals(instrumentId)) {
                continue;
            }
            List<String> clients = instrumentClients.get(instrument);
            clients.remove(clientId);
        }
    }

    public String getClientInstrument(String clientId) {
        if(clientId == null) {
            return null;
        }
        return clientInstrument.get(clientId);
    }

    public Map<String, String> getClientInstruments() {
        return clientInstrument;
    }

    public Map<String, List<String>> getInstrumentClients() {
        return instrumentClients;
    }

    public void setPageRange(IntRange sectionPageRange) {
        this.pageRange = sectionPageRange;
    }

    public IntRange getPageRange() {
        return pageRange;
    }

    public int getEndPageNo() {
        IntRange intRange = getPageRange();
        if(intRange == null) {
            return -1;
        }
        return intRange.getEnd();
    }

    public int getStartPageNo() {
        IntRange intRange = getPageRange();
        if(intRange == null) {
            return -1;
        }
        return intRange.getStart();
    }

    public Set<String> getClients() {
        return clientInstrument.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionInfo that = (SectionInfo) o;
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
                ", owner='" + owner + '\'' +
                ", clientInstrument=" + clientInstrument +
                '}';
    }
}
