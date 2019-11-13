package com.xenaksys.szcore.score;


public class ScoreElement {
    private String scoreName;
    private String instrumentName;
    private String pageName;
    private int pageNo;
    private String barName;
    private int barNo;
    private int timeSigNum;
    private int timeSigDenom;
    private int tempoBpm;
    private int tempoBeatValue;
    private int beatNo;
    private long startTimeMillis;
    private long durationTimeMillis;
    private long endTimeMillis;
    private int startBaseBeatUnits;
    private int durationBeatUnits;
    private int endBaseBeatUnits;
    private int xStartPxl;
    private int xEndPxl;
    private int yStartPxl;
    private int yEndPxl;
    private int unitBeatNo;
    private int isUpbeat;
    private String resource;

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public int getBarNo() {
        return barNo;
    }

    public void setBarNo(int barNo) {
        this.barNo = barNo;
    }

    public int getTimeSigNum() {
        return timeSigNum;
    }

    public void setTimeSigNum(int timeSigNum) {
        this.timeSigNum = timeSigNum;
    }

    public int getTimeSigDenom() {
        return timeSigDenom;
    }

    public void setTimeSigDenom(int timeSigDenom) {
        this.timeSigDenom = timeSigDenom;
    }

    public int getTempoBpm() {
        return tempoBpm;
    }

    public void setTempoBpm(int tempoBpm) {
        this.tempoBpm = tempoBpm;
    }

    public int getTempoBeatValue() {
        return tempoBeatValue;
    }

    public void setTempoBeatValue(int tempoBeatValue) {
        this.tempoBeatValue = tempoBeatValue;
    }

    public int getBeatNo() {
        return beatNo;
    }

    public void setBeatNo(int beatNo) {
        this.beatNo = beatNo;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getDurationTimeMillis() {
        return durationTimeMillis;
    }

    public void setDurationTimeMillis(long durationTimeMillis) {
        this.durationTimeMillis = durationTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public int getStartBaseBeatUnits() {
        return startBaseBeatUnits;
    }

    public void setStartBaseBeatUnits(int startBaseBeatUnits) {
        this.startBaseBeatUnits = startBaseBeatUnits;
    }

    public int getDurationBeatUnits() {
        return durationBeatUnits;
    }

    public void setDurationBeatUnits(int durationBeatUnits) {
        this.durationBeatUnits = durationBeatUnits;
    }

    public int getEndBaseBeatUnits() {
        return endBaseBeatUnits;
    }

    public void setEndBaseBeatUnits(int endBaseBeatUnits) {
        this.endBaseBeatUnits = endBaseBeatUnits;
    }

    public int getxStartPxl() {
        return xStartPxl;
    }

    public void setxStartPxl(int xStartPxl) {
        this.xStartPxl = xStartPxl;
    }

    public int getxEndPxl() {
        return xEndPxl;
    }

    public void setxEndPxl(int xEndPxl) {
        this.xEndPxl = xEndPxl;
    }

    public int getyStartPxl() {
        return yStartPxl;
    }

    public void setyStartPxl(int yStartPxl) {
        this.yStartPxl = yStartPxl;
    }

    public int getyEndPxl() {
        return yEndPxl;
    }

    public void setyEndPxl(int yEndPxl) {
        this.yEndPxl = yEndPxl;
    }

    public int getUnitBeatNo() {
        return unitBeatNo;
    }

    public void setUnitBeatNo(int unitBeatNo) {
        this.unitBeatNo = unitBeatNo;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getIsUpbeat() {
        return isUpbeat;
    }

    public void setIsUpbeat(int isUpbeat) {
        this.isUpbeat = isUpbeat;
    }

    public String getResource() {
        return resource;
    }
}
