package com.xenaksys.szcore.gui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

import static com.xenaksys.szcore.Consts.EMPTY;

public class MovementSection {
    private StringProperty section = new SimpleStringProperty(EMPTY);
    private IntegerProperty startPage = new SimpleIntegerProperty(0);
    private IntegerProperty endPage = new SimpleIntegerProperty(0);
    private IntegerProperty voteNo = new SimpleIntegerProperty(0);
    private IntegerProperty minVote = new SimpleIntegerProperty(0);
    private IntegerProperty maxVote = new SimpleIntegerProperty(0);
    private IntegerProperty avgVote = new SimpleIntegerProperty(0);
    private IntegerProperty voterNo = new SimpleIntegerProperty(0);
    private BooleanProperty isActive = new SimpleBooleanProperty(false);
    private ListProperty<String> parts = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final AudienceVote audienceVote = new AudienceVote();


    public String getSection() {
        return section.get();
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public int getStartPage() {
        return startPage.get();
    }

    public IntegerProperty startPageProperty() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage.set(startPage);
    }

    public int getEndPage() {
        return endPage.get();
    }

    public IntegerProperty endPageProperty() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage.set(endPage);
    }

    public int getVoteNo() {
        return voteNo.get();
    }

    public IntegerProperty voteNoProperty() {
        return voteNo;
    }

    public void setVoteNo(int voteNo) {
        this.voteNo.set(voteNo);
    }

    public int getMinVote() {
        return minVote.get();
    }

    public IntegerProperty minVoteProperty() {
        return minVote;
    }

    public void setMinVote(int minVote) {
        this.minVote.set(minVote);
    }

    public int getMaxVote() {
        return maxVote.get();
    }

    public IntegerProperty maxVoteProperty() {
        return maxVote;
    }

    public void setMaxVote(int maxVote) {
        this.maxVote.set(maxVote);
    }

    public int getAvgVote() {
        return avgVote.get();
    }

    public IntegerProperty avgVoteProperty() {
        return avgVote;
    }

    public void setAvgVote(int avgVote) {
        this.avgVote.set(avgVote);
    }

    public int getVoterNo() {
        return voterNo.get();
    }

    public IntegerProperty voterNoProperty() {
        return voterNo;
    }

    public void setVoterNo(int voterNo) {
        this.voterNo.set(voterNo);
    }

    public ObservableList<String> getParts() {
        return parts.get();
    }

    public ListProperty<String> partsProperty() {
        return parts;
    }

    public void addPart(String part) {
        this.parts.add(part);
    }

    public void setParts(ObservableList<String> parts) {
        this.parts.set(parts);
    }

    public boolean isIsActive() {
        return isActive.get();
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public AudienceVote getAudienceVote() {
        return audienceVote;
    }

    public void copy(MovementSection from) {
        setSection(from.getSection());
        setStartPage(from.getStartPage());
        setEndPage(from.getEndPage());
        setIsActive(from.isIsActive());
        setParts(from.getParts());
        setAvgVote(from.getAvgVote());
        setMaxVote(from.getMaxVote());
        setMinVote(from.getMinVote());
        setVoteNo(from.getVoteNo());
        setVoterNo(from.getVoterNo());
    }


    public void reset() {
        setSection(EMPTY);
        setStartPage(0);
        setEndPage(0);
        setIsActive(false);
        parts.clear();
        setAvgVote(0);
        setMaxVote(0);
        setMinVote(0);
        setVoteNo(0);
        setVoterNo(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovementSection section1 = (MovementSection) o;
        return section.getValue().equals(section1.section.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(section);
    }

    @Override
    public String toString() {
        return "Section{" +
                "section=" + section +
                ", startPage=" + startPage +
                ", endPage=" + endPage +
                ", voteNo=" + voteNo +
                ", minVote=" + minVote +
                ", maxVote=" + maxVote +
                ", avgVote=" + avgVote +
                '}';
    }

}
