package com.xenaksys.szcore.model;

import com.xenaksys.szcore.algo.IntRange;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SectionInfo {
    private final String sectionId;
    private String owner;
    private IntRange pageRange;
    private final Map<String, String> clientInstrument = new HashMap<>();

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

    public void setPageRange(IntRange sectionPageRange) {
        this.pageRange = sectionPageRange;
    }

    public IntRange getPageRange() {
        return pageRange;
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
