package com.xenaksys.szcore.score;


import java.util.Comparator;
import java.util.Objects;

import static com.xenaksys.szcore.Consts.COMMA;

public class ScoreElement implements Comparable<ScoreElement>{
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

    public boolean isUpbeat() {
        return isUpbeat == 1;
    }

    public void setIsUpbeat(int isUpbeat) {
        this.isUpbeat = isUpbeat;
    }

    public String getResource() {
        return resource;
    }

    public static ScoreElement clone(ScoreElement se) {
        ScoreElement out = new ScoreElement();
        out.setScoreName(se.getScoreName());
        out.setInstrumentName(se.getInstrumentName());
        out.setPageName(se.getPageName());
        out.setPageNo(se.getPageNo());
        out.setBarName(se.getBarName());
        out.setBarNo(se.getBarNo());
        out.setTimeSigNum(se.getTimeSigNum());
        out.setTimeSigDenom(se.getTimeSigDenom());
        out.setTempoBpm(se.getTempoBpm());
        out.setTempoBeatValue(se.getTempoBeatValue());
        out.setBeatNo(se.getBeatNo());
        out.setStartTimeMillis(se.getStartTimeMillis());
        out.setDurationTimeMillis(se.getDurationTimeMillis());
        out.setEndTimeMillis(se.getEndTimeMillis());
        out.setStartBaseBeatUnits(se.getStartBaseBeatUnits());
        out.setDurationBeatUnits(se.getDurationBeatUnits());
        out.setEndBaseBeatUnits(se.getEndBaseBeatUnits());
        out.setxStartPxl(se.getxStartPxl());
        out.setxEndPxl(se.getxEndPxl());
        out.setyStartPxl(se.getyStartPxl());
        out.setyEndPxl(se.getyEndPxl());
        out.setUnitBeatNo(se.getUnitBeatNo());
        out.setIsUpbeat(se.getIsUpbeat());
        out.setResource(se.getResource());
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreElement that = (ScoreElement) o;
        return pageNo == that.pageNo && barNo == that.barNo && timeSigNum == that.timeSigNum && timeSigDenom == that.timeSigDenom && tempoBpm == that.tempoBpm && tempoBeatValue == that.tempoBeatValue && beatNo == that.beatNo && startTimeMillis == that.startTimeMillis && durationTimeMillis == that.durationTimeMillis && endTimeMillis == that.endTimeMillis && startBaseBeatUnits == that.startBaseBeatUnits && durationBeatUnits == that.durationBeatUnits && endBaseBeatUnits == that.endBaseBeatUnits && xStartPxl == that.xStartPxl && xEndPxl == that.xEndPxl && yStartPxl == that.yStartPxl && yEndPxl == that.yEndPxl && unitBeatNo == that.unitBeatNo && isUpbeat == that.isUpbeat && Objects.equals(scoreName, that.scoreName) && Objects.equals(instrumentName, that.instrumentName) && Objects.equals(pageName, that.pageName) && Objects.equals(barName, that.barName) && Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scoreName, instrumentName, pageName, pageNo, barName, barNo, timeSigNum, timeSigDenom, tempoBpm, tempoBeatValue, beatNo, startTimeMillis, durationTimeMillis, endTimeMillis, startBaseBeatUnits, durationBeatUnits, endBaseBeatUnits, xStartPxl, xEndPxl, yStartPxl, yEndPxl, unitBeatNo, isUpbeat, resource);
    }

    @Override
    public int compareTo(ScoreElement o) {
        return Comparator.comparingInt(ScoreElement::getBeatNo)
                .thenComparing(ScoreElement::getResource)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return "ScoreElement{" +
                "scoreName='" + scoreName + '\'' +
                ", instrumentName='" + instrumentName + '\'' +
                ", pageName='" + pageName + '\'' +
                ", pageNo=" + pageNo +
                ", barName='" + barName + '\'' +
                ", barNo=" + barNo +
                ", timeSigNum=" + timeSigNum +
                ", timeSigDenom=" + timeSigDenom +
                ", tempoBpm=" + tempoBpm +
                ", tempoBeatValue=" + tempoBeatValue +
                ", beatNo=" + beatNo +
                ", startTimeMillis=" + startTimeMillis +
                ", durationTimeMillis=" + durationTimeMillis +
                ", endTimeMillis=" + endTimeMillis +
                ", startBaseBeatUnits=" + startBaseBeatUnits +
                ", durationBeatUnits=" + durationBeatUnits +
                ", endBaseBeatUnits=" + endBaseBeatUnits +
                ", xStartPxl=" + xStartPxl +
                ", xEndPxl=" + xEndPxl +
                ", yStartPxl=" + yStartPxl +
                ", yEndPxl=" + yEndPxl +
                ", unitBeatNo=" + unitBeatNo +
                ", isUpbeat=" + isUpbeat +
                ", resource='" + resource + '\'' +
                '}';
    }

    public String toCsvString() {
        return scoreName + COMMA
                + instrumentName + COMMA
                + pageName + COMMA
                + pageNo + COMMA
                + barName + COMMA
                + barNo + COMMA
                + timeSigNum + COMMA
                + timeSigDenom + COMMA
                + tempoBpm  + COMMA
                + tempoBeatValue + COMMA
                + beatNo + COMMA
                + startTimeMillis + COMMA
                + durationTimeMillis + COMMA
                + endTimeMillis + COMMA
                + startBaseBeatUnits + COMMA
                + durationBeatUnits + COMMA
                + endBaseBeatUnits + COMMA
                + xStartPxl + COMMA
                + xEndPxl + COMMA
                + yStartPxl + COMMA
                + yEndPxl + COMMA
                + isUpbeat + COMMA
                + resource + COMMA
                + unitBeatNo;
    }
}
