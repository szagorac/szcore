package com.xenaksys.szcore.score.web;

import java.util.List;

public class WebScoreInfo {
    private String title;
    private String name;
    private String scoreDir;
    private String partHtmlPage;
    private List<String> instruments;
    private int bpm;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<String> instruments) {
        this.instruments = instruments;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public String getScoreDir() {
        return scoreDir;
    }

    public void setScoreDir(String scoreDir) {
        this.scoreDir = scoreDir;
    }

    public void setPartPageName(String partPageName) {
        this.partHtmlPage = partPageName;
    }

    public String getPartHtmlPage() {
        return partHtmlPage;
    }
}
